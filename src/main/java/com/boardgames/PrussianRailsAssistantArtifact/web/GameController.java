package com.boardgames.PrussianRailsAssistantArtifact.web;

import com.boardgames.PrussianRailsAssistantArtifact.model.Player;
import com.boardgames.PrussianRailsAssistantArtifact.model.PlayerConnectScore;
import com.boardgames.PrussianRailsAssistantArtifact.model.RailRoad;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class GameController {
    private final List<Player> players = new ArrayList<>();
    private static final List<RailRoad> railRoads = new ArrayList<>();

    public GameController() {
        RailRoad preu = new RailRoad(0, "Preußische Ostbahn", 3, "Black", "May build up to 4 track; 20 black track; Königsberg start");
        RailRoad nied = new RailRoad(1, "Niederschlesische", 2, "Brown", "No City Penalty; 17 brown track; Breslau start");
        RailRoad sach = new RailRoad(2, "Sächsische", 1, "Orange", "May build up to 2 track; 11 orange track; Leipzig start");
        RailRoad baye = new RailRoad(3, "Bayerische", 3, "Blue", "Track cost -1 per hex; 16 blue track; München start");
        RailRoad main = new RailRoad(4, "Main-Weser", 1, "Yellow", "Double one City Income; 14 yellow track; Kassel start");
        RailRoad badi = new RailRoad(5, "Badische", 1, "Red", "One free rural track/build; 15 red track; Mannheim start");
        RailRoad koln = new RailRoad(6, "Köln-Mindener", 1, "Purple", "$5 maximum expediture/build; 12 purple track; Essen star");
        RailRoad berl = new RailRoad(7, "Berlin-Hamburger", 1, "Green", "Must connect both to receive Dividends; 13 green track; Wittenberge start");
        railRoads.addAll(List.of(preu, nied, sach, baye, main, badi, koln, berl));
    }

    @PostMapping("/players")
    public void setupPlayers(@RequestBody List<String> playerNames) {
        for (String player : playerNames) {
            players.add(new Player(player, new ArrayList<>()));
        }
        System.out.println("added players");
    }

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return players;
    }

    @PostMapping("/add-share")
    public void addRailRoadShareToPlayer(@RequestParam String playerName,
                                         @RequestBody Integer railRoadId) {
        System.out.println(playerName + " is purchasing 1 share of " + railRoadId);
        Optional<Player> playerOptional = players.stream().filter(player -> Objects.equals(player.getPlayerName(), playerName)).findFirst();
        playerOptional.ifPresent(player -> player.getRailRoadIdShares().add(railRoadId));
    }

    @GetMapping("/rail-roads")
    public List<RailRoad> getRailRoads() {
        return railRoads;
    }

    @PostMapping("/rail-road-value")
    public void updateRailRoadValue(@RequestParam Integer railRoadId,
                                    @RequestBody Integer value) {
        Optional<RailRoad> railRoadOptional = railRoads.stream().filter(railRoad -> Objects.equals(railRoad.getId(), railRoadId)).findFirst();
        railRoadOptional.ifPresent(railRoad -> railRoad.setValue(value));
        System.out.println("hit " + railRoadId + ": " + value);
    }

    @GetMapping("/connect")
    public List<PlayerConnectScore> connectRailRoads(@RequestParam Integer railRoadId,
                                                     @RequestParam Integer numberOfNewConnections) {
        System.out.println("railRoadId: " + railRoadId + " connections: " + numberOfNewConnections);
        List<PlayerConnectScore> playerConnectScores = new ArrayList<>();
        PlayerConnectScore playerConnectScore;
        for (Player player : players) {
            playerConnectScore = new PlayerConnectScore(player.getPlayerName());
            int dividendValue;
            for (Integer heldShareId : player.getRailRoadIdShares()) {
                dividendValue = railRoads.stream().filter(railRoad -> Objects.equals(railRoad.getId(), heldShareId)).map(RailRoad::getValue).findFirst().get();
                if (Objects.equals(heldShareId, railRoadId)) {
                    dividendValue = dividendValue * numberOfNewConnections * 2;
                }
                playerConnectScore.setPayout(playerConnectScore.getPayout() + dividendValue);
            }
            playerConnectScores.add(playerConnectScore);
        }
        System.out.println(playerConnectScores);
        return playerConnectScores;
    }
}
