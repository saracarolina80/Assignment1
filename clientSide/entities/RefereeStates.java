package clientSide.entities;

/**
 *    Definição dos estados internos do árbitro durante o seu ciclo de vida.
 */

public final class RefereeStates {


    public static final int START_OF_THE_MATCH = 0;


    public static final int START_OF_A_GAME = 1;


    public static final int TEAMS_READY = 2;


    public static final int WAIT_FOR_TRIAL_CONCLUSION = 3;


    public static final int END_OF_A_GAME = 4;


    public static final int END_OF_THE_MATCH = 5;

    /**
     *   Não pode ser instanciado.
     */
    private RefereeStates() {}
}
