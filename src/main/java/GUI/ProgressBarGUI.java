package GUI;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;

/**
 * classe che gestisce la GUI della schermata di caricamento.
 */
public class ProgressBarGUI extends JPanel {

    private static final Color COLORE_SFONDO_BARRA = new Color(17, 15, 32); 
    private static final Color COLORE_AVANZAMENTO_BARRA = new Color(81, 107, 162); // 0,150,220
    private static final Color COLORE_BORDO_BARRA = new  Color(26, 27, 56); // 0,120,190
    private static final Color COLORE_TESTO = Color.WHITE;
    private static final Font FONT_PROGRESSO = new Font("Dialog", Font.BOLD, 20);

    private JProgressBar progressBar;
    private JLabel progressBarLabel;
    /**
     * supporto per i cambiamenti di proprietà, usato per segnalare il completamento del caricamento
     */
    private final PropertyChangeSupport support;
    private final Image background;

    /**
     * costruttore della classe.
     */
    public ProgressBarGUI() {
        this.support = new PropertyChangeSupport(this);
        // carica l'immagine di sfondo una sola volta per efficienza
        this.background = new ImageIcon("src/main/resources/img/progressBarBackground.png").getImage(); 
        initComponents();
    }

    /**
     * avvia l'animazione della barra di avanzamento.
     */
    public void startProgressBar() {
        // usa un timer per aggiornare la barra a intervalli regolari
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            private int counter = 0;
            @Override
            public void run() {
                if (counter <= 100) {
                    progressBar.setValue(counter);
                    progressBarLabel.setText("INIZIALIZZAZIONE COSMICA... " + counter + "%");
                    counter++;
                } else {
                    // caricamento completato
                    progressBarLabel.setText("IL TEMPIO SI È RISVEGLIATO !!");
                    // attendi un breve istante prima di notificare il completamento
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            setFinished(true);
                        }
                    }, 1000);
                    // ferma il timer 
                    timer.cancel();
                }
            }
        };
        // esegue il task ogni 35 millisecondi per un'animazione fluida
        timer.scheduleAtFixedRate(task, 0, 35);
    }

    /**
     * inizializza e assembla i componenti grafici della schermata.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // configurazione della barra di avanzamento
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(500, 40));
        progressBar.setForeground(COLORE_AVANZAMENTO_BARRA);
        progressBar.setBackground(COLORE_SFONDO_BARRA);
        progressBar.setBorder(BorderFactory.createLineBorder(COLORE_BORDO_BARRA, 2));
        progressBar.setStringPainted(false); // disabilita il testo di default

        // configurazione dell'etichetta di testo
        progressBarLabel = new JLabel("INIZIALIZZAZIONE COSMICA... 0%", SwingConstants.CENTER);
        progressBarLabel.setFont(FONT_PROGRESSO);
        progressBarLabel.setForeground(COLORE_TESTO);

        // aggiunge l'etichetta personalizzata sopra la barra
        progressBar.setLayout(new BorderLayout());
        progressBar.add(progressBarLabel, BorderLayout.CENTER);

        // personalizza l'aspetto della barra di avanzamento
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                Insets b = progressBar.getInsets();
                int barRectWidth = progressBar.getWidth() - (b.right + b.left);
                int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
                int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

                // disegna lo sfondo della barra
                g2d.setColor(progressBar.getBackground());
                g2d.fillRect(b.left, b.top, barRectWidth, barRectHeight);

                // disegna la parte di avanzamento
                g2d.setColor(progressBar.getForeground());
                g2d.fillRect(b.left, b.top, amountFull, barRectHeight);
            }
        });

        // aggiunge la barra di avanzamento al pannello, centrandola
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(progressBar, gbc);
    }

    /**
     * sovrascrive il metodo paintComponent per disegnare l'immagine di sfondo.
     * 
     * @param g l'oggetto Graphics usato per disegnare
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // disegna l'immagine di sfondo per coprire l'intero pannello
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // metodi per la gestione degli eventi (PropertyChange) 

    /**
     * notifica ai listener che il caricamento è terminato.
     * 
     * @param isFinished deve essere 'true' per segnalare la fine
     */
    public void setFinished(boolean isFinished) {
        support.firePropertyChange("isFinished", null, isFinished);
    }

    /**
     * aggiunge un listener per i cambiamenti di proprietà.
     * 
     * @param listener il listener da aggiungere
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * rimuove un listener per i cambiamenti di proprietà.
     * 
     * @param listener il listener da rimuovere
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}