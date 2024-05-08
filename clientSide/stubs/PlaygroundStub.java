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
 *  Stub to the Playground
 *
 *    It instantiates a remote reference to the Playground
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class PlaygroundStub {

   
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
    public PlaygroundStub (String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }    


    /**
     * Wait for the contestants to be ready. 
     */
    public void waitContestants() {
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

        outMessage = new Message(MessageType.WAITCONT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        if(inMessage.getMsgType() != MessageType.WAITCONDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     * Watch the trial.
     */
    public void watchTrial() {
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
      
        outMessage = new Message(MessageType.WATCHTRIAL);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
      
        if(inMessage.getMsgType() != MessageType.WATCHTRIALDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
    
    /**
     * Assert the trial decision.
     * @param ropePosition 
     * @return 
     */
    public int assertTrialDecision(int ropePosition) {
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
      
        outMessage = new Message(MessageType.ASSERTTDEC);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
      
        if(inMessage.getMsgType() != MessageType.ASSERTTDECDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();

        return inMessage.getRefereeId();
    }

    /**
     * Start a trial.
     */
    public void startTrial() {
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

        outMessage = new Message(MessageType.STARTTRIAL);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        if(inMessage.getMsgType() != MessageType.STARTTRIALDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();
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


public boolean isMatchFinished() {
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

    outMessage = new Message(MessageType.MFINISH);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.MFINISHDONE){
        GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
        GenericIO.writelnString(inMessage.toString());
        System.exit(1);
    }
    com.close();
    return  inMessage.getMatchStillGoing();    
}



public void followCoachAdvice() {
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

        outMessage = new Message(MessageType.FCAD);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        if(inMessage.getMsgType() != MessageType.FCADDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }


public void getReady() {
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

    outMessage = new Message(MessageType.GETREADY);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.GETREADYDONE){
        GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
        GenericIO.writelnString(inMessage.toString());
        System.exit(1);
    }
    com.close();
}


public void pullTheRope() {
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

    outMessage = new Message(MessageType.PULLR);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.PULLRDONE){
        GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
        GenericIO.writelnString(inMessage.toString());
        System.exit(1);
    }
    com.close();
}

public void amDone() {
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

        outMessage = new Message(MessageType.AMDONE);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        if(inMessage.getMsgType() != MessageType.AMDONEDONE){
            GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }


public void isMatchStillGoing(boolean b) {
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

    outMessage = new Message(MessageType.MATCHGOING, b);
    com.writeObject(outMessage);
    inMessage = (Message) com.readObject();

    if(inMessage.getMsgType() != MessageType.MATCHGOINGDONE){
        GenericIO.writelnString("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
        GenericIO.writelnString(inMessage.toString());
        System.exit(1);
    }
    com.close();
}
}
