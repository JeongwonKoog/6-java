package service;

import model.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RankingService {

    public List<Player> sortRanking(List<Player> players) {

        return players.stream()
                .sorted(
                        Comparator.comparingInt(Player::getScore)
                                .reversed()
                )
                .collect(Collectors.toList());
    }

    public double getAverageScore(List<Player> players) {

        return players.stream()
                .mapToInt(Player::getScore)
                .average()
                .orElse(0);
    }

    public int getHighestScore(List<Player> players) {

        return players.stream()
                .mapToInt(Player::getScore)
                .max()
                .orElse(0);
    }
}