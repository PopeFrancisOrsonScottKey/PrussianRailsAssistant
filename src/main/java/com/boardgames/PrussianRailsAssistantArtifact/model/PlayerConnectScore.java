package com.boardgames.PrussianRailsAssistantArtifact.model;

import lombok.Data;

@Data
public class PlayerConnectScore {
    private String playerName;
    private Integer payout;

    public PlayerConnectScore(String playerName) {
        this.playerName = playerName;
        this.payout = 0;
    }
}
