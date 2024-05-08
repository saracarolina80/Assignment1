package clientSide.main;

import clientSide.entities.*;
import clientSide.stubs.*;
import serverSide.main.*;
import serverSide.sharedRegions.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Client side of the Game of Rope (contestant).
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class ClientGameOfRopeContestant
{
  /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the contestantBench server
     *        args[1] - port number for listening to service requests
     *        args[2] - name of the platform where is located the playground server
     *        args[3] - port number for listening to service requests
     *        args[4] - name of the platform where is located the general repository server
     *        args[5] - port number for listening to service requests
     */
   public static void main (String [] args)
   {
      String contestantBenchServerHostName;                               // name of the platform where is located the contestant bench server
      int contestantBenchServerPortNumb = -1;                             // port number for listening to service requests

      String playgroundServerHostName;                               // name of the platform where is located the playground server
      int playgroundServerPortNumber = -1;                             // port number for listening to service requests

      String genReposServerHostName;                                 // name of the platform where is located the general repository server
      int genReposServerPortNumb = -1;                               // port number for listening to service requests
      
      Contestant[][] contestants = new Contestant[SimulPar.NUM_TEAMS][SimulPar.TEAM_SIZE]; // array of arrays of contestant threads
      
      ContestantsBenchStub benchStub;
      RefereeSiteStub refereeSiteStub;
      PlaygroundStub playgroundStub;
  
      GeneralRepositoryStub genReposStub;                                 // remote reference to the general repository


     /* getting problem runtime parameters */

      if (args.length != 6)
         { GenericIO.writelnString ("Wrong number of parameters!");
           System.exit (1);
         }
      
          // get contestantbench parameters 
          contestantBenchServerHostName = args[0];
        try
        { 
          contestantBenchServerPortNumb = Integer.parseInt (args[1]);
        }
        catch (NumberFormatException e)
        { 
            GenericIO.writelnString ("args[1] is not a number!");
            System.exit (1);
        }
        if ((contestantBenchServerPortNumb < 4000) || (contestantBenchServerPortNumb >= 65536))
        { 
            GenericIO.writelnString ("args[1] is not a valid port number!");
            System.exit (1);
        }
        // get playground parameters 
        playgroundServerHostName = args[2];
        try
        { 
          playgroundServerPortNumber = Integer.parseInt (args[3]);
        }
        catch (NumberFormatException e)
        { 
            GenericIO.writelnString ("args[3] is not a number!");
            System.exit (1);
        }
        if ((playgroundServerPortNumber < 4000) || (playgroundServerPortNumber >= 65536))
        { 
            GenericIO.writelnString ("args[3] is not a valid port number!");
            System.exit (1);
        }

      genReposServerHostName = args[4];
      try
      { genReposServerPortNumb = Integer.parseInt (args[5]);
      }
      catch (NumberFormatException e)
      { GenericIO.writelnString ("args[5] is not a number!");
        System.exit (1);
      }
      if ((genReposServerPortNumb < 4000) || (genReposServerPortNumb >= 65536))
         { GenericIO.writelnString ("args[5] is not a valid port number!");
           System.exit (1);
         }

     /* problem initialization */

      benchStub = new ContestantsBenchStub (contestantBenchServerHostName, contestantBenchServerPortNumb);
      playgroundStub = new PlaygroundStub (playgroundServerHostName, playgroundServerPortNumber);
      genReposStub = new GeneralRepositoryStub (genReposServerHostName, genReposServerPortNumb);
      genReposStub.initSimul("log.txt"); 

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
        for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
            contestants[i][j] = new Contestant("Cont_" + (i+1) + (j+1), (i+1), (j+1), playgroundStub, benchStub);

        }
    }

     /* start of the simulation */

     for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
      for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
        contestants[i][j].start ();
      }
    }

     /* waiting for the end of the simulation */

      GenericIO.writelnString ();
      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
        for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
       try
        { contestants[i][j].join ();
        }
        catch (InterruptedException e) {}
      }
    }
      GenericIO.writelnString ();
   }
}
