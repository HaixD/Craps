package model;

import libraries.observer.Subject;

/**
 * ObservedCrapsPlayer represents a CrapsPlayer that complies with the 
 * observer design pattern.
 */
public class ObservedCrapsPlayer extends CrapsPlayer {
    /** Triggers when setBank is used. */
    private final Subject<Integer> myOnBankSet;

    /** Triggers when the outcome of a game is decided. */
    private final Subject<WinState> myOnGameResult;
    /** Triggers when a game is won by the player. */
    private final Subject<Integer> myOnGameWon;
    /** Triggers when a game is lost by the player. */
    private final Subject<Integer> myOnGameLoss;

    /** Triggers when the point is set. */
    private final Subject<Integer> myOnPointSet;

    /** Triggers when the dice is rolled/rerolled. */
    private final Subject<Dice> myOnDiceSet;
    /** Same as onDiceSet but only gets die1's number. */
    private final Subject<Integer> myOnDie1Set;
    /** Same as onDiceSet but only gets die2's number. */
    private final Subject<Integer> myOnDie2Set;
    /** Same as onDiceSet but only gets the sum of dice. */
    private final Subject<Integer> myOnDiceTotalSet;

    /** Triggers when setBet is used. */
    private final Subject<Integer> myOnBetSet;

    /** 
     * Triggers when the game's state changes from not playing to playing or 
     * from playing to not playing. Returns true if it is from not playing to
     * playing.
     */
    private final Subject<Boolean> myOnGameStateSet;
    /** Triggers at the moment the game can be started. */
    private final Subject<Boolean> myOnGameStartable;
    
