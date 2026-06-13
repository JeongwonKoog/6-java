package main.service;

import main.Main; // 👈 [추가] 설정창의 난이도를 가져오기 위해 임포트합니다.
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

                // 🟢 [난이도 조절] 느낌표가 뜨고 난 뒤, 진짜로 뒤돌아볼 때까지의 대기 시간(ms) 설정
                int warningDelay = 800; // 기본 Normal 모드: 0.8초의 유예 시간
                String difficulty = Main.getDifficulty();

                if ("Easy".equals(difficulty)) {
                    warningDelay = 1400; // 순한맛(쉬움): 1.4초 동안 깜빡여서 초보자도 안전하게 대피 가능
                } else if ("Hard".equals(difficulty)) {
                    warningDelay = 350;  // 매운맛(어려움): 0.35초 만에 빛의 속도로 돌아봄 (초인적인 반응속도 필요)
                }

                teacherTimer.setInitialDelay(warningDelay);
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