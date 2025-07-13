package gameplay;

import backend.DatabaseConnection;
import entity.Game;
import entity.GameManager;
import entity.Item;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

/**
 * classe che gestisce il mini-gioco del trivia.
 * le domande vengono recuperate da un'API esterna.
 */
public class TriviaGame {

    // set di alias per interpretare l'input del giocatore.
    private static final Set<String> TRUE_ALIASES = Set.of("vero", "v", "si", "s", "true", "t");
    private static final Set<String> FALSE_ALIASES = Set.of("falso", "f", "no", "n", "false");
    
    // URL dell'API e configurazioni per la connessione
    private static final String API_URL = "https://opentdb.com/api.php?amount=1&category=17&difficulty=easy&type=boolean";
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIMEOUT_MS = 5000; // 5 secondi di timeout

    private int correctAnswers = 0;
    private String currentQuestion;
    private String correctAnswerFromAPI; // la risposta corretta fornita dall'API (es. "true" o "false")

    private static TriviaGame instance;

    /**
     * enumerazione per rappresentare l'intenzione del giocatore in modo chiaro.
     * è utile perché vogliamo usare una logica inversa secondo la quale
     * il vero è falso e il falso è vero.
     */
    private enum PlayerIntent {
        SAID_TRUE,
        SAID_FALSE,
        INVALID
    }

    /**
     * restituisce l'unica istanza del gioco.
     *
     * @return l'istanza di TriviaGame.
     */
    public static TriviaGame getInstance() {
        if (instance == null) {
            instance = new TriviaGame();
        }
        return instance;
    }

