package main.ui;

import main.model.GameState;
import main.model.GameState.TeacherState;
import main.service.EngineService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * 게임의 메인 렌더링 캔버스: 이미지 기반 애니메이션 및 이펙트 처리
 */
public class GameCanvasPanel extends JPanel {
    private final GameState gameState;
    private final EngineService engineService;
    private int highScore = 0;

    // 애니메이션 타이머
    private Timer animationTimer;
    private int currentFrame = 0;       // 춤 애니메이션 현재 프레임 (0~3)
    private boolean wasDancing = false;  // 직전 상태가 춤추는 중이었는지 기억하는 변수
    private boolean flashToggle = false; // 경고창 깜빡임 연출용 플래그

    // 캐릭터 위치 및 크기 (픽셀)
    private static final int STUDENT_X = 150;      // 학생 X 좌표
    private static final int STUDENT_Y = 300;      // 학생 Y 좌표
    private static final int STUDENT_WIDTH = 200;  // 학생 너비
    private static final int STUDENT_HEIGHT = 250; // 학생 높이

    private static final int TEACHER_X = 90;       // 교수님 X 좌표
    private static final int TEACHER_Y = 160;      // 교수님 Y 좌표
    private static final int TEACHER_WIDTH = 210;  // 교수님 너비
    private static final int TEACHER_HEIGHT = 280; // 교수님 높이

    public GameCanvasPanel(GameState gameState, EngineService engineService, int highScore) {
        this.gameState = gameState;
        this.engineService = engineService;
        this.highScore = highScore;

        setPreferredSize(new Dimension(480, 580));
        setBackground(Color.BLACK);

        initAnimationTimer();
        initKeyBindings();
    }

    private void initAnimationTimer() {
        animationTimer = new Timer(150, e -> {
            boolean isCurrentlyDancing = (engineService != null && engineService.isDancing());

            // 경고 이펙트 깜빡임 토글
            flashToggle = !flashToggle;

            if (isCurrentlyDancing) {
                currentFrame = (currentFrame + 1) % 4;

                if (!wasDancing && !gameState.isGameOver()) {
                    SoundManager.playBGM("bgm.wav");
                }
            } else {
                if (wasDancing) {
                    SoundManager.stopBGM();
                }
            }

            if (gameState.isGameOver() && wasDancing) {
                SoundManager.stopBGM();
            }

            wasDancing = isCurrentlyDancing;
            repaint();
        });
        animationTimer.start();
    }

    private void initKeyBindings() {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "startDancing");
        actionMap.put("startDancing", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engineService != null && !gameState.isGameOver()) {
                    engineService.startDancing();
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "stopDancing");
        actionMap.put("stopDancing", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engineService != null) {
                    engineService.stopDancing();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 0) 배경
        if (ImageLoader.배경 != null) {
            g.drawImage(ImageLoader.배경, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(222, 184, 135));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 1) 교수님 그리기
        BufferedImage teacherImage = null;
        if (gameState.getCurrentTeacherState() == TeacherState.TEACHING) {
            teacherImage = ImageLoader.상태1;
        } else if (gameState.getCurrentTeacherState() == TeacherState.WARNING) {
            teacherImage = ImageLoader.상태2;
        } else if (gameState.getCurrentTeacherState() == TeacherState.LOOKING) {
            teacherImage = ImageLoader.상태3;
        }
        if (teacherImage != null) {
            g.drawImage(teacherImage, TEACHER_X, TEACHER_Y, TEACHER_WIDTH, TEACHER_HEIGHT, null);
        }

        // 🟢 [3번 연출 추가] 교수님 경고(WARNING) 상태일 때 머리 위에 깜빡이는 위기경보 이펙트
        if (gameState.getCurrentTeacherState() == TeacherState.WARNING) {
            g2d.setFont(new Font("나눔고딕", Font.BOLD, 22));
            if (flashToggle) {
                g2d.setColor(Color.RED);
                g2d.drawString("⚠️ WARNING! ⚠️", TEACHER_X + 25, TEACHER_Y - 15);
            } else {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("  🚨 CAUTION 🚨  ", TEACHER_X + 25, TEACHER_Y - 15);
            }
        }

        // 2) 학생 그리기
        BufferedImage studentImage = null;
        if (engineService != null && engineService.isDancing()) {
            if (ImageLoader.학생_춤 != null && ImageLoader.학생_춤.length > currentFrame) {
                studentImage = ImageLoader.학생_춤[currentFrame];
            }
        } else {
            studentImage = ImageLoader.학생_공부;
        }
        if (studentImage != null) {
            g.drawImage(studentImage, STUDENT_X, STUDENT_Y, STUDENT_WIDTH, STUDENT_HEIGHT, null);
        }

        // 🟢 [2번 콤보 추가] 학생 머리 위에 실시간 콤보 및 피버 타임 이펙트 그리기
        if (engineService != null && engineService.getCombo() > 0) {
            int currentCombo = engineService.getCombo();

            if (gameState.getCurrentTeacherState() == TeacherState.WARNING) {
                // 경고 중에 춤추면 미친 듯이 반짝이는 크리티컬 피버!
                g2d.setFont(new Font("Impact", Font.ITALIC, 28));
                g2d.setColor(flashToggle ? Color.CYAN : Color.MAGENTA);
                g2d.drawString("🔥 DOUBLE FEVER! 🔥", STUDENT_X, STUDENT_Y - 40);
            } else if (currentCombo >= 15) {
                // 15콤보 이상일 때 일반 피버 상태
                g2d.setFont(new Font("Impact", Font.BOLD, 24));
                g2d.setColor(Color.ORANGE);
                g2d.drawString("💥 FEVER TIME 💥", STUDENT_X + 15, STUDENT_Y - 40);
            }

            // 기본 콤보 수치 출력
            g2d.setFont(new Font("Impact", Font.PLAIN, 22));
            g2d.setColor(Color.YELLOW);
            g2d.drawString(currentCombo + " COMBO", STUDENT_X + 45, STUDENT_Y - 15);
        }

        drawUI(g2d);
        if (gameState.isGameOver()) {
            drawGameOverOverlay(g2d);
        }
    }

    private void drawUI(Graphics2D g2d) {
        if (gameState.getCurrentScore() > highScore) {
            highScore = gameState.getCurrentScore();
        }

        g2d.setFont(new Font("Courier New", Font.BOLD, 18));
        g2d.setColor(Color.GREEN);
        String scoreText = String.format("SCORE: %04d", gameState.getCurrentScore());
        g2d.drawString(scoreText, 20, 530);

        g2d.setColor(Color.ORANGE);
        String hiScoreText = String.format("HI-SCORE: %04d", highScore);
        g2d.drawString(hiScoreText, 280, 530);

        g2d.setFont(new Font("나눔고딕", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String statusText = (engineService != null && engineService.isDancing()) ? "🎶 춤 중..." : "대기 중";
        g2d.drawString(statusText, 200, 555);
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("나눔고딕", Font.BOLD, 48));
        g2d.setColor(Color.RED);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        g2d.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2 - 40);

        g2d.setFont(new Font("Courier New", Font.BOLD, 28));
        g2d.setColor(Color.YELLOW);
        String finalScoreText = String.format("FINAL SCORE: %04d", gameState.getCurrentScore());
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(finalScoreText);
        g2d.drawString(finalScoreText, (getWidth() - textWidth) / 2, getHeight() / 2 + 20);
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        currentFrame = 0;
    }

    public void resumeAnimation() {
        if (animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    public void updateHighScore(int newHighScore) {
        this.highScore = newHighScore;
    }
}