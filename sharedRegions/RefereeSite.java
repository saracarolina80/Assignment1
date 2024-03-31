package sharedRegions;

import entities.Coach;
import entities.CoachStates;
import entities.Referee;
import entities.RefereeStates;
import genclass.TextFile;
import main.SimulPar;

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

    GeneralRepos repos;

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
        repos = new GeneralRepos(logFileName);
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
     * Set coach state.
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
            System.out.println("Failed to open the log file " + logFileName + " for appending!");
            System.exit(1);
        }
        repos.reportStatus();

        if (!log.close()) {
            System.out.println("Failed to close the log file " + logFileName + "!");
            System.exit(1);
        }
    }

    /**
     * Announce a new game.
     *
     * @param referee the referee announcing the new game
     */
    public void announceNewGame(Referee referee) {
        String refereeName = referee.getName();
        try {
            lock.lock();
            System.out.println(refereeName + " is announcing a new game.");
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
     *
     * @param referee the referee calling for the new trial
     */
    public void callTrial(Referee referee) {
        String refereeName = referee.getName();

        try {
            lock.lock();
            System.out.println(refereeName + " is calling for a new trial!");
            trialCallCount = 2;
            callTrial.signalAll();
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            while (coachesCount != 2) {
                try {
                    informReferee.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setRefereeState(RefereeStates.TEAMS_READY);
            System.out.println(refereeName + " acknowledges that teams are ready for the trial.");
            coachesCount = 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Inform the referee that teams are ready.
     *
     * @param coach the coach informing the referee
     */
    public void informReferee(Coach coach) {
        String coachName = coach.getName();

        try {
            lock.lock();
            coachesCount++;
            System.out.println(coachName + " is informing the referee that the team is ready.");
            informReferee.signal();
        } finally {
            lock.unlock();
        }
        setCoachState(CoachStates.WATCH_TRIAL);
    }

    /**
     * Review notes after a trial.
     *
     * @param coach the coach reviewing notes with the referee
     */
    public void reviewNotes(Coach coach) {
        String coachName = coach.getName();

        try {
            lock.lock();
            setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
           // System.out.println("TRIAL CALLL COUNT: " + trialCallCount);
            while (trialCallCount == 0) {
             //   System.out.println("REVIEWD NOTESS");
                try {
                    callTrial.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            trialCallCount--;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Declare the winner of the game.
     *
     * @param referee      the referee declaring the game winner
     * @param ropePosition the position of the rope at the end of the game
     * @return the winning team (1 or 2) or 0 for a draw
     */
    public int declareGameWinner(Referee referee, int ropePosition) {
        setRefereeState(RefereeStates.END_OF_A_GAME);
        if (ropePosition < 0) {
            System.out.println("Game " + gamesCount + " won by Team 1!");
            return 1;
        } else if (ropePosition > 0) {
            System.out.println("Game " + gamesCount + " won by Team 2!");
            return 2;
        } else {
            System.out.println("Game " + gamesCount + " ended in a draw!");
            return 0;
        }
    }

    /**
     * Declare the winner of the match.
     *
     * @param referee the referee declaring the match winner
     * @param result  the result of the match: negative for Team 1 win, positive for Team 2 win, zero for draw
     */
    public synchronized void declareMatchWinner(Referee referee, int result) {
        setRefereeState(RefereeStates.END_OF_THE_MATCH);
        if (result < 0) {
            System.out.println("Team 1 has won the match!");
        } else if (result > 0) {
            System.out.println("Team 2 has won the match!");
        } else {
            System.out.println("It's a draw! ");
        }
    }

    /**
     * Wait for a new game to be announced.
     *
     * @param coach the coach waiting for a new game
     */
    public void waitNewGame(Coach coach) {
        String coachName = coach.getName();

        try {
            lock.lock();
            while (newGameCount == 0) {
                try {
                    announceNewGame.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            newGameCount--;
        } finally {
            lock.unlock();
        }
    }
}
