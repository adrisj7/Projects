package control.cbp;

/**
 * Wooo CBP!
 * 
 * @author adris
 *
 */
public abstract class Command {

    enum COMMAND_STATE {
        STARTING, RUNNING, OFF
    };

    private COMMAND_STATE state;

    public Command() {
        state = COMMAND_STATE.OFF;
    }

    void _start() {
        state = COMMAND_STATE.STARTING;
    }

    void _loop() {
        switch (state) {
            case STARTING:
                initialize();
                state = COMMAND_STATE.RUNNING;
                break;
            case RUNNING:
                if (!isFinished()) {
                    execute();
                } else {
                    end();
                    state = COMMAND_STATE.OFF;
                }
            case OFF:
                // Do nothing
                break;
        }
    }

    COMMAND_STATE getState() {
        return state;
    }

    // Look familiar?
    protected abstract void initialize();
    protected abstract void execute();
    protected abstract boolean isFinished();
    protected abstract void end();
    // no interrupted, we don't got time for that
}
