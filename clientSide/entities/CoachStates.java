package clientSide.entities;

/**
 *    Definição dos estados internos do treinador durante o seu ciclo de vida.
 */

public final class CoachStates {


    public static final int WAIT_FOR_REFEREE_COMMAND = 0;

    public static final int ASSEMBLE_TEAM = 1;

    public static final int WATCH_TRIAL = 2;

    public static final int END_OF_THE_MATCH = 3;

    /**
     *   Não pode ser instanciado.
     */
    private CoachStates() {}
}
