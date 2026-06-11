package main.model;

import java.util.UUID;

/**
 * 플레이어의 데이터 모델 클래스 (DTO)
 * [F-07] 고유 ID 자동 발급 기능 포함
 */
public class Player {
    private final String id;       // 고유 ID (UUID 기반 발급)
    private final String name;     // 플레이어 이름
    private int score;             // 최종 점수
    private final long timestamp;  // 플레이한 시간 기록 (랭킹 동률 시 최신순 정렬용)

    /**
     * 새로운 게임을 시작할 때 사용하는 생성자
     * 고유 ID와 현재 시간이 자동으로 발급됩니다.
     *
     * @param name 플레이어 이름
     */
    public Player(String name) {
        this.id = UUID.randomUUID().toString().substring(0, 8); // 8자리 간결한 고유 ID 생성
        this.name = name;
        this.score = 0;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * CSV 스토리지 등에서 기존 데이터를 불러올 때 사용하는 생성자
     */
    public Player(String id, String name, int score, long timestamp) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    // --- 비즈니스 로직 ---

    /**
     * 플레이어의 점수를 증가시킵니다.
     * @param amount 추가할 점수
     */
    public void addScore(int amount) {
        if (amount > 0) {
            this.score += amount;
        }
    }

    /**
     * CSV 저장용 문자열 변환 (CsvStorage에서 사용)
     */
    public String toCsvRow() {
        return String.format("%s,%s,%d,%d", id, name, score, timestamp);
    }

    // --- Getters ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}