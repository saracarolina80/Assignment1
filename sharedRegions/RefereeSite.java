package sharedRegions;

import entities.RefereeStates;
import genclass.TextFile;

import java.util.Objects;

/**
 * Referee Site.
 *
 * It is responsible for handling referee-related actions during the rope game.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class RefereeSite {

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the referee.
     */
    private RefereeStates refereeState;

    /**
     * Instantiation of the referee site object.
     *
     * @param logFileName name of the logging file
     */
    public RefereeSite(String logFileName) {
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        refereeState = RefereeStates.START_OF_THE_MATCH;
        reportStatus();
    }

    /**
     * Set referee state.
     *
     * @param state referee state
     */
    public synchronized void setRefereeState(RefereeStates state) {
        refereeState = state;
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
        log.writelnString("Referee State: " + refereeState.toString());
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }

    /**
     * Announce a new game.
     */
    public synchronized void announceNewGame() {
        setRefereeState(RefereeStates.START_OF_A_GAME);
    }

    /**
     * Call for a new trial.
     */
    public synchronized void callTrial() {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Inform the referee that teams are ready.
     */
    public synchronized void informReferee() {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Review notes after a trial.
     */
    public synchronized void reviewNotes() {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Declare the winner of the game.
     */
    public synchronized void declareGameWinner() {
        setRefereeState(RefereeStates.END_OF_A_GAME);
    }

    /**
     * Declare the winner of the match.
     */
    public synchronized void declareMatchWinner() {
        setRefereeState(RefereeStates.END_OF_THE_MATCH);
    }
}
