package control.cbp;

import java.util.LinkedList;

/**
 * I think this is what handles the cbp...
 * 
 * @author adris
 *
 */
public class Scheduler {
    private static Scheduler instance = null;

    private LinkedList<Command> commands;

    public Scheduler() {
        commands = new LinkedList<>();
    }

    /**
     * @return Static scheduler
     */
    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }
}
