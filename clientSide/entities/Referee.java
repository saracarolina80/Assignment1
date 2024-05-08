package clientSide.entities;

import clientSide.stubs.*;
import serverSide.main.SimulPar;

/**
 *    referee thread.
 *
 *      It simulates the referee life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */
public class Referee extends Thread {


    /**
   *  Reference to the stub of the refereeSite. 
   */
    private final RefereeSiteStub refereeSiteStub;

     /**
   *  Reference to the stub of the playground.
   */
    private final PlaygroundStub playgroundStub;
    

     /**
   *  referee identification.
   */
    private int refereeID;

    /**
   *  referee state.
   */
    private int refereeState;

    public Referee(String name,int refereeID, RefereeSiteStub refereeSiteStub, PlaygroundStub playgroundStub) {
        super(name);
        this.refereeID = refereeID;
        this.refereeSiteStub = refereeSiteStub;
        this.playgroundStub = playgroundStub;
        refereeState = RefereeStates.START_OF_THE_MATCH;
    }

    /**
   *   Set referee id.
   *
   *     @param id referee id
   */

   public void setRefereeID (int id)
   {
      refereeID = id;
   }

  /**
   *   Get referee id.
   *
   *     @return referee id
   */

   public int getRefereeID ()
   {
      return refereeID;
   }

  /**
   *   Set referee state.
   *
   *     @param state new referee state
   */

   public void setRefereeState (int state)
   {
      refereeState = state;
   }

  /**
   *   Get referee state.
   *
   *     @return referee state
   */

   public int getRefereeState ()
   {
      return refereeState;
   }

     @Override
    public void run() {
            int winner = 0;
            int numTrials = 0;
            int ropePosition = 0;
            
            for (int numGames = 0; numGames < SimulPar.NUM_GAMES; numGames++) {

            
                    refereeSiteStub.announceNewGame();
                    playgroundStub.isMatchStillGoing(false);

                    ropePosition = 0;

                do {
                    System.out.println("\n\n ------ TRIAL " + numTrials + "------\n");
                    System.out.println("Rope is in position: " + ropePosition);
                    playgroundStub.isMatchStillGoing(false);
                    refereeSiteStub.callTrial();

                    playgroundStub.startTrial();

                    ropePosition = playgroundStub.assertTrialDecision(ropePosition);

                    numTrials++;
                    
                }while ( Math.abs(ropePosition) != SimulPar.KNOCKOUT_THRESHOLD && numTrials <= SimulPar.NUM_TRIALS );
                
                System.out.println("\n\n ------ GAME DECISION ------\n");
                winner += refereeSiteStub.getGameWinner(ropePosition);
                numTrials = 0;
                ropePosition  = 0;
            }
            refereeSiteStub.getGameWinner(winner);
            playgroundStub.isMatchStillGoing(true);
            
           //  System.out.println("LETS FINISH");
        }
}

