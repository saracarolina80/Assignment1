package serverSide.main;


/**
 *    Definition of the simulation parameters.
 */

 public final class SimulPar
 {
   /**
    *   Number of teams competing in the game.
    */
    public static final int NUM_TEAMS = 2;
 
    public static final int NUM_GAMES = 3;
    
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
    *   Strength of the contestant 
    */  
  
    public static final int STRENGTH = 5;   
   /**
    *   Strength units lost when pulling the rope.
    */
    public static final int STRENGTH_LOSS = 1;
 
   /**
    *   Strength units gained when seating at the bench.
    */
    public static final int STRENGTH_GAIN = 1;


    /**
    *  Position ? at the end of the rope for present trial
    */
    public static final int POSITION = 3;
 
 }