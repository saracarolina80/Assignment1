package commInfra;


/**
 *   Type of the exchanged messages.
 *the
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */

public class MessageType
{

    /******************** General repository messages ********************/

  /**
   *  Initialization of the logging file name and the number of iterations (service request).
   */

   public static final int LOGFN = 1;

  
  /**
  *  Logging file was initialized (reply).
  */
  public static final int LOGFNDONE = 2;


  /**
     *  Set referee state (service request).
     */
    public static final int SETRST = 3;

    /**
     *  Set coach (service request).
     */
    public static final int SETCST = 4;
  
     /**
     *  Set contestant (service request).
     */
    public static final int SETCONST = 5;


  /**
   *  Coach call contestants (service request) .
   */

   public static final int CALLCONT = 6;


   
  /**
   *  Coach call contestants (reply) .
   */

   public static final int CALLCONTDONE = 7;

  /**
   *  contestant sitdown (service request) .
   */

   public static final int SITDOWN = 8;

   /**
   *  contestant sitdown (reply) .
   */

   public static final int SITDOWNDONE = 9;


  /**
   * is match finished (service request).
   */

   public static final int MFINISH = 10;

    /**
   * is match finished ( reply).
   */

   public static final int MFINISHDONE = 11;


  /**
   *  follow coach advice (service request).
   */

   public static final int FCAD = 12;

  /**
   *  follow coach advice (reply).
   */

   public static final int FCADDONE = 13;

  /**
   * get ready (service request).
   */

   public static final int GETREADY = 14;
  
   /**
   * get ready ( reply).
   */

   public static final int GETREADYDONE = 15;

  /**
   *  pull the rope (service request).
   */

   public static final int PULLR = 16;

    /**
   *   pull the rope (reply).
   */

   public static final int PULLRDONE = 17;

     /**
   * AM DONE (service request).
   */

   public static final int AMDONE = 18;

    /**
   *  AM DONE (reply).
   */

   public static final int AMDONEDONE = 19;

     /**
   * is match still going (service request).
   */

   public static final int MATCHGOING = 20;

    /**
   *   is match still going  (reply).
   */

   public static final int MATCHGOINGDONE = 21;

    /**
   *  SHUTDOWN (service request).
   */

   public static final int SHUT = 22;

  /**
   * SHUTDOWN (reply).
   */

   public static final int SHUTDONE = 23;


    /**
   *  WAIT NEW GAME (service request).
   */

   public static final int WAITNGAME = 24;

  /**
   *  WAIT NEW GAME (reply).
   */

   public static final int WAITNGAMEDONE = 25;

  /**
   *  Set barber and customer states (service request).
   */

   public static final int STBCST = 26;

  /**
   *  Setting acknowledged (reply).
   */

   public static final int SACK = 27;


    /**
   *  REVIEW NOTES (service request).
   */

   public static final int REVIEWN = 28;

  /**
   * REVIEW NOTES (reply).
   */

   public static final int REVIEWNDONE = 29;


    /**
   *  INFORM REFEREE (service request).
   */

   public static final int INFORMR = 30;

  /**
   *  INFORME REFEREE (reply).
   */

   public static final int INFORMRDONE = 31;

    /**
   *  ANNOUNCE NEW GAME (service request).
   */

   public static final int ANNOUNCENGAME = 32;

  /**
   * ANNOUNCE NEW GAME (reply).
   */

   public static final int ANNOUNCENGAMEDONE = 33;


    /**
   *  CALL TRIAL (service request).
   */

   public static final int CALLTRIAL = 34;

  /**
   *  CALL TRIAL  (reply).
   */

   public static final int CALLTRIALDONE = 35;

    /**
   * GET GAME WINNER (service request).
   */

   public static final int GAMEWINNER = 36;

  /**
   * GET GAME WINNER (reply).
   */

   public static final int GAMEWINNERDONE = 37;

/**
   *  End operation (service request) .
   */

   public static final int ENDOP = 38;


   
  /**
   *   End operation(reply) .
   */

   public static final int EOPDONE =39;

   /**
   *  wait contestants (service request) .
   */

   public static final int WAITCONT = 40;


   
  /**
   *    wait contestants  (reply) .
   */

   public static final int WAITCONDONE =41;


   /**
   * watch trial (service request) .
   */

   public static final int WATCHTRIAL = 42;


   
  /**
   *   watch trial (reply) .
   */

   public static final int WATCHTRIALDONE =43;


   /**
   * ASSERT TRIAL DECISION (service request) .
   */

   public static final int ASSERTTDEC = 44;


   
  /**
   *   ASSERTT TRIAL DECISION (reply) .
   */

   public static final int ASSERTTDECDONE =45;


   /**
   * START trial (service request) .
   */

   public static final int STARTTRIAL = 46;


   
  /**
   *  START trial (reply) .
   */

   public static final int STARTTRIALDONE =47;


  public static final int MATCHGOINGNOTDONE = 48;



    /**
   * GET MATCH WINNER (service request).
   */

   public static final int MATCHWINNER = 49;

  /**
   * GET MATCH WINNER (reply).
   */

   public static final int MATCHWINNERDONE = 50;
}
