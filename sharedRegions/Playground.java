package sharedRegions;

import entities.Coach;
import entities.CoachStates;
import entities.Contestant;
import entities.ContestantStates;
import entities.Referee;
import genclass.TextFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Playground.
 *
 * It is responsible for handling playground-related actions during the rope game.
 * It is implemented as an implicit monitor.
 * All public methods are executed in mutual exclusion.
 * There are no internal synchronization points.
 */
public class Playground {
    private final ReentrantLock lock;

    private final Condition contestantArrived;
    private final Condition assertTrialDecision;
    private final Condition startTrial;
    private final Condition amDone;

    private int startTrialCount = 0;
    private int assertTrialDecisionCount = 0;
    private int amDoneCount = 0;
    private HashMap<Integer, Integer> contestantCount = new HashMap<>(Map.of(1, 0, 2, 0));
    private HashMap<Integer , Contestant[]> playground_contestants = new HashMap<>();

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




    /**
     * Instantiation of the playground object.
     *  @param logFileName name of the logging file
     */
    public Playground(String logFileName) {
        lock = new ReentrantLock(true);
        contestantArrived = lock.newCondition();
        assertTrialDecision = lock.newCondition();
        startTrial = lock.newCondition();
        amDone = lock.newCondition();
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

    /**
     * Wait for the athletes to be ready.
     * @param coach 
     */
    public void waitAthletes(Coach coach) {
        int id = (int)coach.getId(); // Assuming coaches are threads

        try {
            lock.lock();
            while (contestantCount.get(id) != 3) {         // 3 jogadores para o trial
                try {
                    contestantArrived.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            contestantCount.put(id, 0);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Watch the trial.
     * @param coach 
     */
    public void watchTrial(Coach coach) {
        int id = (int) coach.getId();

        try {
            lock.lock();
            System.out.println("COACH " + id + " is gonna start watching trial");
            while(assertTrialDecisionCount == 0){
                try{
                    System.out.println("COACH " + id + " is waiting watching trial");
                    assertTrialDecision.await();
                    System.out.println("COACH " + id + " received trial decision");
                } catch (InterruptedException e){}
            }
            System.out.println("COACH " + id + " will proceed with trial decision");
            assertTrialDecisionCount = assertTrialDecisionCount - 1;
        } finally {
            lock.unlock();
        }
    }
    /**
     * Assert the trial decision.
     * @param referee 
     */
    public void assertTrialDecision(Referee referee) {
        // Implementation omitted for brevity
    }

    /**
     * Start a trial.
     * @param referee 
     */
    public void startTrial(Referee referee) {
        // Implementation omitted for brevity
    }

    /**
     * End of a trial.
     *
     * @param contestant contestant
     */
    public void amDone(Contestant contestant) {
        // Implementation omitted for brevity
    }

    /**
     * Pull the rope.
     *
     * @param contestant contestant
     */
    public void pullTheRope(Contestant contestant) {
        // Implementation omitted for brevity
    }

    /**
     * Get ready for a trial.
     *
     * @param contestant contestant
     */
    public void getReady(Contestant contestant) {
        // Implementation omitted for brevity
    }

    /**
     * Follow coach's advice.
     *
     * @param contestant contestant
     */
    public void followCoachAdvice(Contestant contestant) {
        int id = (int)contestant.getId();
        int teamId = id/10;

        try {
            lock.lock();
            System.out.println("ATHLETE " + id + " arrived in playground");
            // Add athlete to the benchPlayers list
            Contestant[] listOfContestants = playground_contestants.get(teamId);
            if (listOfContestants == null || listOfContestants.length == 0){
                playground_contestants.put(teamId , new Contestant[]{contestant});
            } else {
                Contestant[] updatedList = new Contestant[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = contestant;
                playground_contestants.put(teamId, updatedList);
            }
            contestantCount.put(teamId, contestantCount.get(teamId) + 1);
            System.out.println("ATHLETE " + id + " added themselves to the playground list.");
            contestantArrived.signalAll();
            System.out.println("ATHLETE " + id + " signaled arrival to coaches");
            setContestantState(ContestantStates.STAND_IN_POSITION);
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            System.out.println("ATHLETE " + id + " is gonna start waiting for trial to start");
            while(startTrialCount == 0){
                try{
                    System.out.println("ATHLETE " + id + " is waiting start trial");
                    startTrial.await();
                    System.out.println("ATHLETE " + id + " received start trial");
                } catch (InterruptedException e){}
            }
            System.out.println("ATHLETE " + id + " will proceed with start trial");
            startTrialCount = startTrialCount - 1;
        } finally {
            lock.unlock();
        }
    }
}
