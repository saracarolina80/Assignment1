package serverSide.sharedRegions;

import serverSide.main.*;
import clientSide.entities.*;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.Objects;

/**
 *  General Repository of information.
 *
 *    It is responsible to keep the visible internal state of the problem and to provide means for it
 *    to be printed in the logging file.
 *    It is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are no internal synchronization points.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class GeneralRepos {
    /**
     * Name of the logging file.
     */
    private String logFileName;


     /**
     *  State of the Referee
     */
    private int refereeState;


    /**
     *  State of the coach of team #
     */
    private int [] coachState;

    /**
     *  State of the contestant # (# - 1 .. 5) of team whose coach was listed to the immediate left
     */
    private int [][] contestantState;

    /**
     *  Strength of the contestant # (# - 1 .. 5) of team whose coach was listed to the immediate left
     */
    private int [][] contestant_strength; 


    /**
     * Instantiation of a general repository object.
     *
     * @param logFileName name of the logging file
     */
    public GeneralRepos() {
        
        this.logFileName = "logger";
        
        refereeState = 0;
        refereeState = RefereeStates.START_OF_THE_MATCH;           // COMEÇO DO JOGO
        
        coachState = new int[SimulPar.NUM_TEAMS+1];
        for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
            coachState[i] = CoachStates.WAIT_FOR_REFEREE_COMMAND;       // COACH INITIAL STATE
        //    System.out.println("State COACH: " + coachState[i]);
        }

        contestantState = new int[SimulPar.NUM_TEAMS+1][SimulPar.TEAM_SIZE+1];
        contestant_strength = new int[SimulPar.NUM_TEAMS][SimulPar.TEAM_SIZE];
        for (int j = 0; j < SimulPar.NUM_TEAMS; j++) {    
            for (int i = 0; i < SimulPar.TEAM_SIZE ; i++){
                contestantState[j][i] = ContestantStates.SEAT_AT_THE_BENCH;    //  CONTESTANT INITIAL STATE
                contestant_strength[j][i] = SimulPar.STRENGTH;
             //   System.out.println("State C: " + contestantState[j][i] + " Strength: " + contestant_strength[j][i]);
        }
    }
    }   

    /**
   *   Operation initialization of simulation.
   *
   *   New operation.
   *
   *     @param logFileName name of the logging file

   */

   public synchronized void initSimul (String logFileName)
   {
      if (!Objects.equals (logFileName, ""))
         this.logFileName = logFileName;
    
      reportInitialStatus ();
   }

  /**
   *   Operation server shutdown.
   *
   *   New operation.
   

   public synchronized void shutdown ()
   {
       nEntities += 1;
       if (nEntities >= SimulPar.E)
          ServerSleepingBarbersGeneralRepos.waitConnection = false;
   }
   */

    public void setCoachState (int id, int state)
   {
      coachState[id] = state;
      reportStatus ();
   }



   public void setContestantState (int teamId, int id, int state)
   {
      contestantState[teamId][id] = state;
      reportStatus ();
   }

   public void setRefereeState (int id, int state)
   {
      refereeState = state;
      reportStatus ();
   }


    /**
     * Write the header to the logging file.
     */
    private void reportInitialStatus() {
        TextFile log = new TextFile();
        if (!log.openForWriting(".", logFileName)) {
            GenericIO.writelnString("Failed to create the file " + logFileName + "!");
            System.exit(1);
        }
        log.writelnString("                Game of the Rope - Description of the internal state");
        log.writelnString("Ref     Coa 1   Cont 1    Cont 2    Cont 3   Cont 4    Cont 5    Coa 2    Cont 1   Cont 2   Cont 3   Cont 4   Cont 5           Trial");
        log.writelnString("Sta     Stat    Sta SG     Sta SG   Sta SG   Sta SG    Sta SG    Stat     Sta SG   Sta SG   Sta SG   Sta SG   Sta SG  3  2  1  . 1 2 3 NB PS");
        if (!log.close ())
        { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
          System.exit (1);
        }
        reportStatus ();
    }


    /**
   *  Write two state lines at the end of the logging file.
   *
   *  
   *  Internal operation.
   */
  public void reportStatus ()
  {
      TextFile log = new TextFile ();                      // instantiation of a text file handler

      String lineStatus1 = "";                              // state line 1 to be printed
      String lineStatus2 = "      ";                              // state line 2 to be printed

      if (!log.openForAppending (".", logFileName))
      { 
          GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
          System.exit (1);
      }

      /*******  lineStatus1 ********/

      switch(refereeState)
      {
          case RefereeStates.START_OF_THE_MATCH:      lineStatus1 +=  "ST_M   ";
                                                          break;
          case RefereeStates.START_OF_A_GAME:     lineStatus1 +=  "ST_G  ";
                                                          break;
          case RefereeStates.TEAMS_READY:      lineStatus1 +=  "T_R  ";
                                                          break;
          case RefereeStates.WAIT_FOR_TRIAL_CONCLUSION:     lineStatus1 +=  "WT_TC  ";
                                                          break;
          case RefereeStates.END_OF_A_GAME:   lineStatus1 +=  "E_G  ";
                                                          break;
          case RefereeStates.END_OF_THE_MATCH:   lineStatus1 +=  "E_M  ";
                                                          break;                                               
      }

      for (int i = 0; i < SimulPar.NUM_TEAMS; i++)
      {
          switch(coachState[i])
          {
              case CoachStates.WAIT_FOR_REFEREE_COMMAND:  lineStatus1 +=  " WRC ";
                                                              break;
              case CoachStates.ASSEMBLE_TEAM:    lineStatus1 +=  " ASS_T  ";
                                                              break;
              case CoachStates.WATCH_TRIAL:           lineStatus1 +=  " W_T ";
                                                              break;
              case CoachStates.END_OF_THE_MATCH:   lineStatus1 +=  " E_M ";
                                                              break;
          }
      }

      for (int i = 0; i < SimulPar.TEAM_SIZE; i++) {
        for (int j = 0; j < SimulPar.NUM_TEAMS; j++) {
            switch(contestantState[j][i]) {
                case ContestantStates.SEAT_AT_THE_BENCH:  
                    lineStatus1 +=  " S_B ";
                    break;
                case ContestantStates.STAND_IN_POSITION:    
                    lineStatus1 +=  " S_INP  ";
                    break;
                case ContestantStates.DO_YOUR_BEST:           
                    lineStatus1 +=  " DO_B ";
                    break;
                case ContestantStates.END_OF_THE_MATCH:   
                    lineStatus1 +=  " E_M";
                    break;
            }
        }
    }    
      log.writelnString (lineStatus1);
      if (!log.close ())
      { 
          GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
          System.exit (1);
      }
  }

    public void reportFinalStatus ()
    {
        TextFile log = new TextFile ();                  	// instantiation of a text file handler
        if (!log.openForAppending (".", logFileName))
		{ 
			GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
			System.exit (1);
		}

        // FALTA ALTERAR ISTO AQUI
        log.writelnString("\nGame # was won by team # by knock out in # trials. / by points. / was a draw. Match was won by team # (#-#). / was a draw.");

        if (!log.close ())
		{ 
			GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
			System.exit (1);
		}
    }


    /**
	 *   Write in the logging file the legend.
	 */
	public void reportLegend()
	{
		TextFile log = new TextFile ();                  	// instantiation of a text file handler
		if (!log.openForAppending (".", logFileName))
		{ 
			GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
			System.exit (1);
		}
		
		log.writelnString("\nLegend:");
        log.writelnString("Ref Sta    - state of the referee");
        log.writelnString("Coa # Stat - state of the coach of team # (# - 1 .. 2)");
        log.writelnString("Cont # Sta - state of the contestant # (# - 1 .. 5) of team whose coach was listed to the immediate left");
        log.writelnString("Cont # SG  - strength of the contestant # (# - 1 .. 5) of team whose coach was listed to the immediate left");
        log.writelnString("TRIAL - ?  - contestant identification at the position ? at the end of the rope for present trial (? - 1 .. 3)");
        log.writelnString("TRIAL - NB - trial number");
        log.writelnString("TRIAL - PS - position of the centre of the rope at the beginning of the trial");

		if (!log.close ())
		{ 
			GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
			System.exit (1);
		}
	}


}