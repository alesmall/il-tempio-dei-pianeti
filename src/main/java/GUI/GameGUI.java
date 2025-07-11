package GUI;

import entity.GameManager;
import entity.Game;
import gameplay.UserInputManager;
import util.Mixer;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter; // Aggiunto import per hover
import java.awt.event.MouseEvent;  // Aggiunto import per hover
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;


/** 
 * classe che gestisce la GUI della schermata di gioco principale.
 */
public class GameGUI extends JPanel {

    private static final Color COLORE_SFONDO_PANNELLO = new Color(10, 15, 45);
    private static final Color COLORE_PANNELLI_TESTO = new Color(17, 24, 68); 
    private static final Color COLORE_BORDO = new Color(0, 180, 200);
    private static final Color COLORE_BORDO_SCURO = new Color(63, 84, 158);
    private static final Color COLORE_TESTO_PRINCIPALE = new Color(220, 220, 220);
    private static final Color COLORE_SFONDO_TOOLBAR = new Color(30, 50, 110);     
    private static final Color COLORE_SFONDO_TOOLBAR_HOVER = new Color(35, 50, 105); 
    private static final Font FONT_TESTO_GIOCO = new Font("Dialog", Font.PLAIN, 13);
    private static final Font FONT_INVENTARIO = new Font("DialogInput", Font.PLAIN, 15); // TODO forse meglio dialog?
    private static final Font FONT_TOOLBAR = new Font("Dialog", Font.BOLD, 15);


    // array con i nomi delle immagini delle stanze per il caricamento dinamico
    private static final String[] NOMI_STANZE = {
        "Sole", "Luna", "Mercurio", "Venere", "Terra", "Marte", "Giove", "Saturno", "Urano", "Nettuno",
        "StanzaMV", "StanzaMU", "StanzaGSN", "OblioTotale", "ParzialeSalvezza", "RinascitaStellare", "RisveglioCosmico"
    };

    private static JLabel timerLabel;
    private static JPanel imagePanel;
    private static JTextPane displayTextPane;
    private static JTextArea inventoryTextArea;
    private static JButton musicButton;
    private static CardLayout cardLayout;
    private JTextField userInputField;
    private final Map<String, Image> imageCache = new HashMap<>();

