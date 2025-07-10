package entity;

/**
 * classe che rappresenta un corridoio tra due stanze nel gioco.
 */
public class Corridor {

    private Room startingRoom;
    private CommandType direction;
    private Room arrivingRoom;
    private boolean locked;

    /**
     * restituisce la stanza di partenza del corridoio.
     *
     * @return la stanza di partenza
     */
    public Room getStartingRoom() {
        return startingRoom;
    }

    /**
     * restituisce la direzione del corridoio.
     *
     * @return la direzione
     */
    public CommandType getDirection() {
        return direction;
    }

    /**
     * restituisce la stanza di arrivo del corridoio.
     *
     * @return la stanza di arrivo
     */
    public Room getArrivingRoom() {
        return arrivingRoom;
    }

    /**
     * imposta la stanza di partenza del corridoio.
     *
     * @param room la stanza di partenza
     */
    public void setStartingRoom(Room room) {
        this.startingRoom = room;
    }

    /**
     * imposta la stanza di arrivo del corridoio.
     *
     * @param room la stanza di arrivo
     */
    public void setArrivingRoom(Room room) {
        this.arrivingRoom = room;
    }

    /**
     * restituisce lo stato del corridoio.
     *
     * @return true se il corridoio è bloccato, false altrimenti
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * imposta lo stato del corridoio.
     *
     * @param locked true se il corridoio deve essere bloccato, false altrimenti
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
