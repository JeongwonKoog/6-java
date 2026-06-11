package main.service;

import main.model.GameState;
import main.model.GameState.TeacherState;
import main.model.Player;

import javax.swing.Timer;

/**
 * 게임의 핵심 루프 및 판정을 담당하는 서비스
 * [F-03] 사망 판정 (선생님이 볼 때 춤추면 게임 오버)
 * [F-05] 난이도 가속 (점수가 높아질수록 가중치 및 속도 변화 가능)
 */
public class EngineService {
    private final GameState gameState;
    private final Player player;
    private final Runnable uiRefreshCallback; // 화면 점수 및 상태 갱신용 콜백
    private final Runnable gameOverCallback;   // 게임 오버 시 ResultDialog 등을 띄울 콜백

    private Timer gameLoopTimer;
    private boolean isDancing = false;         // 현재 플레이어가 버튼을 누르고 있는지 여부

    /**
     * @param gameState         게임 전체 상태 모델
     * @param player            현재 플레이 중인 유저 모델
     * @param uiRefreshCallback UI를 리프레시하기 위한 콜백 함수
     * @param gameOverCallback  게임 오버 처리를 위한 콜백 함수
     */
    public EngineService(GameState gameState, Player player, Runnable uiRefreshCallback, Runnable gameOverCallback) {
        this.gameState = gameState;
        this.player = player;
        this.uiRefreshCallback = uiRefreshCallback;
        this.gameOverCallback = gameOverCallback;

        initGameLoop();
    }

    /**
     * 실시간 판정 및 점수 누적을 위한 틱(Tick) 타이머 초기화
     * 대략 0.1초(100ms)마다 플레이어 상태를 체크합니다.
     */
    private void initGameLoop() {
        gameLoopTimer = new Timer(100, e -> {
            if (gameState.isGameOver()) {
                stopEngine();
                return;
            }

            // [F-03] 사망 판정: 선생님이 뒤돌아봤는데(LOOKING) 춤추고 있다면(isDancing)
            if (gameState.getCurrentTeacherState() == TeacherState.LOOKING && isDancing) {
                handleGameOver();
                return;
            }

            // 플레이어가 안전하게 춤추고 있다면 점수 가산
            if (isDancing && gameState.getCurrentTeacherState() == TeacherState.TEACHING) {
                // [F-05] 난이도 가속/보상 설계: 점수가 높아질수록 기본 틱당 획득 점수 상승
                int scoreBonus = calculateScoreBonus();
                player.addScore(scoreBonus);
                gameState.setCurrentScore(player.getScore());
            }

            // UI 스레드에 화면 갱신 요청
            uiRefreshCallback.run();
        });
    }

    /**
     * 현재 점수에 따른 가속 가중치 계산 [F-05]
     */
    private int calculateScoreBonus() {
        int current = player.getScore();
        if (current > 500) return 3;  // 500점 초과 시 틱당 3점씩 폭등
        if (current > 200) return 2;  // 200점 초과 시 틱당 2점씩 상승
        return 1;                     // 기본 점수
    }

    /**
     * 플레이어가 춤을 추기 시작할 때 호출 (Mouse Press / Key Press 이벤트와 연동)
     */
    public void startDancing() {
        if (gameState.isGameOver()) return;
        this.isDancing = true;
    }

    /**
     * 플레이어가 춤을 멈췄을 때 호출 (Mouse Release / Key Release 이벤트와 연동)
     */
    public void stopDancing() {
        this.isDancing = false;
    }

    /**
     * 게임 엔진 시작
     */
    public void startEngine() {
        if (gameLoopTimer != null && !gameLoopTimer.isRunning()) {
            gameLoopTimer.start();
        }
    }

    /**
     * 게임 엔진 정지
     */
    public void stopEngine() {
        if (gameLoopTimer != null && gameLoopTimer.isRunning()) {
            gameLoopTimer.stop();
        }
    }

    /**
     * 걸렸을 때의 게임 오버 처리
     */
    private void handleGameOver() {
        gameState.setGameOver(true);
        stopEngine();
        uiRefreshCallback.run(); // 마지막 걸린 상태 UI 반영
        gameOverCallback.run();  // 결과창 팝업 등 후속 처리
    }

    public boolean isDancing() {
        return isDancing;
    }
}