package entity;

import java.util.ArrayList;
import java.util.List;

import backend.DatabaseConnection;

/**
 * classe che rappresenta una stanza nel gioco.
 */
public class Room {

    private String name;
    private String currentState;
    private List<Item> items;

    /**
     * istanzia una nuova stanza.
     */
    public Room() {
        this.items = new ArrayList<>();
    }

    /**
     * imposta il nome della stanza.
     *
     * @param name il nome della stanza
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * restituisce il nome della stanza.
     *
     * @return il nome della stanza
     */
    public String getName() {
        return name;
    }

    /**
     * imposta lo stato della stanza.
     *
     * @param state lo stato della stanza
     */
    public void setState(String state) {
        currentState = state;
    }

    /**
     * restituisce lo stato della stanza.
     *
     * @return lo stato della stanza
     */
    public String getState() {
        return currentState;
    }

    /**
     * controlla se la stanza contiene l'oggetto.
     *
     * @param item l'oggetto
     * @return true se la stanza contiene l'oggetto, false altrimenti
     */
    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    /**
     * aggiunge l'oggetto alla stanza.
     *
     * @param item l'oggetto
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * rimuove l'oggetto dalla stanza.
     *
     * @param item l'oggetto
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * restituisce tutti gli oggetti presenti nella stanza.
     *
     * @return la lista degli oggetti
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * stampa la descrizione della stanza.
     */
    public void printDescription() {
        DatabaseConnection.printFromDB("osserva", name, currentState, "0", "0");
    } 

    /**
     * override del metodo equals.
     *
     * @param obj l'oggetto da confrontare
     * @return true se i due oggetti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * override del metodo hashCode.
     *
     * @return l'hashcode dell'oggetto
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
