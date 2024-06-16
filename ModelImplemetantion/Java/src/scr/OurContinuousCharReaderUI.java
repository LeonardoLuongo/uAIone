package scr;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OurContinuousCharReaderUI extends JFrame {
    private JTextField inputField;
    private HashMap<String, LocalDateTime> keysPressed;

    public OurContinuousCharReaderUI()
    {}

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
                    // displayPressedKeys();
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
                // displayPressedKeys();
                inputField.setText("");
            }
        });

        // Make the frame visible
        setVisible(true);
    }

    private void displayPressedKeys() {
        StringBuilder pressedKeys = new StringBuilder("Keys pressed: ");
        for (Map.Entry<String, LocalDateTime> entry : keysPressed.entrySet()) {
            pressedKeys.append(entry.getKey()).append(" ");
        }
        System.out.println(pressedKeys.toString().trim());
    }

    public HashMap<String, LocalDateTime> getKeysPressed()
    {
        synchronized(keysPressed)
        {
            return keysPressed;
        }
    } 


    public static void main(String[] args) {
        // Run the UI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> (new OurContinuousCharReaderUI()).start());
    }

}
