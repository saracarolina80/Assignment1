package sharedRegions;

import entities.Contestant;
import entities.ContestantStates;
import genclass.TextFile;
import main.SimulPar;
import entities.Coach;
import entities.CoachStates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ContestantsBench {

    private final ReentrantLock lock;
    private final Condition callContestants;
    private final Condition allContestantSeated;
    public final HashMap<Integer, Contestant[]> benchContestants = new HashMap<>();
    public final HashMap<Integer, Contestant[]> chosenContestants = new HashMap<>();
    private int[] callContestantsCount = {0, 0};


    /**
     * Name of the logging file.
     */
    private final String logFileName;

    GeneralRepos repos;
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
        if ((logFileName == null) || Objects.equals(logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;
            
         // Inicializa o hashmap benchContestants com listas vazias para cada treinador
        for (int i = 1; i <= SimulPar.NUM_TEAMS; i++) {
            benchContestants.put(i, new Contestant[0]);
        }
        repos = new GeneralRepos(logFileName);

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


    public void callContestants(Coach coach) {
        String coachName = coach.getName();

        String[] partes = coachName.split(" ");
        int coachId = Integer.parseInt(partes[1]); 
       
        Random random = new Random();
        int chooseMode = random.nextInt(2);

        try {
            lock.lock();
            // Wait until all athletes are seated at the bench
            while (benchContestants.get(coachId).length < 5) {
                try {
                    System.out.println("COACH " + coachName + " is waiting for all athletes to sit down");
                    allContestantSeated.await(); // Wait for all athletes to sit down
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("All CONTESTANTS of team" + coachId + " seated"    );
            System.out.println("COACH " + coachName + " is choosing the team");
           
            Contestant[] listOfContestants = benchContestants.get(coachId);
   
            if (chooseMode == 1) {
                // Choose the top 3 strengths
                System.out.println("COACH " + coachName + " choose mode STRENGTH");
                Arrays.sort(listOfContestants, (a1, a2) -> Integer.compare(a2.getStrength(), a1.getStrength()));
            } else {
                // Choose Random
                System.out.println("COACH " + coachName + " choose mode RANDOM");
                Collections.shuffle(Arrays.asList(listOfContestants));
            }

            Contestant[] chosen = Arrays.copyOfRange(listOfContestants, 0, 3);
            chosenContestants.put(coachId, chosen);

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
            benchContestants.put(coachId, remainingPlayers.toArray(new Contestant[0]));
            for (Contestant athlete : benchContestants.get(coachId)) {
                System.out.print("BENCH: "  + athlete.getName() + " ");
            }
        
            for (Contestant athlete : chosenContestants.get(coachId)) {
                if(athlete != null) {
                System.out.print("Chosen:" + athlete.getName() + " ");
                } else {
                    System.out.println("ERROR");
                }
            }
           

            // Awake all contestants to check if they are chosen
            callContestantsCount[coachId-1] = SimulPar.TEAM_SIZE;
            callContestants.signalAll();

        } finally {
            lock.unlock();
        }
    }

    public void sitDown(Contestant contestant) {
        String contestantName = contestant.getName();

        String[] partes = contestantName.split(" ");
        char teamChar = partes[1].charAt(0); 
        int teamId = Character.getNumericValue(teamChar); 

        // Update contestant state to reflect sitting at the bench
        setContestantState(ContestantStates.SEAT_AT_THE_BENCH);
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
            System.out.println("CONTESTANT " + contestantName + " added themselves to the bench");
            
            

            
        // Signal when all athletes are seated
        if (benchContestants.get(teamId).length == 5) {
            allContestantSeated.signalAll();
        }

        // Wait until they are called
        while(callContestantsCount[teamId-1] == 0){
           
            try{
            //    System.out.println("CONTESTANT " + contestantName + " is waiting");
                callContestants.await();
            //    System.out.println("CONTESTANT " + contestantName + " is awaken");
            } catch (InterruptedException e){}
        }
       // System.out.println("CONTESTANT " + contestantName + " decrements callContestantsCount");
        callContestantsCount[teamId-1]--;

        if (isContestantInChosenPlayers(teamId, contestant, chosenContestants)){
            
            
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

            contestant.setChosen(true);
        } else {
            
            contestant.incrementStrength();
          
            Contestant[] benchList = benchContestants.get(teamId);
            int indexToRemove = -1;
            for (int i = 0; i < benchList.length; i++) {
                if (benchList[i].equals(contestant)) {
                    indexToRemove = i;
                    break;
                }
            }
            Contestant[] newArray = new Contestant[benchList.length - 1];
            System.arraycopy(benchList, 0, newArray, 0, indexToRemove);
            System.arraycopy(benchList, indexToRemove + 1, newArray, indexToRemove, benchList.length - indexToRemove - 1);
            benchContestants.put(teamId, newArray);
            contestant.setChosen(false);
        }
    } finally {
        lock.unlock();
    }
}

    private boolean isContestantInChosenPlayers(int teamId, Contestant contestant, HashMap<Integer, Contestant[]> chosenContestants) {
        if (chosenContestants.containsKey(teamId)) {
            Contestant[] contestants = chosenContestants.get(teamId);
            if (contestants != null) {
                for (Contestant cont : contestants) {
                    if (cont != null) {
                       // System.out.println("Player: " + cont.getName()); // Print para depuração
                        if (cont.equals(contestant)) {
                            return true;
                        }
                    } else {
                  //      System.out.println("Player is null!"); // Print para depuração
                    }
                }
            } else {
            //    System.out.println("Players array is null!"); // Print para depuração
            }
        } else {
         //   System.out.println("Team ID not found in chosen players map!"); // Print para depuração
        }
        return false;
    }
    
}
