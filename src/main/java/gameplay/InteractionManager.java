package gameplay;

import entity.Item;
import entity.Room;
import util.Mixer;
import entity.Game;
import GUI.GameGUI;
import GUI.WordleGUI;
import entity.GameManager;
import backend.DatabaseConnection;

/**
 * classe che contiene la logica specifica delle interazioni del gioco.
 */
public class InteractionManager {

    private Game game;
    private final GameManager gameManager = new GameManager();

    /**
     * costruttore della classe.
     *
     * @param game l'istanza del gioco
     */
    public InteractionManager(Game game) {
        this.game = game;
    }

    /**
     * le (eventuali) azioni da eseguire quando il giocatore osserva un oggetto.
     *
     * @param i l'oggetto da osservare
     * @return true se l'osserva prevede azioni specifiche aggiuntive, false altrimenti
     */
    public boolean executeLook(Item i) { 
        if (i.hasName("Totem")
                && game.getCurrentRoom().getName().equals("Terra") && (game.getCurrentRoom().getState().equals("start")
                || game.getCurrentRoom().getState().equals("wrong"))) {
            i.getDescription(game.getCurrentRoom());
            UserInputFlow.event = 4; // impiccato
            UserInputFlow.startHangmanGame();
            OutputDisplayManager.displayText("> “_ _ _     _ _ _ _ _ _     _ _ _ _ _     _ _ _ _ _ _ _ _ _     _ _ _ _ _ _ _ _”");
            OutputDisplayManager.displayText("(puoi indovinare una lettera per volta o l'intera frase, senza virgolette e senza punto finale)");
            return true;
        }

        if (i.hasName("Terminale")
                && game.getCurrentRoom().getName().equals("Luna") && (game.getCurrentRoom().getState().equals("start")
                || game.getCurrentRoom().getState().equals("wrong"))) {
            i.getDescription(game.getCurrentRoom());
            OutputDisplayManager.displayText("> “_ _     _ _ _ _ _ _ _ _ _     _ _ _     _ _ _ _ _ _ _ _ _");
            OutputDisplayManager.displayText("_ _ _ _ _ _ _ _ _     L' _ _ _     _ _ _ _ _");
            OutputDisplayManager.displayText("_ _ _ _     _ _ _ _ _ _ _ _ _ _ _”");
            OutputDisplayManager.displayText("(inserisci l'intera frase, senza virgolette e senza punto finale)");
            UserInputFlow.event = 6; // messaggio segreto
            return true;
        }
        
        if (i.nameContains("Cristallo") && i.isPickable()) {
            OutputDisplayManager.displayText("> La superficie del cristallo emana un bagliore caldo e intenso. Al suo interno, frammenti di luce e memoria danzano silenziosi.");
            return true;
        }
        else if (i.nameContains("Cristallo") && !i.isPickable()) {
            OutputDisplayManager.displayText("> Il cristallo è ancora troppo fragile per essere toccato. La sua bellezza è un mistero che attende di essere svelato.");
            return true;
        }
        
        if (i.nameContains("Lettera") && i.isPickable()) {
            i.getDescription(game.getCurrentRoom());
            return true;
        }
        else if (i.nameContains("Lettera") && !i.isPickable()) {
            OutputDisplayManager.displayText("> La lettera non può ancora essere toccata. Le sue parole rimangono un enigma, custodito nel silenzio.");
            return true;
        }

        return false;
    }

    /**
     * le (eventuali) azioni da eseguire quando il giocatore osserva una stanza.
     *
     * @param room la stanza da osservare
     */
    public void executeRoomLook(Room room) { 
        if (room.getName().equals("Urano") && (room.getState().equals("start") || room.getState().equals("wrong"))) {
            UserInputFlow.event = 2; // per il trivia game 
            TriviaGame triviaGame = TriviaGame.getInstance();
            triviaGame.getNewQuestion();
        }
        if (room.getName().equals("Mercurio") && (room.getState().equals("start") || room.getState().equals("wrong"))) {
            OutputDisplayManager.displayText("> Una figura maestosa domina il cielo: tre stelle perfettamente allineate formano la cintura di un antico cacciatore, con la spada al fianco e il braccio alzato verso le stelle. Chi è? (inserisci solo il nome)");
            UserInputFlow.event = 3; // per il gioco della costellazione 
        }
        if (room.getName().equals("Nettuno") && (room.getState().equals("start") || room.getState().equals("wrong"))) { 
            UserInputFlow.event = 5; // per il wordle 
            GameGUI.setImagePanel("Wordle");
            WordleGUI wordlePanel = GameGUI.getWordle();
            if (wordlePanel != null) wordlePanel.startNewGameSession(); 
        }
    }

