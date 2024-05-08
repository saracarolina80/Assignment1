package serverSide.entities;

import serverSide.sharedRegions.*;
import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;
/**
 *  Service provider agent for access to the Referee Site.
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class RefereeSiteClientProxy extends Thread implements CoachCloning , RefereeCloning {

   
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

   private RefereeSiteInterface refSiteInter;


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
   *  referee identification.
   */

   private int refereeID;

  /**
   *  referee state.
   */

   private int refereeState;


     /**
   *  result
   */

   private int result;

   /**
   *  Instantiation of a client proxy.
   *
   *     @param sconi communication channel
   *     @param refSiteInter interface to the referee site
   */

   public RefereeSiteClientProxy (ServerCom sconi, RefereeSiteInterface refSiteInter)
   {
      super ("RefereeSiteClientProxy_" + RefereeSiteClientProxy.getProxyId ());
      this.sconi = sconi;
      this.refSiteInter = refSiteInter;
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
      { cl = Class.forName ("serverSide.entities.RefereeSiteClientProxy");
      }
      catch (ClassNotFoundException e)
      { GenericIO.writelnString ("Data type RefereeSiteClientProxy was not found!");
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
   *   Get team id.
   *
   *     @return team id
   */

   public int getTeamID ()
   {
      return teamID;
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
   *  rope position.
   */

   private int ropePosition;



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

  /**
   *   Get rope position.
   *
   *     @return referee state
   */

   public int getRopePosition ()
   {
      return ropePosition ;
   }


    /**
   *   Set rope position.
   *
   *     @return rope position
   */

   public void setRopePosition (int rope)
   {
     ropePosition = rope;


   }


   public void setResult (int res) {
      result = res;
   }
   
   public int getResult() {
      return result;
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
      { outMessage = refSiteInter.processAndReply (inMessage);         // process it
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
