package gameplay;

import backend.DatabaseConnection;
import entity.Game;
import entity.GameManager;
import entity.Item;
import GUI.GameGUI;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * classe che gestisce la logica del mini-gioco Wordle.
 * è disaccoppiata dalla GUI: riceve un input e restituisce un risultato.
 */
public class WordleGame {

    // enumerazioni pubbliche per una comunicazione chiara dello stato
    public enum GameState { IN_PROGRESS, VICTORY, DEFEAT }
    
    /**
     * rappresenta lo stato di una singola lettera dopo un tentativo.
     * ogni stato possiede anche il colore corrispondente da visualizzare nella GUI,
     * disaccoppiando la logica di gioco dalla decisione stilistica.
     */
    public enum LetterState {
        CORRECT_SPOT(new Color(23, 147, 10)), // la lettera è corretta e si trova nella posizione giusta
        WRONG_SPOT(new Color(178, 149, 22)), // la lettera è presente nella parola, ma in una posizione diversa
        NOT_IN_WORD(new Color(58, 58, 60)); // la lettera non è presente in nessuna posizione nella parola

        private final Color color; // ogni istanza del'enum ha il suo campo color
        
        /**
         * costruttore privato per associare un colore a ogni stato.
         * 
         * @param color il colore da associare a questo stato
         */
        LetterState(Color color) { this.color = color; } 
        
        public Color getColor() { return color; } // metodo getter, restituisce il colore associato a questo stato
    }
    
    /**
     * classe interna immutabile che contiene il risultato completo di un singolo tentativo.
     * garantisce la comunicazione strutturata tra la logica di gioco (WordleGame)
     * e la sua rappresentazione visiva (WordleGUI).
     */
    public static class GuessResult {
        public final GameState gameState; // lo stato generale del gioco dopo questo tentativo
        public final LetterState[] letterStates; // un array che descrive lo stato di ogni singola lettera del tentativo
        
        /**
         * costruttore per creare un nuovo risultato.
         * 
         * @param gameState lo stato attuale del gioco
         * @param letterStates l'array con lo stato di ogni lettera
         */
        public GuessResult(GameState gameState, LetterState[] letterStates) {
            this.gameState = gameState;
            this.letterStates = letterStates;
        }
    }
    
    // costanti e stato del gioco
    private static final int MAX_LETTERS = 5;
    private static final int MAX_ATTEMPTS = 6;
    
    private int currentAttempt = 0;
    private String wordToGuess;
    private boolean isGameOver = false;
    
    private static final String API_URL = "https://random-word-api.herokuapp.com/word?lang=it&length=5";
    private static final int TIMEOUT_MS = 5000;
    
