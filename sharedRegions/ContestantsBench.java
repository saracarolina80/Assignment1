package sharedRegions;

import entities.CoachStates;
import genclass.TextFile;

import java.util.Objects;

/**
 * Bench.
 *
 * It is responsible for handling bench-related actions during the rope game.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class ContestantsBench {

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the coach.
     */
    private CoachStates coachState;

    /**
     * Instantiation of the bench object.
     *
     * @param logFileName name of the logging file
     */
    public ContestantsBench(String logFileName) {
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
        reportStatus();
    }

    /**
     * Set coach state.
     *
     * @param state coach state
     */
    public synchronized void setCoachState(CoachStates state) {
        coachState = state;
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
        log.writelnString("Coach State: " + coachState.toString());
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }

    /**
     * Call contestants to the bench.
     */
    public synchronized void callContestants() {
        setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
    }

    /**
     * Sit down on the bench.
     */
    public synchronized void sitDown() {
        setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
    }
}