package engine;

import entity.*;
import gameplay.InteractionManager;
import gameplay.OutputDisplayManager;

import java.util.HashMap;
import java.util.Set;

import backend.DatabaseConnection;

/**
 * classe che gestisce l'esecuzione dei comandi interpretati dal Parser.
 * funziona come un dispatcher che, basandosi su una chiave (CommandKey),
 * invoca l'azione (Action) corretta.
 */
public class CommandExecutor {

    private Game game;
    private HashMap<CommandKey, Action> commandMap;
    private InteractionManager interaction;

    /**
     * istanzia una mappa di tutti i comandi e i loro comportamenti/azioni.
     *
     * @param game l'istanza del gioco
     */
    public CommandExecutor(Game game) {
        this.game = game;
        this.interaction = new InteractionManager(game);
        commandMap = new HashMap<>();

        initializeDefaultActions();
    }

    /**
     * registra un'azione per una data chiave.
     * 
     * @param key la chiave del comando
     * @param action l'azione da eseguire quando il comando viene invocato
     */
    public void registerAction(CommandKey key, Action action) {
        commandMap.put(key, action);
    }

    /**
     * comportamento generalizzato dei comandi di movimento.
     * 
     * @param direction la direzione del comando
     * @return un'azione che permette di muoversi nella direzione specificata
     */
    private Action createDirectionAction(CommandType direction) {
        return _ -> {
            Corridor corridor = game.getCorridorsMap().stream()
                    .filter(c -> c.getStartingRoom().getName().equals(game.getCurrentRoom().getName()) && c.getDirection() == direction)
                    .findFirst()
                    .orElse(null);

            if (corridor != null && !corridor.isLocked()) {
                game.setCurrentRoom(corridor.getArrivingRoom());
                DatabaseConnection.printFromDB("0", game.getCurrentRoom().getName(), game.getCurrentRoom().getState(), "0", "0"); 
            } else if (corridor != null && corridor.isLocked()) {
                OutputDisplayManager.displayText("> Il corridoio verso " + direction + " è bloccato! Riprova quando avrai il bracciale completo..."); 
            } else {
                OutputDisplayManager.displayText("> Ahia, è un muro quello! Non c'è un corridoio verso " + direction + "."); 
            }
        };
    }

