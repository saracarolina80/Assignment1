package entities;

/**
 *    Definição dos estados internos do contestant durante o seu ciclo de vida.
 */

public final class ContestantStates {

    public static final int SEAT_AT_THE_BENCH = 0;

    public static final int STAND_IN_POSITION = 1;

    public static final int DO_YOUR_BEST = 2;

    public static final int END_OF_THE_MATCH = 3;

    /**
     *   Não pode ser instanciado.
     */
    private ContestantStates() {}
}
