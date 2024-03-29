package entities;

import main.SimulPar;
import sharedRegions.*;

public class Contestant extends Thread {
    private final Playground playground;
    private final ContestantsBench bench;
    private final GeneralRepos repos;
    private int contestantState;
    private int strength = SimulPar.STRENGTH;


    public Contestant(String contestantId, GeneralRepos repos, Playground playground, ContestantsBench bench) {
        super(contestantId);
        this.playground = playground;
        this.strength = SimulPar.STRENGTH;
        this.repos = repos;
        this.bench = bench;
        this.contestantState = ContestantStates.SEAT_AT_THE_BENCH;
    }

    public void decrementStrength() {
        if (strength > 0) {
            strength = strength - SimulPar.STRENGTH_LOSS;
        }
    }

    public void incrementStrength() {
         strength = strength + SimulPar.STRENGTH_GAIN;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public void run() {
        while (!playground.isMatchFinished(this)) {
            bench.sitDown(this);
            switch (contestantState) {
                case ContestantStates.SEAT_AT_THE_BENCH:
                    playground.followCoachAdvice(this);
                    break;
                case ContestantStates.STAND_IN_POSITION:
                    playground.getReady(this);
                    break;
                case ContestantStates.DO_YOUR_BEST:
                    playground.pullTheRope(this);
                    playground.amDone(this);
                    bench.sitDown(this);
                    break;
                default:
                    break;
            }
    }

    }
    public void setContestantState(int newContestantState) {
        switch (newContestantState) {
            case ContestantStates.SEAT_AT_THE_BENCH:
                this.contestantState = ContestantStates.SEAT_AT_THE_BENCH;
                break;
            case ContestantStates.STAND_IN_POSITION:
                this.contestantState = ContestantStates.STAND_IN_POSITION;
                break;
            case ContestantStates.DO_YOUR_BEST:
                this.contestantState = ContestantStates.DO_YOUR_BEST;
                break;
            default:
                break;
        }
    }
    
}