    /**
     * inizializza le azioni predefinite per i comandi del gioco.
     * registra i comandi di movimento, osservazione, aiuto, inventario,
     * presa, rilascio, uso e fusione degli oggetti.
     */
    private void initializeDefaultActions() {
        // comando per stampare l'aiuto
        registerAction(new CommandKey(CommandType.AIUTO, 0),
                _ -> {
                    OutputDisplayManager.displayText("> Comandi disponibili:");
                    GameManager gameManager = new GameManager();
                    Set<Command> commands = gameManager.getAllCommands();
                    commands.forEach(c -> OutputDisplayManager.displayText(">  - " + c.getName()));
                    OutputDisplayManager.displayText("> (hint: per ulteriori informazioni clicca sul punto interrogativo in alto)"); 
                }
        );

        // comando per andare a nord
        registerAction(new CommandKey(CommandType.NORD, 0),
                createDirectionAction(CommandType.NORD));

        // comando per andare a sud
        registerAction(new CommandKey(CommandType.SUD, 0),
                createDirectionAction(CommandType.SUD));

        // comando per andare a est
        registerAction(new CommandKey(CommandType.EST, 0),
                createDirectionAction(CommandType.EST));

        // comando per andare a ovest
        registerAction(new CommandKey(CommandType.OVEST, 0),
                createDirectionAction(CommandType.OVEST));

        // comando per stampare l'inventario
        registerAction(new CommandKey(CommandType.INVENTARIO, 0),
                _ -> game.printInventory());
        
        // comando per prendere un oggetto
        registerAction(new CommandKey(CommandType.PRENDI, 1),
                p -> {
                    if (game.getInventory().contains(p.getItem1())) {
                        OutputDisplayManager.displayText("> Hai già " + p.getItem1().getName() + " nell'inventario!");
                    } else if (game.getCurrentRoom().getItems().contains(p.getItem1())) {
                        if (p.getItem1().isPickable()) {
                            game.addInventory(p.getItem1());
                            game.getCurrentRoom().removeItem(p.getItem1());
                            interaction.executeTake(p.getItem1());
                            OutputDisplayManager.displayText("> Hai raccolto: " + p.getItem1().getName() + "!");
                        } else {
                            OutputDisplayManager.displayText("> Non puoi raccogliere " + p.getItem1().getName() + "!");
                        }
                    } else {
                        OutputDisplayManager.displayText("> Non c'è " + p.getItem1().getName() + " nella stanza!");
                    }
                });

        // comando per lasciare un oggetto
        registerAction(new CommandKey(CommandType.LASCIA, 1),
                p -> {
                    if (game.getInventory().contains(p.getItem1())) {
                        OutputDisplayManager.displayText("> Hai lasciato cadere: " + p.getItem1().getName() + "!");
                        game.removeInventory(p.getItem1());
                        game.getCurrentRoom().getItems().add(p.getItem1());
                    } else if (game.getCurrentRoom().getItems().contains(p.getItem1())) {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " è già per terra!");
                    } else {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " non è nell'inventario!");
                    }
                });
        
        // comando per stampare la descrizione della stanza
        registerAction(new CommandKey(CommandType.OSSERVA, 0),
                _ -> {
                    game.getCurrentRoom().printDescription();
                    interaction.executeRoomLook(game.getCurrentRoom());
                });

        // comando per osservare un oggetto
        registerAction(new CommandKey(CommandType.OSSERVA, 1),
                p -> {
                    if ((game.getCurrentRoom().getItems().contains(p.getItem1())) || (game.getInventory().contains(p.getItem1()))) {
                        if(!interaction.executeLook(p.getItem1())) { // se non sono previste azioni aggiuntive dopo l'osserva, stampa direttamente la descrizione dell'oggetto
                            p.getItem1().getDescription(game.getCurrentRoom());
                        }
                    } else {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " non è nè nella stanza nè nell'inventario!");
                    }
                });

        // comando per usare un oggetto da solo
        registerAction(new CommandKey(CommandType.USA, 1),
                p -> {
                    if (game.getInventory().contains(p.getItem1()) || game.getCurrentRoom().getItems().contains(p.getItem1())) {
                        String statusBeforeAction = game.getCurrentRoom().getState();
                        if (interaction.executeUseSingleItem(p.getItem1())) {
                            DatabaseConnection.printFromDB("usa", game.getCurrentRoom().getName(), statusBeforeAction, p.getItem1().getName(), "0"); 
                        } else {
                            OutputDisplayManager.displayText("> Non puoi usare " + p.getItem1().getName() + " qui o da solo!");
                        }
                    } else {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " non è nè nell'inventario nè nella stanza!");
                    }
                });

        // comando per usare un oggetto su un altro oggetto
        registerAction(new CommandKey(CommandType.USA, 2),
                p -> {
                    if (game.getInventory().contains(p.getItem1())) {
                        if (game.getCurrentRoom().getItems().contains(p.getItem2())) {
                            String statusBeforeAction = game.getCurrentRoom().getState();
                            if (interaction.executeUseCombination(p.getItem1(), p.getItem2())) {
                                DatabaseConnection.printFromDB("usa", game.getCurrentRoom().getName(), statusBeforeAction, p.getItem1().getName(), p.getItem2().getName()); 
                            } else {
                                OutputDisplayManager.displayText("> Non puoi usare " + p.getItem1().getName() + " su " + p.getItem2().getName() + "!");
                            }
                        } else {
                            OutputDisplayManager.displayText("> " + p.getItem2().getName() + " non è nella stanza o non può essere usato così!");
                        }
                    } else {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " non è nell'inventario!");
                    }
                });

        // comando per fondere due oggetti
        registerAction(new CommandKey(CommandType.UNISCI, 2),
                p -> {
                    if (game.getInventory().contains(p.getItem1()) && game.getInventory().contains(p.getItem2())) {
                        if (p.getItem1() == p.getItem2()) {
                            OutputDisplayManager.displayText("> " + p.getItem1().getName() + " non può fondersi con se stesso!");
                        } else if (!interaction.executeFuseCombination(p.getItem1(), p.getItem2())) {
                            OutputDisplayManager.displayText("> Non puoi unire " + p.getItem1().getName() + " con " + p.getItem2().getName() + "!");
                        }
                    } else {
                        OutputDisplayManager.displayText("> " + p.getItem1().getName() + " o " + p.getItem2().getName() + " non sono nell'inventario!");
                    }
                });
    }

    /**
     * esegue il comando passato come parametro.
     *
     * @param request il comando interpretato da eseguire
     */
    public void execute(ParsedCommand request) {
        int args = (request.getItem1() != null) ? ((request.getItem2() != null) ? 2 : 1) : 0;

        CommandKey key = new CommandKey(request.getCommand(), args);
        Action action = commandMap.get(key);

        if (action != null) {
            action.execute(request);
        } else {
            OutputDisplayManager.displayText("> Il tempio tace: azione non valida :("); 
        }
    }
}
