package entity;

import GUI.GameGUI;
import gameplay.OutputDisplayManager;

import java.util.List;
import java.util.Map;

/**
 * classe che rappresenta il gioco.
 */
public class Game {

    private String nickname;
    private List<Item> inventory;
    private List<Corridor> corridorsMap;
    private Map<String, String> statesMap; // mappa degli stati delle stanze
    private String currentTime;
    private Room currentRoom;

    private static Game game = new Game(); // istanza del gioco

    /**
     * imposta l'istanza del gioco.
     *
     * @param game il gioco
     */
    public static void setUpGame(Game game) {
        Game.game = game;
        GameGUI.setImagePanel(game.getCurrentRoom().getName());
    }

    /**
     * restituisce l'istanza del gioco.
     *
     * @return l'istanza
     */
    public static Game getInstance() {
        return game;
    }

    /**
     * restituisce il nickname del giocatore.
     *
     * @return il nickname
     */
    public String getNickname() {
        return game.nickname;
    }

    /**
     * imposta il nickname del giocatore.
     *
     * @param nickname il nickname
     */
    public void setNickname(String nickname) {
        game.nickname = nickname;
    }

    /**
     * restituisce l'inventario.
     *
     * @return l'inventario
     */
    public List<Item> getInventory() {
        return game.inventory;
    }

    /**
     * aggiunge un oggetto all'inventario.
     *
     * @param item l'oggetto da aggiungere
     */
    public void addInventory(Item item) {
        game.inventory.add(item);
        List<String> itemsNames = game.inventory.stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GameGUI.updateInventoryTextArea(itemsNamesArray); 
    }

    /**
     * rimuove un oggetto dall'inventario.
     *
     * @param item l'oggetto da rimuovere
     */
    public void removeInventory(Item item) {
        game.inventory.remove(item);
        List<String> itemsNames = game.inventory.stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GameGUI.updateInventoryTextArea(itemsNamesArray); 
    }

    /**
     * stampa l'inventario.
     */
    public void printInventory() {
        OutputDisplayManager.displayText("> Inventario: ");
        for (Item item : game.inventory) {
            OutputDisplayManager.displayText(">  - " + item.getName());
        }
    }

    /**
     * restituisce la mappa dei corridoi.
     *
     * @return la lista dei corridoi
     */
    public List<Corridor> getCorridorsMap() {
        return game.corridorsMap;
    }

    /**
     * sblocca un corridoio.
     *
     * @param r1 la stanza di partenza
     * @param r2 la stanza di arrivo
     */
    public void unlockCorridor(String r1, String r2) {
        for (Corridor corridor : game.corridorsMap) {
            if (corridor.getStartingRoom().getName().equals(r1) && corridor.getArrivingRoom().getName().equals(r2)) {
                corridor.setLocked(false);
            }
        }
    }

    /**
     * restituisce lo stato della stanza.
     *
     * @param room la stanza
     * @return lo stato della stanza
     */
    public String getRoomState(String room) {
        return game.statesMap.get(room);
    }

    /**
     * imposta lo stato della stanza.
     *
     * @param room la stanza
     * @param state lo stato
     */
    public void setRoomState(String room, String state) {
        game.statesMap.replace(room, state);
        game.corridorsMap.stream()
                .filter(corridor -> corridor.getStartingRoom().getName().equals(room))
                .forEach(corridor -> corridor.getStartingRoom().setState(state));
    }

    /**
     * restituisce la stanza corrente.
     *
     * @return la stanza corrente
     */
    public Room getCurrentRoom() {
        return game.currentRoom;
    }

    /**
     * imposta la stanza corrente.
     *
     * @param room la stanza
     */
    public void setCurrentRoom(Room room) {
        for (Corridor corridor : game.corridorsMap) {
            if (corridor.getStartingRoom().equals(room)) {
                game.currentRoom = corridor.getStartingRoom();
                GameGUI.setImagePanel(game.currentRoom.getName());
                return;
            }
        }
        game.currentRoom = room;
        GameGUI.setImagePanel(game.currentRoom.getName());
    }

    /**
     * restituisce il tempo di gioco.
     *
     * @return il tempo di gioco
     */
    public String getCurrentTime() {
        return game.currentTime;
    }

    /**
     * imposta il tempo di gioco.
     *
     * @param currentTime il tempo di gioco
     */
    public void setCurrentTime(String currentTime) {
        game.currentTime = currentTime;
    }
}
