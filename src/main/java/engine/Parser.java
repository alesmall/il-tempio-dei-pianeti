package engine;

import entity.Item;
import entity.Command;
import entity.GameManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * classe che gestisce il parsing dell'input dell'utente,
 * trasformandolo in un comando comprensibile dal gioco.
 */
public class Parser {

    private final Set<Command> availableCommands;
    private final Set<Item> availableItems;
    private final Set<String> stopWords;

    /**
     * costruttore della classe.
     */
    public Parser() {
        GameManager gameManager = new GameManager();
        this.availableCommands = gameManager.getAllCommands();
        this.availableItems = gameManager.getAllItems();
        this.stopWords = loadStopWords("src/main/resources/static/stopwords.txt");
    }

    /**
     * carica le stop words da un file.
     *
     * @param filePath il percorso del file contenente le stop words
     * @return un set di stop words
     */
    private Set<String> loadStopWords(String filePath) {
        Set<String> words = new HashSet<>();
        // utilizza la feature try-with-resources di Java per leggere le stop words da un file
        // e gestire automaticamente la chiusura del BufferedReader
        // (il reader viene chiuso in automatico non appena il blocco try termina,
        // sia che termini normalmente, sia che venga lanciata un'eccezione)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            // TODO: oppure throw new RuntimeException(e);
            System.err.println("errore durante il caricamento delle stop words: " + e.getMessage());
        }
        return Collections.unmodifiableSet(words);
    }

    /**
     * esegue il parsing di una stringa di input.
     * @param input la stringa inserita dall'utente
     * @return un oggetto ParsedCommand con il risultato del parsing
     */
    public ParsedCommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ParsedCommand(null, null, null);
        }

        // divide l'input in parole, rimuove le stop words e converte in minuscolo
        List<String> words = Arrays.stream(input.trim().toLowerCase().split("\\s+"))
                .filter(word -> !stopWords.contains(word))
                .collect(Collectors.toList());

        // se non ci sono parole, ritorna un comando nullo
        if (words.size() == 0) {
            return new ParsedCommand(null, null, null);
        }

        // trova il comando corrispondente alla prima parola
        Command foundCommand = findCommand(words.get(0));
        if (foundCommand == null) {
            return new ParsedCommand(null, null, null);
        }

        Item item1 = (words.size() > 1) ? findItem(words.get(1)) : null;
        Item item2 = (words.size() > 2) ? findItem(words.get(2)) : null;

        return new ParsedCommand(foundCommand.getCommandType(), item1, item2);
    }

    /**
     * trova un comando dato un nome o un alias.
     * @param word la parola da cercare
     * @return il Command trovato o null
     */
    private Command findCommand(String word) {
        return availableCommands.stream()
                .filter(command -> 
                    // controlla il nome principale (case-insensitive)
                    command.getName().equalsIgnoreCase(word) ||
                    // o controlla se qualsiasi alias corrisponde (case-insensitive)
                    command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(word))
                )
                .findFirst()
                .orElse(null);
    }

    /**
     * trova un oggetto dato un nome o un alias.
     * @param word la parola da cercare
     * @return l'Item trovato o null
     */
    private Item findItem(String word) {
        return availableItems.stream()
                .filter(item -> 
                    item.getName().equalsIgnoreCase(word) ||
                    item.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(word))
                )
                .findFirst()
                .orElse(null);
    }
}
