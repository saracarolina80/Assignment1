package serverSide.sharedRegions;

import serverSide.main.*;
import serverSide.entities.*;
import clientSide.entities.*;
import clientSide.stubs.*;


import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 *  Contestants Bench.
 *
 *    Is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ContestantsBench {

    private final ReentrantLock lock;
    private final Condition callContestants;
    private final Condition allContestantSeated;
    public final HashMap<Integer, ContestantsBenchClientProxy[]> benchContestants = new HashMap<>();
    public final HashMap<Integer, ContestantsBenchClientProxy[]> chosenContestants = new HashMap<>();
    private final int[] callContestantsCount = {0, 0};

    
     /**
     *   Reference to the stub of the general repository.
     */
    private final GeneralRepositoryStub reposStub;

    /**
     *  Reference to coach threads.
     */

    private final ContestantsBenchClientProxy [] coa;

    
    /**
     *  Reference to contestants threads.
     */

     private final ContestantsBenchClientProxy [][] cont;


    public ContestantsBench(GeneralRepositoryStub reposStub) {
        coa = new ContestantsBenchClientProxy[SimulPar.NUM_TEAMS+1];
        cont = new ContestantsBenchClientProxy[SimulPar.NUM_TEAMS+1][SimulPar.TEAM_SIZE+1];
        lock = new ReentrantLock(true);
        callContestants = lock.newCondition();
        allContestantSeated = lock.newCondition();
        
        this.reposStub = reposStub;
        // Initialize benchContestants hashmap with empty lists for each team
        for (int i = 1; i <= SimulPar.NUM_TEAMS; i++) {
            benchContestants.put(i, new ContestantsBenchClientProxy[0]);
        }

    }

     /**
   *  Operation call contestants.
   *
   *  Transition call contestants in the life cycle of the coach.
     *  (WAIT_FOR_REFEREE_COMMAND -> ASSEMBLE_TEAM)
   *  It is called by the coach when he wants to choose the contestants for the game.
   *
   */
    public void callContestants() {
        int coachID = ((ContestantsBenchClientProxy) Thread.currentThread()).getCoachID();        
        coa[coachID] = (ContestantsBenchClientProxy) Thread.currentThread();
        System.out.println(coa[coachID]);
        Random random = new Random();
        int chooseMode = random.nextInt(2);

        try {
            lock.lock();
            while (benchContestants.get(coachID).length < 5) {
                try {
                    System.out.println("Coach: " + coachID + " is waiting for all contestants to be seated at bench.");
                    allContestantSeated.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("ALL CONTESTANTS of team " + coachID + " are seated!");
            System.out.println("Coach " + coachID + " is now choosing the team.");

            coa[coachID].setCoachState(CoachStates.ASSEMBLE_TEAM);
            reposStub.setCoachState(coachID , coa[coachID].getCoachState());
            
            ContestantsBenchClientProxy[] listOfContestants = benchContestants.get(coachID);

            if (chooseMode == 1) {
                System.out.println("Coach: " + coachID + " chooses the team based on STRENGTH.");
                Arrays.sort(listOfContestants, Comparator.comparingInt(ContestantsBenchClientProxy::getStrength).reversed());
            } else {
                System.out.println(" Coach " + coachID + " chooses the team randomly.");
                Collections.shuffle(Arrays.asList(listOfContestants));
            }

            ContestantsBenchClientProxy[] chosen = Arrays.copyOfRange(listOfContestants, 0, 3);
            chosenContestants.put(coachID, chosen);

            List<ContestantsBenchClientProxy> remainingContestants = new ArrayList<>();
            for (ContestantsBenchClientProxy player : listOfContestants) {
                if (!Arrays.asList(chosen).contains(player)) {
                    remainingContestants.add(player);
                }
            }
            benchContestants.put(coachID, remainingContestants.toArray(new ContestantsBenchClientProxy[0]));

            for (ContestantsBenchClientProxy cont : benchContestants.get(coachID)) {
                System.out.println("contestant in BENCH: " + cont.getTeamID() + cont.getContestantID() + " ");
            }
            for (ContestantsBenchClientProxy cont : chosenContestants.get(coachID)) {
                if (cont != null) {
                   System.out.println("contestant Chosen: " + cont.getTeamID() + cont.getContestantID() + " ");
                } else {
                    System.out.println("ERROR");
                }
            }
            callContestantsCount[coachID-1] = SimulPar.TEAM_SIZE;
            callContestants.signalAll();

        } finally {
            lock.unlock();
        }
    }


     /**
   *  Operation sitdown.
   *
   *  Transition seat down in the life cycle of the contestant.
     *  INITIAL STATE -> SEAT_AT_THE_BENCH)
   *  It is called by the contestant when he wants to seat at bench.
   *
   */
    public void sitDown() {
        int contestantID = ((ContestantsBenchClientProxy) Thread.currentThread()).getContestantID();  
        int teamID = ((ContestantsBenchClientProxy) Thread.currentThread()).getTeamID();   
          
        cont[teamID][contestantID] = (ContestantsBenchClientProxy) Thread.currentThread();

        cont[teamID][contestantID].setContestantState(ContestantStates.SEAT_AT_THE_BENCH);
        reposStub.setContestantState(teamID,contestantID , cont[teamID][contestantID].getContestantID());

        try {
            lock.lock();
            ContestantsBenchClientProxy[] listOfContestants = benchContestants.get(teamID);
            if (listOfContestants == null || listOfContestants.length == 0) {
                benchContestants.put(teamID, new ContestantsBenchClientProxy[]{cont[teamID][contestantID]});
            } else {
                ContestantsBenchClientProxy[] updatedList = new ContestantsBenchClientProxy[listOfContestants.length + 1];
                System.arraycopy(listOfContestants, 0, updatedList, 0, listOfContestants.length);
                updatedList[listOfContestants.length] = cont[teamID][contestantID];
                benchContestants.put(teamID, updatedList);
            }
            System.out.println("Contestant " + cont[teamID][contestantID] + " takes a seat at the bench.");
         //   System.out.println("BENCH: " + benchContestants.get(teamID).length);
            if (benchContestants.get(teamID).length == 5) {
                allContestantSeated.signalAll();
            }

         //  System.out.println("count: " + callContestantsCount[teamID-1]);
            while (callContestantsCount[teamID-1] == 0) {
                try {
                    callContestants.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            callContestantsCount[teamID-1]--;
        //    System.out.println("count2: " + callContestantsCount[teamID-1]);

            if (isContestantInChosenPlayers(teamID, cont[teamID][contestantID] , chosenContestants)) {
                removeContestantFromChosenPlayers(teamID, cont[teamID][contestantID] , chosenContestants);
                cont[teamID][contestantID].setChosen(true);
            } else {
                cont[teamID][contestantID].incrementStrength();
                removeContestantFromBench(teamID, cont[teamID][contestantID] ,benchContestants);
                cont[teamID][contestantID].setChosen(false);
               // System.out.println("HERE");
            }
        } finally {
            lock.unlock();
        }
    }

    /*
     * Verify if contestant is chosen by the coach
     */
    private boolean isContestantInChosenPlayers(int teamId, ContestantsBenchClientProxy contestant, HashMap<Integer, ContestantsBenchClientProxy[]> chosenPlayers) {
        if (chosenPlayers.containsKey(teamId)) {
            ContestantsBenchClientProxy[] players = chosenPlayers.get(teamId);
        for (ContestantsBenchClientProxy player : players) {
            if (player.equals(contestant)) {
                return true; 
            }
        }
    }
        return false;
    }

    /*
     * remove contestant of the chosen's by the coach
     */
    private void removeContestantFromChosenPlayers(int teamId, ContestantsBenchClientProxy contestant, HashMap<Integer, ContestantsBenchClientProxy[]> chosenContestants) {
        ContestantsBenchClientProxy[] chosenList = chosenContestants.get(teamId);
    
        int indexToRemove = -1;
        for (int i = 0; i < chosenList.length; i++) {
            if (chosenList[i].equals(contestant)) {
                indexToRemove = i;
                break;
            }
        }
        ContestantsBenchClientProxy[] newArray = new ContestantsBenchClientProxy[chosenList.length - 1];
        System.arraycopy(chosenList, 0, newArray, 0, indexToRemove);
        System.arraycopy(chosenList, indexToRemove + 1, newArray, indexToRemove, chosenList.length - indexToRemove - 1);
        chosenContestants.put(teamId, newArray);
    }


    /*
     * remove contestant of the NOT chosen's by the coach
     */
    private void removeContestantFromBench(int teamId, ContestantsBenchClientProxy contestant,HashMap<Integer, ContestantsBenchClientProxy[]> benchContestants) {
        ContestantsBenchClientProxy[] benchList = benchContestants.get(teamId);
        int indexToRemove = -1;
        for (int i = 0; i < benchList.length; i++) {
            if (benchList[i].equals(contestant)) {
                indexToRemove = i;
                break;
            }
        }
        ContestantsBenchClientProxy[] newArray = new ContestantsBenchClientProxy[benchList.length - 1];
        System.arraycopy(benchList, 0, newArray, 0, indexToRemove);
        System.arraycopy(benchList, indexToRemove + 1, newArray, indexToRemove, benchList.length - indexToRemove - 1);
        benchContestants.put(teamId, newArray);
    }


    /**
     *   Operation server shutdown.
     *
     *   New operation.
     
    public synchronized void shutdown ()
    {
        nEntities += 1;
        // System.out.println("AssaultParty ID : " + assaultParty_ID + " nEntities: " + nEntities);
        if (nEntities >= SimulPar.E_ASSAULTP)
        {
            if (assaultParty_ID == 0)
                ServerHeistMuseumAssaultP0.waitConnection = false;
            else
                ServerHeistMuseumAssaultP1.waitConnection = false;
        }
    }   
    */ 

}
