package model;

import java.util.UUID;

public class Player {

    private String playerId;
    private String playerName;
    private int score;

    public Player(String playerName) {
        this.playerId = UUID.randomUUID().toString();
        this.playerName = playerName;
        this.score = 0;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int point) {
        this.score += point;
    }

    public void resetScore() {
        this.score = 0;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}