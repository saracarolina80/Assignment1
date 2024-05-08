package clientSide.stubs;

import serverSide.main.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Stub to the Contestants Bench.
 *
 *    It instantiates a remote reference to the Contestants Bench.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ContestantsBenchStub {

   
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
    public ContestantsBenchStub (String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }    


/**
   *  Operation end of work.
   *
   *   New operation.
   *
   *      @param refereeID referee id
   */

   public void endOperation (int refereeID)
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
      outMessage = new Message (MessageType.ENDOP, refereeID);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.EOPDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getRefereeId() != refereeID)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid referee id!");
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


public void callContestants() {
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

   outMessage = new Message(MessageType.CALLCONT);
   com.writeObject(outMessage);
   inMessage = (Message) com.readObject();

   if(inMessage.getMsgType() != MessageType.CALLCONTDONE){
       GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
       GenericIO.writelnString(inMessage.toString());
       System.exit(1);
   }
   com.close();
}


public void sitDown() {
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

   outMessage = new Message(MessageType.SITDOWN);
   com.writeObject(outMessage);
   inMessage = (Message) com.readObject();

   if(inMessage.getMsgType() != MessageType.SITDOWNDONE){
      GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
      GenericIO.writelnString(inMessage.toString());
      System.exit(1);
   }
   com.close();
}
}
