package sharedRegions;

import entities.Coach;
import entities.CoachStates;
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
    private final Condition teamsReady;
    private final Condition trialConclusion; 
    private final Condition informReferee;
    private final Condition callTrial;
    private int trialCallCount;
    private int coachesCount;

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the referee.
     */
    private int refereeState;

    /**
     * State of the coach.
     */
    private int coachState;



    /**
     * Instantiation of the referee site object.
     *
     * @param logFileName name of the logging file
     */
    public RefereeSite(String logFileName) {
        lock = new ReentrantLock(true);
        teamsReady = lock.newCondition();
        trialConclusion = lock.newCondition();
        informReferee = lock.newCondition();
        callTrial = lock.newCondition();
        coachesCount = 0;
        trialCallCount = 0;
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        refereeState = RefereeStates.START_OF_THE_MATCH;
        coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
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
     * Set referee state.
     *
     * @param Cstate coach state
     */
    public synchronized void setCoachState(int Cstate) {
        coachState = Cstate;
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
        log.writelnString("Coach State: " + coachState);
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
        int id = (int) referee.getId();

        try {
            lock.lock();
            System.out.println("REFEREE " + id + " is announcing new trial");
            trialCallCount = 2;
            callTrial.signalAll();
        } finally {
           lock.unlock(); 
        }

        try {
            lock.lock();
            System.out.println("REFEREE " + id + " will wait for teams ready");
            while(coachesCount != 2){
                try{
                    System.out.println("REFEREE " + id + " is waiting for teams ready");
                    informReferee.await();
                    System.out.println("REFEREE " + id + " received inform referee");
                } catch (InterruptedException e){}
            }
            setRefereeState(RefereeStates.TEAMS_READY);
            System.out.println("REFEREE " + id + " will proceed, teams ready");
            coachesCount = 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inform the referee that teams are ready.
     * @param coach 
     */
    public synchronized void informReferee(Coach coach) {
        try {
            lock.lock();
            coachesCount++;
            if (coachesCount == 2) {
                teamsReady.signal(); // Signal the referee that teams are ready
            }
        } finally {
            lock.unlock();
        }
        setCoachState(CoachStates.WATCH_TRIAL);
    }

    public synchronized void waitForTeamsReady(Referee referee) {
        try {
            lock.lock();
            while (coachesCount < 2) {
                try {
                    teamsReady.await(); // Referee waits until both teams are ready
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setRefereeState(RefereeStates.TEAMS_READY);
        } finally {
            lock.unlock();
        }
    }


    public synchronized void assertTrialDecision(Referee referee) {
        try {
            lock.lock();
            while (trialCallCount == 0) {
                try {
                    trialConclusion.await(); // Referee waits until trial conclusion
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            trialCallCount--;
            setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Review notes after a trial.
     * @param coach 
     */
    public synchronized void reviewNotes(Coach coach) {
        int id = (int) coach.getId();

        try {
            lock.lock();
            System.out.println("COACH " + id + " is reviewing notes with referee");
            while(trialCallCount == 0){
                try{
                    System.out.println("COACH " + id + " is waiting for call trial");
                    callTrial.await();
                    System.out.println("COACH " + id + " received call trial");
                } catch (InterruptedException e){}
            }
            System.out.println("COACH " + id + " will proceed call trial");
            setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
            trialCallCount = trialCallCount - 1;
        } finally {
            lock.unlock();
        }
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

}
