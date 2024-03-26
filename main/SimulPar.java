package main;

/**
 *    Definition of the simulation parameters.
 */

 public final class SimulPar
 {

     /**
    *   Number of coaches in the game
    */
    public static final int COACHES_NUMBER = 2;
 
   /**
    *   Number of teams competing in the game.
    */
    public static final int NUM_TEAMS = 2;
 
   /**
    *   Number of contestants in each team.
    */
    public static final int TEAM_SIZE = 5;
 
   /**
    *   Number of trials in each game.
    */
    public static final int NUM_TRIALS = 6;
 
   /**
    *   Length unit threshold for knockout victory.
    */
    public static final int KNOCKOUT_THRESHOLD = 4;
 
   /**
    *   Strength units lost when pulling the rope.
    */
    public static final int STRENGTH_LOSS = 1;
 
   /**
    *   Strength units gained when seating at the bench.
    */
    public static final int STRENGTH_GAIN = 1;
 
   /**
    *   It cannot be instantiated.
    */
    private SimulPar () { }
 }