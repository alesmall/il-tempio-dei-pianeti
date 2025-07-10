package entity;

import java.util.List;

/**
 * classe che rappresenta un comando nel gioco.
 */
public class Command {

    private String name;
    private List<String> aliases;
    private CommandType type;

    /**
     * istanzia un nuovo comando.
     *
     * @param name il nome
     * @param aliases gli alias
     * @param type il tipo
     */
    public Command(String name, List<String> aliases, CommandType type) {
        this.name = name;
        this.aliases = aliases;
        this.type = type;
    }

    /**
     * restituisce il nome del comando.
     *
     * @return il nome
     */
    public String getName() {
        return name;
    }

    /**
     * restituisce gli alias del comando.
     *
     * @return gli alias
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * restituisce il tipo del comando.
     *
     * @return il tipo del comando
     */
    public CommandType getCommandType() {
        return type;
    }
}
