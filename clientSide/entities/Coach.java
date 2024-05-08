package clientSide.entities;

import clientSide.stubs.*;


/**
 *    Coach thread.
 *
 *      It simulates the coach life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */
public class Coach extends Thread {

    /**
   *  Reference to the stub of the contestantsbench.
   */
    private final ContestantsBenchStub benchStub;

    /**
   *  Reference to the stub of the refereeSite. 
   */
    private final RefereeSiteStub refereeSiteStub;

    /**
   *  Reference to the stub of the playground.
   */
    private final PlaygroundStub playgroundStub;


  /**
   *  coach state.
   */
    private int coachState;

    /**
   *  coach identification.
   */
    private int coachID;

    
    private int chooseContestants;


      /**
   *   Instantiation of a coach thread.
   *
   *     @param name thread name
   *     @param coachID coach id
   *     @param bench reference to the contestants bench
   *     @param refereesite reference to the refereeSite
   *     @param playground reference to the playground
   */
    public Coach(String name, int coachID,  ContestantsBenchStub benchStub, RefereeSiteStub refereeSiteStub, PlaygroundStub playgroundStub) {
        super(name);
        this.benchStub = benchStub;
        this.coachID = coachID;
        this.refereeSiteStub = refereeSiteStub;
        this.playgroundStub = playgroundStub;
        this.coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
    }

     /**
   *   Set coach id.
   *
   *     @param id coach id
   */

   public void setCoachID (int id)
   {
    coachID = id;
   }

  /**
   *   Get coach id.
   *
   *     @return coach id
   */

   public int getCoachID ()
   {
      return coachID;
   }

  /**
   *   Set coach state.
   *
   *     @param state new coach state
   */

   public void setCoachState (int state)
   {
      coachState = state;
   }

  /**
   *   Get coach state.
   *
   *     @return coach state
   */

   public int getCoachState ()
   {
      return coachState;
   }

    @Override
    public void run() {
     
        refereeSiteStub.waitNewGame();
        do{
        
                    refereeSiteStub.reviewNotes();
                    benchStub.callContestants();
                    playgroundStub.waitContestants();
                    refereeSiteStub.informReferee();
                    playgroundStub.watchTrial();
                   
                  //  refereeSite.waitNewGame();
         } while (!playgroundStub.isMatchFinished());
       // System.out.println("PLEASE PLEASE");
    }
    // BEST 3 OR RANDOM
    public int getChooseMode() {
        return chooseContestants;
    }
}
