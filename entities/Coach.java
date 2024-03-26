package entities;

import sharedRegions.*;

public class Coach extends Thread {
    private RefereeSite refereeSite;
    private Playground playground;
    private GeneralRepos repos;
    private int coachState;

    public Coach(String name, RefereeSite refereeSite, Playground playground, GeneralRepos repos) {
        super(name);
        this.refereeSite = refereeSite;
        this.playground = playground;
        this.repos = repos;
        this.coachState = CoachStates.WAIT_FOR_REFEREE_COMMAND;
    }

    @Override
    public void run() {
        while (coachState != CoachStates.END_OF_THE_MATCH) {
            switch (coachState) {
                case CoachStates.WAIT_FOR_REFEREE_COMMAND:
                    waitForRefereeCommand();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case CoachStates.ASSEMBLE_TEAM:
                    assembleTeam();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case CoachStates.WATCH_TRIAL:
                    watchTrial();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                default:
                    // Trate qualquer outro caso não previsto
                    break;
            }
        }
    }

    private void waitForRefereeCommand() {
        // Implemente a lógica para o estado de espera pelo comando do árbitro
    }

    private void assembleTeam() {
        // Implemente a lógica para o estado de montagem do time
    }

    private void watchTrial() {
        // Implemente a lógica para o estado de assistir ao julgamento
    }
}
