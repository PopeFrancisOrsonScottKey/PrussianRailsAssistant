package com.boardgames.PrussianRailsAssistantArtifact.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Player {
    private String playerName;
    private List<Integer> railRoadIdShares;
}
