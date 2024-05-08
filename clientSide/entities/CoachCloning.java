package clientSide.entities;

/**
 *    coach cloning.
 *
 *      It specifies his own attributes.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public interface CoachCloning
{
      /**
   *   Set coach id.
   *
   *     @param id coach id
   */

   public void setCoachID (int id);

  /**
   *   Get coach id.
   *
   *     @return coach id
   */

   public int getCoachID ();

  /**
   *   Set coach state.
   *
   *     @param state new coach state
   */

   public void setCoachState (int state);

  /**
   *   Get coach state.
   *
   *     @return coach state
   */

   public int getCoachState ();

}
