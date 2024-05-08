package clientSide.main;

import clientSide.entities.*;
import clientSide.stubs.*;
import serverSide.main.*;
import serverSide.sharedRegions.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Client side of the Game of Rope (coach).
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class ClientGameOfRopeCoach
{
  /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the contestantBench server
     *        args[1] - port number for listening to service requests
     *        args[2] - name of the platform where is located the refereeSite server
     *        args[3] - port number for listening to service requests
     *        args[4] - name of the platform where is located the playground server
     *        args[5] - port number for listening to service requests
     *        args[6] - name of the platform where is located the general repository server
     *        args[7] - port number for listening to service requests
     */
   public static void main (String [] args)
   {
      String contestantBenchServerHostName;                               // name of the platform where is located the contestant bench server
      int contestantBenchServerPortNumb = -1;                             // port number for listening to service requests

      String playgroundServerHostName;                               // name of the platform where is located the playground server
      int playgroundServerPortNumber = -1;                             // port number for listening to service requests

      String refereeSiteServerHostName;                               // name of the platform where is located the refereeSite server
      int refereeSiteServerPortNumb = -1;                             // port number for listening to service requests

      String genReposServerHostName;                                 // name of the platform where is located the general repository server
      int genReposServerPortNumb = -1;                               // port number for listening to service requests
      
      Coach [] coach = new Coach[SimulPar.NUM_TEAMS];

      
      ContestantsBenchStub benchStub;
      RefereeSiteStub refereeSiteStub;
      PlaygroundStub playgroundStub;
  
      GeneralRepositoryStub genReposStub;                                 // remote reference to the general repository


     /* getting problem runtime parameters */

      if (args.length != 8)
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

        // get refereeSite  parameters 
        refereeSiteServerHostName = args[2];
        try
        { 
          refereeSiteServerPortNumb = Integer.parseInt (args[3]);
        }
        catch (NumberFormatException e)
        { 
            GenericIO.writelnString ("args[3] is not a number!");
            System.exit (1);
        }
        if ((refereeSiteServerPortNumb < 4000) || (refereeSiteServerPortNumb >= 65536))
        { 
            GenericIO.writelnString ("args[3] is not a valid port number!");
            System.exit (1);
        }

        // get playground parameters 
        playgroundServerHostName = args[4];
        try
        { 
          playgroundServerPortNumber = Integer.parseInt (args[5]);
        }
        catch (NumberFormatException e)
        { 
            GenericIO.writelnString ("args[5] is not a number!");
            System.exit (1);
        }
        if ((playgroundServerPortNumber < 4000) || (playgroundServerPortNumber >= 65536))
        { 
            GenericIO.writelnString ("args[5] is not a valid port number!");
            System.exit (1);
        }

      genReposServerHostName = args[6];
      try
      { genReposServerPortNumb = Integer.parseInt (args[7]);
      }
      catch (NumberFormatException e)
      { GenericIO.writelnString ("args[7] is not a number!");
        System.exit (1);
      }
      if ((genReposServerPortNumb < 4000) || (genReposServerPortNumb >= 65536))
         { GenericIO.writelnString ("args[7] is not a valid port number!");
           System.exit (1);
         }

     /* problem initialization */

      benchStub = new ContestantsBenchStub (contestantBenchServerHostName, contestantBenchServerPortNumb);
      refereeSiteStub = new RefereeSiteStub (refereeSiteServerHostName, refereeSiteServerPortNumb);
      playgroundStub = new PlaygroundStub (playgroundServerHostName, playgroundServerPortNumber);
      genReposStub = new GeneralRepositoryStub (genReposServerHostName, genReposServerPortNumb);
      genReposStub.initSimul("log.txt"); 

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
        coach[i] = new Coach("Coa_" + (i+1), (i+1) , benchStub, refereeSiteStub , playgroundStub);
       
      }

     /* start of the simulation */

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++){
        coach[i].start ();
      }

     /* waiting for the end of the simulation */

      GenericIO.writelnString ();
      for (int i = 0; i < SimulPar.NUM_TEAMS; i++)
      { try
        { coach[i].join ();
        }
        catch (InterruptedException e) {}
      }
      GenericIO.writelnString ();
   }
}