    /**
     * le (eventuali) azioni da eseguire quando il giocatore prende un oggetto.
     *
     * @param i l'oggetto da prendere
     * @return true se l'azione viene eseguita, false altrimenti
     */
    public boolean executeTake(Item i) { 
        if (i.nameContains("Cristallo") && i.nameContains(game.getCurrentRoom().getName())) { // quando si prende il cristallo, la stanza diventa done!!
            game.setRoomState(game.getCurrentRoom().getName(), "done");
            return true;
        }
        if (i.getName().equals("BraccialeVuoto")) { // quando si prende il bracciale, la stanza diventa done!! il gioco può iniziare
            game.setRoomState("Sole", "done");
            game.unlockCorridor("Sole", "Terra");
            game.unlockCorridor("Sole", "StanzaGSN");
            game.unlockCorridor("Sole", "StanzaMU");
            game.unlockCorridor("Sole", "StanzaMV");
            return true;
        }
        return false;
    }

    /**
     * le azioni da eseguire quando il giocatore usa un singolo oggetto.
     *
     * @param i l'oggetto usato
     * @return true se l'azione viene eseguita, false altrimenti
     */
    public boolean executeUseSingleItem(Item i) { 
        if (i.hasName("Mascherina") && game.getCurrentRoom().getName().equals("Venere")) {
            game.removeInventory(i);
            Item cristalloVenere = (Item) gameManager.getItemFromName("CristalloVenere");
            cristalloVenere.setPickable(true);
            Item letteraVenere = (Item) gameManager.getItemFromName("LetteraVenere");
            letteraVenere.setPickable(true);
            return true;
        }

        if ((i.hasName("Carne") || i.hasName("Frutta")) && game.getCurrentRoom().getName().equals("Giove")) {
            game.removeInventory(i);
            return true;
        }
        if (i.hasName("Grano") && game.getCurrentRoom().getName().equals("Giove")) {
            game.removeInventory(i);
            Item cristalloGiove = (Item) gameManager.getItemFromName("CristalloGiove");
            cristalloGiove.setPickable(true);
            Item letteraGiove = (Item) gameManager.getItemFromName("LetteraGiove");
            letteraGiove.setPickable(true);
            return true;
        }

        if (i.hasName("Specchio") && game.getCurrentRoom().getName().equals("Saturno")) {
            game.removeInventory(i);
            Item cristalloSaturno = (Item) gameManager.getItemFromName("CristalloSaturno");
            cristalloSaturno.setPickable(true);
            Item letteraSaturno = (Item) gameManager.getItemFromName("LetteraSaturno");
            letteraSaturno.setPickable(true);
            return true;
        }

        return false;
    }

    /**
     * le azioni da eseguire quando il giocatore usa una combinazione di un oggetto nell'inventario
     * e un oggetto presente nella stanza.
     *
     * @param i1 l'oggetto nell'inventario
     * @param i2 l'oggetto nella stanza
     * @return true se l'azione viene eseguita, false altrimenti
     */
    public boolean executeUseCombination(Item i1, Item i2) { 
        if (i1.hasName("BastoneAffilato") && i2.hasName("Portone")) {
            game.removeInventory(i1);
            Item cristalloMarte = (Item) gameManager.getItemFromName("CristalloMarte");
            cristalloMarte.setPickable(true);
            Item letteraMarte = (Item) gameManager.getItemFromName("LetteraMarte");
            letteraMarte.setPickable(true);
            return true;
        }
        if (i1.hasName("Bastone") && i2.hasName("Lama")) {
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BastoneAffilato"));
            DatabaseConnection.printFromDB("unisci", "0", "0", i1.getName(), i2.getName());
            return true;
        }
        if (i1.hasName("BraccialeVuoto") && i2.hasName("Nucleo")) {
            UserInputFlow.event = 7; // oblio totale
            Mixer.playEffect("leaving");
            game.removeInventory(i1);
            game.setRoomState("Sole", "OblioTotale");
            return true;
        } else if (i1.hasName("BraccialeStellare") && i2.hasName("Nucleo")) {
            UserInputFlow.event = 9; // rinascita stellare
            Mixer.playEffect("leaving");
            game.removeInventory(i1);
            game.setRoomState("Sole", "RinascitaStellare");
            return true;
        } else if (i1.hasName("BraccialeLunare") && i2.hasName("Nucleo")) {
            UserInputFlow.event = 10; // risveglio cosmico
            Mixer.playEffect("leaving");
            game.removeInventory(i1);
            game.setRoomState("Sole", "RisveglioCosmico");
            return true;
        } else if (i1.nameContains("Bracciale") && i2.hasName("Nucleo")) {
            UserInputFlow.event = 8; // parziale salvezza
            Mixer.playEffect("leaving");
            game.removeInventory(i1);
            game.setRoomState("Sole", "ParzialeSalvezza");
            return true;
        }
        
        return false;
    }

