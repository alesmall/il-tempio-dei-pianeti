package engine;

/**
 * interfaccia del comportamento di un comando e dell'azione da eseguire.
 * 
 * è un'interfaccia funzionale, con execute come unico metodo astratto.
 * rende possibile l'utilizzo di lambda expressions per implementare
 * il comportamento di un comando specifico. 
 * utilizzare, al posto di un parametro di tipo CommandBehavior, l'espressione lambda: p -> game.doSomething()
 * vuol dire "creare un oggetto che implementa l'interfaccia CommandBehavior,
 * il cui metodo execute prende come parametro p e al suo interno esegue game.doSomething()".
 */
public interface Action {
    /**
     * metodo che esegue il comando.
     *
     * @param parsedText il testo analizzato
     */
    void execute(ParsedCommand parsedText);
}
