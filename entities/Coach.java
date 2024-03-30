package entities;

import sharedRegions.*;

public class Coach extends Thread {
    private final ContestantsBench bench;
    private final RefereeSite refereeSite;
    private final Playground playground;
    private int coachState;
    private int chooseContestants;



    public Coach(String name, ContestantsBench bench, RefereeSite refereeSite, Playground playground) {
        super(name);
        this.bench = bench;
        this.refereeSite = refereeSite;
        this.playground = playground;
        this.coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
    }

    @Override
    public void run() {
        
        while (!playground.isMatchFinished(this)) {
                    refereeSite.waitNewGame(this);
                    bench.callContestants(this);
                    playground.waitAthletes(this);
                    refereeSite.informReferee(this);
                    playground.watchTrial(this);
                    refereeSite.reviewNotes(this);
                    refereeSite.waitNewGame(this);
            }
         }
    // BEST 3 OR RANDOM
    public int getChooseMode() {
        return chooseContestants;
    }

    public void setCoachState(int newCoachState) {
        switch (newCoachState) {
            case CoachStates.WAIT_FOR_REFEREE_COMMAND:
                this.coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
                break;
            case CoachStates.ASSEMBLE_TEAM:
                this.coachState = CoachStates.ASSEMBLE_TEAM;
                break;
            case CoachStates.WATCH_TRIAL:
                this.coachState = CoachStates.WATCH_TRIAL;
                break;
            case CoachStates.END_OF_THE_MATCH:
                this.coachState = CoachStates.END_OF_THE_MATCH;
                break;
            default:
                break;
        }
    }
}
