package core;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public abstract class MidiLiveReader {

    // Do we have a valid device?
    private boolean hasDevice = false;

    // The device we're listening to
    private MidiDevice device;

    public MidiLiveReader(String deviceName) {

        if (deviceName.equals("")) {
            System.out.println("No argument provided for device name, printing ALL device names:");
        }

        // Set up our device
        device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            MidiDevice checkDevice;
            try {
                checkDevice = MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                // Move on
                System.err.println("MidiUnavailable, ignoring and moving on.");
                continue;
            }
            String name = checkDevice.getDeviceInfo().getName();

            // If we're sending a blank, just print out the device names.
            if (deviceName.equals("")) {
                System.out.println("DEVICE: " + name);
            } else if (name.equals(deviceName)) {
                device = checkDevice;
                hasDevice = true;
                break;
            }
        }

        if (hasDevice) {
            System.out.println("Device found!");
        } else {
            System.err.println("Device not found");
            return;
        }

        try {
            device.getTransmitter().setReceiver(new MidiInputReceiver());
            device.open();
        } catch (MidiUnavailableException e) {
            System.err.println("Unable to set the receiver");
            e.printStackTrace();
        }

    }

    public MidiLiveReader() {
        this("");
    }

    // IMPLEMENT ME
    protected abstract void event(byte type, byte id, byte value);

    private class MidiInputReceiver implements Receiver {

        @Override
        public void close() {
            // Nothing for now
        }
        
        @Override
        public void send(MidiMessage msg, long timestamp) {
            byte[] data = msg.getMessage();
            byte type = data[0];
            byte id = data[1];
            byte value = data[2];
            
            if (hasDevice) {
                event(type, id, value);
            }
        }
    }
}
