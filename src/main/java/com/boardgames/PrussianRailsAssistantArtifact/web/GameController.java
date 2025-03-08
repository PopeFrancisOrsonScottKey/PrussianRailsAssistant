package com.boardgames.PrussianRailsAssistantArtifact.web;

import com.boardgames.PrussianRailsAssistantArtifact.model.Player;
import com.boardgames.PrussianRailsAssistantArtifact.model.PlayerConnectScore;
import com.boardgames.PrussianRailsAssistantArtifact.model.RailRoad;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
public class GameController {
    private List<Player> players = new ArrayList<>();
    private List<RailRoad> railRoads;
    private final List<RailRoad> railRoadsOriginal = new ArrayList<>();
    public static final String LOCATION = "location";

    public GameController() {
        RailRoad preu = new RailRoad(0, "Preußische Ostbahn", 3, "Black", "May build up to 4 track; 20 black track; Königsberg start", "", true);
        RailRoad nied = new RailRoad(1, "Niederschlesische", 2, "Brown", "No City Penalty; 17 brown track; Breslau start", "", true);
        RailRoad sach = new RailRoad(2, "Sächsische", 1, "Orange", "May build up to 2 track; 11 orange track; Leipzig start", "", true);
        RailRoad baye = new RailRoad(3, "Bayerische", 3, "Blue", "Track cost -1 per hex; 16 blue track; München start", "", true);
        RailRoad main = new RailRoad(4, "Main-Weser", 1, "Yellow", "Double one City Income; 14 yellow track; Kassel start", "", true);
        RailRoad badi = new RailRoad(5, "Badische", 1, "Red", "One free rural track/build; 15 red track; Mannheim start", "", true);
        RailRoad koln = new RailRoad(6, "Köln-Mindener", 1, "Purple", "$5 maximum expediture/build; 12 purple track; Essen star", "", true);
        RailRoad berl = new RailRoad(7, "Berlin-Hamburger", 1, "Green", "Must connect both to receive Dividends; 13 green track; Wittenberge start", "Connected?", false);
        railRoadsOriginal.addAll(List.of(preu, nied, sach, baye, main, badi, koln, berl));
        railRoads = new ArrayList<>();
        for (RailRoad railRoad : railRoadsOriginal) {
            RailRoad copy = new RailRoad(railRoad.getId(), railRoad.getName(), railRoad.getValue(), railRoad.getColor(), railRoad.getDescription(), railRoad.getCustom(), railRoad.getChecked());
            railRoads.add(copy);
        }
    }

    @RequestMapping("/")
    public ResponseEntity<String> homePage() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, "/players.html");
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<Void> reset() {
        System.out.println("resetting");
        railRoads = new ArrayList<>(railRoadsOriginal);
        players = new ArrayList<>();
        System.out.println("Railroads: " + railRoads);
        System.out.println("Players: " + players);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/players")
    public ResponseEntity<String> setupPlayers(@RequestBody List<String> playerNames) {
        for (String player : playerNames) {
            players.add(new Player(player, new ArrayList<>()));
        }
        System.out.println("added players " + playerNames);

        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION, "/gamepage.html");
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @GetMapping("/players")
    @ResponseBody
    public List<Player> getPlayers() {
        System.out.println("fetching players: " + players);
        return players;
    }

    @PostMapping("/add-share")
    @ResponseBody
    public ResponseEntity<Void> addRailRoadShareToPlayer(@RequestParam String playerName,
                                         @RequestBody Integer railRoadId) {
        System.out.println(playerName + " is purchasing 1 share of " + railRoadId);
        Optional<Player> playerOptional = players.stream().filter(player -> Objects.equals(player.getPlayerName(), playerName)).findFirst();
        playerOptional.ifPresent(player -> player.getRailRoadIdShares().add(railRoadId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rail-roads")
    @ResponseBody
    public ResponseEntity<List<RailRoad>> getRailRoads() {
        return ResponseEntity.ok().body(railRoads);
    }

    @PostMapping("/rail-road-value")
    @ResponseBody
    public ResponseEntity<Void> updateRailRoadValue(@RequestParam Integer railRoadId,
                                    @RequestBody Integer value) {
        Optional<RailRoad> railRoadOptional = railRoads.stream()
        .filter(railRoad -> Objects.equals(railRoad.getId(), railRoadId))
        .findFirst();
        railRoadOptional.ifPresent(railRoad -> railRoad.setValue(value));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/connect")
    @ResponseBody
    public ResponseEntity<List<PlayerConnectScore>> connectRailRoads(@RequestParam Integer railRoadId,
                                                     @RequestParam Integer numberOfNewConnections) {
        List<PlayerConnectScore> playerConnectScores = new ArrayList<>();
        PlayerConnectScore playerConnectScore;
        for (Player player : players) {
            playerConnectScore = new PlayerConnectScore(player.getPlayerName());
            int dividendValue;
            for (Integer heldShareId : player.getRailRoadIdShares()) {
                if (railRoads.stream().filter(railRoad -> Objects.equals(heldShareId, railRoad.getId())).findFirst().map(RailRoad::getChecked).orElse(false)) {
                dividendValue = railRoads.stream().filter(railRoad -> Objects.equals(railRoad.getId(), heldShareId)).map(RailRoad::getValue).findFirst().get();
                if (Objects.equals(heldShareId, railRoadId)) {
                    dividendValue = dividendValue * numberOfNewConnections * 2;
                }
                playerConnectScore.setPayout(playerConnectScore.getPayout() + dividendValue);
            }
            }
            playerConnectScores.add(playerConnectScore);
        }
        return ResponseEntity.ok().body(playerConnectScores);
    }

    @PostMapping("/update-checked")
    @ResponseBody
    public ResponseEntity<Void> updateChecked(@RequestParam Integer railRoadId,
    @RequestBody Map<String, Integer> body) {
        Optional<RailRoad> railRoadOptional = railRoads.stream().filter(railRoad -> Objects.equals(railRoadId, railRoad.getId()))
        .findFirst();
        railRoadOptional.ifPresent(railRoad -> railRoad.setChecked(body.get("value") == 1));
        return ResponseEntity.ok().build();
    }
}
