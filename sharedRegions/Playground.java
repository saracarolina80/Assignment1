package sharedRegions;

import entities.Coach;
import entities.Contestant;
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
    private int startTrialCount = 0;
    private int assertTrialDecisionCount = 0;
    private int amDoneCount = 0;
    private HashMap<Integer, Integer> contestantCount = new HashMap<>(Map.of(1, 0, 2, 0));
    private HashMap<Integer , Contestant[]> playground_contestants = new HashMap<>();

    /**
     * Instantiation of the playground object.
     */
    public Playground() {
        lock = new ReentrantLock(true);
        reportStatus();
    }

    /**
     * Write the current state to the logging file.
     */
    private void reportStatus() {
        // Implementation omitted for brevity
    }

    /**
     * Wait for the athletes to be ready.
     * @param coach 
     */
    public void waitAthletes(Coach coach) {
        int id = (int)coach.getId(); // Assuming coaches are threads

        try {
            lock.lock();
            while (contestantCount .get(id) != 3) {         // 3 jogadores para o trial
                try {
                    athleteArrived.await();
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
        int id = Thread.currentThread().getId(); // Assuming coaches are threads

        try {
            lock.lock();
            while (assertTrialDecisionCount == 0) {
                try {
                    assertTrialDecision.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            assertTrialDecisionCount--;
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
