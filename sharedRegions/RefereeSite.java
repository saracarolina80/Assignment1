package sharedRegions;

import entities.Coach;
import entities.Contestant;
import entities.Referee;
import entities.RefereeStates;
import genclass.TextFile;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock; 

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

    private final ReentrantLock lock;

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the referee.
     */
    private int refereeState;

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
    public synchronized void setRefereeState(int state) {
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
        log.writelnString("Referee State: " + refereeState);
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }

    /**
     * Announce a new game.
     * @param referee 
     */
    public synchronized void announceNewGame(Referee referee) {
        int id = (int) referee.getId();
        try {
            lock.lock();
            System.out.println("REFEREE " + id + " is announcing new game");
            setRefereeState(RefereeStates.START_OF_A_GAME);
        } finally {
           lock.unlock(); 
        }
    }

    /**
     * Call for a new trial.
     * @param referee 
     */
    public synchronized void callTrial(Referee referee) {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Inform the referee that teams are ready.
     * @param coach 
     */
    public synchronized void informReferee(Coach coach) {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Review notes after a trial.
     * @param coach 
     */
    public synchronized void reviewNotes(Coach coach) {
        setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
    }

    /**
     * Declare the winner of the game.
     * @param referee 
     */
    public synchronized void declareGameWinner(Referee referee) {
        setRefereeState(RefereeStates.END_OF_A_GAME);
    }

    /**
     * Declare the winner of the match.
     * @param referee 
     */
    public synchronized void declareMatchWinner(Referee referee) {
        setRefereeState(RefereeStates.END_OF_THE_MATCH);
    }

    public void callTrial(Referee referee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'callTrial'");
    }

    public void declareMatchWinner(Referee referee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'declareMatchWinner'");
    }
}
