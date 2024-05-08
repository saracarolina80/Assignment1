package clientSide.entities;

/**
 *    referee cloning.
 *
 *      It specifies his own attributes.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public interface RefereeCloning
{
     /**
   *   Set referee id.
   *
   *     @param id referee id
   */

   public void setRefereeID (int id);

  /**
   *   Get referee id.
   *
   *     @return referee id
   */

   public int getRefereeID ();

  /**
   *   Set referee state.
   *
   *     @param state new referee state
   */

   public void setRefereeState (int state);

  /**
   *   Get referee state.
   *
   *     @return referee state
   */

   public int getRefereeState ();
}
