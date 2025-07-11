package engine;

import entity.CommandType;

/**
 * classe che rappresenta la chiave dell'esecutore di comandi.
 * combina il tipo di comando con il numero di oggetti coinvolti
 * per creare una chiave specifica che permette a CommandExecutor di recuperare dalla sua mappa
 * l'esatto pezzo di codice (la lambda) da eseguire, risolvendo l'ambiguità tra comandi omonimi
 * ma con un numero diverso di argomenti.
 */
public class CommandKey {

    private CommandType command;
    private int args;

    /**
     * istanzia una nuova chiave per l'esecutore di comandi con gli elementi specificati.
     *
     * @param c  il tipo di comando
     * @param a  il numero di argomenti
     */
    public CommandKey(CommandType c, int a) {
        this.command = c;
        this.args = a;
    }

    /**
     * override del metodo equals.
     *
     * @param o l'oggetto da confrontare
     * @return true se i due oggetti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandKey)) return false;
        CommandKey that = (CommandKey) o;
        return command == that.command && args == that.args;
    }

    /**
     * override del metodo hashCode.
     *
     * @return l'hashcode dell'oggetto
     */
    @Override 
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + args;
        return result;
    }
}
