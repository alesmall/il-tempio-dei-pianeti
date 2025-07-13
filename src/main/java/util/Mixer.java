package util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.HashMap;
import static GUI.GameGUI.musicButtonSetTextGame;
import static GUI.MenuGUI.musicButtonSetTextMenu;
import static GUI.CreditsGUI.musicButtonSetTextCredits;

/**
 * classe che gestisce la musica. 
 */
public class Mixer extends Thread {

    private static Clip[] clips;
    private static int currentClip;
    private static boolean running = true;
    private static HashMap<String, Integer> roomToClipIndex;
    private static Mixer instance;

    /**
     * costruttore della classe Mixer.
     */
    private Mixer() { 
        clips = new Clip[11];
        // menu
        loadClip(0, "src/main/resources/audio/menu.wav");
        // sole
        loadClip(1, "src/main/resources/audio/sole.wav");
        // ovest
        loadClip(2, "src/main/resources/audio/ovest.wav");
        // sud
        loadClip(3, "src/main/resources/audio/sud.wav");
        // nord
        loadClip(4, "src/main/resources/audio/nord.wav");
        // est
        loadClip(5, "src/main/resources/audio/est.wav");
        // luna
        loadClip(6, "src/main/resources/audio/luna.wav");
        // finale
        loadClip(7, "src/main/resources/audio/finale.wav");

        // mappa delle stanze ai rispettivi indici delle clip audio
        roomToClipIndex = new HashMap<>();

        roomToClipIndex.put("Menu", 0);

        roomToClipIndex.put("Sole", 1);

        roomToClipIndex.put("StanzaGSN", 2);
        roomToClipIndex.put("Giove", 2);
        roomToClipIndex.put("Saturno", 2);
        roomToClipIndex.put("Nettuno", 2);
        roomToClipIndex.put("Wordle", 2);

        roomToClipIndex.put("StanzaMV", 3);
        roomToClipIndex.put("Mercurio", 3);
        roomToClipIndex.put("Venere", 3);

        roomToClipIndex.put("StanzaMU", 4);
        roomToClipIndex.put("Marte", 4);
        roomToClipIndex.put("Urano", 4);

        roomToClipIndex.put("Terra", 5);
        roomToClipIndex.put("Luna", 6);

        roomToClipIndex.put("OblioTotale", 7);
        roomToClipIndex.put("ParzialeSalvezza", 7);
        roomToClipIndex.put("RinascitaStellare", 7);
        roomToClipIndex.put("RisveglioCosmico", 7);
    }

    /**
     * carica la musica dal percorso del file.
     *
     * @param index    l'indice del clip
     * @param filePath il percorso del file
     */
    private void loadClip(int index, String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clips[index] = AudioSystem.getClip();
            clips[index].open(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * riproduce un effetto sonoro da file, senza interrompere la musica di sottofondo.
     *
     * @param effect il nome dell'effetto
     */
    public static void playEffect(String effect) {
        new Thread(() -> {
            try {
                File file = null;
                if (effect.equals("binding")) {
                    file = new File("src/main/resources/audio/binding.wav");
                } else if (effect.equals("progressbar")) {
                    file = new File("src/main/resources/audio/progressbar.wav");
                } else if (effect.equals("leaving")) {
                    file = new File("src/main/resources/audio/leaving.wav");
                }
                if (file == null || !file.exists()) {
                    System.err.println("effetto sonoro non trovato: " + effect);
                    return;
                }
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip sfx = AudioSystem.getClip();
                sfx.open(audioStream);
                if (running) {
                    if (effect.equals("binding")) {
                        sfx.start();
                        sfx.loop(0);
                    } else if (effect.equals("progressbar")) {
                        sfx.start();
                        sfx.loop(2);
                    } else if (effect.equals("leaving")) {
                        sfx.start();
                        sfx.loop(6);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * restituisce l'istanza del Mixer (singleton).
     *
     * @return l'istanza del Mixer
     */
    public static Mixer getInstance() {
        if (instance == null) {
            instance = new Mixer();
        }
        return instance;
    }

    /**
     * override del metodo run.
     * avvia la musica del menu iniziale.
     */
    @Override
    public void run() {
        running = true;
        try {
            if (clips[0] != null) {
                clips[0].start();
                clips[0].loop(Clip.LOOP_CONTINUOUSLY);
                currentClip = 0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * controlla se la musica è in esecuzione.
     *
     * @return lo stato di esecuzione
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * avvia la musica.
     */
    public static void startClip() {
        if (clips[currentClip] != null) {
            running = true;
            reverseIcons();
            clips[currentClip].start();
        }
    }

    /**
     * ferma la musica.
     */
    public static void stopClip() {
        if (clips[currentClip] != null) {
            running = false;
            reverseIcons();
            clips[currentClip].stop();
        }
    }

    /**
     * inverte le icone della musica.
     */
    public static void reverseIcons() {
        if (!running) {
            musicButtonSetTextGame("🔇");
            musicButtonSetTextMenu("🔇");
            musicButtonSetTextCredits("🔇");
        } else {
            musicButtonSetTextGame("🔊");
            musicButtonSetTextMenu("🔊");
            musicButtonSetTextCredits("🔊");
        }
    }

    /**
     * cambia la musica.
     *
     * @param i l'indice della musica
     */
    private static void changeClip(int i) {
        if (running) {
            if (clips[currentClip] != null) {
                clips[currentClip].stop();
            }
            if (clips[i] != null) {
                clips[i].start();
                clips[i].loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        currentClip = i;
    }

    /**
     * cambia la musica in base alla stanza.
     * 
     * @param room la stanza la quale musica bisogna far partire
     */
    public static void changeRoomMusic(String room) {
        changeClip(roomToClipIndex.getOrDefault(room, 0));
    }
}
