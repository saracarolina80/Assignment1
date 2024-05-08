package commInfra;

import java.io.*;
import genclass.GenericIO;

/**
 *   Internal structure of the exchanged messages.
 *
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */

public class Message implements Serializable
{
  /**
   *  Serialization key.
   */

   private static final long serialVersionUID = 2021L;

  /**
   *  Message type.
   */

   private int msgType = -1;


   private boolean b = false;

   private int strength = 0;

   private int ropePosition = 0;

  /**
   *  coach identification.
   */

   private int coachID = -1;

   /**
   *  referee identification.
   */

   private int refereeID = -1;


   /**
   *  team identification.
   */

   private int teamID = -1;

     
  /**
   *  coach state.
   */

   private int coachState = -1;

  /**
   *  contestant identification.
   */

   private int contestantID = -1;

  /**
   *  contestant state.
   */

   private int contestantState = -1;

   /**
   *  referee state.
   */

   private int refereeState = -1;

  /**
   *  gameWinner.
   */

   private int gameWinner = -1;

  /**
   *  Name of the logging file.
   */

   private String fName = null;


     /**
   *  result.
   */

   private int result = -1;


  /**
   *  Message instantiation (form 1).
   *
   *     @param type message type
   */

   public Message (int type)
   {
      msgType = type;
   }

   public Message (int type, String filename)
   {
      msgType = type;
      fName = filename;
   }

  /**
   *  Message instantiation (form 2).
   *
   *     @param type message type
   *     @param id referee / coach / contestant identification
   *     @param state referee / coach / contestant state
   */

   public Message (int type, int id, int state)
   {
      msgType = type;
      if ((msgType == MessageType.AMDONE) || (msgType == MessageType.PULLR) || (msgType == MessageType.GETREADY)
       || (msgType == MessageType.FCAD))
         { contestantID= id;
           contestantState = state;
         }
         else if ((msgType == MessageType.CALLTRIAL) || (msgType == MessageType.INFORMR) || (msgType == MessageType.REVIEWN) ||
                  (msgType == MessageType.WAITNGAME) || (msgType == MessageType.WAITCONT) || (msgType == MessageType.WATCHTRIAL) || (msgType == MessageType.CALLCONT))
                 { coachID= id;
                   coachState = state;
                 }
         else if ((msgType == MessageType.ANNOUNCENGAME) || (msgType == MessageType.MATCHWINNER) || (msgType == MessageType.STARTTRIAL))
                { refereeID= id;
                  refereeState = state;
                }
                 else { GenericIO.writelnString ("Message type = " + msgType + ": non-implemented instantiation!");
                        System.exit (1);
                      }
   }


   public Message (int type, int id)
   {
      msgType = type;
      refereeID= id;
   }


   public Message(int type, int id,  boolean b) {
      msgType = type;
      contestantID = id;
      this.b = b;
   }

   public Message(int type, boolean b) {
      msgType = type;
      this.b = b;
   }

   public Message (int type, int id, int state, int p)
   {
      msgType = type;
      if ((msgType == MessageType.GAMEWINNER))
                { refereeID= id;
                  refereeState = state;
                  gameWinner = p;
                }
      else  if ((msgType == MessageType.ASSERTTDEC))
      { refereeID= id;
        refereeState = state;
        ropePosition = p;
      }
      else { GenericIO.writelnString ("Message type = " + msgType + ": non-implemented instantiation!");
            System.exit (1);
            }
   }
   

/**
   *  Getting message type.
   *
   *     @return message type
   */

   public int getMsgType ()
   {
      return (msgType);
   }

  /**
   *  Getting coach identification.
   *
   *     @return coach identification
   */

   public int getCoachId ()
   {
      return (coachID);
   }

   /**
   *  Getting team identification.
   *
   *     @return team identification
   */

   public int getTeamID()
   {
      return (teamID);
   }


  /**
   *  Getting referee state.
   *
   *     @return referee state
   */

   public int getRefereeState ()
   {
      return (refereeState);
   }

  /**
   *  Getting referee identification.
   *
   *     @return referee identification
   */

   public int getRefereeId ()
   {
      return (refereeID);
   }

  /**
   *  Getting coach state.
   *
   *     @return coach state
   */

   public int getCoachState ()
   {
      return (coachState);
   }


     /**
   *  Getting contestant identification.
   *
   *     @return contestant identification
   */

   public int getContestantId ()
   {
      return (contestantID);
   }

  /**
   *  Getting contestant state.
   *
   *     @return contestant state
   */

   public int getContestantState ()
   {
      return (contestantState);
   }
   

    /**
   *  Getting contestant strength.
   *
   *     @return contestant strength
   */

   public int getStrength ()
   {
      return (strength);
   }
   

    /**
   *  Getting is match still going.
   *
   *     @return match still going
   */

   public boolean getMatchStillGoing ()
   {
      return (b);
   }

    /**
   *  Getting rope position.
   *
   *     @return rope position
   */

   public int getRopePosition ()
   {
      return (ropePosition);
   }
   
  /**
   *  Getting name of logging file.
   *
   *     @return name of the logging file
   */

   public String getLogFName ()
   {
      return (fName);
   }


  /**
   *  Printing the values of the internal fields.
   *
   *  It is used for debugging purposes.
   *
   *     @return string containing, in separate lines, the pair field name - field value
   */

   @Override
   public String toString ()
   {
      return ("Message type = " + msgType +
              "\nReferee Id = " + refereeID +
              "\nReferee State = " + refereeState +
              "\nCoach Id = " + coachID +
              "\nCoach State = " + coachState +
              "\nContestant Id = " + contestantID +
              "\nContestant State = " + contestantState +
              "\nName of logging file = " + fName);
   }

   public void setResult (int res) {
      result = res;
   }
   public int getResult() {
      return result;
   }
}
