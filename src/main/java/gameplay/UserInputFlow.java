package gameplay;

import engine.CommandExecutor;
import engine.Parser;
import entity.Game;
import entity.GameManager;
import entity.Item;
import GUI.GameGUI;
import GUI.WordleGUI;
import GUI.ManagerGUI;
import backend.Client;
import backend.DatabaseConnection;
import engine.ParsedCommand;
import util.TimerManager;
import java.util.List;

/**
 * classe che invia l'input dell'utente alla parte corretta del gioco
 * e gestisce gli eventi più semplici.
 */
public class UserInputFlow {

    public static int Event;
    private static Parser parser;
    private static CommandExecutor commandExecutor;
    private static WordleGUI wordleGUI;
    private static TriviaGame triviaGame;
    private static HangmanGame hangmanGame;
    private static boolean isNameConfirmed;
    private static boolean isGameOver;
    private static boolean starsGuessed = false;

    /**
     * gestisce il flusso del gioco in base all'evento corrente.
     *
     * @param text l'input dell'utente
     */
    public static void gameFlow(final String text) {
        OutputDisplayManager.displayText(text);

        switch (Event) { 
            case 0:
                parserFlow(text);
                break;
            case 1:
                nicknameFlow(text);
                break;
            case 2: // urano 
                triviaFlow(text);
                break;
            case 3: // mercurio
                starsFlow(text);
                break;
            case 4: // terra
                hangmanFlow(text);
                break;
            case 5: // nettuno
                wordleFlow(text);
                break;
            case 6: // luna 
                secretFlow(text);
                break;
            case 7:
                endingFlow("OblioTotale");
                break;
            case 8:
                endingFlow("ParzialeSalvezza");
                break;
            case 9:
                endingFlow("RinascitaStellare");
                break;
            case 10:
                endingFlow("RisveglioCosmico");
                break;
            default:
                parserFlow(text);
                break;
        }
    }

    /**
     * metodo che gestisce il parsing e l'esecuzione dei comandi.
     *
     * @param text l'input dell'utente
     */
    private static void parserFlow(final String text) {
        ParsedCommand output = parser.parse(text);
        if (output.getCommand() != null) {
            commandExecutor.execute(output);
        } else {
            OutputDisplayManager.displayText("> Il tempio non comprende questo gesto, riprova! :(");
        }
    }

    /**
     * metodo che gestisce l'input del nickname.
     *
     * @param text l'input dell'utente
     */
    private static void nicknameFlow(final String text) {
        if (!isNameConfirmed) {
            OutputDisplayManager.displayText("> È questo il nome che vuoi incidere nell'universo? (S/N)");
            Game.getInstance().setNickname(text);
            isNameConfirmed = true;
        } else {
            if (text.equalsIgnoreCase("S")) {
                OutputDisplayManager.displayText("> Perfetto! Il cosmo ti stava aspettando, " + Game.getInstance().getNickname() + ".");
                OutputDisplayManager.displayText("> Il tuo cammino tra i pianeti può cominciare. Guardati intorno: anche il silenzio nasconde segreti.");
                Event = 0;
            } else {
                OutputDisplayManager.displayText("> Forse è stato un errore del cosmo. Ripeti il tuo nome.");
                isNameConfirmed = false;
            }
        }
    }

