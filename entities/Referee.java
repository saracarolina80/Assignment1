package entities;

import sharedRegions.*;

public class Referee extends Thread {
    private final RefereeSite refereeSite;
    private final Playground playground;
    private int refereeState;

    public Referee(String name, RefereeSite refereeSite, Playground playground) {
        super(name);
        this.refereeSite = refereeSite;
        this.playground = playground;
        this.refereeState = RefereeStates.START_OF_THE_MATCH;
    }

    @Override
    public void run() {
        switch (refereeState) {
            case RefereeStates.START_OF_THE_MATCH:
                refereeSite.announceNewGame(this);
                break;
            case RefereeStates.START_OF_A_GAME:
                refereeSite.callTrial(this);
                playground.assertTrialDecision(this);
                break;
            case RefereeStates.TEAMS_READY:
                playground.startTrial(this);
                break;
            case RefereeStates.WAIT_FOR_TRIAL_CONCLUSION:
                refereeSite.callTrial(this);
                playground.assertTrialDecision(this);
                refereeSite.declareGameWinner(this);
                break;
            case RefereeStates.END_OF_A_GAME:
                refereeSite.announceNewGame(this);
                refereeSite.declareMatchWinner(this);
                break;
            case RefereeStates.END_OF_THE_MATCH:
                break;
        }
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
