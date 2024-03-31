package entities;

import main.SimulPar;
import sharedRegions.*;

public class Referee extends Thread {
    private final RefereeSite refereeSite;
    private final Playground playground;
    private int refereeState;


    public Referee(String name, RefereeSite refereeSite, Playground playground) {
        super(name);
        this.refereeSite = refereeSite;
        this.playground = playground;
        refereeState = RefereeStates.START_OF_THE_MATCH;
    }

     @Override
    public void run() {
        int winner = 0;

        for (int numGames = 0; numGames < SimulPar.NUM_GAMES; numGames++) {

        
                refereeSite.announceNewGame(this);
                playground.signalMatchStatus(false);

            
            int numTrials = 1;
            int ropePosition = 0;

            while (numTrials <= SimulPar.NUM_TRIALS && Math.abs(ropePosition) != SimulPar.KNOCKOUT_THRESHOLD) {
                System.out.println("\n\n ---------------TRIAL " + numTrials + "---------------n\n");
                System.out.println("Rope is in position: " + ropePosition);
                playground.signalMatchStatus(false);
                refereeSite.callTrial(this);

                playground.startTrial(this);

                ropePosition = playground.assertTrialDecision(this, ropePosition);


    
                winner += refereeSite.declareGameWinner(this, ropePosition);
                numTrials++;
                
            }
        
        
    }
        playground.signalMatchStatus(true);
        refereeSite.declareMatchWinner(this, winner);
        
}
    
    public void setRefereeState(int newRefereeState) {
        switch (newRefereeState) {
            case RefereeStates.START_OF_THE_MATCH:
                this.refereeState = RefereeStates.START_OF_THE_MATCH;
                break;
            case  RefereeStates.START_OF_A_GAME:
                this.refereeState =  RefereeStates.START_OF_A_GAME;
                break;
            case RefereeStates.TEAMS_READY:
                this.refereeState = RefereeStates.TEAMS_READY;
                break;
            case RefereeStates.WAIT_FOR_TRIAL_CONCLUSION:
                this.refereeState = RefereeStates.WAIT_FOR_TRIAL_CONCLUSION;
                break;
            case RefereeStates.END_OF_A_GAME:
                this.refereeState = RefereeStates.END_OF_A_GAME;
                break;
            case RefereeStates.END_OF_THE_MATCH:
                this.refereeState = RefereeStates.END_OF_THE_MATCH;
                break;
            default:
                break;
        }
    }
}
