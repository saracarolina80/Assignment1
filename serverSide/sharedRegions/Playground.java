package serverSide.sharedRegions;

import clientSide.entities.*;

import serverSide.entities.PlaygroundClientProxy;
import serverSide.main.SimulPar;

import java.util.HashMap;
import java.util.Map;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import clientSide.stubs.GeneralRepositoryStub;

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
    private final Condition matchFinishedCondition;

    private int startTrialCount = 0;
    private int assertTrialDecisionCount = 0;
    private int amDoneCount = 0;

    private int matchFinishedCount = 0;
    private boolean matchFinished = false; 
    private HashMap<Integer, Integer> contestantCount = new HashMap<>(Map.of(1, 0, 2, 0));
    private HashMap<Integer , PlaygroundClientProxy[]> playground_contestants = new HashMap<>();
    
 
    GeneralRepositoryStub reposStub;

     /**
   *  Reference to referee threads.
   */

    private final PlaygroundClientProxy [] ref;

    /**
     *  Reference to coach threads.
     */

    private final PlaygroundClientProxy [] coa;

    /**
     *  Reference to contestant threads.
     */

     private final PlaygroundClientProxy [][] cont;

    /**
     * Instantiation of the playground object.
     *  @param repos2 name of the logging file
     */
    public Playground(GeneralRepositoryStub reposStub) {
        lock = new ReentrantLock(true);
        contestantArrived = lock.newCondition();
        assertTrialDecision = lock.newCondition();
        startTrial = lock.newCondition();
        matchFinishedCondition = lock.newCondition();
        amDone = lock.newCondition();

        ref = new PlaygroundClientProxy[1];
        coa = new PlaygroundClientProxy[SimulPar.NUM_TEAMS+1];
        cont = new PlaygroundClientProxy[SimulPar.NUM_TEAMS+1][SimulPar.TEAM_SIZE+1];
       
        this.reposStub = reposStub;
    }

    /**
   *  Operation wait for contestants.
   *
   *
   *  It is called by the coach when he needs to wait for the contestants.
   *
   */
    public void waitContestants() {
        int coachID = ((PlaygroundClientProxy) Thread.currentThread()).getCoachID();        
        coa[coachID] = (PlaygroundClientProxy) Thread.currentThread();

        try {
            lock.lock();
            while (contestantCount.get(coachID) != 3) {        
                try {
                    contestantArrived.await();
                } catch (InterruptedException e) {
                }
            }
            contestantCount.put(coachID, 0);

        } finally {
            lock.unlock();
        }
    }

     /**
   *  Operation WATCH TRIAL.
   *  It is called by the coach when he wants to watch the trial (after referee starts it).
   *
   */
    public void watchTrial() {
        int coachID = ((PlaygroundClientProxy) Thread.currentThread()).getCoachID();        
        coa[coachID] = (PlaygroundClientProxy) Thread.currentThread();

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
     * 
     * 
     * Operation Assert the trial decision.
     * 
     * It is called by the referee to calculate which teams wins the trial.
     * @param ropePosition 
     * @return ropePosition
     * 
     */
    public int assertTrialDecision(int ropePosition) {
        int refereeID = ((PlaygroundClientProxy) Thread.currentThread()).getRefereeID();   
        
        amDoneCount = 0;

        int strongestTeam = -1;
        int maxTotalStrength = 0;

        // Iterate over each team and calculate its total strength
        for (Integer team : playground_contestants.keySet()) {
            PlaygroundClientProxy[] contestants = playground_contestants.get(team);
            int totalStrength = 0;
            for (PlaygroundClientProxy contestant : contestants) {
                totalStrength += contestant.getStrength();
            }

            
            if (totalStrength > maxTotalStrength) {
                maxTotalStrength = totalStrength;
                strongestTeam = team;
            }
        }

     
        int losingTeam = (strongestTeam == 1) ? 2 : 1;
        int losingStrength = 0;
        for (PlaygroundClientProxy contestant : playground_contestants.get(losingTeam)) {
            losingStrength += contestant.getStrength();
        }

        
        int pointDiff = Math.abs(maxTotalStrength - losingStrength);

     
        if (pointDiff == 0) {
           
            System.out.println("DRAW with a total of " + maxTotalStrength + " points for each team!");
        } else {
            // Determine the winner based on the total strength
            System.out.println("Team " + strongestTeam + " WON this trial with a total of " + maxTotalStrength + " points");
            System.out.println("The difference was: " + pointDiff);
        }


        // Update rope position based on which team wins
        if (pointDiff > 0) {
            if (strongestTeam == 2) {
                ropePosition++; // Team 2 wins, rope moves to the right
                System.out.println("THE ROPE moved 1 place to the right and its center is now at position "
                        + ropePosition);
            } else if (strongestTeam == 1) {
                ropePosition--; // Team 1 wins, rope moves to the left
                System.out.println("THE ROPE moved 1 place to the left and its center is now at position "
                        + ropePosition);
            }
        } else {
            System.out.println(refereeID
                    + "THE ROPE didn't move and the center is still at position "
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
   *  Operation Start trial.
   *
   *  Transition start trial in the life cycle of the Referee.
     *  (TEAMS_READY -> WAIT_FOR_TRIAL_CONCLUSION)
     *
   *  It is called by the referee when he wants to start the trial.
   *
   */
    public void startTrial() {
        int refereeID = ((PlaygroundClientProxy) Thread.currentThread()).getRefereeID();   

        try {
            lock.lock();
            System.out.println("-------- START OF THE TRIAL ---------");
            startTrialCount = 6;
            startTrial.signalAll();
        } finally {
            lock.unlock();
        }

        try {
            lock.lock();
            while (amDoneCount != 6) {
                try {
                    ref[refereeID] = (PlaygroundClientProxy) Thread.currentThread();
                    ref[refereeID].setRefereeState(RefereeStates.WAIT_FOR_TRIAL_CONCLUSION);
                    reposStub.setRefereeState(refereeID , ref[refereeID].getRefereeState());
                    amDone.await();
                    System.out.println("REFEREE received amDone");
                } catch (InterruptedException e) {
                }
            }
            System.out.println("..........TRIAL DECISION..........");
        } finally {
            lock.unlock();  
        }
    }

   /**
   *  Operation AM DONE.
   *  It is called by the contestants when he wants to finish pulling the rope.
   *
   *  STILL IN STATE : DO_YOUR_BEST
   */
    public void amDone() {
        int contestantID = ((PlaygroundClientProxy) Thread.currentThread()).getContestantID();  
        int teamID = ((PlaygroundClientProxy) Thread.currentThread()).getTeamID();   

        cont[teamID][contestantID] = (PlaygroundClientProxy) Thread.currentThread();


        try {
            lock.lock();
            System.out.println(cont[teamID][contestantID] + " is DONE! UFF");
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
            cont[teamID][contestantID].decrementStrength();
            
            System.out.println(cont[teamID][contestantID] + "'s new strength: " + cont[teamID][contestantID].getStrength());
            // Remove self from playground
            PlaygroundClientProxy[] listOfContestants = playground_contestants.get(teamID);
            int indexToRemove = -1;
            for (int i = 0; i < listOfContestants.length; i++) {
                if (listOfContestants[i].equals(cont[teamID][contestantID])) {
                    indexToRemove = i;
                    break;
                }
            }
            if (indexToRemove != -1) {
                PlaygroundClientProxy[] newList = new PlaygroundClientProxy[listOfContestants.length - 1];
                for (int i = 0, j = 0; i < listOfContestants.length; i++) {
                    if (i != indexToRemove) {
                        newList[j++] = listOfContestants[i];
                    }
                }
                playground_contestants.put(teamID, newList);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
   *  Operation pulling the rope.
   *  It is called by the contestants when he wants to pulling the rope.
   *
   * *  STILL IN STATE : DO_YOUR_BEST
   */
    public void pullTheRope() {
        int contestantID = ((PlaygroundClientProxy) Thread.currentThread()).getContestantID();  
        int teamID = ((PlaygroundClientProxy) Thread.currentThread()).getTeamID();   
        cont[teamID][contestantID] = (PlaygroundClientProxy) Thread.currentThread();



        System.out.println(cont[teamID][contestantID] +  " is pulling the rope with strength: " + cont[teamID][contestantID] .getStrength());
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
   *  Operation GET READY.
   * 
   * Transition getting ready in the life cycle of the Contestant.
     *  (STAND_IN_POSITION -> DO_YOUR_BEST)
     *
   *  It is called by the contestants when he wants to finish pulling the rope.
   *
   */
    public void getReady() {
        int contestantID = ((PlaygroundClientProxy) Thread.currentThread()).getContestantID();  
        int teamID = ((PlaygroundClientProxy) Thread.currentThread()).getTeamID();   

        cont[teamID][contestantID] = (PlaygroundClientProxy) Thread.currentThread();

        cont[teamID][contestantID].setContestantState(ContestantStates.DO_YOUR_BEST);
        reposStub.setContestantState(teamID, contestantID , cont[teamID][contestantID].getContestantState());
    }

    /**
     * Operation Follow coach's advice.
     * 
     * * Transition getting ready in the life cycle of the Contestant.
     *  (SEAT_AT_THE_BENCH -> STAND_IN_POSITION)
     *
   *  It is called by the contestants when needs to follows coach advice : go to playground.
   * 
     * Only runs for contestants chosen by the coach
     */
    public void followCoachAdvice() {
        int contestantID = ((PlaygroundClientProxy) Thread.currentThread()).getContestantID();  
        int teamID = ((PlaygroundClientProxy) Thread.currentThread()).getTeamID(); 

        cont[teamID][contestantID] = (PlaygroundClientProxy) Thread.currentThread();


        cont[teamID][contestantID].setContestantState(ContestantStates.STAND_IN_POSITION);
        reposStub.setContestantState(teamID, contestantID , cont[teamID][contestantID].getContestantState());

        try {
            lock.lock();
            System.out.println(cont[teamID][contestantID] + " is in playground");
            
            PlaygroundClientProxy[] listOfContestants = playground_contestants.get(teamID);
            if (listOfContestants == null || listOfContestants.length == 0) {
                playground_contestants.put(teamID , new PlaygroundClientProxy[] { cont[teamID][contestantID]});
            } else {
                PlaygroundClientProxy[] updatedList = new PlaygroundClientProxy[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = cont[teamID][contestantID];
                playground_contestants.put(teamID, updatedList);
            }
            contestantCount.put(teamID, contestantCount.get(teamID)+1);
            contestantArrived.signalAll();
          
            
        } finally {
            lock.unlock();
        }
        try {
            lock.lock();
            while(startTrialCount == 0){
                try{
                    startTrial.await();
                
                } catch (InterruptedException e){}
            }
            startTrialCount = startTrialCount - 1;
        } finally {
            lock.unlock();
        }
    }

    /*
     * Set if Match ended.
     * Call by referee
     */
    public void isMatchStillGoing(boolean ended) {
        try {
            lock.lock();
            matchFinished = ended;
            matchFinishedCount = 12;
            matchFinishedCondition.signalAll();
        } finally {
           lock.unlock(); 
        }
    }

     /*
     * Validate if the Match ended.
     * Call by coach and contestant
     */
    public boolean isMatchFinished() {
        try {
            lock.lock();
            while(matchFinishedCount == 0 ){
                try{
                    matchFinishedCondition.await();
                } catch (InterruptedException e){}
            }
            matchFinishedCount--;
        } finally {
            lock.unlock();
        }

        return matchFinished;
    }
}
