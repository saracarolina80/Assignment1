package serverSide.sharedRegions;

import serverSide.main.SimulPar;
import serverSide.entities.*;
import clientSide.entities.*;
import commInfra.*;


/**
 *  Interface to the Playground.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    Playground and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class PlaygroundInterface {


    /**
   *  Reference to the Playground
   */

   private final Playground playground;

   /**
   *  Instantiation of an interface to the Playground
   *
   *    @param playground reference to the Playground
   */

   public PlaygroundInterface (Playground layground)
   {
      this.playground = layground;
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
      { case MessageType.MATCHGOING:  if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                       throw new MessageException ("Invalid contestant id!", inMessage);
                                      else if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                      throw new MessageException ("Invalid coach id!", inMessage);
                                   break;
        case MessageType.MFINISH:   if (inMessage.getRefereeId () != 0 )
                                       throw new MessageException ("Invalid REFEREE id!", inMessage);
                                    else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                                              throw new MessageException ("Invalid REFEREE state!", inMessage);
                                   break;
        case MessageType.WAITCONT: if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                       throw new MessageException ("Invalid coach id!", inMessage);
                                    else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                                          throw new MessageException ("Invalid coach state!", inMessage);
                                   break;
        case MessageType.WATCHTRIAL:  if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                          throw new MessageException ("Invalid coach id!", inMessage);
                                       else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                                             throw new MessageException ("Invalid coach state!", inMessage);
                                   break;
        case MessageType.ASSERTTDEC:   if (inMessage.getRefereeId () != 0 )
                                          throw new MessageException ("Invalid REFEREE id!", inMessage);
                                       else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                                          throw new MessageException ("Invalid REFEREE state!", inMessage);
                                   break;
        case MessageType.STARTTRIAL:  if (inMessage.getRefereeId () != 0 )
                                          throw new MessageException ("Invalid REFEREE id!", inMessage);
                                       else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                                          throw new MessageException ("Invalid REFEREE state!", inMessage);
                                    break;

         case MessageType.AMDONE:  if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                    throw new MessageException ("Invalid contestant id!", inMessage);
                                   else if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                   throw new MessageException ("Invalid coach id!", inMessage);
                                break;
         case MessageType.PULLR:  if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                    throw new MessageException ("Invalid contestant id!", inMessage);
                                   else if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                   throw new MessageException ("Invalid coach id!", inMessage);
                                break;
         case MessageType.GETREADY:  if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                    throw new MessageException ("Invalid contestant id!", inMessage);
                                   else if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                   throw new MessageException ("Invalid coach id!", inMessage);
                                break;
         case MessageType.FCAD:  
                              if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                throw new MessageException ("Invalid contestant id!", inMessage);
                               else if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                               throw new MessageException ("Invalid coach id!", inMessage);
                            break;
        default:                   throw new MessageException ("Invalid message type!", inMessage);
      }

     /* processing */

      switch (inMessage.getMsgType ())

      { case MessageType.MATCHGOING:  
                                   playground.isMatchStillGoing(inMessage.getMatchStillGoing());
                                      outMessage = new Message (MessageType.MATCHGOINGDONE);
                                                               
                                   break;
        case MessageType.MFINISH:    ((PlaygroundClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                   
                                    boolean finish = playground.isMatchFinished();
                                    outMessage = new Message (MessageType.MFINISHDONE,finish);
                                   break;
        case MessageType.WAITCONT: ((PlaygroundClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                   playground.waitContestants();
                                   outMessage = new Message (MessageType.WAITCONDONE,
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getCoachID (),
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getCoachState ());
                                   break;
        case MessageType.WATCHTRIAL: ((PlaygroundClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                   playground.watchTrial();
                                   outMessage = new Message (MessageType.WATCHTRIALDONE,
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getCoachID (),
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getCoachState ());
                                   break;
        case MessageType.ASSERTTDEC: ((PlaygroundClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setRopePosition(inMessage.getRopePosition ());
                                   int ropePosition = playground.assertTrialDecision(((PlaygroundClientProxy) Thread.currentThread()).getRopePosition());
                                   
                                   outMessage = new Message (MessageType.ASSERTTDECDONE,
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getRefereeID (),
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getRefereeState(), ropePosition);
                                   break;
        case MessageType.STARTTRIAL: ((PlaygroundClientProxy) Thread.currentThread ()).setRefereeID(inMessage.getRefereeId ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setRefereeState(inMessage.getRefereeState ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setRopePosition(inMessage.getRopePosition ());
                                   playground.startTrial();
                                   
                                   outMessage = new Message (MessageType.STARTTRIALDONE,
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getRefereeID (),
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getRefereeState());
                                   break;
        case MessageType.AMDONE: ((PlaygroundClientProxy) Thread.currentThread ()).setContestantId(inMessage.getContestantId ());
                                   ((PlaygroundClientProxy) Thread.currentThread ()).setContestantState(inMessage.getContestantState ());
                                   ((ContestantsBenchClientProxy) Thread.currentThread ()).setStrength(inMessage.getStrength ());
                                   playground.amDone();
                                   outMessage = new Message (MessageType.AMDONEDONE,
                                                             ((PlaygroundClientProxy) Thread.currentThread ()).getContestantID (),
                                        ((PlaygroundClientProxy) Thread.currentThread ()).getContestantState ());
                                    break;
        case MessageType.PULLR: ((PlaygroundClientProxy) Thread.currentThread ()).setContestantId(inMessage.getContestantId ());
                                 ((PlaygroundClientProxy) Thread.currentThread ()).setContestantState(inMessage.getContestantState ());
                                 ((ContestantsBenchClientProxy) Thread.currentThread ()).setStrength(inMessage.getStrength ());
                                 playground.pullTheRope();
                                 outMessage = new Message (MessageType.PULLRDONE,
                                                         ((PlaygroundClientProxy) Thread.currentThread ()).getContestantID (),
                                                                                       ((PlaygroundClientProxy) Thread.currentThread ()).getContestantState ());
                                    break;
        case MessageType.GETREADY: ((PlaygroundClientProxy) Thread.currentThread ()).setContestantId(inMessage.getContestantId ());
                                    ((PlaygroundClientProxy) Thread.currentThread ()).setContestantState(inMessage.getContestantState ());
                                    ((ContestantsBenchClientProxy) Thread.currentThread ()).setStrength(inMessage.getStrength ());
                                    playground.getReady();
                                    outMessage = new Message (MessageType.GETREADYDONE,
                                                            ((PlaygroundClientProxy) Thread.currentThread ()).getContestantID (),
                                                            ((PlaygroundClientProxy) Thread.currentThread ()).getContestantState ());
                                    break;                  
        case MessageType.FCAD: ((PlaygroundClientProxy) Thread.currentThread ()).setContestantId(inMessage.getContestantId ());
                                    ((PlaygroundClientProxy) Thread.currentThread ()).setContestantState(inMessage.getContestantState ());
                                    ((ContestantsBenchClientProxy) Thread.currentThread ()).setStrength(inMessage.getStrength ());
                                    playground.followCoachAdvice();
                                    outMessage = new Message (MessageType.FCADDONE,
                                                            ((PlaygroundClientProxy) Thread.currentThread ()).getContestantID (),
                                                            ((PlaygroundClientProxy) Thread.currentThread ()).getContestantState ());
                                    break;  
                                 
      }


     return (outMessage);
   }
}
