package fun;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import core.MidiLiveReader;

public class KeyboardMapper extends MidiLiveReader {

    // Map holding midi note/key pairs
    private HashMap<Byte, Integer> midiToKey;

    // Typer
    private Robot robot;

    public KeyboardMapper() {
        super("Portable G-1");

        midiToKey = new HashMap<>();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Failed to initialize Robot! Be sure to be using java with a header.");
            e.printStackTrace();
        }
    }

    public void mapKey(byte midiID, int key) {
        midiToKey.put(midiID, key);
    }

    @Override
    protected void event(byte type, byte id, byte value) {
        Integer key = midiToKey.get(id);
        if (key != null) {
            if (value > 0) {
                robot.keyPress(key);
                System.out.println("press\t " + key);
            } else {
                robot.keyRelease(key);
                System.out.println("release\t " + key);
            }
        } else {
            System.out.printf("[EVENT] t: %d,\t id: %d,\t v: %d\n", type, id, value);
        }
    }

    public static void main(String[] args) {
        KeyboardMapper mapper = new KeyboardMapper();

        // Dinosaur game, Fur Elise
        /*
        mapper.mapKey((byte)64 , KeyEvent.VK_SPACE);
        mapper.mapKey((byte)62 , KeyEvent.VK_SPACE);
        mapper.mapKey((byte)57 , KeyEvent.VK_SPACE);
        mapper.mapKey((byte)59 , KeyEvent.VK_SPACE);
        mapper.mapKey((byte)60 , KeyEvent.VK_SPACE);
        */
        // Wipeout pulse
        // Main turning (Bassline)
        mapper.mapKey((byte)36, KeyEvent.VK_LEFT); // Low C
        mapper.mapKey((byte)43, KeyEvent.VK_RIGHT); // Low G
        mapper.mapKey((byte)31, KeyEvent.VK_LEFT); // Lower G
        mapper.mapKey((byte)38, KeyEvent.VK_RIGHT); // Low D

        // Airbrakes (upper melody chords)
        mapper.mapKey((byte)72, KeyEvent.VK_W); // High C
        mapper.mapKey((byte)74, KeyEvent.VK_W); // High D
        mapper.mapKey((byte)60, KeyEvent.VK_Q); // High C
        mapper.mapKey((byte)62, KeyEvent.VK_Q); // High D
        
        // Use Powerup
        mapper.mapKey((byte)40, KeyEvent.VK_A); // Low E
        mapper.mapKey((byte)35, KeyEvent.VK_A); // Low B
        

        // Always hold Z
        mapper.robot.keyPress(KeyEvent.VK_Z);

    }
}
