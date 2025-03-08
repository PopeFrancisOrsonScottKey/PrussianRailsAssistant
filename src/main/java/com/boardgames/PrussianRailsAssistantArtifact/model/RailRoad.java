package com.boardgames.PrussianRailsAssistantArtifact.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RailRoad {
    private Integer id;
    private String name;
    private Integer value;
    private String color;
    private String description;
    private String custom;
    private Boolean checked;
}
