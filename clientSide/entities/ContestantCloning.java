package clientSide.entities;

/**
 *    contestant cloning.
 *
 *      It specifies his own attributes.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public interface ContestantCloning
{
      
    /**
   *   Set contestant id.
   *
   *     @param id contestant id
   */

   public void setContestantId (int id);

  /**
   *   Get contestant id.
   *
   *     @return contestant id
   */

   public int getContestantID ();

   /**
   *   Get team id.
   *
   *     @return team id
   */

   public int getTeamID ();


  /**
   *   Set contestant state.
   *
   *     @param state new contestant state
   */

   public void setContestantState (int state);

  /**
   *   Get contestant state.
   *
   *     @return contestant state
   */

   public int getContestantState ();
}
