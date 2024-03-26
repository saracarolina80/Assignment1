package sharedRegions;

import entities.PlayState;
import genclass.TextFile;

import java.util.Objects;

/**
 * Playground.
 *
 * It is responsible for handling playground-related actions during the rope game.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class Playground {

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the playground.
     */
    private PlayState playgroundState;

    /**
     * Instantiation of the playground object.
     *
     * @param logFileName name of the logging file
     */
    public Playground(String logFileName) {
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        playgroundState = PlayState.INITIAL;
        reportStatus();
    }

    /**
     * Set playground state.
     *
     * @param state playground state
     */
    public synchronized void setPlaygroundState(PlayState state) {
        playgroundState = state;
        reportStatus();
    }

    /**
     * Write the current state to the logging file.
     */
    private void reportStatus() {
        TextFile log = new TextFile();
        if (!log.openForAppending(".", logFileName)) {
            System.out.println("Failed to open for appending the file " + logFileName + "!");
            System.exit(1);
        }
        log.writelnString("Playground State: " + playgroundState.toString());
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }

    /**
     * Wait for the athletes to be ready.
     */
    public synchronized void waitAthletes() {
        setPlaygroundState(PlayState.WAIT_FOR_TEAMS);
    }

    /**
     * Watch the trial.
     */
    public synchronized void watchTrial() {
        setPlaygroundState(PlayState.WAIT_FOR_REFEREE_COMMAND);
    }

    /**
     * Assert the trial decision.
     */
    public synchronized void assertTrialDecision() {
        setPlaygroundState(PlayState.WAIT_FOR_REFEREE_COMMAND);
    }

    /**
     * Start a trial.
     */
    public synchronized void startTrial() {
        setPlaygroundState(PlayState.WAIT_FOR_REFEREE_COMMAND);
    }

    /**
     * End of a trial.
     */
    public synchronized void amDone() {
        setPlaygroundState(PlayState.INITIAL);
    }

    /**
     * Pull the rope.
     */
    public synchronized void pullTheRope() {
        setPlaygroundState(PlayState.INITIAL);
    }

    /**
     * Get ready for a trial.
     */
    public synchronized void getReady() {
        setPlaygroundState(PlayState.WAIT_FOR_REFEREE_COMMAND);
    }

    /**
     * Follow coach's advice.
     */
    public synchronized void followCoachAdvice() {
        setPlaygroundState(PlayState.WAIT_FOR_REFEREE_COMMAND);
    }
}
