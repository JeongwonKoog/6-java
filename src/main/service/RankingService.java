package main.service;

import main.model.Player;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * 플레이어 기록을 정렬하고 통계를 계산하는 서비스
 * [F-09] Stream API 기반 랭킹 정렬 및 통계 처리
 */
public class RankingService {

    /**
     * 전체 플레이어 목록을 점수 높은 순으로 정렬하여 상위 N명만 반환합니다.
     * 점수가 같을 경우, 먼저 달성한 사람(Timestamp가 작은 사람)이 우선순위를 가집니다.
     *
     * @param allPlayers 전체 플레이어 리스트
     * @param limit      추출할 상위 등수 제한 (예: 10명)
     * @return 정렬 및 제한된 플레이어 리스트
     */
    public List<Player> getTopRankings(List<Player> allPlayers, int limit) {
        return allPlayers.stream()
                // 1순위: 점수 내림차순(reversed), 2순위: 달성 시간 오름차순(먼저 달성한 순)
                .sorted(Comparator.comparingInt(Player::getScore).reversed()
                        .thenComparingLong(Player::getTimestamp))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 역대 최고 점수를 스트림으로 계산하여 반환합니다.
     */
    public int getHighScore(List<Player> allPlayers) {
        return allPlayers.stream()
                .mapToInt(Player::getScore)
                .max()
                .orElse(0); // 데이터가 없으면 0점 반환
    }

    /**
     * 플레이어들의 평균 점수를 계산하여 반환합니다.
     */
    public double getAverageScore(List<Player> allPlayers) {
        return allPlayers.stream()
                .mapToDouble(Player::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * 특정 플레이어의 등수를 계산합니다.
     * (자신보다 점수가 높은 사람의 수 + 1)
     *
     * @param allPlayers 전체 플레이어 리스트
     * @param targetId   찾고자 하는 플레이어의 고유 ID
     * @return 1부터 시작하는 등수, 찾지 못하면 -1
     */
    public int getPlayerRank(List<Player> allPlayers, String targetId) {
        // 먼저 정렬된 리스트를 뽑아옴
        List<Player> sortedRank = allPlayers.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed()
                        .thenComparingLong(Player::getTimestamp))
                .collect(Collectors.toList());

        // 스트림 인덱스를 활용해 해당 ID의 위치를 찾음
        for (int i = 0; i < sortedRank.size(); i++) {
            if (sortedRank.get(i).getId().equals(targetId)) {
                return i + 1; // 0등은 없으니 +1
            }
        }
        return -1;
    }
}