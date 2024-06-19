package src.OurContinuousCharReaderUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.HashMap;

public class OurContinuousCharReaderUI extends JFrame 
{
    /*
     * Questa classe implementa un'interfaccia grafica che permette all'utente di 
     * inserire comandi tramite tastiera [w, a, s, d]. I comandi premuti vengono 
     * registrati e mostrati nella finestra.
     */

    private JTextField inputField;
    private HashMap<String, LocalDateTime> keysPressed;


    public void start() {
        // Initialize the set to keep track of pressed keys
        keysPressed = new HashMap<>();

        // Set up the frame
        setTitle("Continuous Character Reader");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Initialize the text field for input
        inputField = new JTextField(20);
        add(inputField);

        // Add key listener to the text field
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                synchronized(keysPressed)
                {
                    if(keysPressed.get(KeyEvent.getKeyText(keyCode)) == null)
                    {
                        keysPressed.put(KeyEvent.getKeyText(keyCode), LocalDateTime.now());
                    }
                }
                
                inputField.setText("");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();

                synchronized(keysPressed)
                {
                    keysPressed.remove(KeyEvent.getKeyText(keyCode));
                }
                inputField.setText("");
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    public HashMap<String, LocalDateTime> getKeysPressed()
    {
        synchronized(keysPressed)
        {
            return keysPressed;
        }
    } 

    /*public static void main(String[] args) {
        // Run the UI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> (new OurContinuousCharReaderUI()).start());
    }*/
}
