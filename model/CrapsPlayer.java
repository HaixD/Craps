package model;

import java.util.Objects;

/**
 * CrapsPlayer is a wrapper for Craps that allows for betting.
 */
public class CrapsPlayer {    
    /** How much money the player has. */
    private int myBank;
    /** How much money the player is betting. */
    private int myBet;
    /** The current game of craps. */
    private Craps myCurrentGame;
    /** How many times the player has won. */
    private int myWins;
    /** How many times the player has lost. */
    private int myLosses;
    /** The player's most recent bet. */
    private int myLastBet;
    
    /** 
     * Constructs a player.
     */
    public CrapsPlayer() {
        this.myBank = 0;
        this.myBet = 0;
        this.myCurrentGame = null;
        this.myWins = 0;
        this.myLosses = 0;
        this.myLastBet = 0;
    }

    /**
     * Copy constructor for player.
     * @param other The player to copy.
     */
    public CrapsPlayer(CrapsPlayer other) {
        this.myBank = other.myBank;
        this.myBet = other.myBet;
        if (other.myCurrentGame == null) {
            this.myCurrentGame = null;
        } else {
            this.myCurrentGame = new Craps(other.myCurrentGame);
        }
        this.myWins = other.myWins;
        this.myLosses = other.myLosses;
        this.myLastBet = other.myLastBet;
    }

    /**
     * Resets the player with the new bank amount.
     * 
     * @param bankAmount The new bank amount.
     */
    public void reinitialize(int bankAmount) {
        if (this.isPlaying()) {
            throw new IllegalStateException("Cannot initialize bank if player is already in game. Use setBank instead");
        }

        this.resetPlayer();
        this.setBank(bankAmount);
    }
    
    /**
     * Gets the bank.
     * 
     * @return The bank.
     */
    public int getBank() {
        return this.myBank;
    }
    
    /**
     * Sets the bank.
     * 
     * @param amount The new bank amount.
     */
    public void setBank(int amount) {
        this.myBank = Math.max(amount, 0);
    }

    /**
     * Gets the bet.
     * 
     * @return The bet.
     */
    public int getBet() {
        return this.myBet;
    }

    /**
     * Sets the bet.
     * 
     * @param bet The new bet.
     */
    public void setBet(int bet) {
        this.myBet = Math.max(0, Math.min(bet, this.myBank));
    }

    /**
     * Increments the bet.
     * 
     * @param myBet The amount to increment.
     */
    public void incrementBet(int amount) {
        this.setBet(this.myBet + amount);
    }

    /**
     * Gets the point.
     * 
     * @return The point.
     */
    public int getPoint() {
        return this.myCurrentGame.getMyPoint();
    }
    
    /**
     * Gets the dice.
     * 
     * @return The dice.
     */
    public Dice getDice() {
        return this.myCurrentGame.getMyDice();
    }

    /**
     * Gets the wins.
     * 
     * @return The wins.
     */
    public int getMyWins() {
        return this.myWins;
    }

    /**
     * Gets the losses.
     * 
     * @return The losses.
     */
    public int getMyLosses() {
        return this.myLosses;
    }

    /**
     * Gets the last bet.
     * 
     * @return The last bet.
     */
    public int getMyLastBet() {
        return this.myLastBet;
    }

    /**
     * Checks if the player is currently playing a game.
     * 
     * @return True  if the player is currently playing.
     */
    public boolean isPlaying() {
        return this.myCurrentGame != null;
    }

    /**
     * Deletes the current game and bet.
     */
    public void resetGame() {
        this.myCurrentGame = null;
        this.setBet(0);
    }

    /**
     * Resets the player's progress (bank, wins, losses, and game).
     */
    public void resetPlayer() {
        this.resetGame();
        this.setBank(0);

        myWins = 0;
        myLosses = 0;
        myLastBet = 0;
    }
    
    /**
     * Rolls the dice of the current game.
     * 
     * @return The outcome after rolling.
     */
    protected WinState rollDice() {
        return Objects.requireNonNull(this.myCurrentGame).roll();
    }

    /**
     * Handles the intricacies of rolling a dice.
     * 
     * @param firstTurn True if it is the first turn.
     */
    protected void updateGame(boolean firstTurn) {
        WinState result = this.rollDice();
        
        if (result.equals(WinState.WON)) {
            this.setBank(this.myBank + this.getBet() * 2);

            this.myWins += 1;
            this.myLastBet = this.myBet;

            this.resetGame();
        } else if (result.equals(WinState.LOSS)) {
            this.myLosses += 1;
            this.myLastBet = this.myBet;

            this.resetGame();
        }
    }

    /**
     * Rerolls the dice of a craps game.
     */
    public void continueGame() {
        if (this.myCurrentGame.isFirstTurn()) {
            throw new IllegalStateException("Cannot continue to roll dice before first turn (use startGame() first)");
        }
        
        this.updateGame(false);
    }

    /**
     * Initiates a craps game.
     */
    public void startGame() {
        if ((this.myBet <= 0) || (this.myBet > this.myBank)) {
            throw new IllegalArgumentException("Bet not legal");
        } else if (this.myCurrentGame != null) {
            throw new IllegalStateException("Cannot start a game when one is already ongoing (use rollDice() instead)");
        }

        this.myCurrentGame = new Craps();
        this.setBank(this.myBank - this.getBet());
        
        this.updateGame(true);
    }
}
