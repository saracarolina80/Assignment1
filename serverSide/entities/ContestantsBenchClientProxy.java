package serverSide.entities;

import serverSide.main.SimulPar;
import serverSide.sharedRegions.*;
import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Service provider agent for access to the Contestants Bench.
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class ContestantsBenchClientProxy extends Thread implements CoachCloning , ContestantCloning {

   
    /**
   *  Number of instantiayed threads.
   */

   private static int nProxy = 0;

  /**
   *  Communication channel.
   */

   private ServerCom sconi;

   /**
   *  Interface to the Contestant Bench
   */

   private ContestantsBenchInterface benchInter;


    /**
   *  team identification.
   */

   private int teamID;

   /**
   *  coach identification.
   */

   private int coachID;

  /**
   *  coach state.
   */

   private int coachState;

  /**
   *  contenstant identification.
   */

   private int contestantID;

  /**
   *  contestant state.
   */

   private int contestantState;

   /**
   *  Instantiation of a client proxy.
   *
   *     @param sconi communication channel
   *     @param benchInter interface to the contestant bench
   */

   public ContestantsBenchClientProxy (ServerCom sconi, ContestantsBenchInterface benchInter)
   {
      super ("ContestantBenchProxy_" + ContestantsBenchClientProxy.getProxyId ());
      this.sconi = sconi;
      this.benchInter = benchInter;
   }

   /**
   *  Generation of the instantiation identifier.
   *
   *     @return instantiation identifier
   */
   private static int getProxyId ()
   {
      Class<?> cl = null;                                            // representation of the ContestantsBenchClientProxy object in JVM
      int proxyId;                                                   // instantiation identifier

      try
      { cl = Class.forName ("serverSide.entities.ContestantsBenchClientProxy");
      }
      catch (ClassNotFoundException e)
      { GenericIO.writelnString ("Data type ContestantsBenchClientProxy was not found!");
        e.printStackTrace ();
        System.exit (1);
      }
      synchronized (cl)
      { proxyId = nProxy;
        nProxy += 1;
      }
      return proxyId;
   }

     /**
   *  contestant strength.
   */
    private int strength = SimulPar.STRENGTH;


     /**
   *  contestant strength.
   */
   private boolean isChosen = false;


   /**
   *   Get isChosen.
   *
   *     @return isChosen
   */
   public boolean getWillPlay(){
      return isChosen;
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

  public void setStrength(int Nstrength) {
   strength = Nstrength;
}


  public void setChosen(boolean isChosenByCoach){
      isChosen = isChosenByCoach; 
  }

   /**
   *  Life cycle of the service provider agent.
   */
   @Override
   public void run ()
   {
      Message inMessage = null,                                      // service request
              outMessage = null;                                     // service reply

     /* service providing */

      inMessage = (Message) sconi.readObject ();                     // get service request
      try
      { outMessage = benchInter.processAndReply (inMessage);         // process it
      }
      catch (MessageException e)
      { GenericIO.writelnString ("Thread " + getName () + ": " + e.getMessage () + "!");
        GenericIO.writelnString (e.getMessageVal ().toString ());
        System.exit (1);
      }
      sconi.writeObject (outMessage);                                // send service reply
      sconi.close ();                                                // close the communication channel
   }

}
