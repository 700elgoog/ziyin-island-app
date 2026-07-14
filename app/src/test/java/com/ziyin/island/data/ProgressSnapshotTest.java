package com.ziyin.island.data;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import org.junit.Test;

public class ProgressSnapshotTest {
    @Test
    public void starsRewardExplorationAndCompletionWithoutLeaderboardScore() {
        ProgressSnapshot snapshot = new ProgressSnapshot(
                Set.of("a", "o", "e"),
                Set.of("b01_a_o_e"),
                15
        );

        assertEquals(5, snapshot.getStars());
    }
}