    /**
     * inizializza una nuova partita, recuperando la parola dall'API.
     */
    public void initializeGame() {
        currentAttempt = 0;
        isGameOver = false;
        try {
            URI uri = new URI(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS); // stabilisce un limite di tempo per la richiesta di connessione iniziale con il server
            conn.setReadTimeout(TIMEOUT_MS); // stabilisce un limite di tempo per ricevere i dati dopo la connessione
            
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) { result.append(line); }
            }
            wordToGuess = result.toString()
                                .replace("[", "")
                                .replace("]", "")
                                .replace("\"", "")
                                .toUpperCase();
        } catch (Exception e) {
            System.err.println("errore API wordle, uso parola di default. causa: " + e.getMessage());
            wordToGuess = "COSMO";
        }
    }
    
    /**
     * metodo principale per processare il tentativo di un giocatore.
     * 
     * @param guess la parola di 5 lettere inserita
     * @return un oggetto GuessResult, o null se l'input non è valido
     */
    public GuessResult processGuess(String guess) {
        if (isGameOver) return null;

        if (!isValidGuess(guess)) {
            return null;
        }
        
        LetterState[] states = calculateLetterStates(guess.toUpperCase());
        currentAttempt++;
        
        if (guess.equalsIgnoreCase(wordToGuess)) {
            handleWin();
            return new GuessResult(GameState.VICTORY, states);
        }
        
        if (currentAttempt >= MAX_ATTEMPTS) {
            handleDefeat();
            return new GuessResult(GameState.DEFEAT, states);
        }
        
        return new GuessResult(GameState.IN_PROGRESS, states);
    }
    
    /**
     * controlla se l'input del giocatore è una parola valida di 5 lettere.
     * 
     * @param text l'input da controllare
     * @return true se valido, altrimenti false
     */
    public boolean isValidGuess(String text) {
        if (text == null || text.trim().length() != MAX_LETTERS) {
            OutputDisplayManager.displayText("> La creatura scuote la testa lentamente, il suono attutito dall'acqua... La sequenza deve essere composta da " + MAX_LETTERS + " lettere..." );
            return false;
        }
        return true;
    }

    /**
     * calcola lo stato per ogni lettera del tentativo.
     * 
     * @param guess la parola tentata
     * @return un array di LetterState
     */
    private LetterState[] calculateLetterStates(String guess) {
        LetterState[] states = new LetterState[MAX_LETTERS];
        char[] wordChars = wordToGuess.toCharArray();
        char[] guessChars = guess.toCharArray();
        
        for (int i = 0; i < MAX_LETTERS; i++) {
            if (guessChars[i] == wordChars[i]) {
                states[i] = LetterState.CORRECT_SPOT;
                wordChars[i] = '*'; // indica che quella lettera della parola giusta non serve più perché indovinata
                guessChars[i] = '-'; // indica che quella lettera della guess non serve più perché indovinata
            }
        }
        
        for (int i = 0; i < MAX_LETTERS; i++) { // per ogni lettera di guess
            if (guessChars[i] == '-') continue; // quella lettera della guess non serve perché già indovinata
            
            boolean found = false;
            for (int j = 0; j < MAX_LETTERS; j++) { // per ogni lettera di word
                if (guessChars[i] == wordChars[j]) {
                    states[i] = LetterState.WRONG_SPOT;
                    wordChars[j] = '*'; 
                    found = true;
                    break;
                }
            }
            if (!found) {
                states[i] = LetterState.NOT_IN_WORD;
            }
        }
        return states;
    }

    /**
     * esegue le azioni di gioco quando il giocatore vince.
     */
    private void handleWin() {
        isGameOver = true;
        OutputDisplayManager.displayText("> Gli occhi della creatura si illuminano di comprensione.");
        DatabaseConnection.printFromDB("osserva", "Nettuno", "correct", "0", "0");
        
        Game game = Game.getInstance();
        game.setRoomState("Nettuno", "correct");
        UserInputFlow.Event = 0;
        GameGUI.setImagePanel(game.getCurrentRoom().getName());
        
        GameManager gameManager = new GameManager();
        Item cristalloNettuno = gameManager.getItemFromName("CristalloNettuno");
        cristalloNettuno.setPickable(true);
        Item letteraNettuno = gameManager.getItemFromName("LetteraNettuno");
        letteraNettuno.setPickable(true);
    }

    /**
     * esegue le azioni di gioco quando il giocatore perde.
     */
    private void handleDefeat() {
        isGameOver = true;
        DatabaseConnection.printFromDB("osserva", "Nettuno", "wrong", "0", "0");
        OutputDisplayManager.displayText("> La parola segreta era: " + wordToGuess + " :( La creatura emette un suono malinconico e si ritira nell'oscurità. (osserva di nuovo la stanza per riprovare...)" );
                                             
        Game game = Game.getInstance();
        game.setRoomState("Nettuno", "wrong");
        UserInputFlow.Event = 0;
        GameGUI.setImagePanel(game.getCurrentRoom().getName());
    }
    
    /**
     * restituisce il numero di tentativi correnti (utile per la GUI).
     * 
     * @return il numero del tentativo attuale (da 0 a 5)
     */
    public int getCurrentAttempt() {
        return this.currentAttempt;
    }
}