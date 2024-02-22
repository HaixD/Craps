package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Craps represents a one-time craps game.
 */
public class Craps {
    /** The rolls that lead to a win on the first turn. */
    private static final Set<Integer> firstTurnWins = new HashSet<>(Arrays.asList(7, 11));
    /** The rolls that lead to a loss on the first turn. */
    private static final Set<Integer> firstTurnloss = new HashSet<>(Arrays.asList(2, 3, 12));
    /** The roll that leads to a loss on turns after the first. */
    private static final int laterTurnLoss = 7;
    
    /** The point of this current game. */
    private int myPoint;
    /** The current dice roll of this game. */
    private Dice myDice;

    /**
     * Constructs a Craps game with no point.
     */
    public Craps() {
        this.myPoint = -1;
    }

    /**
     * Copy constructor for Craps.
     * 
     * @param other The craps game to copy.
     */
    public Craps(Craps other) {
        this.myPoint = other.myPoint;
        this.myDice = other.myDice;
    }

    /**
     * Gets the point.
     * 
     * @return The point.
     */
    public int getMyPoint() {
        return this.myPoint;
    }

    /**
     * Gets the dice.
     * 
     * @return The dice.
     */
    public Dice getMyDice() {
        return this.myDice;
    }

    /**
     * Checks if it is the first turn.
     * 
     * @return True if it is the first turn.
     */
    public boolean isFirstTurn() {
        return this.myPoint == -1;
    }

    /**
     * Checks the result of the game.
     * 
     * @param firstTurn Whether it is supposed to be the first turn.
     * @return The game result.
     */
    private WinState getResult(boolean firstTurn) {
        int sum = this.myDice.getSum();
        
        if (firstTurn) {
            if (Craps.firstTurnWins.contains(sum)) {
                return WinState.WON;
            } else if (Craps.firstTurnloss.contains(sum)) {
                return WinState.LOSS;
            }
        } else {
            if (sum == this.myPoint) {
                return WinState.WON;
            } else if (sum == Craps.laterTurnLoss) {
                return WinState.LOSS;
            }
        }

        return WinState.ONGOING;
    }

    /**
     * Rolls the dice.
     * 
     * @return The outcome.
     */
    public WinState roll() {
        this.myDice = Dice.newRoll();

        if (this.isFirstTurn()) {
            this.myPoint = this.myDice.getSum();
            return this.getResult(true);
        }

        return this.getResult(false);
    }
}