package sharedRegions;

import entities.Coach;
import entities.CoachStates;
import entities.Contestant;
import entities.ContestantStates;
import entities.Referee;
import entities.RefereeStates;
import genclass.TextFile;
import main.SimulPar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
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
    private final Condition contestant_intMatch;
    private final Condition coach_inMatch;

    private int startTrialCount = 0;
    private int assertTrialDecisionCount = 0;
    private int amDoneCount = 0;
    private int gameCount = 0;
    private int ContestantmatchFinishedCount= 0; 
    private int CoachmatchFinishedCount = 0;
    private boolean matchFinished = false; 
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

    GeneralRepos repos ;

    /**
     * Instantiation of the playground object.
     *  @param logFileName name of the logging file
     */
    public Playground(String logFileName) {
        lock = new ReentrantLock(true);
        contestantArrived = lock.newCondition();
        assertTrialDecision = lock.newCondition();
        startTrial = lock.newCondition();
        coach_inMatch = lock.newCondition();
        contestant_intMatch = lock.newCondition();
        amDone = lock.newCondition();
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
        repos = new GeneralRepos(logFileName);
        reportStatus();
    }

    /**
     * Write the current state to the logging file.
     */
    private void reportStatus() {
        TextFile log = new TextFile();
        if (!log.openForAppending(".", logFileName)) {
            System.out.println("Failed to open the file " + logFileName + " for appending content!");
            System.exit(1);
        }
        
        repos.reportStatus();
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
     * Wait for the contestants to be ready.
     * @param coach 
     */
    public void waitContestants(Coach coach) {
        String coachName = coach.getName();

        String[] parts = coachName.split(" ");
        int coachId = Integer.parseInt(parts[1]); 
        System.out.println("AHAHAHAHAHAH");
        try {
            lock.lock();
            while (contestantCount.get(coachId ) != 3) {        
                try {
                    contestantArrived.await();
                } catch (InterruptedException e) {
                }
            }
            contestantCount.put(coachId, 0);

        } finally {
            lock.unlock();
        }
        System.out.println("FOGOGOGOGO");
        setCoachState(CoachStates.ASSEMBLE_TEAM);
    }

    /**
     * Watch the trial.
     * @param coach 
     */
    public void watchTrial(Coach coach) {
        String coachName = coach.getName();
        System.out.println("WATCHING THE TRIAL");
        try {
            lock.lock();
            while(assertTrialDecisionCount == 0){
                try{
                    assertTrialDecision.await();
                } catch (InterruptedException e){}
            }
            assertTrialDecisionCount = assertTrialDecisionCount - 1;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Assert the trial decision.
     * @param referee 
     * @param ropePosition 
     * @return 
     */
    public int assertTrialDecision(Referee referee, int ropePosition) {
        String refereeName = referee.getName();
        amDoneCount = 0;

        int strongestTeam = -1;
        int maxTotalStrength = 0;

        // Iterate over each team and calculate its total strength
        for (Integer team : playground_contestants.keySet()) {
            Contestant[] contestants = playground_contestants.get(team);
            int totalStrength = 0;
            for (Contestant contestant : contestants) {
                totalStrength += contestant.getStrength();
            }

            
            if (totalStrength > maxTotalStrength) {
                maxTotalStrength = totalStrength;
                strongestTeam = team;
            }
        }

     
        int losingTeam = (strongestTeam == 1) ? 2 : 1;
        int losingStrength = 0;
        for (Contestant contestant : playground_contestants.get(losingTeam)) {
            losingStrength += contestant.getStrength();
        }

        
        int pointDiff = Math.abs(maxTotalStrength - losingStrength);

     
        if (pointDiff == 0) {
           
            System.out.println(refereeName + " found the result: It was a draw with a total of " + maxTotalStrength + " points for each team!");
        } else {
            // Determine the winner based on the total strength
            System.out.println(refereeName + " found the result: Team " + strongestTeam + " won this trial with a total of " + maxTotalStrength + " points; " + pointDiff + " more than the other team!");
        }

        // Update rope position based on which team wins
        if (pointDiff > 0) {
            if (strongestTeam == 2) {
                ropePosition++; // Team 2 wins, rope moves to the right
                System.out.println(refereeName
                        + " found the result: rope moved 1 point to the right and its center is now at position "
                        + ropePosition);
            } else if (strongestTeam == 1) {
                ropePosition--; // Team 1 wins, rope moves to the left
                System.out.println(refereeName
                        + " found the result: rope moved 1 point to the left and its center is now at position "
                        + ropePosition);
            }
        } else {
            System.out.println(refereeName
                    + " found the result: It was a draw. The rope does not move and its center is still at position "
                    + ropePosition);
        }

        // Signal assertTrialDecision
        try {
            lock.lock();
            assertTrialDecisionCount = 8;
            assertTrialDecision.signalAll();
        } finally {
            lock.unlock();
        }

        // Return the updated rope position
        return ropePosition;
    }

    /**
     * Start a trial.
     * @param referee 
     */
    public void startTrial(Referee referee) {
        String refereeName = referee.getName();

        try {
            lock.lock();
            System.out.println(refereeName + " is starting the trial");
            startTrialCount = 6;
            startTrial.signalAll();
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            while (amDoneCount != 6) {
                try {
                    referee.setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
                    amDone.await();
                    System.out.println(refereeName + " received amDone");
                } catch (InterruptedException e) {
                }
            }
            System.out.println(refereeName + " will proceed with the trial decision");
        } finally {
            lock.unlock();  
        }
    }

    /**
     * End of a trial.
     *
     * @param contestant contestant
     */
    public void amDone(Contestant contestant) {
        String contestantName = contestant.getName();

        String[] parts = contestantName.split(" ");
        char teamChar = parts[1].charAt(0); 
        int teamId = Character.getNumericValue(teamChar);

        // Signal amDone
        try {
            lock.lock();
            System.out.println(contestantName + " is signaling amDone");
            amDoneCount++;
            amDone.signalAll();
        } finally {
            lock.unlock();
        }

        // Wait for assertTrialDecision
        try {
            lock.lock();
            while (assertTrialDecisionCount == 0) {
                try {
                    assertTrialDecision.await();
                } catch (InterruptedException e) {
                }
            }
            assertTrialDecisionCount = assertTrialDecisionCount - 1;

            // After "Playing the game" decrement strength
            contestant.decrementStrength();
            System.out.println(contestantName + "'s new strength: " + contestant.getStrength());
            // Remove self from playground
            Contestant[] listOfContestants = playground_contestants.get(teamId);
            int indexToRemove = -1;
            for (int i = 0; i < listOfContestants.length; i++) {
                if (listOfContestants[i].equals(contestant)) {
                    indexToRemove = i;
                    break;
                }
            }
            if (indexToRemove != -1) {
                Contestant[] newList = new Contestant[listOfContestants.length - 1];
                for (int i = 0, j = 0; i < listOfContestants.length; i++) {
                    if (i != indexToRemove) {
                        newList[j++] = listOfContestants[i];
                    }
                }
                playground_contestants.put(teamId, newList);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Pull the rope.
     *
     * @param contestant contestant
     */
    public void pullTheRope(Contestant contestant) {
        String contestantName = contestant.getName();

        System.out.println(contestantName + " pulled the rope. Strength is: " + contestant.getStrength());
        // Wait random amount of time between 1 and 3 seconds
        Random random = new Random();
        int minWaitTime = 1000; // 1 second
        int maxWaitTime = 3000; // 3 seconds
        int randomWaitTime = random.nextInt(maxWaitTime - minWaitTime + 1) + minWaitTime;
        try {
            Thread.sleep(randomWaitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ready for a trial.
     *
     * @param contestant contestant
     */
    public void getReady(Contestant contestant) {
        String contestantName = contestant.getName();
        setContestantState(ContestantStates.DO_YOUR_BEST);
    }

    /**
     * Follow coach's advice.
     *
     * @param contestant contestant
     * Only runs for contestants chosen by the coach
     */
    public void followCoachAdvice(Contestant contestant) {
        String contestantName = contestant.getName();

        String[] parts = contestantName.split(" ");
        char teamChar = parts[1].charAt(0); 
        int teamId = Character.getNumericValue(teamChar);

        setContestantState(ContestantStates.STAND_IN_POSITION);
        try {
            lock.lock();
            System.out.println(contestantName + " arrived in the playground");
            
            Contestant[] listOfContestants = playground_contestants.get(teamId);
            if (listOfContestants == null || listOfContestants.length == 0) {
                playground_contestants.put(teamId , new Contestant[] { contestant});
            } else {
                Contestant[] updatedList = new Contestant[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = contestant;
                playground_contestants.put(teamId, updatedList);
            }
            contestantCount.put(teamId, contestantCount.get(teamId)+1);
            contestantArrived.signalAll();
            System.out.println(contestantName + " signaled arrival to coaches");
            
        } finally {
            lock.unlock();
        }
        try {
            lock.lock();
            while(startTrialCount == 0){
                try{
                    startTrial.await();
                    System.out.println(contestantName + " received start trial");
                } catch (InterruptedException e){}
            }
            startTrialCount = startTrialCount - 1;
        } finally {
            lock.unlock();
        }
    }

    public void signalMatchStatus(boolean ended) {
        try {
            lock.lock();
            System.out.println("Ref is signaling match finished: " + ended);
            matchFinished = ended;
            CoachmatchFinishedCount = 2;        
            ContestantmatchFinishedCount = 10;
            contestant_intMatch.signalAll();
            coach_inMatch.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean verifyIfInMatch(Coach coach) {
        String coachName = coach.getName();
        try {
            lock.lock();
            while (CoachmatchFinishedCount == 0) {
                try {
                    coach_inMatch.await();
                    System.out.println(coachName + " received match finished");
                } catch (InterruptedException e) {
                }
            }
            CoachmatchFinishedCount--;
        } finally {
            lock.unlock();
        }

        return matchFinished;
    }
    public boolean verifyIfInMatch(Contestant contestant) {
        String contestantName = contestant.getName();
        try {
            lock.lock();
            while (ContestantmatchFinishedCount == 0) {
                try {
                    contestant_intMatch.await();
                    System.out.println(contestantName + " received match finished");
                } catch (InterruptedException e) {
                }
            }
            ContestantmatchFinishedCount--;
        } finally {
            lock.unlock();
        }

        return matchFinished;
    }
}
