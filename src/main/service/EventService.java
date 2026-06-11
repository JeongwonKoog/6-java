package main.service;

import main.model.GameState;
import main.model.GameState.TeacherState;
import javax.swing.Timer;
import java.util.Random;

public class EventService {
    private final GameState gameState;
    private final Runnable uIUpdateCallback; // 상태가 바뀔 때 UI를 갱신할 콜백
    private Timer teacherTimer;
    private final Random random = new Random();

    public EventService(GameState gameState, Runnable uIUpdateCallback) {
        this.gameState = gameState;
        this.uIUpdateCallback = uIUpdateCallback;
    }

    public void startTeacherRoutine() {
        // 기본적으로 1초~3초 사이 랜덤하게 칠판을 보다가 경고 상태로 전환
        int randomDelay = 1000 + random.nextInt(2000);

        teacherTimer = new Timer(randomDelay, e -> {
            if (gameState.isGameOver()) {
                teacherTimer.stop();
                return;
            }

            // 상태 전환 로직 (TEACHING -> WARNING -> LOOKING -> TEACHING)
            TeacherState current = gameState.getCurrentTeacherState();
            if (current == TeacherState.TEACHING) {
                gameState.setCurrentTeacherState(TeacherState.WARNING);
                teacherTimer.setInitialDelay(800); // 경고 시간은 0.8초로 고정 (난이도 조절 가능)
                teacherTimer.restart();
            } else if (current == TeacherState.WARNING) {
                gameState.setCurrentTeacherState(TeacherState.LOOKING);
                teacherTimer.setInitialDelay(1000 + random.nextInt(1000)); // 뒤돌아보는 시간 1~2초
                teacherTimer.restart();
            } else {
                gameState.setCurrentTeacherState(TeacherState.TEACHING);
                startTeacherRoutine(); // 다시 처음으로
            }

            uIUpdateCallback.run(); // UI에 변경 사항 알림
        });

        teacherTimer.setRepeats(false);
        teacherTimer.start();
    }
}