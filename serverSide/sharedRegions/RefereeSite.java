package serverSide.sharedRegions;
import serverSide.entities.RefereeSiteClientProxy;
import serverSide.main.SimulPar;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import clientSide.entities.CoachStates;
import clientSide.entities.RefereeStates;
import clientSide.stubs.GeneralRepositoryStub;


/**
 * Referee Site.
 *
 * It is responsible for handling refereeSite-related actions during the rope game.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class RefereeSite {

    private final ReentrantLock lock;
    private final Condition informReferee;
    private final Condition callTrial;
    private final Condition announceNewGame;

    private int trialCallCount;
    private int coachesCount;

    private int gamesCount;
    private int newGameCount;

    
    /**
   *   Reference to the general repository.
   */
    private GeneralRepositoryStub reposStub;

    /**
   *  Reference to referee threads.
   */

    private  RefereeSiteClientProxy [] ref;

     /**
   *  Reference to coach threads.
   */

   private  RefereeSiteClientProxy [] coa;

    /**
     * Instantiation of the referee site
     *
     * @param repos name of the logging file
     */
    public RefereeSite(GeneralRepositoryStub reposStub) {
        ref = new RefereeSiteClientProxy[1];
        coa = new RefereeSiteClientProxy[SimulPar.NUM_TEAMS+1];
        lock = new ReentrantLock(true);
        informReferee = lock.newCondition();
        callTrial = lock.newCondition();
        announceNewGame = lock.newCondition();
        coachesCount = 0;
        trialCallCount = 0;
        gamesCount = 0;
        newGameCount = 0;
        this.reposStub = reposStub;
    }


    /**
   *  Operation announce new game.
   *
   *  Transition start operations in the life cycle of the Referee.
     *  (START_OF_THE_MATCH -> START_OF_A_GAME)
     * OR 
     *  (END_OF_THE_GAME -> START OF A GAME)
   *  It is called by the referee when he wants to announce a new game.
   *
   */
    public void announceNewGame() {
        int refereeID = ((RefereeSiteClientProxy) Thread.currentThread()).getRefereeID();        
        try {
            lock.lock();
            System.out.println("--- NEW GAME ---");
            ref[refereeID] = (RefereeSiteClientProxy) Thread.currentThread();
            ref[refereeID].setRefereeState(RefereeStates.START_OF_A_GAME);
            reposStub.setRefereeState(refereeID , ref[refereeID].getRefereeState());
            gamesCount++;
            newGameCount = 2;
            announceNewGame.signal();
        } finally {
            lock.unlock();
        }
    }

   /**
   *  Operation call for a new trial.
   *
   * Transition to teams ready in the life cycle of the Referee.
     *  (START_OF_A_GAME -> TEAMS_READY)
     * 
   *  It is called by the referee when he wants to start the trial
   *
   */
    public void callTrial() {
        int refereeID = ((RefereeSiteClientProxy) Thread.currentThread()).getRefereeID(); 

        try {
            lock.lock();
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
            ref[refereeID] = (RefereeSiteClientProxy) Thread.currentThread();
            ref[refereeID].setRefereeState(RefereeStates.TEAMS_READY);
            reposStub.setRefereeState(refereeID , ref[refereeID].getRefereeState());
            System.out.println(refereeID + " knows : TEAMS_READY");
            coachesCount = 0;
        } finally {
            lock.unlock();
        }
    }

   /**
   *  Operation Inform the referee.
   *
   *  Transition to watch trial in the life cycle of the Coach.
     *  (ASSEMBLE_TEAM -> WATCH_TRIAL)

   *  It is called by the coach when he wants to inform the referee the team chosen.
   *
   */
    public void informReferee() {
            int coachID = ((RefereeSiteClientProxy) Thread.currentThread()).getCoachID();        
            coa[coachID] = (RefereeSiteClientProxy) Thread.currentThread();
            coa[coachID].setCoachState(CoachStates.WATCH_TRIAL);
            reposStub.setCoachState(coachID , coa[coachID].getCoachState());
        try {
            lock.lock();
            coachesCount++;
            informReferee.signal();
        } finally {
            lock.unlock();
        }
      
    }

     /**
   *  Operation Review Notes
   *
   *  Transition to WAIT for the referee in the life cycle of the Coach.
     *  (WATCH_TRIAL -> WAIT_FOR_REFEREE_COMMAND)

   *  It is called by the coach when he wants review the notes after the triall .
   *
   */
    public void reviewNotes() {
        int coachID = ((RefereeSiteClientProxy) Thread.currentThread()).getCoachID();  
        try {
            lock.lock();
            coa[coachID] = (RefereeSiteClientProxy) Thread.currentThread();
            coa[coachID].setCoachState(CoachStates.WAIT_FOR_REFEREE_COMMAND);
            reposStub.setCoachState(coachID , coa[coachID].getCoachState());
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
     * Operation Declare the winner of the game.
     * * 
     * Transition to end of the game in the life cycle of the Referee.
     *  (WAIT_FOR_TRIAL_CONCLUSION -> END_OF_A_GAME)
     * 
     * 
     * @param ropePosition the position of the rope at the end of the game
     * @return the winning team (1 or 2) or 0 for a draw
     */
    public int getGameWinner(int ropePosition) {
        int refereeID = ((RefereeSiteClientProxy) Thread.currentThread()).getRefereeID();    
        ref[refereeID] = (RefereeSiteClientProxy) Thread.currentThread();
        ref[refereeID].setRefereeState(RefereeStates.END_OF_A_GAME);
        reposStub.setRefereeState(refereeID , ref[refereeID].getRefereeState());
        if (ropePosition < 0) {
            System.out.println("Game " + gamesCount + " won by Team 1!");
            return -1;
        } else if (ropePosition > 0) {
            System.out.println("Game " + gamesCount + " won by Team 2!");
            return 1;
        } else {
            System.out.println("Game " + gamesCount + " ended in a draw!");
            return 0;
        }
    }

    /**
     * Declare the winner of the match.
     * 
     * Transition to end of the game in the life cycle of the Referee.
     *  (END_OF_A_GAME -> END_OF_THE_MATCH)
     * 
     *
     * @param result  the result of the match: negative for Team 1 win, positive for Team 2 win, zero for draw
     * @return the match winning team (1 or 2) or 0 for a draw
     */
    public void getMatchWinner(int result) {
        int refereeID = ((RefereeSiteClientProxy) Thread.currentThread()).getRefereeID();    
        ref[refereeID] = (RefereeSiteClientProxy) Thread.currentThread();
        ref[refereeID].setRefereeState(RefereeStates.END_OF_THE_MATCH);
        reposStub.setRefereeState(refereeID , ref[refereeID].getRefereeState());
        if (result < 0) {
            System.out.println("Team 1 has won the match!");
        } else if (result > 0) {
            System.out.println("Team 2 has won the match!");
        } else {
            System.out.println("It's a draw! ");
        }
    }

    /**
     * Operation Wait for a new game to be announced.
     *
     * Coach waits for the referee to announce a new Game   (after reviweing notes)
     */
    public void waitNewGame() {
      //  int coachID = ((Coach) Thread.currentThread()).getCoachID();  

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
