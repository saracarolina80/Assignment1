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
    private final Condition announceNewGame;

    private int trialCallCount;
    private int coachesCount;

    private int gamesCount;
    private int newGameCount;

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
        announceNewGame = lock.newCondition();
        coachesCount = 0;
        trialCallCount = 0;
        gamesCount = 0;
        newGameCount = 0;
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        refereeState = RefereeStates.START_OF_THE_MATCH;
        // coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
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
        String refereeName = referee.getName();
        try {
            lock.lock();
            System.out.println("REFEREE " + refereeName + " is announcing new game");
            referee.setRefereeState(RefereeStates.START_OF_A_GAME);
            gamesCount++;
            newGameCount = 2;
            announceNewGame.signal();
        } finally {
           lock.unlock(); 
        }
    }

    /**
     * Call for a new trial.
     * @param referee 
     */
    public synchronized void callTrial(Referee referee) {
        String refereeName = referee.getName();

        try {
            lock.lock();
            System.out.println("REFEREE " + refereeName + " is announcing new trial");
            trialCallCount = 2;
            callTrial.signalAll();
        } finally {
           lock.unlock(); 
        }

        try {
            lock.lock();
            System.out.println("REFEREE " + refereeName + " will wait for teams ready");
            while(coachesCount != 2){
                try{
                    System.out.println("REFEREE " + refereeName + " is waiting for teams ready");
                    informReferee.await();
                    System.out.println("REFEREE " + refereeName + " received inform referee");
                } catch (InterruptedException e){}
            }
            setRefereeState(RefereeStates.TEAMS_READY);
            System.out.println("REFEREE " + refereeName + " will proceed, teams ready");
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
        String coachName = coach.getName();
        try {
            lock.lock();
            coachesCount++;
            informReferee.signal();
            System.out.println("COACH " + coachName + "is informing referee.");
            while (coachesCount < 2) {
                try {
                    teamsReady.await(); // Referee waits until both teams are ready
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (coachesCount == 2) {
                teamsReady.signal(); // Signal the referee that teams are ready
            }
        } finally {
            lock.unlock();
        }
        setCoachState(CoachStates.WATCH_TRIAL);
    }

    /**
     * Review notes after a trial.
     * @param coach 
     */
    public synchronized void reviewNotes(Coach coach) {
        String coachName = coach.getName();

        try {
            lock.lock();
            System.out.println("COACH " + coachName + " is reviewing notes with referee");
            setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
            while(trialCallCount == 0){
                try{
                    System.out.println("COACH " + coachName + " is waiting for call trial");
                    callTrial.await();
                    System.out.println("COACH " + coachName + " received call trial");
                } catch (InterruptedException e){}
            }
            System.out.println("COACH " + coachName + " will proceed call trial");
            trialCallCount = trialCallCount - 1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Declare the winner of the game.
     * @param referee 
     * @param ropePosition 
     */
    public int declareGameWinner(Referee referee, int ropePosition) {
        setRefereeState(RefereeStates.END_OF_A_GAME);
        if (ropePosition < 0) {
            System.out.println("Game " + gamesCount + " won by team 1!");
            return 1;         
        }
        else if (ropePosition > 0) {
            System.out.println("Game " + gamesCount + " won by team 2!");
            return 2;          
        }
        else {
            System.out.println("Game " + gamesCount + " was a draw!");
            return 0;          
        }
    }

    /**
     * Declare the winner of the match.
     * @param referee 
     * @param winner 
     */
    public synchronized void declareMatchWinner(Referee referee, int result) {
        setRefereeState(RefereeStates.END_OF_THE_MATCH);
        if (result < 0) {
            System.out.println("Congratulations! Team 1 has won the match!");
        }
        else if (result > 0) {
            System.out.println("Congratulations! Team 2 has won the match!");
        }
        else {
            System.out.println("It seems we have a draw! Congratulations to both teams!");  //se for 3 jogos, nao acontece
        }     
    }

    public void waitNewGame(Coach coach) {
        String coachName = coach.getName();

        try {
            lock.lock();
            System.out.println("COACH " + coachName + " will wait for new game");
            
            while(newGameCount == 0){       
                try{
                    System.out.println("COACH " + coachName + " is waiting for new game");
                    announceNewGame.await();
                    System.out.println("COACH " + coachName + " received new game");
                } catch (InterruptedException e){}
            }
            
            System.out.println("COACH " + coachName + " will proceed");
            newGameCount = newGameCount - 1;
        } finally {
            lock.unlock();
        }
    }

}
