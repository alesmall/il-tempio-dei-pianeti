package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import entity.Corridor;
import entity.Game;
import entity.GameManager;
import entity.Item;
import entity.Room;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * classe che gestisce la conversione dei file json in classi java e viceversa.
 */
public class Converter {

    // usiamo prettyPrinting per avere file JSON leggibili
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * metodo che gestisce la conversione dei file json in classi java nel caso di una nuova partita.
     *
     * @return la mappa di tutti gli oggetti (items)
     */
    public Map<String, Item> convertJsonToJavaClass() {
        Path gamePath = Paths.get("src/main/resources/static/Game.json");
        Path itemsPath = Paths.get("src/main/resources/static/Items.json");
        return processJsonFiles(gamePath, itemsPath);
    }

    /**
     * metodo che gestisce la conversione dei file json in classi java nel caso di una partita caricata da un salvataggio.
     *
     * @return la mappa di tutti gli oggetti (items)
     */
    public Map<String, Item> loadGame() {
        Path gamePath = Paths.get("src/main/resources/save/SavedGame.json");
        Path itemsPath = Paths.get("src/main/resources/save/SavedItems.json");
        return processJsonFiles(gamePath, itemsPath);
    }

    /**
     * converte l'istanza del gioco in un file json per salvare la partita.
     */
    public void convertGameToJson() {
        Game game = Game.getInstance();
        String json = gson.toJson(game);

        try {
            // assicurare che la directory di salvataggio esista, o crearla se non esiste
            Path savePath = Paths.get("src/main/resources/save/SavedGame.json");
            Files.createDirectories(savePath.getParent());
            // scrivere il file json
            Files.write(savePath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("impossibile salvare il file di gioco.", e);
        }
    }

    /**
     * converte gli oggetti (che non sono nè nell'inventario nè in una stanza, quindi sono ancora da "creare")
     * in un file json per salvare la partita.
     */
    public void convertItemsToJson() {
        Game game = Game.getInstance();
        GameManager gameManager = new GameManager();
        Set<Item> allItems = gameManager.getAllItems();

        // raccoglie tutti gli oggetti presenti nel gioco (inventario e stanze) in un unico Set
        Set<Item> itemsInPlay = game.getInventory().stream()
                .collect(Collectors.toSet());
        
        // trova tutti gli oggetti unici che si trovano in tutte le stanze del gioco e li aggiunge a itemsInPlay
        game.getCorridorsMap().stream()
                .flatMap(corridor -> Stream.of(corridor.getStartingRoom(), corridor.getArrivingRoom())) // flatmap per ottenere uno stream di stanze da entrambi i lati del corridoio
                .distinct() // evita di processare la stessa stanza più volte, in base al metodo equals() della classe Room
                .flatMap(room -> room.getItems().stream()) // flatmap genera un unico stream di tutti gli oggetti in tutte le stanze
                .forEach(itemsInPlay::add); // method reference per la lambda: item -> itemsInPlay.add(item)
                // dato che itemsInPlay è un Set, aggiungerà solo elementi unici.

        // filtra la lista completa per trovare solo gli oggetti non in gioco
        Set<Item> itemsToSave = allItems.stream()
                .filter(item -> !itemsInPlay.contains(item))
                .collect(Collectors.toSet());

        String json = gson.toJson(itemsToSave);
        try {
            Path savePath = Paths.get("src/main/resources/save/SavedItems.json");
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("impossibile salvare il file degli oggetti.", e);
        }
    }
    
    /**
     * converte i file json di una partita e dei suoi oggetti in classi java.
     * restituisce una mappa contenente tutti gli oggetti mappati per nome.
     *
     * @param gameFilePath   il percorso del file della partita
     * @param itemsFilePath  il percorso del file degli oggetti
     * @return la mappa degli oggetti
     */
    private Map<String, Item> processJsonFiles(Path gameFilePath, Path itemsFilePath) {
        // usiamo try-with-resources per gestire automaticamente la chiusura dei reader.
        try (
            Reader gameReader = Files.newBufferedReader(gameFilePath, StandardCharsets.UTF_8);
            JsonReader gameJsonReader = new JsonReader(gameReader)
        ) {
            Map<String, Item> allItems = new HashMap<>();
            Map<String, Room> allRooms = new HashMap<>();

            // leggi il file di gioco
            Game game = gson.fromJson(gameJsonReader, Game.class);
            if (game == null) {
                return null;
            }
            
            Game.setUpGame(game);
            
            // mettiamo gli item dell'inventario nella map
            game.getInventory().forEach(item -> allItems.put(item.getName(), item));
            
            // processiamo le stanze e i loro item, garantendo che esista una sola istanza di ogni stanza in memoria!!
            // questo è necessario per evitare problemi di riferimenti incrociati tra stanze e corridoi, dato che 
            // ogni stanza può essere di partenza per un corridoio e di arrivo per un altro, 
            // ma non dobbiamo avere due istanze della stessa stanza in memoria.
            for (Corridor corridor : game.getCorridorsMap()) {
                // gestisci la stanza di partenza
                Room startingRoom = deduplicateRoom(corridor.getStartingRoom(), allRooms, allItems);
                corridor.setStartingRoom(startingRoom);

                // gestisci la stanza di arrivo
                Room arrivingRoom = deduplicateRoom(corridor.getArrivingRoom(), allRooms, allItems);
                corridor.setArrivingRoom(arrivingRoom);
            }

            // assicuriamoci di impostare la stanza corrente con l'istanza corretta dalla mappa
            if (game.getCurrentRoom() != null && allRooms.containsKey(game.getCurrentRoom().getName())) {
                game.setCurrentRoom(allRooms.get(game.getCurrentRoom().getName()));
            }

            // legge il file degli oggetti aggiuntivi
            if (Files.exists(itemsFilePath)) {
                try ( // nidifichiamo il secondo try-with-resources per gestire il file opzionale degli oggetti
                    Reader itemsReader = Files.newBufferedReader(itemsFilePath, StandardCharsets.UTF_8);
                    JsonReader itemsJsonReader = new JsonReader(itemsReader)
                ) {
                    // TypeToken è il modo per comunicare a Gson il tipo generico completo a runtime
                    Type itemListType = new TypeToken<ArrayList<Item>>() {}.getType();
                    List<Item> itemList = gson.fromJson(itemsJsonReader, itemListType);
                    
                    // aggiungiamo gli item alla map
                    if (itemList != null) {
                        itemList.forEach(item -> allItems.put(item.getName(), item));
                    }
                }
            } else {
                return null;
                // se il file degli oggetti non esiste, non continuiamo. deve sempre esistere, al massimo sarà vuoto!!
            }
            
            return allItems;

        } catch (IOException e) {
            // oppure throw new RuntimeException(e);
            throw new UncheckedIOException("errore durante il processamento dei file JSON.", e);
        }
    }

    /**
     * metodo helper per deduplicare le stanze e processare i loro oggetti.
     *
     * @param room la stanza da processare, potenzialmente una nuova istanza da deserializzazione
     * @param allRooms la mappa di tutte le stanze uniche trovate finora
     * @param allItems la mappa di tutti gli oggetti trovati finora
     * @return l'istanza unica della stanza (o una nuova se non ancora vista)
     */
    private Room deduplicateRoom(Room room, Map<String, Room> allRooms, Map<String, Item> allItems) {
        if (room == null) {
            return null;
        }
        
        if (!allRooms.containsKey(room.getName())) {
            // è la prima volta che vediamo questa stanza, la aggiungiamo alla mappa
            // e mettiamo i suoi oggetti nella mappa globale degli oggetti.
            allRooms.put(room.getName(), room);
            room.getItems().forEach(item -> allItems.put(item.getName(), item));
            return room;
        } else {
            // se la stanza esiste già, aggiorniamo il corridoio con l'istanza esistente
            return allRooms.get(room.getName());
        }
    }
}