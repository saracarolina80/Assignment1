package serverSide.sharedRegions;

import serverSide.main.*;
import clientSide.entities.*;
import commInfra.*;

/**
 *  Interface to the General Repository of Information.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    General Repository and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class GeneralReposInterface
{
  /**
   *  Reference to the general repository.
   */

   private final GeneralRepos repos;

  /**
   *  Instantiation of an interface to the general repository.
   *
   *    @param repos reference to the general repository
   */

   public GeneralReposInterface (GeneralRepos repos)
   {
      this.repos = repos;
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
      Message outMessage = null;                                     // mensagem de resposta

     /* validation of the incoming message */

      switch (inMessage.getMsgType ())
      { case MessageType.LOGFN:  if (inMessage.getLogFName () == null)
                                      throw new MessageException ("Name of the logging file is not present!", inMessage);
                                    
                                   break;
        case MessageType.SETRST:    if (inMessage.getRefereeId () != 0 )
                                      throw new MessageException ("Invalid REFEREE id!", inMessage);
                                      else if ((inMessage.getRefereeState() != RefereeStates.START_OF_THE_MATCH) && (inMessage.getRefereeState () != RefereeStates.END_OF_THE_MATCH))
                                              throw new MessageException ("Invalid REFEREE state!", inMessage);
                                   break;
        case MessageType.SETCONST:    if ((inMessage.getContestantId () < 0) || (inMessage.getContestantId () >= SimulPar.TEAM_SIZE))
                                      throw new MessageException ("Invalid contestant id!", inMessage);
                                      else if ((inMessage.getContestantState () < ContestantStates.SEAT_AT_THE_BENCH) || (inMessage.getContestantState () > ContestantStates.END_OF_THE_MATCH))
                                              throw new MessageException ("Invalid contestant state!", inMessage);
                                   break;
        case MessageType.SETCST:   if ((inMessage.getCoachId () < 0) || (inMessage.getCoachId () >= SimulPar.NUM_TEAMS))
                                      throw new MessageException ("Invalid coach id!", inMessage);
                                      else if ((inMessage.getCoachState () != CoachStates.WAIT_FOR_REFEREE_COMMAND) && (inMessage.getCoachState () != CoachStates.END_OF_THE_MATCH))
                                              throw new MessageException ("Invalid coach state!", inMessage);
                                   break;
        default:                   throw new MessageException ("Invalid message type!", inMessage);
      }

     /* processing */

      switch (inMessage.getMsgType ())

      { case MessageType.LOGFN:  repos.initSimul (inMessage.getLogFName ());
                                   outMessage = new Message (MessageType.LOGFNDONE);
                                   break;
        case MessageType.SETRST:    repos.setRefereeState(inMessage.getRefereeId(), inMessage.getRefereeState ());
                                   outMessage = new Message (MessageType.SACK);
                                   break;
        case MessageType.SETCONST:    repos.setContestantState(inMessage.getTeamID(),inMessage.getContestantId(),inMessage.getContestantState());
                                   outMessage = new Message (MessageType.SACK);
                                   break;
        case MessageType.SETCST:   repos.setContestantState(inMessage.getTeamID(), inMessage.getCoachId (),
                                                                 inMessage.getCoachState ());
                                   outMessage = new Message (MessageType.SACK);

                                   break;
      }

     return (outMessage);
   }
}