    /**
     * le azioni da eseguire quando il giocatore fonde due oggetti.
     *
     * @param i1 il primo oggetto
     * @param i2 il secondo oggetto
     * @return true se l'azione viene eseguita, false altrimenti
     */
    public boolean executeFuseCombination(Item i1, Item i2) {
        if (areItems(i1,i2,"Bastone", "Lama")) {
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BastoneAffilato"));
            DatabaseConnection.printFromDB("unisci", "0", "0", i1.getName(), i2.getName());
            return true;
        }

        if (areItems(i1,i2,"Panno", "Corda")) {
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("Mascherina"));
            DatabaseConnection.printFromDB("unisci", "0", "0", i1.getName(), i2.getName());
            return true;
        }

        if (isItemUpgrade(i1,i2,"BraccialeVuoto")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeLucente"));
            OutputDisplayManager.displayText("> Il tuo bracciale vuoto è diventato un bracciale lucente (1)! È un primo passo, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeLucente")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeIntarsiato"));
            OutputDisplayManager.displayText("> Il tuo bracciale lucente è diventato un bracciale intarsiato (2)! Ottimo lavoro, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeIntarsiato")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeArmonico"));
            OutputDisplayManager.displayText("> Il tuo bracciale intarsiato è diventato un bracciale armonico (3)! Ottimo lavoro, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeArmonico")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeRisonante"));
            OutputDisplayManager.displayText("> Il tuo bracciale armonico è diventato un bracciale risonante (4)! Ottimo lavoro, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeRisonante")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeIncantato"));
            OutputDisplayManager.displayText("> Il tuo bracciale risonante è diventato un bracciale incantato (5)! Ottimo lavoro, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeIncantato")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeSplendente"));
            OutputDisplayManager.displayText("> Il tuo bracciale incantato è diventato un bracciale splendente (6)! Ci sei quasi, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeSplendente")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeCeleste"));
            OutputDisplayManager.displayText("> Il tuo bracciale splendente è diventato un bracciale celeste (7)! Ci sei quasi, continua così :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeCeleste")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeStellare"));
            game.unlockCorridor("Terra", "Luna");
            OutputDisplayManager.displayText("> Il tuo bracciale celeste è diventato un bracciale stellare (8)! La missione potrebbe terminare qui, consegnando il bracciale al Nucleo, ma forse ci sono ancora segreti da scoprire... :)");
            return true;
        }
        if (isItemUpgrade(i1,i2,"BraccialeStellare")) {
            Mixer.playEffect("binding");
            game.removeInventory(i1);
            game.removeInventory(i2);
            game.addInventory((Item) gameManager.getItemFromName("BraccialeLunare"));
            OutputDisplayManager.displayText("> Il tuo bracciale stellare è diventato un bracciale lunare! Il bracciale è ora completo, non ti resta che consegnarlo al Nucleo :)");
            return true;
        }

        return false;
    }

    /**
     * metodo di supporto per verificare se due oggetti hanno i nomi specificati,
     * indipendentemente dal loro ordine. semplifica i controlli di combinazione per la fusione.
     * @param i1 il primo oggetto
     * @param i2 il secondo oggetto
     * @param name1 il nome del primo oggetto da verificare
     * @param name2 il nome del secondo oggetto da verificare
     * @return true se i nomi corrispondono, false altrimenti
     */
    private boolean areItems(Item i1, Item i2, String name1, String name2) {
        return (i1.hasName(name1) && i2.hasName(name2)) || (i1.hasName(name2) && i2.hasName(name1));
    }

    /**
     * metodo di supporto per verificare se due oggetti sono uno un bracciale e l'altro un cristallo
     * indipendentemente dal loro ordine. semplifica i controlli di combinazione per la fusione.
     * @param i1 il primo oggetto
     * @param i2 il secondo oggetto
     * @param name il nome del bracciale a cui fare l'upgrade
     * @return true se i nomi corrispondono, false altrimenti
     */
    private boolean isItemUpgrade(Item i1, Item i2, String name) {
        return (i1.nameContains("Cristallo") && i2.hasName(name)) || (i1.hasName(name) && i2.nameContains("Cristallo"));
    }
}
