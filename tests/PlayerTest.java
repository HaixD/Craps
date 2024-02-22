package tests;

import model.CrapsPlayer;
import model.Dice;
import model.ObservedCrapsPlayer;
import model.WinState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
    private static final int RNG_TEST_ATTEMPTS = 100;
    
    private static CrapsPlayer player;

    @Before
    public void setUp() {
        player = new CrapsPlayer();
    }
    
    /**
     * Checks if setting bank to invalid values will set it to corresponding 
     * correct values and if setting it to valid values works.
     */
    @Test
    public void testBank() {
        player.setBank(-1);
        assertEquals(0, player.getBank());
        
        player.setBank(0);
        assertEquals(0, player.getBank());
        
        player.setBank(1);
        assertEquals(1, player.getBank());
    }

    /**
     * Checks if setting the bet to invalid values will set it to a corresponding
     * correct value and if setting it to a valid value works.
     */
    @Test
    public void testBet() {
        player.setBank(1);

        player.setBet(-10);
        assertEquals(0, player.getBet());
        
        player.setBet(0);
        assertEquals(0, player.getBet());

        player.setBet(0);
        assertThrows(IllegalArgumentException.class, player::startGame);
        
        player.setBet(1);
        assertEquals(1, player.getBet());

        player.setBet(3);
        assertEquals(1, player.getBet());
    }

    /**
     * Checks that all possible die numbers are seen within some amount of games.
     */
    @Test
    public void testDice() {
        player.setBank(PlayerTest.RNG_TEST_ATTEMPTS);
        ObservedCrapsPlayer observedPlayer = new ObservedCrapsPlayer(player);
        Map<Integer, Integer> seenNumbers = new HashMap<>();
        seenNumbers.put(1, 0);
        seenNumbers.put(2, 0);
        seenNumbers.put(3, 0);
        seenNumbers.put(4, 0);
        seenNumbers.put(5, 0);
        seenNumbers.put(6, 0);

        observedPlayer.getDiceSubject().addObserver((Dice dice) -> {
            seenNumbers.put(dice.myDie1, seenNumbers.get(dice.myDie1) + 1);
            seenNumbers.put(dice.myDie2, seenNumbers.get(dice.myDie2) + 1);
        });
        
        for (int i = 0; i < PlayerTest.RNG_TEST_ATTEMPTS; i++) {
            observedPlayer.setBet(1);
            observedPlayer.startGame();
            while (observedPlayer.isPlaying()) {
                observedPlayer.continueGame();
            }
        }

        for (int number = 1; number <= 6; number++) {
            assertTrue(seenNumbers.get(number) >= 1);
        }
    }

    /**
     * Does a "side by side" comparison with the game results by checking
     * the first turn wins/losses work correctly and wins/losses from getting
     * the point or 7 are also done correctly.
     */
    @Test
    public void testWinLoss() {
        Set<Integer> firstTurnWins = new HashSet<>(Arrays.asList(7, 11));
        Set<Integer> firstTurnLoss = new HashSet<>(Arrays.asList(2, 3, 12));
        int laterTurnLoss = 7;

        
        player.setBank(PlayerTest.RNG_TEST_ATTEMPTS);
        ObservedCrapsPlayer observedPlayer = new ObservedCrapsPlayer(player);

        Dice[] lastDice = { null };
        observedPlayer.getDiceSubject().addObserver((Dice dice) -> {
            lastDice[0] = dice;
        });

        Integer[] lastPoint = { null };
        observedPlayer.getPointSubject().addObserver((Integer point) -> {
            lastPoint[0] = point;
        });

        WinState[] lastWinState = { null };
        observedPlayer.getGameResultSubject().addObserver((WinState result) -> {
            lastWinState[0] = result;
        });

        for (int i = 0; i < PlayerTest.RNG_TEST_ATTEMPTS; i++) {
            observedPlayer.setBet(1);
            observedPlayer.startGame();

            if (firstTurnWins.contains(lastDice[0].getSum())) {
                assertEquals(WinState.WON, lastWinState[0]);
                continue;
            } else if (firstTurnLoss.contains(lastDice[0].getSum())) {
                assertEquals(WinState.LOSS, lastWinState[0]);
                continue;
            }
            
            while (observedPlayer.isPlaying()) {
                observedPlayer.continueGame();
            }

            if (lastPoint[0] == lastDice[0].getSum()) {
                assertEquals(WinState.WON, lastWinState[0]);
            } else {
                assertEquals(laterTurnLoss, lastDice[0].getSum());
                assertEquals(WinState.LOSS, lastWinState[0]);
            }
        }
    }
}