    /**
     * recupera una nuova domanda e risposta dall'API e la mostra al giocatore.
     * tenta più volte prima di arrendersi.
     */
    public void getNewQuestion() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
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
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                if (parseAPIResponse(result.toString())) {
                    displayQuestion();
                    return; // successo, esce dal ciclo e dal metodo
                }
            } catch (IOException | URISyntaxException e) {
                System.err.println("tentativo " + (attempt + 1) + " fallito nel contattare l'API: " + e.getMessage());
            }
        }
        // se tutti i tentativi falliscono (es. il computer non ha internet, o il sito dell'API è temporaneamente irraggiungibile)
        OutputDisplayManager.displayText("> Una voce incorporea sussurra nella tua mente...\n“The cosmic winds are troubled. The ancient knowledge cannot reach me now. Return when the ether is calm.”");
        UserInputFlow.event = 0;
    }

    /**
     * esegue il parsing della risposta JSON dall'API in modo sicuro.
     * 
     * @param jsonResponse la stringa JSON di risposta
     * @return true se il parsing ha successo, false altrimenti
     */
    private boolean parseAPIResponse(String jsonResponse) {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (root.has("results") && !root.get("results").getAsJsonArray().isEmpty()) {
                JsonObject questionData = root.get("results").getAsJsonArray().get(0).getAsJsonObject();
                
                JsonElement questionElem = questionData.get("question");
                JsonElement answerElem = questionData.get("correct_answer");

                if (questionElem != null && !questionElem.isJsonNull() && answerElem != null && !answerElem.isJsonNull()) {
                    this.currentQuestion = cleanHtmlString(questionElem.getAsString());
                    this.correctAnswerFromAPI = answerElem.getAsString();
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("errore nel parsing della risposta JSON: " + e.getMessage());
        }
        return false;
    }

    /**
     * controlla la risposta del giocatore.
     *
     * @param playerInput l'input testuale del giocatore
     */
    public void checkGuess(String playerInput) {
        PlayerIntent intent = getPlayerIntent(playerInput.toLowerCase());

        if (intent == PlayerIntent.INVALID) {
            OutputDisplayManager.displayText("> La voce riecheggia, irritata...\n“Your answer is meaningless. Focus. True, or false?”");
            return;
        }

        // logica della risposta invertita
        boolean playerFinalAnswer = (intent == PlayerIntent.SAID_TRUE) ? false : true;
        boolean correctAnswer = "true".equalsIgnoreCase(correctAnswerFromAPI); // il boolean è true se la risposta giusta è "true"

        if (playerFinalAnswer == correctAnswer) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
    }

    /**
     * gestisce la logica per una risposta corretta.
     */
    private void handleCorrectAnswer() {
        // logica a sbarramento: il contatore si incrementa solo con risposte corrette consecutive.
        correctAnswers++;
        if (correctAnswers >= 3) {
            OutputDisplayManager.displayText("> Una voce incorporea suona trionfante nella tua testa.\n“Impressive. You see beyond the veil of illusion. The path is clear. You have earned your reward.”");
            awardPlayer();
        } else {
            int remaining = 3 - correctAnswers;
            OutputDisplayManager.displayText("> Una voce incorporea sussurra con approvazione...\n“Correct. The illusion fades. " + remaining + " more seals remain.”");
            // a questo punto il gestore del flow richiamerà getnewquestion per ottenere la domanda successiva e continuare la sequenza
        }
    }

    /**
     * gestisce la logica per una risposta sbagliata, resettando i progressi.
     */
    private void handleWrongAnswer() {
        // logica a sbarramento: alla prima risposta sbagliata, il contatore viene resettato
        // e il giocatore deve ricominciare la sequenza di 3 risposte corrette.
        correctAnswers = 0;
        Game.getInstance().setRoomState("Urano", "wrong");
        OutputDisplayManager.displayText("> Un'ondata di delusione proveniente dalla voce ti pervade.\n“You falter. The illusion holds. We must begin anew.” (osserva di nuovo la stanza per riprovare...)");
        DatabaseConnection.printFromDB("osserva", "Urano", "wrong", "0", "0");
        UserInputFlow.event = 0;
    }
    
    /**
     * sblocca il cristallo e la lettera come premio al giocatore
     * per aver completato il trivia.
     */
    private void awardPlayer() {
        correctAnswers = 0;
        GameManager gameManager = new GameManager();
        Game game = Game.getInstance();
        game.setRoomState("Urano", "correct");
        
        Item cristalloUrano = gameManager.getItemFromName("CristalloUrano");
        cristalloUrano.setPickable(true);
        
        Item letteraUrano = gameManager.getItemFromName("LetteraUrano");
        letteraUrano.setPickable(true);
        
        DatabaseConnection.printFromDB("osserva", "Urano", "correct", "0", "0");
        UserInputFlow.event = 0;
    }

    /**
     * interpreta l'input del giocatore e lo converte in un'intenzione.
     * 
     * @param input l'input del giocatore, in minuscolo
     * @return l'intenzione del giocatore (SAID_TRUE, SAID_FALSE, o INVALID)
     */
    private PlayerIntent getPlayerIntent(String input) {
        if (TRUE_ALIASES.contains(input)) {
            return PlayerIntent.SAID_TRUE;
        }
        if (FALSE_ALIASES.contains(input)) {
            return PlayerIntent.SAID_FALSE;
        }
        return PlayerIntent.INVALID;
    }

    /**
     * mostra la domanda al giocatore.
     */
    private void displayQuestion() {
        OutputDisplayManager.displayText("> Antichi glifi si illuminano sul muro, formando una domanda nella tua mente:\n“" + currentQuestion + "”");
        OutputDisplayManager.displayText("> vero o falso? ");
    }
    
    /**
     * pulisce una stringa da entità HTML comuni.
     * 
     * @param text la stringa da pulire
     * @return la stringa pulita
     */
    private String cleanHtmlString(String text) {
        return text.replace("&quot;", "\"")
                    .replace("&deg;", "°")
                    .replace("&amp;", "&")
                    .replace("&apos;", "'")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&#039;", "'")
                    .replace("&eacute;", "é")
                    .replace("&egrave;", "è");
    }

    /**
     * resetta il contatore delle risposte corrette.
     */
    public void resetCorrectAnswers() {
        this.correctAnswers = 0;
    }
}