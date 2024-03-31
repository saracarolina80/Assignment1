package sharedRegions;

import entities.Contestant;
import entities.ContestantStates;
import entities.Coach;
import entities.CoachStates;
import genclass.TextFile;
import main.SimulPar;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ContestantsBench {

    private final ReentrantLock lock;
    private final Condition callContestants;
    private final Condition allContestantSeated;
    public final HashMap<Integer, Contestant[]> benchContestants = new HashMap<>();
    public final HashMap<Integer, Contestant[]> chosenContestants = new HashMap<>();
    private final int[] callContestantsCount = {0, 0};

    private final String logFileName;
    private final GeneralRepos repos;


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
        allContestantSeated = lock.newCondition();
        this.logFileName = (logFileName == null || logFileName.equals("")) ? "logger" : logFileName;

        // Initialize benchContestants hashmap with empty lists for each team
        for (int i = 1; i <= SimulPar.NUM_TEAMS; i++) {
            benchContestants.put(i, new Contestant[0]);
        }
        repos = new GeneralRepos(logFileName);

        reportStatus();
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



    private void reportStatus() {
        TextFile log = new TextFile();
        if (!log.openForAppending(".", logFileName)) {
            System.out.println("Failed to open the file " + logFileName + " for appending!");
            System.exit(1);
        }
        repos.reportStatus();
        if (!log.close()) {
            System.out.println("Failed to close the file " + logFileName + "!");
            System.exit(1);
        }
    }

    public void callContestants(Coach coach) {
        String coachName = coach.getName();
        int coachId = Integer.parseInt(coachName.split(" ")[1]);
        Random random = new Random();
        int chooseMode = random.nextInt(2);

        try {
            lock.lock();
            while (benchContestants.get(coachId).length < 5) {
                try {
                    System.out.println(coachName + " is waiting for all contestants to be seated at bench.");
                    allContestantSeated.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("ALL CONTESTANTS of team " + coachId + " are seated!");
            System.out.println(coachName + " is now choosing the team.");

            Contestant[] listOfContestants = benchContestants.get(coachId);

            if (chooseMode == 1) {
                System.out.println(coachName + " chooses the team based on STRENGTH.");
                Arrays.sort(listOfContestants, Comparator.comparingInt(Contestant::getStrength).reversed());
            } else {
                System.out.println(coachName + " chooses the team randomly.");
                Collections.shuffle(Arrays.asList(listOfContestants));
            }

            Contestant[] chosen = Arrays.copyOfRange(listOfContestants, 0, 3);
            chosenContestants.put(coachId, chosen);

            List<Contestant> remainingContestants = new ArrayList<>();
            for (Contestant player : listOfContestants) {
                if (!Arrays.asList(chosen).contains(player)) {
                    remainingContestants.add(player);
                }
            }
            benchContestants.put(coachId, remainingContestants.toArray(new Contestant[0]));

            for (Contestant cont : benchContestants.get(coachId)) {
                System.out.println("cont in BENCH: " + cont.getName() + " ");
            }
            for (Contestant cont : chosenContestants.get(coachId)) {
                if (cont != null) {
                    System.out.println("cont Chosen: " + cont.getName() + " ");
                } else {
                    System.out.println("ERROR");
                }
            }
            callContestantsCount[coachId - 1] = SimulPar.TEAM_SIZE;
            callContestants.signalAll();

        } finally {
            lock.unlock();
        }
    }

    public void sitDown(Contestant contestant) {
        String contestantName = contestant.getName();
        String[] parts = contestantName.split(" ");
        char teamChar = parts[1].charAt(0); 
        int teamId = Character.getNumericValue(teamChar);

        setContestantState(ContestantStates.SEAT_AT_THE_BENCH);

        try {
            lock.lock();
            Contestant[] listOfContestants = benchContestants.get(teamId);
            if (listOfContestants == null || listOfContestants.length == 0) {
                benchContestants.put(teamId, new Contestant[]{contestant});
            } else {
                Contestant[] updatedList = new Contestant[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = contestant;
                benchContestants.put(teamId, updatedList);
            }
            System.out.println(contestantName + " takes a seat at the bench.");

            if (benchContestants.get(teamId).length == 5) {
                allContestantSeated.signalAll();
            }

            while (callContestantsCount[teamId - 1] == 0) {
                try {
                    callContestants.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            callContestantsCount[teamId - 1]--;

            if (isContestantInChosenPlayers(teamId, contestant)) {
                removeContestantFromChosenPlayers(teamId, contestant);
                contestant.setChosen(true);
            } else {
                contestant.incrementStrength();
                removeContestantFromBench(teamId, contestant);
                contestant.setChosen(false);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isContestantInChosenPlayers(int teamId, Contestant contestant) {
        Contestant[] contestants = chosenContestants.getOrDefault(teamId, new Contestant[0]);
        return Arrays.asList(contestants).contains(contestant);
    }

    private void removeContestantFromChosenPlayers(int teamId, Contestant contestant) {
        Contestant[] chosenList = chosenContestants.get(teamId);
        List<Contestant> newList = new ArrayList<>(Arrays.asList(chosenList));
        newList.remove(contestant);
        chosenContestants.put(teamId, newList.toArray(new Contestant[0]));
    }

    private void removeContestantFromBench(int teamId, Contestant contestant) {
        Contestant[] benchList = benchContestants.get(teamId);
        List<Contestant> newList = new ArrayList<>(Arrays.asList(benchList));
        newList.remove(contestant);
        benchContestants.put(teamId, newList.toArray(new Contestant[0]));
    }
}
