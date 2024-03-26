package entities;

import sharedRegions.*;

public class Referee extends Thread {
    private RefereeSite refereeSite;
    private Playground playground;
    private GeneralRepos repos;
    private int refereeState;

    public Referee(String name, RefereeSite refereeSite, Playground playground, GeneralRepos repos) {
        super(name);
        this.refereeSite = refereeSite;
        this.playground = playground;
        this.repos = repos;
        this.refereeState = RefereeStates.START_OF_THE_MATCH; // Inicializando o estado do árbitro
    }

    @Override
    public void run() {
        while (refereeState != RefereeStates.END_OF_THE_MATCH) {
            switch (refereeState) {
                case RefereeStates.START_OF_THE_MATCH:
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case RefereeStates.START_OF_A_GAME:
                    announceNewGame();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case RefereeStates.TEAMS_READY:
                    callTrial();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case RefereeStates.WAIT_FOR_TRIAL_CONCLUSION:
                    waitTrialConclusion();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                case RefereeStates.END_OF_A_GAME:
                    declareGameWinner();
                    // Transição para o próximo estado
                    // Implemente a lógica de transição aqui
                    break;
                default:
                    // Trate qualquer outro caso não previsto
                    break;
            }
        }
        // Quando o loop terminar, o jogo terminou
        declareMatchWinner();
    }

    private void announceNewGame() {
        // Implemente a lógica para o estado de anúncio de um novo jogo
    }

    private void callTrial() {
        // Implemente a lógica para o estado de chamada para um julgamento
    }

    private void waitTrialConclusion() {
        // Implemente a lógica para o estado de espera pelo término do julgamento
    }

    private void declareGameWinner() {
        // Implemente a lógica para o estado de declaração do vencedor do jogo
    }

    private void declareMatchWinner() {
        // Implemente a lógica para o estado de declaração do vencedor do jogo da corda
    }
}
