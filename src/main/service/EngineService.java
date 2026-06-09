package service;

import model.GameState;
import model.Player;

/**
 * F-03 (사망 판정 및 0.1초 오차 제어), F-05 (난이도 가속 로직)를 담당하는
 * 프로젝트의 핵심 게임 루프 엔진 서비스 클래스입니다.
 */
public class EngineService {

    private final GameState gameState;
    private long loopStartTime;
    private boolean isRunning;

    // 기본 가속도 및 판정 오차 상수 설정 (단위: 밀리초)
    private static final long TIME_ERROR_MARGIN = 100; // 0.1초 오차 제어 (F-03)
    private static final double ACCELERATION_RATE = 1.15; // 난이도 가속 비율 (F-05)

    public EngineService(GameState gameState) {
        this.gameState = gameState;
        this.isRunning = false;
    }

    /**
     * 게임 핵심 루프를 시작합니다.
     */
    public void startEngine() {
        if (isRunning) return;

        this.isRunning = true;
        this.loopStartTime = System.currentTimeMillis();
        System.out.println("[Engine] 게임 루프 스레드가 시작되었습니다.");

        // 실제 스레드나 타이머 루프는 프론트엔드 UI(MainFrame)의 렌더링 루프와
        // 동기화하여 실행 흐름을 제어하게 됩니다.
    }

    /**
     * F-05: 난이도 가속 로직
     * 게임이 진행됨에 따라 속도나 난이도 요소를 가속합니다.
     */
    public double calculateAcceleratedSpeed(double baseSpeed, int currentScore) {
        // 점수가 10점 상승할 때마다 속도가 15%씩 가속되는 로직 예시
        int level = currentScore / 10;
        double finalSpeed = baseSpeed * Math.pow(ACCELERATION_RATE, level);

        System.out.printf("[Engine] 현재 난이도 레벨: %d, 가속된 속도: %.2f%n", level, finalSpeed);
        return finalSpeed;
    }

    /**
     * F-03: 사망 판정 및 0.1초(100ms) 오차 제어 로직
     *
     * @param targetTime 이벤트가 발생해야 하는 정확한 목표 시간 (System.currentTimeMillis() 기준)
     * @param userActionTime 사용자가 실제로 반응하여 입력을 수행한 시간
     * @return 사망 여부 (true: 사망 / false: 생존)
     */
    public boolean checkPlayerDeath(long targetTime, long userActionTime) {
        // 목표 시간과 사용자 입력 시간의 절대값 차이를 계산
        long timeDifference = Math.abs(targetTime - userActionTime);

        System.out.printf("[Engine] 판정 - 목표 시간: %d, 입력 시간: %d, 오차: %dms%n",
                targetTime, userActionTime, timeDifference);

        // 0.1초(100ms) 오차 범위를 초과하면 사망 판정 (F-03)
        if (timeDifference > TIME_ERROR_MARGIN) {
            System.out.println("[Engine] 0.1초 오차 범위 초과: PLAYER DEAD (사망)");
            this.isRunning = false;
            return true;
        }

        System.out.println("[Engine] 판정 통과: PLAYER ALIVE (생존)");
        return false;
    }

    /**
     * 게임을 강제로 종료하거나 정지할 때 엔진 상태를 변경합니다.
     */
    public void stopEngine() {
        this.isRunning = false;
        System.out.println("[Engine] 게임 루프가 중지되었습니다.");
    }

    public boolean isEngineRunning() {
        return this.isRunning;
    }
}