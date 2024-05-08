package clientSide.entities;

import clientSide.stubs.*;
import serverSide.main.SimulPar;




/**
 *    Contestant thread.
 *
 *      It simulates the contestant life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */
public class Contestant extends Thread {


     /**
   *  contestant strength.
   */
    private int strength = SimulPar.STRENGTH;

    
    private boolean isChosen;


     /**
   *  contestant identification.
   */
    private int contestantID;
    private int teamID;


    /**
   *  contestant state.
   */
    private int contestantState;


    /**
   *  Reference to the stub of the playground.
   */
    private final PlaygroundStub playgroundStub;

     /**
   *  Reference to the stub of the contestantsbench.
   */
    private final ContestantsBenchStub benchStub;

    public Contestant(String name, int teamID, int contestantID, PlaygroundStub playgroundStub, ContestantsBenchStub benchStub) {
        super(name);
        this.teamID = teamID;
        this.contestantID = contestantID;
        this.playgroundStub = playgroundStub;
        this.strength = SimulPar.STRENGTH;
        this.benchStub = benchStub;
        this.isChosen = false;
        contestantState = ContestantStates.SEAT_AT_THE_BENCH;
    }

    /**
   *   Set contestant id.
   *
   *     @param id contestant id
   */

   public void setContestantId (int id)
   {
      contestantID = id;
   }

  /**
   *   Get contestant id.
   *
   *     @return contestant id
   */

   public int getContestantID ()
   {
      return contestantID;
   }

   /**
   *   Get team id.
   *
   *     @return team id
   */

   public int getTeamID ()
   {
      return teamID;
   }


  /**
   *   Set contestant state.
   *
   *     @param state new contestant state
   */

   public void setContestantState (int state)
   {
      contestantState = state;
   }

  /**
   *   Get contestant state.
   *
   *     @return contestant state
   */

   public int getContestantState ()
   {
      return contestantState;
   }


    public void decrementStrength() {
        if (strength > 0) {
            strength = strength - SimulPar.STRENGTH_LOSS;
        }
    }

    public void incrementStrength() {
         strength = strength + SimulPar.STRENGTH_GAIN;
    }

    public int getStrength() {
        return strength;
    }

    public void setChosen(boolean isChosenByCoach){
        isChosen = isChosenByCoach; 
    }

    @Override
    public void run() {
  
        while(!playgroundStub.isMatchFinished()) {
            benchStub.sitDown();
            if(isChosen) {
                playgroundStub.followCoachAdvice();
                playgroundStub.getReady();
                playgroundStub.pullTheRope();
                playgroundStub.amDone();
            }
            else{
                System.out.println("Contestant " + this.getName() + " was not chosen to this trial!");
            }
        } 
    // System.out.println("FINISH PLEASE!");
    }
}
