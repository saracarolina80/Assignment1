package sharedRegions;

import main.*;
import entities.*;
import genclass.GenericIO;
import genclass.TextFile;

import java.util.Objects;

/**
 * General Repository.
 *
 * It is responsible for keeping the visible internal state of the problem and providing means for it
 * to be printed in the logging file.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class GeneralRepos {
    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * Number of games played.
     */
    private int nGames;

    /**
     * Number of trials in each game.
     */
    private final int nTrials;

    /**
     * State of the referee.
     */
    private int refereeState;

    /**
     * State of the coaches.
     */
    private final int[] coachState;

    /**
     * State of the contestants.
     */
    private final int[] contState;

    /**
     * Instantiation of a general repository object.
     *
     * @param logFileName name of the logging file
     * @param nGames      number of games to be played
     * @param nTrials     number of trials in each game
     */
    public GeneralRepos(String logFileName, int nGames, int nTrials) {
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        this.nGames = nGames;
        this.nTrials = nTrials;
        coachState = new int [SimulPar.NUM_TEAMS];
        contState = new int[SimulPar.TEAM_SIZE * SimulPar.NUM_TEAMS];
        for (int i = 0; i < SimulPar.NUM_TEAMS; i++)
            coachState[i] = CoachStates.WAIT_FOR_REFEREE_COMMAND;
        for (int i = 0; i < SimulPar.TEAM_SIZE * SimulPar.NUM_TEAMS; i++)
            contState[i] = ContestantStates.SEAT_AT_THE_BENCH;
        reportInitialStatus();
    }

    /**
     * Set referee state.
     *
     * @param state referee state
     */
    public synchronized void setRefereeState(int state) {
        refereeState = state;
        reportStatus();
    }

    /**
     * Set coach state.
     *
     * @param id    coach id
     * @param state coach state
     */
    public synchronized void setCoachState(int id, int state) {
        coachState[id] = state;
        reportStatus();
    }

     /**
     * Set contestant state.
     *
     * @param id    contestant id
     * @param state contestant state
     */
    public synchronized void setContestantState(int id, int state) {
        contState[id] = state;
        reportStatus();
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
        log.writelnString("                Rope Game Problem");
        log.writelnString("\nNumber of games = " + nGames + ", Number of trials per game = " + nTrials + "\n");
        log.writelnString("  REFEREE           COACHES                          CONTESTANTS");
        log.writelnString("Stat |  St  Id  Id  St | St Id Id | St Id Id Id Id");
        if (!log.close ())
        { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
          System.exit (1);
        }
        reportStatus ();
    }

    /**
     * Write a state line at the end of the logging file.
     */
    private void reportStatus() {
        TextFile log = new TextFile();
        String lineStatus = "";
        if (!log.openForAppending(".", logFileName)) {
            GenericIO.writelnString("Failed to open for appending the file " + logFileName + "!");
            System.exit(1);
        }

        // Append referee state
        lineStatus += refereeState.toString();

        // Append coach states
        for (int i = 0; i < SimulPar.NUM_TEAMS; i++) {
            lineStatus += " | " + coachState[i].toString() + "  " + i;
        }

        // Append contestant states
        for (int i = 0; i < SimulPar.TEAM_SIZE * SimulPar.NUM_TEAMS; i++) {
            lineStatus += " | " + contState[i].toString() + "  " + i;
        }

        log.writelnString(lineStatus);
        if (!log.close()) {
            GenericIO.writelnString("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }
}
