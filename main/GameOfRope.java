package main;

import entities.*;
import sharedRegions.*;
import genclass.GenericIO;
import genclass.FileOp;

/**
 *   Simulation of the Game of Rope problem.
 *   Dynamic solution based on self-reference.
 */

public class GameOfRope
{
  /**
   *    Main method.
   *
   *    @param args runtime arguments
   */

   public static void main (String [] args)
   {
      Referee referee;                                        // referee thread
      Coach[] coaches = new Coach[SimulPar.NUM_TEAMS];         // array of coach threads
      Contestant[][] contestants = new Contestant[SimulPar.NUM_TEAMS][SimulPar.TEAM_SIZE]; // array of arrays of contestant threads
      GeneralRepos repos;                                      // reference to the general repository
      String fileName;                                         // logging file name
      char opt;                                                // selected option
      boolean success;                                         // end of operation flag

     /* problem initialization */

      GenericIO.writelnString ("\n" + " --- Problem of the Game of Rope ---\n");
      do
      { GenericIO.writeString ("Logging file name? ");
        fileName = GenericIO.readlnString ();
        if (FileOp.exists (".", fileName))
           { do
             { GenericIO.writeString ("There is already a file with this name. Delete it (y - yes; n - no)? ");
               opt = GenericIO.readlnChar ();
             } while ((opt != 'y') && (opt != 'n'));
             if (opt == 'y')
                success = true;
                else success = false;
           }
           else success = true;
      } while (!success);

      repos = new GeneralRepos (fileName);
      referee = new Referee("Referee", repos);

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
          coaches[i] = new Coach("Coach " + (i+1), repos);
          for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
              contestants[i][j] = new Contestant("Contestant " + (i+1) + "-" + (j+1), i, repos);
          }
      }

     /* start of the simulation */
      referee.start ();
      System.out.println("Lauching Referee Thread\n");
      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
          coaches[i].start ();
          System.out.println("Lauching Coach " + coaches[i] + " Thread\n");
          for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
              contestants[i][j].start ();
              System.out.println("Lauching Contestant " + contestants[i][j] + " Thread\n");
          }
      }

     /* waiting for the end of the simulation */
      try {
          referee.join ();
      } catch (InterruptedException ignored) {}
      GenericIO.writelnString ("The referee has terminated.");

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
          try {
              coaches[i].join ();
          } catch (InterruptedException ignored) {}
          GenericIO.writelnString ("The coach " + (i+1) + " has terminated.");
          for (int j = 0; j < SimulPar.TEAM_SIZE; j++) {
              try {
                  contestants[i][j].join ();
              } catch (InterruptedException ignored) {}
              GenericIO.writelnString ("The contestant " + (i+1) + "-" + (j+1) + " has terminated.");
          }
      }
      
      GenericIO.writelnString ();
      System.out.println("--- End of Simulation --- \n");
    }
}
