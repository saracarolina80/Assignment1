package serverSide.sharedRegions;

import serverSide.main.SimulPar;
import serverSide.entities.*;
import clientSide.entities.*;
import commInfra.*;


/**
 *  Interface to the RefereeSite.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    RefereeSite and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class RefereeSiteInterface {


    /**
   *  Reference to the RefereeSite
   */

   private final RefereeSite refereeSite;

   /**
   *  Instantiation of an interface to the RefereeSite
   *
   *    @param refereeSite reference to the RefereeSite
   */

   public RefereeSiteInterface (RefereeSite refereeSite)
   {
      this.refereeSite = refereeSite;
   }


  
  /**
   *  Processing of the incoming messages.
   *
   *  Validation, execution of the corresponding method and generation of the outgoing message.
   *
   *    @param inMessage service request
   *    @return service reply
   *    @throws MessageException if the incoming message is not valid
   */

   public Message processAndReply (Message inMessage) throws MessageException
   {
      Message outMessage = null;                                     // outgoing message

     /* validation of the incoming message */

      switch (inMessage.getMsgType ())
      {
         case MessageType.ANNOUNCENGAME:   if (inMessage.getRefereeId () != 0 )
                  throw new MessageException ("Invalid REFEREE id!", inMessage);
               else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                        throw new MessageException ("Invalid REFEREE state!", inMessage);
            break;
         case MessageType.CALLTRIAL:   if (inMessage.getRefereeId () != 0 )
            throw new MessageException ("Invalid REFEREE id!", inMessage);
               else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                        throw new MessageException ("Invalid REFEREE state!", inMessage);
            break;
         case MessageType.INFORMR: if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                  throw new MessageException ("Invalid coach id!", inMessage);
               else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                     throw new MessageException ("Invalid coach state!", inMessage);
            break;
         case MessageType.REVIEWN: if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
            throw new MessageException ("Invalid coach id!", inMessage);
               else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                     throw new MessageException ("Invalid coach state!", inMessage);
            break;
         case MessageType.GAMEWINNER:   if (inMessage.getRefereeId () != 0 )
            throw new MessageException ("Invalid REFEREE id!", inMessage);
               else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                        throw new MessageException ("Invalid REFEREE state!", inMessage);
            break;
         case MessageType.MATCHWINNER:   if (inMessage.getRefereeId () != 0 )
            throw new MessageException ("Invalid REFEREE id!", inMessage);
               else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                        throw new MessageException ("Invalid REFEREE state!", inMessage);
            break;
         case MessageType.WAITNGAME: if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
            throw new MessageException ("Invalid coach id!", inMessage);
               else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                     throw new MessageException ("Invalid coach state!", inMessage);
            break;
        default:                   throw new MessageException ("Invalid message type!", inMessage);
      }

     /* processing */

      switch (inMessage.getMsgType ())

      { case MessageType.ANNOUNCENGAME:    ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                        ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                       refereeSite.announceNewGame();
                                       outMessage = new Message (MessageType.MFINISHDONE,((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeID (),
                                       ((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeState ());
                                    break;
         case MessageType.CALLTRIAL: ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                    ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                       refereeSite.callTrial();
                                       outMessage = new Message (MessageType.CALLTRIALDONE,((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeID (),
                                       ((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeState ());
                                    break;
         case MessageType.INFORMR: ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                    ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                     refereeSite.informReferee();
                                    outMessage = new Message (MessageType.INFORMRDONE,
                                                               ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachID (),
                                                               ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachState ());
                                     break;
         case MessageType.REVIEWN: ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                    ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                     refereeSite.reviewNotes();
                                    outMessage = new Message (MessageType.REVIEWNDONE,
                                                               ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachID (),
                                                               ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachState ());
                                     break;
         case MessageType.GAMEWINNER: ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                       ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                       ((RefereeSiteClientProxy) Thread.currentThread ()).setRopePosition(inMessage.getRopePosition ());
                                       int gameWinner = refereeSite.getGameWinner(((RefereeSiteClientProxy) Thread.currentThread()).getRopePosition());
                                     outMessage = new Message (MessageType.GAMEWINNERDONE,((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeID (),
                                     ((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeState (), gameWinner);
                                  break;
         case MessageType.MATCHWINNER: ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                  ((RefereeSiteClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                  ((RefereeSiteClientProxy) Thread.currentThread ()).setResult(inMessage.getResult());
                                     refereeSite.getMatchWinner(((RefereeSiteClientProxy) Thread.currentThread()).getResult());
                                     outMessage = new Message (MessageType.MATCHWINNERDONE,((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeID (),
                                     ((RefereeSiteClientProxy) Thread.currentThread ()).getRefereeState ());
                                  break;
         case MessageType.WAITNGAME: ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                  ((RefereeSiteClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                   refereeSite.waitNewGame();
                                  outMessage = new Message (MessageType.WAITNGAMEDONE,
                                                             ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachID (),
                                                             ((RefereeSiteClientProxy) Thread.currentThread ()).getCoachState ());
                                   break;
      }

     return (outMessage);
   }
}
