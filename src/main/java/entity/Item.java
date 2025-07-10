package entity;
import java.util.List;
import java.util.Objects;

import backend.DatabaseConnection;

/**
 * classe che rappresenta un item nel gioco.
 */
public class Item {

    private String name;
    private List<String> aliases;
    private boolean isPickable;
    private boolean isMovable;

    /**
     * restituisce il nome dell'oggetto.
     *
     * @return il nome
     */
    public String getName() {
        return name;
    }

    /**
     * restituisce la lista degli alias dell'oggetto.
     *
     * @return la lista degli alias
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * imposta il nome dell'oggetto.
     *
     * @param name il nome da impostare
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * verifica se l'oggetto ha il nome specificato.
     *
     * @param name il nome da verificare
     * @return true se il nome corrisponde, false altrimenti
     */
    public boolean hasName(String name) {
        return this.name.equals(name);
    }

    /**
     * verifica se il nome dell'oggetto contiene una determinata sottostringa,
     * ignorando maiuscole e minuscole.
     *
     * @param substring la sottostringa da cercare
     * @return true se il nome contiene la sottostringa, false altrimenti
     */
    public boolean nameContains(String substring) {
        if (this.name == null || substring == null) {
            return false; // gestisce i casi in cui i nomi non sono stati inizializzati
        }
        // converte sia il nome dell'oggetto che la sottostringa in minuscolo prima di fare il controllo, per renderlo case-insensitive.
        return this.name.toLowerCase().contains(substring.toLowerCase());
    }

    /**
     * override del metodo equals.
     *
     * @param o l'oggetto da confrontare
     * @return true se gli oggetti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
                Objects.equals(aliases, item.aliases);
    }

    /**
     * ovveride del metodo hashCode.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, aliases);
    }

    /**
     * stampa la descrizione dell'oggetto.
     *
     * @param room la stanza in cui si trova l'oggetto
     *              (se l'oggetto è spostabile, la stanza non viene considerata)
     */
    public void getDescription(Room room) {
        String name = getName();
        if (isMovable) {
            DatabaseConnection.printFromDB("osserva", "0", "0", name, "0");
        } else {
            DatabaseConnection.printFromDB("osserva", room.getName(), "0", name, "0");
        }
    }

    /**
     * restituisce se l'oggetto è raccoglibile.
     *
     * @return true se l'oggetto è raccoglibile, false altrimenti
     */
    public boolean isPickable() {
        return isPickable;
    }

    /**
     * imposta se l'oggetto si può raccogliere o meno.
     *
     * @param b true se l'oggetto è raccoglibile, false altrimenti
     */
    public void setPickable(boolean b) {
        isPickable = b;
    }
}

