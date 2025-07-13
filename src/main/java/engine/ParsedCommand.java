package engine;

import entity.Item;
import entity.CommandType;

/**
 * classe che rappresenta il risultato del parsing.
 * contiene il tipo di comando e gli eventuali oggetti coinvolti.
 */
public class ParsedCommand {

    private final CommandType command;
    private final Item item1;
    private final Item item2;

    
    /**
     * costruisce un nuovo oggetto ParsedCommand con il comando e gli oggetti specificati.
     *
     * @param command il tipo di comando analizzato dall'input
     * @param item1 il primo oggetto associato al comando
     * @param item2 il secondo oggetto associato al comando
     */
    public ParsedCommand(CommandType command, Item item1, Item item2) {
        this.command = command;
        this.item1 = item1;
        this.item2 = item2;
    }

    /**
     * restituisce il tipo di comando.
     *
     * @return il tipo di comando
     */
    public CommandType getCommand() {
        return command;
    }

    /**
     * restituisce il primo oggetto Item.
     *
     * @return il primo oggetto Item
     */
    public Item getItem1() {
        return item1;
    }

    /**
     * restituisce il secondo oggetto Item.
     *
     * @return il secondo oggetto Item
     */
    public Item getItem2() {
        return item2;
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
        if (!(o instanceof ParsedCommand)) return false;
        ParsedCommand that = (ParsedCommand) o;
        return command == that.command &&
                (item1 != null ? item1.equals(that.item1) : that.item1 == null) &&
                (item2 != null ? item2.equals(that.item2) : that.item2 == null);
    }

    /**
     * override del metodo hashCode.
     *
     * @return l'hashcode
     */
    @Override 
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (item1 != null ? item1.hashCode() : 0);
        result = 31 * result + (item2 != null ? item2.hashCode() : 0);
        return result;
    }
}
