package entity;


import util.Converter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * classe che gestisce il gioco.
 */
public class GameManager {

    private static Map<String, Item> allItems;
    private final Converter converter = new Converter();

    /**
     * istanzia una nuova partita e crea tutti gli oggetti.
     */
    public void createGame() {
        allItems = converter.convertJsonToJavaClass();
    }

    /**
     * salva la partita e gli oggetti in un file JSON.
     */
    public void saveGame() {
        converter.convertGameToJson();
        converter.convertItemsToJson();
    }

    /**
     * carica la partita.
     *
     * @return true se la partita è stata caricata correttamente, false altrimenti
     */
    public boolean loadGame() {
        allItems = converter.loadGame();

        return allItems != null && !allItems.isEmpty();
    }

    /**
     * ottiene un oggetto dal suo nome.
     *
     * @param name il nome dell'oggetto
     * @return l'oggetto
     */
    public Item getItemFromName(String name) {
        return allItems.get(name);
    }

    /**
     * restituisce un set con tutti gli oggetti.
     *
     * @return il set di tutti gli oggetti
     */
    public Set<Item> getAllItems() {
        return new HashSet<>(allItems.values());
    }

    /**
     * reimposta la mappa di tutti gli oggetti.
     */
    public void resetAllItems() {
        allItems = null;
    }

    /**
     * istanzia tutti i comandi e li restituisce.
     *
     * @return il set di tutti i comandi
     */
    public Set<Command> getAllCommands() {
        Set<Command> availableCommands = new HashSet<>();

        availableCommands.add(
            new Command(
            "aiuto",
            List.of("comandi", "help", "guida", "comando", "h", "?", "helpme", "assistenza", "supporto"),
            CommandType.AIUTO
            )
        );
        availableCommands.add(
            new Command(
            "nord",
            List.of("n", "north", "avanti", "su", "vaiAvanti", "vaiANord", "sopra"),
            CommandType.NORD
            )
        );
        availableCommands.add(
            new Command(
            "sud",
            List.of("s", "south", "indietro", "giù", "giu", "vaiIndietro", "vaiASud", "sotto"),
            CommandType.SUD
            )
        );
        availableCommands.add(
            new Command(
            "est",
            List.of("east", "destra", "vaiDestra", "vaiADestra", "vaiAEst", "vaiAdEst"), // 'e' non funzionerebbe perché è nella lista delle stopwords
            CommandType.EST
            )
        );
        availableCommands.add(
            new Command(
            "ovest",
            List.of("west", "sinistra", "vaiSinistra", "vaiASinistra", "vaiAOvest", "vaiAdOvest"), // 'o' non funzionerebbe perché è nella lista delle stopwords
            CommandType.OVEST
            )
        );
        availableCommands.add(
            new Command(
            "inventario",
            List.of("i", "inventory", "inv", "borsa", "zaino", "valigia", "sacca", "tasca", "tasche"),
            CommandType.INVENTARIO
            )
        );
        availableCommands.add(
            new Command(
            "prendi",
            List.of("p", "raccogli", "recupera", "intasca", "t", "take", "afferra", "prendiOggetto", "prendiItem"),
            CommandType.PRENDI
            )
        );
        availableCommands.add(
            new Command(
            "lascia",
            List.of("butta", "scarta", "rimuovi", "drop", "abbandona", "deposita", "deponi", "lasciaOggetto", "lasciaItem", "poggia", "appoggia", "poni", "riponi"),
            CommandType.LASCIA
            )
        );
        availableCommands.add(
            new Command(
            "osserva",
            List.of("vedi", "esamina", "guarda", "ammira", "g", "look", "l", "ispeziona", "analizza", "scruta", "leggi", "capiamo"),
            CommandType.OSSERVA
            )
        );
        availableCommands.add(
            new Command(
            "usa",
            List.of("u", "use", "utilizza", "applica", "adopera", "usaOggetto", "usaItem", "utilizzaItem", "utilizzaOggetto", "consegna", "dai", "lancia"),
            CommandType.USA
            )
        );
        availableCommands.add(
            new Command(
            "unisci",
            List.of("componi", "fondi", "combina", "assembla", "fuse", "f",  "mischia", "miscela", "incastra", "lega", "taglia", "intaglia"),
            CommandType.UNISCI
            )
        );

        return availableCommands;
    }

}