    /**
     * metodo che gestisce l'evento del gioco trivia.
     *
     * @param text l'input dell'utente
     */
    public static void triviaFlow(final String text) {
        triviaGame = TriviaGame.getInstance();

        try {
            triviaGame.checkGuess(text);
            if (Event == 2) triviaGame.getNewQuestion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo che gestisce l'evento delle costellazioni.
     * 
     * @param text l'input dell'utente
     */
    public static void starsFlow(final String text) {
        String upText = text.toUpperCase();
        String star1 = "ORIONE";
        String star2 = "PEGASO";

        if (upText.equals(star1) && !starsGuessed) {
            OutputDisplayManager.displayText("> “Ottimo lavoro, viaggiatore delle stelle... ma la prova non è ancora conclusa: alza lo sguardo e dimmi, quale costellazione si cela ora tra le luci del cielo?”");
            OutputDisplayManager.displayText("> Quattro stelle disegnano un grande quadrato scintillante: è il corpo del cavallo alato, pronto a spiccare il volo tra le galassie. Chi è? (inserisci solo il nome)");
            starsGuessed = true;
        } else if (starsGuessed && upText.equals(star2)) {
            DatabaseConnection.printFromDB("osserva", "Mercurio", "correct", "0", "0");
            Game game = Game.getInstance();
            GameManager gameManager = new GameManager();
            game.getCurrentRoom().setState("correct");
            Item cristalloMercurio = (Item) gameManager.getItemFromName("CristalloMercurio");
            cristalloMercurio.setPickable(true);
            Item letteraMercurio = (Item) gameManager.getItemFromName("LetteraMercurio");
            letteraMercurio.setPickable(true);
            UserInputFlow.Event = 0;
        } else {
            DatabaseConnection.printFromDB("osserva", "Mercurio", "wrong", "0", "0");
            OutputDisplayManager.displayText("> (osserva di nuovo la stanza per riprovare...)");
            starsGuessed = false;
            Game game = Game.getInstance();
            game.getCurrentRoom().setState("wrong");
            UserInputFlow.Event = 0;
        }
    }

    /**
     * avvia il gioco dell'impiccato.
     */
    public static void startHangmanGame() {
        hangmanGame = new HangmanGame(); // ogni volta che il giocatore avvia l'enigma del totem, crei una nuova istanza dell'oggetto HangmanGame, quindi non serve resettarlo
    }

    /** 
     * metodo che gestisce l'evento del gioco dell'impiccato.
     *
     * @param text l'input dell'utente
     */
    private static void hangmanFlow(final String text) {
        hangmanGame.processInput(text);
    }

    /** 
     * metodo che gestisce l'evento del gioco wordle.
     *
     * @param text l'input dell'utente
     */
    public static void wordleFlow(final String text) {
        wordleGUI = GameGUI.getWordle();
        wordleGUI.processPlayerGuess(text.trim().toUpperCase());
    }

    /**
     * metodo che gestisce l'evento del messaggio segreto della Luna.
     *
     * @param text l'input dell'utente
     */
    private static void secretFlow(final String text) {
        String upText = text.toUpperCase();
        String secret = "IL BRACCIALE DEL VIANDANTE RISVEGLIA L''ECO DELLA LUCE DIMENTICATA";

        if (upText.equals(secret)) {
            DatabaseConnection.printFromDB("osserva", "Luna", "correct", "0", "0");

            Game game = Game.getInstance();
            GameManager gameManager = new GameManager();
            game.getCurrentRoom().setState("correct");
            Item cristalloLuna = (Item) gameManager.getItemFromName("CristalloLuna");
            cristalloLuna.setPickable(true);
            Item letteraLuna = (Item) gameManager.getItemFromName("LetteraLuna");
            letteraLuna.setPickable(true);
        } else {
            DatabaseConnection.printFromDB("osserva", "Luna", "wrong", "0", "0");
            OutputDisplayManager.displayText("> (osserva di nuovo il terminale per riprovare...)");
            Game game = Game.getInstance();
            game.getCurrentRoom().setState("wrong");
        }
        UserInputFlow.Event = 0;
    }

    /** 
     * metodo che gestisce la fine del gioco.
     *
     * @param text l'input dell'utente
     */
    private static void endingFlow(final String finale) throws RuntimeException {
        if (!isGameOver) {
            DatabaseConnection.printFromDB("0", "Sole", finale, "0", "0"); // il finale
            GameGUI.setImagePanel(finale);

            try {
                Client client = new Client();
                client.sendPostRequest(Game.getInstance().getNickname(), TimerManager.getTime(), finale);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            OutputDisplayManager.displayText("");
            DatabaseConnection.printFromDB("0", finale, finale, "0", "0"); // "frase ad effetto" di quel finale
            OutputDisplayManager.displayText("> Il tuo viaggio è giunto al termine. Grazie per aver attraversato il Tempio dei Pianeti!! <3"); 
            OutputDisplayManager.displayText("> Scrivi qualsiasi cosa per concludere e tornare al menù. :)");
            isGameOver = true;
        } else {
            ManagerGUI.closeGame();
        }
    }

    /**
     * imposta il flusso di gioco per una nuova partita.
     */
    public static void setUpGameFlow(final Game game) {
        Event = 1;
        DatabaseConnection.printFromDB("0", "Sole", "start", "0", "0"); 
        isNameConfirmed = false;
        isGameOver = false;
        // TODO non so se serve new Thread(() -> wordleGame = new WordleGame()).start(); // avvia il gioco wordle in un thread separato, per evitare che il gioco si "congeli" in attesa della risposta dall'API (che potrebbe essere lenta o non funzionare).
        // il gioco principale continua a funzionare fluidamente, e quando il Wordle sarà pronto, la variabile wordleGame verrà valorizzata in background.
        triviaGame = TriviaGame.getInstance(); // prepara il mini-gioco dei Trivia. Ottiene l'istanza del gioco e ne resetta il punteggio
        triviaGame.resetCorrectAnswers();
        parser = new Parser();
        commandExecutor = new CommandExecutor(game);
    }

    /**
     * imposta il flusso di gioco per una partita già caricata.
     */
    public static void setUpLoadedGameFlow(final Game game) {
        Event = 0;
        isNameConfirmed = true;
        isGameOver = false;
        // TODO non so se serve new Thread(() -> wordleGame = new WordleGame()).start();
        triviaGame = TriviaGame.getInstance(); // TODO: forse è da memorizzare il completamento dei minigiochi, per non ricaricarli inutilmente
        triviaGame.resetCorrectAnswers();
        parser = new Parser();
        commandExecutor = new CommandExecutor(game);
        List<String> itemsNames = game.getInventory().stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GameGUI.updateInventoryTextArea(itemsNamesArray);
        OutputDisplayManager.displayText("> Il cosmo ti riconosce, " + game.getNickname() + ". Ricordi la tua ultima scelta? Guardati intorno.");
        DatabaseConnection.printFromDB("0", game.getCurrentRoom().getName(), game.getRoomState(game.getCurrentRoom().getName()).toString(), "0", "0");
    }
}