    /**
     * costruttore della classe GameGUI.
     */
    public GameGUI() {
        UIManager.put("ToolTip.background", COLORE_SFONDO_PANNELLO); 
        UIManager.put("ToolTip.foreground", COLORE_TESTO_PRINCIPALE);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(COLORE_BORDO));
        initComponents();
    }
    
    /**
     * inizializza e assembla tutti i componenti della GUI.
     */
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(COLORE_SFONDO_PANNELLO);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // aggiunge i pannelli principali al layout
        add(createToolBar(), BorderLayout.NORTH);
        add(createMainDisplayPanel(), BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);
    }
    
    /**
     * crea la toolbar superiore con i pulsanti di controllo.
     * 
     * @return la JToolBar configurata
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setBackground(COLORE_SFONDO_PANNELLO);
        toolBar.setMargin(new Insets(5, 5, 5, 5));

        JButton goBackButton = createToolBarButton(" Torna al Menù ", "abbandona la sessione e torna al menù principale");
        goBackButton.addActionListener(this::goBackButtonActionPerformed);

        JButton saveGameButton = createToolBarButton(" Salva ", "salva i progressi attuali della missione");
        saveGameButton.addActionListener(_ -> saveGameButtonActionPerformed());
        
        JButton helpButton = createToolBarButton(" Guida ", "mostra la guida dei comandi");
        helpButton.addActionListener(this::helpButtonActionPerformed);
        
        musicButton = createToolBarButton(Mixer.isRunning() ? "🔊" : "🔇", "attiva o disattiva la musica");
        musicButton.addActionListener(this::musicButtonActionPerformed);
        musicButton.setPreferredSize(new Dimension(55, 38)); // Dimensione leggermente più grande
        
        timerLabel = new JLabel(" 00:00:00 ");
        timerLabel.setFont(FONT_TOOLBAR);
        timerLabel.setForeground(COLORE_TESTO_PRINCIPALE);
        timerLabel.setBorder(BorderFactory.createLineBorder(COLORE_BORDO, 1));
        
        toolBar.add(goBackButton);
        toolBar.add(saveGameButton);
        toolBar.add(helpButton);
        toolBar.add(musicButton);
        toolBar.add(Box.createHorizontalGlue()); // spinge il timer a destra
        toolBar.add(timerLabel);
        
        return toolBar;
    }
    
    /**
     * metodo per creare i pulsanti della toolbar con uno stile coerente.
     * 
     * @param text il testo del pulsante
     * @param toolTip il testo da mostrare al passaggio del mouse
     * @return un JButton personalizzato
     */
    private JButton createToolBarButton(String text, String toolTip) {
        JButton button = new JButton(text);
        button.setFont(FONT_TOOLBAR);
        button.setForeground(COLORE_TESTO_PRINCIPALE);
        button.setBackground(COLORE_SFONDO_TOOLBAR); 
        button.setFocusPainted(false);
        button.setToolTipText(toolTip);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDO),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        button.setContentAreaFilled(false);
        button.setOpaque(false); 
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                button.setBackground(COLORE_SFONDO_TOOLBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
                button.setOpaque(false);
                button.setBackground(COLORE_SFONDO_TOOLBAR);
            }
        });

        return button;
    }

    /**
     * crea il pannello principale di output e input a sinistra.
     * 
     * @return il pannello configurato
     */
    private JPanel createMainDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        // area di testo per l'output del gioco 
        displayTextPane = new JTextPane();
        displayTextPane.setEditable(false);
        displayTextPane.setFont(FONT_TESTO_GIOCO);
        displayTextPane.setBackground(COLORE_PANNELLI_TESTO);
        displayTextPane.setForeground(COLORE_TESTO_PRINCIPALE);
        displayTextPane.setMargin(new Insets(10, 10, 10, 10));
        displayTextPane.setOpaque(true); 

        JScrollPane scrollPaneOutput = new JScrollPane(displayTextPane);
        scrollPaneOutput.getViewport().setOpaque(true);
        scrollPaneOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPaneOutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneOutput.getViewport().setBackground(COLORE_PANNELLI_TESTO);
        scrollPaneOutput.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_SCURO, 2));
        
        // campo per l'input dell'utente 
        userInputField = new JTextField();
        userInputField.setFont(FONT_TESTO_GIOCO);
        userInputField.setBackground(new Color(17, 24, 68));
        userInputField.setForeground(COLORE_TESTO_PRINCIPALE);
        userInputField.setCaretColor(COLORE_BORDO); // cursore colorato
        userInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLORE_BORDO_SCURO, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        userInputField.addActionListener(this::userInputFieldActionPerformed);
        UserInputManager.startInputListener();
        
        panel.add(scrollPaneOutput, BorderLayout.CENTER);
        panel.add(userInputField, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * crea il pannello laterale destro con l'immagine della stanza e l'inventario.
     * 
     * @return il pannello configurato
     */
    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(420, 0)); 

        // pannello immagine
        imagePanel = createImagePanel();

        // area inventario 
        inventoryTextArea = new JTextArea("Inventario:\n");
        inventoryTextArea.setEditable(false);
        inventoryTextArea.setFont(FONT_INVENTARIO);
        inventoryTextArea.setBackground(COLORE_PANNELLI_TESTO);
        inventoryTextArea.setForeground(COLORE_TESTO_PRINCIPALE);
        inventoryTextArea.setLineWrap(true);
        inventoryTextArea.setWrapStyleWord(true);
        inventoryTextArea.setMargin(new Insets(10, 10, 10, 10));
        inventoryTextArea.setOpaque(true);

        JScrollPane scrollPaneInventory = new JScrollPane(inventoryTextArea);
        scrollPaneInventory.getViewport().setOpaque(true);
        scrollPaneInventory.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPaneInventory.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneInventory.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_SCURO, 2));
        scrollPaneInventory.getViewport().setBackground(COLORE_PANNELLI_TESTO);
        scrollPaneInventory.setPreferredSize(new Dimension(0, 218)); 
        
        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(scrollPaneInventory, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * inizializza il pannello delle immagini caricando dinamicamente tutte le stanze e i minigiochi.
     * 
     * @return il pannello configurato con CardLayout
     */
    private JPanel createImagePanel() {
        JPanel panel = new JPanel();
        cardLayout = new CardLayout();
        panel.setLayout(cardLayout);
        panel.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_SCURO, 2));
        
        // aricamento "pigro" delle immagini delle stanze (man mano che servono)
        for (String roomName : NOMI_STANZE) {
            String imagePath = "src/main/resources/img/" + roomName + ".jpeg";
            JPanel roomPanel = createLazyImagePanel(imagePath);
            panel.add(roomPanel, roomName);
        }

        // aggiunta dei pannelli dei mini-giochi
        panel.add(new WordleGUI(), "Wordle"); // TODO non avevo fatto già partire wordle su un thread? nel manager

        return panel;
    }

    /**
     * crea un pannello che carica un'immagine "pigramente".
     * l'immagine viene caricata solo la prima volta che il pannello è visibile.
     * 
     * @param path il percorso dell'immagine
     * @return un JPanel che disegna l'immagine
     */
    private JPanel createLazyImagePanel(String path) {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image image = imageCache.get(path);

                if (image == null) {
                    try {
                        image = new ImageIcon(path).getImage();
                        imageCache.put(path, image);
                    } catch (Exception e) {
                        System.err.println("impossibile caricare immagine: " + path);
                        g.setColor(Color.BLACK);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        return;
                    }
                }
                
                if (image != null) {
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
    }

    /**
     * restituisce l'istanza di WordleGUI.
     *
     * @return il pannello Wordle
     */
    public static WordleGUI getWordle() {
        if (imagePanel == null) return null;
        for (java.awt.Component comp : imagePanel.getComponents()) {
            if (comp instanceof WordleGUI) return ((WordleGUI) comp);
        }
        return null;
    }
    
    // metodi per la gestione degli eventi (ActionListeners) e logica di gioco

    /**
     * gestisce l'azione del pulsante di ritorno al menu principale.
     * @param evt l'evento 
     */
    private void goBackButtonActionPerformed(ActionEvent evt) {
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Sei sicuro di voler abbandonare la missione attuale?\nI progressi non salvati andranno persi.", 
            "ritorno al menù principale", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            goBack();
        }
    }

    /**
     * gestisce l'azione del pulsante di salvataggio.
     */
    private void saveGameButtonActionPerformed() {
        GameManager gameManager = new GameManager();
        Game game = Game.getInstance();
        game.setCurrentTime(timerLabel.getText());
        try {
            gameManager.saveGame();
            JOptionPane.showMessageDialog(this, "Salvataggio missione completato.\nI dati sono al sicuro nel cosmo :)", "salvataggio riuscito!!", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore cosmico: impossibile salvare i dati della missione :(", "errore di salvataggio", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * gestisce l'azione del pulsante di aiuto e rende visibile la guida.
     * @param evt l'evento 
     */
    private void helpButtonActionPerformed(ActionEvent evt) {
        HelpGUI.getInstance().setVisible(true);
        HelpGUI.getInstance().setLocationRelativeTo(null);
    }
    
    /**
     * gestisce l'azione del pulsante della musica e attiva o disattiva la musica di sottofondo.
     * @param evt l'evento
     */
    private void musicButtonActionPerformed(ActionEvent evt) {
        if (Mixer.isRunning()) {
            Mixer.stopClip();
        } else {
            Mixer.startClip();
        }
    }
    
    /**
     * gestisce l'azione dell'invio nel campo di input utente.
     * @param evt l'evento
     */
    private void userInputFieldActionPerformed(ActionEvent evt) {
        String text = userInputField.getText();
        userInputField.setText("");
        UserInputManager.setCurrentInput(text);
    }

    /**
     * gestisce il ritorno al menu principale, resettando lo stato del gioco.
     */
    public void goBack() {
        CardLayout cl = (CardLayout) getParent().getLayout();
        cl.show(getParent(), "MenuGUI");
        displayTextPane.setText("");
        inventoryTextArea.setText("Inventario:\n");
        Mixer.changeRoomMusic("Menu");
    }

    /**
     * resetta i minigiochi. TODO serve farlo?? o tutti i minigiochi creano una nuova istanza? capiamo
     * questo metodo viene chiamato quando si torna al menu principale o si inizia una nuova partita.
     */

    // metodi statici per l'aggiornamento della UI dall'esterno

    /**
     * imposta il testo del timer nella label.
     * @param time il testo da visualizzare nel timer
     */
    public static void timerLabelSetTime(String time) {
        if (timerLabel != null) timerLabel.setText(" " + time + " ");
    }
    
    /**
     * mostra il testo nell'area di testo principale.
     * @param text il testo da visualizzare
     */
    public static void displayTextPaneSetText(String text) {
        if (displayTextPane != null) {
            String existingText = displayTextPane.getText();
            displayTextPane.setText(existingText.isEmpty() ? text : existingText + "\n" + text);
            displayTextPane.setCaretPosition(displayTextPane.getDocument().getLength());
            displayTextPane.repaint();
        }
    }

    /**
     * cambia il pannello delle immagini in base al nome della stanza.
     * @param panelName il nome del pannello da mostrare
     */
    public static void setImagePanel(String panelName) {
        if (cardLayout != null && imagePanel != null) {
            Mixer.changeRoomMusic(panelName);
            cardLayout.show(imagePanel, panelName);
        }
    }
    
    /**
     * imposta il testo del pulsante della musica nella schermata di gioco.
     * @param text il testo da visualizzare nel pulsante
     */
    public static void musicButtonSetTextGame(String text) {
        if (musicButton != null) musicButton.setText(text);
    }

    /**
     * aggiorna l'area di testo dell'inventario con gli oggetti attuali.
     * @param items l'array di oggetti da visualizzare nell'inventario
     */
    public static void updateInventoryTextArea(String[] items) {
        if (inventoryTextArea != null) {
            StringBuilder inventory = new StringBuilder("Inventario:\n");
            int maxHorItems = 2; 

            for (int i = 0; i < items.length; i++) {
                inventory.append(" - ").append(items[i]);
                if ((i + 1) % maxHorItems == 0 || i == items.length - 1) {
                    inventory.append("\n");
                } else {
                    inventory.append("   ");
                }
            }
            inventoryTextArea.setText(inventory.toString());
        }
    }

    /**
     * restituisce i FontMetrics dell'area di testo principale.
     * @return i FontMetrics dell'area di testo
     */
    public static FontMetrics getTextPaneFontMetrics() {
        return (displayTextPane != null) ? displayTextPane.getFontMetrics(displayTextPane.getFont()) : null;
    }
    
    /**
     * restituisce la larghezza dell'area di testo principale.
     * @return la larghezza dell'area di testo
     */
    public static int getTextPaneWidth() {
        return (displayTextPane != null) ? displayTextPane.getWidth() : 0;
    }
}