package entities;

import sharedRegions.*;

public class Contestant extends Thread {
    private Playground playground;
    private GeneralRepos repos;
    private int teamId;
    private int contestantId;
    private int contestantState;

    public Contestant(String name, int teamId, int contestantId, Playground playground, GeneralRepos repos) {
        super(name);
        this.teamId = teamId;
        this.contestantId = contestantId;
        this.playground = playground;
        this.repos = repos;
        this.contestantState = ContestantStates.SEAT_AT_THE_BENCH; // Inicializando o estado do competidor
    }

    @Override
    public void run() {
        while (contestantState != ContestantStates.END_OF_THE_MATCH) {
            switch (contestantState) {
                case ContestantStates.SEAT_AT_THE_BENCH:
                    seatAtTheBench();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case ContestantStates.STAND_IN_POSITION:
               
