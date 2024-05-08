package clientSide.stubs;


import genclass.*;
import commInfra.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.Ref;
import java.util.Objects;


/**
 *  Stub to the Refereee Site.
 *
 *    It instantiates a remote reference to the Referee Site
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class RefereeSiteStub {

       
    /**
    *  Name of the platform where is located the general repository server.
    */
    private String serverHostName;

    /**
    *  Port number for listening to service requests.
    */
    private int serverPortNumb;

    /**
     *   Instantiation of a stub to the general repository.
     *
     *     @param serverHostName name of the platform where is located the general repository server
     *     @param serverPortNumb port number for listening to service requests
     */
    public RefereeSiteStub (String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }    
/**
   *  Operation end of work.
   *
   *   New operation.
   *
   */

   public void endOperation ()
   {
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())
      { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.ENDOP);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.EOPDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }

   /**
   *   Operation server shutdown.
   *
   *   New operation.
   */

   public void shutdown ()
   {
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())
      { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.SHUT);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.SHUTDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }
public void waitNewGame() {
    ClientCom com;                                                 // communication channel
    Message outMessage,                                            // outgoing message
            inMessage;                                             // incoming message

    com = new ClientCom (serverHostName, serverPortNumb);
    while (!com.open ())
    { try
    { Thread.sleep ((long) (1000));
    }
    catch (InterruptedException e) {}
    }

    outMessage = new Message(MessageType.WAITNGAME);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.WAITNGAMEDONE){
        GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
        GenericIO.writelnString(inMessage.toString());
        System.exit(1);
    }
    com.close();
}

public void reviewNotes() {
  ClientCom com;                                                 // communication channel
  Message outMessage,                                            // outgoing message
          inMessage;                                             // incoming message

  com = new ClientCom (serverHostName, serverPortNumb);
  while (!com.open ())
  { try
  { Thread.sleep ((long) (1000));
  }
  catch (InterruptedException e) {}
  }

  outMessage = new Message(MessageType.REVIEWN);
  com.writeObject(outMessage);
  inMessage = (Message) com.readObject();

  if(inMessage.getMsgType() != MessageType.REVIEWNDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
  }
  com.close();
}
public void informReferee() {
  ClientCom com;                                                 // communication channel
  Message outMessage,                                            // outgoing message
          inMessage;                                             // incoming message

  com = new ClientCom (serverHostName, serverPortNumb);
  while (!com.open ())
  { try
  { Thread.sleep ((long) (1000));
  }
  catch (InterruptedException e) {}
  }

  outMessage = new Message(MessageType.INFORMR);
  com.writeObject(outMessage);
  inMessage = (Message) com.readObject();

  if(inMessage.getMsgType() != MessageType.INFORMRDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
  }
  com.close();
}
public void announceNewGame() {
  ClientCom com;                                                 // communication channel
  Message outMessage,                                            // outgoing message
          inMessage;                                             // incoming message

  com = new ClientCom (serverHostName, serverPortNumb);
  while (!com.open ())
  { try
  { Thread.sleep ((long) (1000));
  }
  catch (InterruptedException e) {}
  }

  outMessage = new Message(MessageType.ANNOUNCENGAME);
  com.writeObject(outMessage);
  inMessage = (Message) com.readObject();

  if(inMessage.getMsgType() != MessageType.ANNOUNCENGAMEDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
  }
  com.close();
}
public void callTrial() {
  ClientCom com;                                                 // communication channel
  Message outMessage,                                            // outgoing message
          inMessage;                                             // incoming message

  com = new ClientCom (serverHostName, serverPortNumb);
  while (!com.open ())
  { try
  { Thread.sleep ((long) (1000));
  }
  catch (InterruptedException e) {}
  }

  outMessage = new Message(MessageType.CALLTRIAL);
  com.writeObject(outMessage);
  inMessage = (Message) com.readObject();

  if(inMessage.getMsgType() != MessageType.CALLTRIALDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
  }
  com.close();
}
public int getGameWinner(int ropePosition) {
  ClientCom com;                                                 // communication channel
  Message outMessage,                                            // outgoing message
          inMessage;                                             // incoming message

  com = new ClientCom (serverHostName, serverPortNumb);
  while (!com.open ())
  { try
  { Thread.sleep ((long) (1000));
  }
  catch (InterruptedException e) {}
  }

  outMessage = new Message(MessageType.GAMEWINNER);
  com.writeObject(outMessage);
  inMessage = (Message) com.readObject();

  if(inMessage.getMsgType() != MessageType.GAMEWINNERDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
  }
  com.close();

  return inMessage.getRefereeId();
  
}
}
