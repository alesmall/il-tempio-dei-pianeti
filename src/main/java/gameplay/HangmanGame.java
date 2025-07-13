package gameplay;

import backend.DatabaseConnection;
import entity.Game;
import entity.GameManager;
import entity.Item;
import java.util.HashSet;
import java.util.Set;

/**
 * classe che gestisce la logica del gioco dell'impiccato (l'enigma del totem).
 */
public class HangmanGame {
    
    private static final String PHRASE_TO_GUESS = "CHI SEMINA VENTO RACCOGLIE TEMPESTA";
    private static final int MAX_FAILED_ATTEMPTS = 5;

    // stato del gioco
    private final char[] hiddenPhrase;
    private final Set<Character> alreadyGuessedLetters;
    private int failedAttempts = 0;
    private boolean isGameOver = false;

    /**
     * costruttore della classe, inizializza un nuovo gioco dell'impiccato.
     */
    public HangmanGame() {
        this.alreadyGuessedLetters = new HashSet<>();
        this.hiddenPhrase = new char[PHRASE_TO_GUESS.length()];

        for (int i = 0; i < PHRASE_TO_GUESS.length(); i++) {
            if (Character.isLetter(PHRASE_TO_GUESS.charAt(i))) {
                hiddenPhrase[i] = '_';
            } else {
                hiddenPhrase[i] = ' ';
            }
        }
    }

    /**
     * metodo principale che gestisce l'input del giocatore.
     * 
     * @param userInput l'input del giocatore
     */
    public void processInput(String userInput) {
        if (isGameOver) {
            OutputDisplayManager.displayText("> Il totem rimane immobile. La sfida è terminata.");
            return;
        }

        if (userInput == null || userInput.trim().isEmpty()) {
            OutputDisplayManager.displayText("> Il totem non reagisce. Devi pronunciare una lettera o l'intera frase.");
            return;
        }

        String normalizedInput = userInput.trim().toUpperCase();

        if (normalizedInput.length() == 1 && Character.isLetter(normalizedInput.charAt(0))) {
            checkLetter(normalizedInput.charAt(0));
        } else {
            checkPhrase(normalizedInput);
        }

        if (isGameWon()) {
            handleWin();
        } else if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            handleLoss();
        }
    }

    /**
     * controlla una singola lettera fornita dal giocatore.
     * 
     * @param letter la lettera da controllare
     */
    private void checkLetter(char letter) {
        if (alreadyGuessedLetters.contains(letter)) {
            OutputDisplayManager.displayText("> La lettera '" + letter + "' è già illuminata sul totem. Pronunciane un'altra.");
            return;
        }

        alreadyGuessedLetters.add(letter);
        boolean found = false;

        for (int i = 0; i < PHRASE_TO_GUESS.length(); i++) {
            if (PHRASE_TO_GUESS.charAt(i) == letter) {
                hiddenPhrase[i] = letter;
                found = true;
            }
        }

        if (found) {
            OutputDisplayManager.displayText("> Appena pronunci la lettera, i simboli corrispondenti sul totem si illuminano!\n" + getCurrentPhraseState());
        } else {
            failedAttempts++;
            int remainingAttempts = MAX_FAILED_ATTEMPTS - failedAttempts;
            OutputDisplayManager.displayText("> La lettera che hai pronunciato si dissolve nell'aria senza effetto. Un glifo del fallimento si incide sul totem.\n> Tentativi rimasti: " + remainingAttempts);
        }
    }

    /**
     * controlla se la frase inserita dal giocatore è la soluzione corretta.
     * 
     * @param guess la frase tentata dal giocatore
     */
    private void checkPhrase(String guess) {
        if (guess.equalsIgnoreCase(PHRASE_TO_GUESS)) {
            for (int i = 0; i < PHRASE_TO_GUESS.length(); i++) {
                hiddenPhrase[i] = PHRASE_TO_GUESS.charAt(i);
            }
        } else {
            failedAttempts++;
            int remainingAttempts = MAX_FAILED_ATTEMPTS - failedAttempts;
            OutputDisplayManager.displayText("> Pronunci l'intera iscrizione, ma il totem rimane impassibile. Non è la sequenza corretta.\n> Tentativi rimasti: " + remainingAttempts);
        }
    }
    
    /**
     * verifica se il giocatore ha indovinato l'intera frase.
     * 
     * @return true se non ci sono più lettere nascoste, false altrimenti
     */
    private boolean isGameWon() {
        for (char c : hiddenPhrase) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * gestisce la logica di vittoria del gioco.
     */
    private void handleWin() {
        isGameOver = true;
        OutputDisplayManager.displayText("> L'intera iscrizione sul totem brilla di una luce accecante! Hai dimostrato la tua saggezza.\n" + PHRASE_TO_GUESS);
        
        DatabaseConnection.printFromDB("osserva", "Terra", "correct", "0", "0");
        UserInputFlow.event = 0; 

        Game game = Game.getInstance();
        game.setRoomState("Terra", "correct");
        GameManager gameManager = new GameManager();
        Item cristalloTerra = gameManager.getItemFromName("CristalloTerra");
        cristalloTerra.setPickable(true);
        Item letteraTerra = gameManager.getItemFromName("LetteraTerra");
        letteraTerra.setPickable(true);
    }
    
    /**
     * gestisce la logica di sconfitta del gioco.
     */
    private void handleLoss() {
        isGameOver = true;
        OutputDisplayManager.displayText("> L'ultimo glifo del fallimento si completa. Le lettere luminose sul totem si spengono una ad una, lasciandoti nell'oscurità. Hai fallito. (osserva di nuovo il totem per riprovare...)");
        UserInputFlow.event = 0;
        DatabaseConnection.printFromDB("osserva", "Terra", "wrong", "0", "0");

        Game game = Game.getInstance();
        game.setRoomState("Terra", "wrong");
    }
    
    /**
     * costruisce e restituisce una stringa che rappresenta lo stato attuale della frase da indovinare.
     * aggiunge uno spazio dopo ogni lettera e quattro spazi tra le parole.
     * 
     * @return la rappresentazione testuale della frase formattata
     */
    private String getCurrentPhraseState() {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < hiddenPhrase.length; i++) {
            char currentChar = hiddenPhrase[i];
            
            if (currentChar == ' ') {
                sb.append("     ");
            } else {
                sb.append(currentChar).append(' ');
            }
        }
        
        return sb.toString().trim();
    }
}