    /**
     * Constructs a new ObservedCrapsPlayer.
     */
    public ObservedCrapsPlayer() {
        super();
        this.myOnBankSet = new Subject<>();

        this.myOnGameResult = new Subject<>();
        this.myOnGameWon = this.myOnGameResult
            .getDerivedSubject((WinState state) -> state == WinState.WON)
            .getConvertedSubject((WinState state) -> this.getMyWins());
        this.myOnGameLoss = this.myOnGameResult
            .getDerivedSubject((WinState state) -> state == WinState.LOSS)
            .getConvertedSubject((WinState state) -> this.getMyLosses());

        this.myOnPointSet = new Subject<>();

        this.myOnDiceSet = new Subject<>();
        this.myOnDie1Set = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.myDie1);
        this.myOnDie2Set = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.myDie2);
        this.myOnDiceTotalSet = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.getSum());

        this.myOnBetSet = new Subject<>();

        this.myOnGameStateSet = new Subject<>();
        this.myOnGameStartable = this.myOnGameStateSet
            .getConvertedSubject((Boolean started) -> started ? false : (super.getBank() > 0));
    }

    /**
     * Constructs an ObservedCrapsPlayer from an existing CrapsPlayer.
     * @param parent The CrapsPlayer to copy.
     */
    public ObservedCrapsPlayer(CrapsPlayer parent) {
        super(parent);
        this.myOnBankSet = new Subject<>();

        this.myOnGameResult = new Subject<>();
        this.myOnGameWon = this.myOnGameResult
            .getDerivedSubject((WinState state) -> state == WinState.WON)
            .getConvertedSubject((WinState state) -> this.getMyWins());
        this.myOnGameLoss = this.myOnGameResult
            .getDerivedSubject((WinState state) -> state == WinState.LOSS)
            .getConvertedSubject((WinState state) -> this.getMyLosses());

        this.myOnPointSet = new Subject<>();

        this.myOnDiceSet = new Subject<>();
        this.myOnDie1Set = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.myDie1);
        this.myOnDie2Set = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.myDie2);
        this.myOnDiceTotalSet = myOnDiceSet
            .getConvertedSubject((Dice dice) -> dice.getSum());

        this.myOnBetSet = new Subject<>();

        this.myOnGameStateSet = new Subject<>();
        this.myOnGameStartable = this.myOnGameStateSet
            .getConvertedSubject((Boolean started) -> started ? false : (this.getBank() > 0));
    }

    /**
     * Gets the bank subject.
     * 
     * @return The bank subject.
     */
    public Subject<Integer> getBankSubject() {
        return this.myOnBankSet;
    }

    /**
     * Gets the game result subject.
     * 
     * @return The game result subject.
     */
    public Subject<WinState> getGameResultSubject() {
        return this.myOnGameResult;
    }

    /**
     * Gets the game won subject.
     * 
     * @return The game won subject.
     */
    public Subject<Integer> getGameWonSubject() {
        return this.myOnGameWon;
    }

    /**
     * Gets the game loss subject.
     * @return The game loss subject.
     */
    public Subject<Integer> getGameLossSubject() {
        return this.myOnGameLoss;
    }

    /**
     * Gets the point subject.
     * 
     * @return The point subject.
     */
    public Subject<Integer> getPointSubject() {
        return this.myOnPointSet;
    }

    /**
     * Gets the dice subject.
     * 
     * @return The dice subject.
     */
    public Subject<Dice> getDiceSubject() {
        return this.myOnDiceSet;
    }

    /**
     * Gets the die #1 subject.
     * @return The die #1 subject.
     */
    public Subject<Integer> getDie1Subject() {
        return this.myOnDie1Set;
    }

    /**
     * Gets the die #2 subject.
     * @return The die #2 subject.
     */
    public Subject<Integer> getDie2Subject() {
        return this.myOnDie2Set;
    }

    /**
     * Gets the die total subject.
     * @return The die total subject.
     */
    public Subject<Integer> getDiceTotalSubject() {
        return this.myOnDiceTotalSet;
    }

    /**
     * Gets the bet subject.
     * 
     * @return The bet subject.
     */
    public Subject<Integer> getBetSubject() {
        return this.myOnBetSet;
    }
    
    /**
     * Gets the game state subject.
     * 
     * @return The game state subject.
     */
    public Subject<Boolean> getGameStateSubject() {
        return this.myOnGameStateSet;
    }
    
    /**
     * Gets the game startable subject.
     * 
     * @return The game startable subject.
     */
    public Subject<Boolean> getGameStartableSubject() {
        return this.myOnGameStartable;
    }

    @Override
    public void reinitialize(int bankAmount) {
        super.reinitialize(bankAmount);
        this.myOnBankSet.update(this.getBank());
        this.myOnGameStateSet.update(this.isPlaying());
    }
    
    @Override
    public int getBank() {
        return super.getBank();
    }
    
    @Override
    public void setBank(int amount) {
        super.setBank(amount);

        this.myOnBankSet.update(super.getBank());
    }

    @Override
    public int getBet() {
        return super.getBet();
    }

    @Override
    public void setBet(int bet) {
        super.setBet(bet);

        this.myOnBetSet.update(super.getBet());
    }

    @Override
    public void incrementBet(int amount) {
        super.incrementBet(amount);

        this.myOnBetSet.update(super.getBet());
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public void resetGame() {
        super.resetGame();
        this.myOnGameStateSet.update(this.isPlaying());
    }

    @Override
    public void resetPlayer() {
        super.resetPlayer();

        this.myOnGameWon.update(this.getMyWins()); // 0
        this.myOnGameLoss.update(this.getMyLosses()); // 0
        this.myOnBetSet.update(this.getBet()); // 0
        this.myOnBankSet.update(this.getBank()); // 0
        this.myOnGameStateSet.update(this.isPlaying()); // false
    }
    
    @Override
    protected void updateGame(boolean firstTurn) {
        int prevWins = super.getMyWins();
        int prevLosses = super.getMyLosses();

        super.updateGame(firstTurn);

        if (super.getMyWins() > prevWins) {
            this.myOnGameResult.update(WinState.WON);
        } else if (super.getMyLosses() > prevLosses) {
            this.myOnGameResult.update(WinState.LOSS);
        }
    }

    @Override
    protected WinState rollDice() {
        boolean firstTurn = super.getPoint() == -1;
        
        WinState result = super.rollDice();

        this.myOnDiceSet.update(super.getDice());
        if (firstTurn) {
            this.myOnPointSet.update(super.getPoint());
        }
        
        return result;
    }

    @Override
    public void startGame() {
        super.startGame();

        if (super.isPlaying()) {
            this.myOnGameStateSet.update(true); // game only changes from not playing to playing if there is no first turn win
        }
    }
}
