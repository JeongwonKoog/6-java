package model;

import java.util.UUID;

public class Player {

    private String playerId;
    private String playerName;
    private int score;

    // [원본 생성자] 이름만 넣고 새로 게임을 시작할 때 (UUID 자동 발급)
    public Player(String playerName) {
        this.playerId = UUID.randomUUID().toString();
        this.playerName = playerName;
        this.score = 0;
    }

    // ⭐ [추가된 핵심 생성자] CsvStorage.java의 59번째 줄 에러를 해결하는 열쇠!
    // 저장된 파일에서 고유 ID, 이름, 점수를 읽어와 그대로 복구할 때 사용합니다.
    public Player(String playerId, String playerName, int score) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
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

    // 💡 유지된 원본 로직: 점수 추가
    public void addScore(int point) {
        this.score += point;
    }

    // 💡 유지된 원본 로직: 점수 초기화
    public void resetScore() {
        this.score = 0;
    }

    // 💡 유지된 원본 로직: toString 디버깅용
    @Override
    public String toString() {
        return "Player{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}