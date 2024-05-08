package serverSide.sharedRegions;

import serverSide.entities.*;
import clientSide.entities.*;
import commInfra.*;


/**
 *  Interface to the Contestants Bench.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    Contestants Bench and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ContestantsBenchInterface {


    /**
   *  Reference to the Contestants bench
   */

   private final ContestantsBench contestantsBench;

   /**
   *  Instantiation of an interface to the contestant bench.
   *
   *    @param contestantsBench reference to the contestant bench
   */

   public ContestantsBenchInterface (ContestantsBench contestantsBench)
   {
      this.contestantsBench = contestantsBench;
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
            case MessageType.CALLCONT:   // callContestants()
                                    if ((inMessage.getCoachState() < CoachStates.WAIT_FOR_REFEREE_COMMAND) || (inMessage.getCoachState() > CoachStates.END_OF_THE_MATCH))
                                        throw new MessageException ("Invalid Coach state!", inMessage);
                                    break;
            case MessageType.SITDOWN:   // sitDown()
                                    if ((inMessage.getContestantState() < ContestantStates.SEAT_AT_THE_BENCH) || (inMessage.getContestantState() > ContestantStates.END_OF_THE_MATCH))
                                        throw new MessageException ("Invalid Contestant state!", inMessage);
                                    break;
            default:                throw new MessageException ("Invalid message type!", inMessage);
        }


     /* processing */

      switch (inMessage.getMsgType ())

      { case MessageType.CALLCONT:  ((ContestantsBenchClientProxy) Thread.currentThread ()).setCoachID(inMessage.getCoachId ());
                                   ((ContestantsBenchClientProxy) Thread.currentThread ()).setCoachState(inMessage.getCoachState ());
                                    contestantsBench.callContestants();
                                    outMessage = new Message (MessageType.CALLCONTDONE,
                                                            ((ContestantsBenchClientProxy) Thread.currentThread ()).getCoachID (),
                                                            ((ContestantsBenchClientProxy) Thread.currentThread ()).getCoachState ());
                                   break;
        case MessageType.SITDOWN:    ((ContestantsBenchClientProxy) Thread.currentThread ()).setContestantId(inMessage.getContestantId ());
                                        ((ContestantsBenchClientProxy) Thread.currentThread ()).setContestantState(inMessage.getContestantState ());    
                                    ((ContestantsBenchClientProxy) Thread.currentThread ()).setStrength(inMessage.getStrength ());
                                    contestantsBench.sitDown();
                                      outMessage = new Message (MessageType.SITDOWNDONE,
                                                                ((ContestantsBenchClientProxy) Thread.currentThread ()).getContestantID (), ((ContestantsBenchClientProxy) Thread.currentThread()).getWillPlay());
                                     
                                   break;
      }

     return (outMessage);
   }
}
