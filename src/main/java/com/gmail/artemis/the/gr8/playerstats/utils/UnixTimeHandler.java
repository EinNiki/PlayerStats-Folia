package com.gmail.artemis.the.gr8.playerstats.utils;

import java.time.Instant;

public class UnixTimeHandler {

    //calculates whether a player has played recently enough to fall within the lastPlayedLimit
    //if lastPlayedLimit == 0, this always returns true (since there is no limit)
    public static boolean hasPlayedSince(long lastPlayedLimit, long lastPlayed) {
        long maxLastPlayed = System.currentTimeMillis() - lastPlayedLimit * 24 * 60 * 60 * 1000;

        System.out.println("maxLastPlayed: " + maxLastPlayed);
        System.out.println("Player last played: " + lastPlayed);

        return lastPlayedLimit == 0 || lastPlayed >= maxLastPlayed;
    }
}
