package sharedRegions;

import entities.Contestant;
import entities.ContestantStates;
import genclass.TextFile;
import entities.Coach;
import entities.CoachStates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ContestantsBench {

    private final ReentrantLock lock;
    private final Condition callContestants;
    private final HashMap<Integer, Contestant[]> benchContestants = new HashMap<>();
    private final HashMap<Integer, Contestant[]> chosenContestants = new HashMap<>();

    /**
     * Name of the logging file.
     */
    private final String logFileName;

    /**
     * State of the referee.
     */
    private int refereeState;

    /**
     * State of the coach.
     */
    private int coachState;

    /**
     * State of the contestant.
     */
    private int contState;

    public ContestantsBench(String logFileName) {
        lock = new ReentrantLock(true);
        callContestants = lock.newCondition();
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        reportStatus();
    }

    /**
     * Write the current state to the logging file.
     */
    private void reportStatus() {
        TextFile log = new TextFile();
        if (!log.openForAppending(".", logFileName)) {
            System.out.println("Failed to open for appending the file " + logFileName + "!");
            System.exit(1);
        }
        log.writelnString("Referee State: " + refereeState);
        log.writelnString("Coach State: " + coachState);
        log.writelnString("Contestant State: " + contState);
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
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
     * @param Cstate coach state
     */
    public synchronized void setCoachState(int Cstate) {
        coachState = Cstate;
        reportStatus();
    }


    /**
     * Set contestant state.
     *
     * @param state contestant state
     */
    public synchronized void setContestantState(int state) {
        contState = state;
        reportStatus();
    }


    public void callContestants(Coach coach) {
        int id = (int) coach.getId();
        int chooseMode = coach.getChooseMode();

        try {
            lock.lock();
            System.out.println("COACH " + id + " is choosing the team");
            Contestant[] listOfContestants = benchContestants.get(id);
            if (chooseMode == 1) {
                // Choose the top 3 strengths
                System.out.println("COACH " + id + " choose mode STRENGTH");
                Arrays.sort(listOfContestants, (c1, c2) -> Integer.compare(c2.getStrength(), c1.getStrength()));
            } else {
                // Choose Random
                System.out.println("COACH " + id + " choose mode RANDOM");
                Collections.shuffle(Arrays.asList(listOfContestants));
            }
            Contestant[] chosen = Arrays.copyOfRange(listOfContestants, 0, 3);
            chosenContestants.put(id, chosen);

            // Remove chosen players from benchPlayers
            List<Contestant> remainingPlayers = new ArrayList<>();
            for (Contestant player : listOfContestants) {
                boolean chosenPlayer = false;
                for (Contestant chosenContestant : chosen) {
                    if (chosenContestant.equals(player)) {
                        chosenPlayer = true;
                        break;
                    }
                }
                if (!chosenPlayer) {
                    remainingPlayers.add(player);
                }
            }
            benchContestants.put(id, remainingPlayers.toArray(new Contestant[0]));

            // Update coach state to assemble team
            setCoachState(CoachStates.ASSEMBLE_TEAM);

            // Awake all contestants to check if they are chosen
            callContestants.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void sitDown(Contestant contestant) {
        int id = (int) contestant.getId();
        int teamId = id / 10;

        try {
            lock.lock();
            // Add contestant to the benchPlayers list
            Contestant[] listOfContestants = benchContestants.get(teamId);
            if (listOfContestants == null || listOfContestants.length == 0) {
                benchContestants.put(teamId, new Contestant[]{contestant});
            } else {
                Contestant[] updatedList = new Contestant[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = contestant;
                benchContestants.put(teamId, updatedList);
            }
            System.out.println("CONTESTANT " + id + " added themselves to the bench");
            
            // Update contestant state to reflect sitting at the bench
            setContestantState(ContestantStates.SEAT_AT_THE_BENCH);

            // Wait until they are called
            while (!isContestantInChosenPlayers(teamId, contestant, chosenContestants)) {
                try {
                    System.out.println("CONTESTANT " + id + " is waiting");
                    callContestants.await();
                    System.out.println("CONTESTANT " + id + " is awaken");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("CONTESTANT " + id + " is called to the team");

            // Remove from chosenPlayers
            Contestant[] chosenList = chosenContestants.get(teamId);
            int indexToRemove = -1;
            for (int i = 0; i < chosenList.length; i++) {
                if (chosenList[i].equals(contestant)) {
                    indexToRemove = i;
                    break;
                }
            }
            Contestant[] newArray = new Contestant[chosenList.length - 1];
            System.arraycopy(chosenList, 0, newArray, 0, indexToRemove);
            System.arraycopy(chosenList, indexToRemove + 1, newArray, indexToRemove, chosenList.length - indexToRemove - 1);
            chosenContestants.put(teamId, newArray);
        } finally {
            lock.unlock();
        }
    }


    private boolean isContestantInChosenPlayers(int teamId, Contestant contestant, HashMap<Integer, Contestant[]> chosenPlayers) {
        Contestant[] players = chosenPlayers.get(teamId);
        if (players != null) {
            for (Contestant player : players) {
                if (player.equals(contestant)) {
                    return true;
                }
            }
        }
        return false;
    }
}
