package main.ui;

import main.model.GameState;
import main.model.GameState.TeacherState;
import main.service.EngineService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 게임의 메인 렌더링 캔버스: 이미지 기반 애니메이션
 * - 배경, 학생(공부/춤), 교수님, UI를 레이어링하여 렌더링
 * - 애니메이션 타이머로 춤 프레임 순환
 */
public class GameCanvasPanel extends JPanel {
    private final GameState gameState;
    private final EngineService engineService;
    private int highScore = 0;

    // 애니메이션 타이머
    private Timer animationTimer;
    private int currentFrame = 0;  // 춤 애니메이션 현재 프레임 (0~3)

    // 캐릭터 위치 및 크기 (픽셀)
    private static final int STUDENT_X = 200;      // 학생 X 좌표
    private static final int STUDENT_Y = 450;      // 학생 Y 좌표
    private static final int STUDENT_WIDTH = 120;  // 학생 너비
    private static final int STUDENT_HEIGHT = 150; // 학생 높이

    private static final int TEACHER_X = 350;       // 교수님 X 좌표
    private static final int TEACHER_Y = 100;       // 교수님 Y 좌표
    private static final int TEACHER_WIDTH = 200;  // 교수님 너비
    private static final int TEACHER_HEIGHT = 300; // 교수님 높이

    public GameCanvasPanel(GameState gameState, EngineService engineService, int highScore) {
        this.gameState = gameState;
        this.engineService = engineService;
        this.highScore = highScore;

        setPreferredSize(new Dimension(480, 580));
        setBackground(Color.BLACK);

        // 애니메이션 타이머 초기화 (150ms 간격)
        initAnimationTimer();
    }

    /**
     * 애니메이션 타이머 초기화: 150ms마다 춤 프레임 업데이트
     */
    private void initAnimationTimer() {
        animationTimer = new Timer(150, e -> {
            // 춤을 추고 있을 때만 프레임 증가
            if (engineService != null && engineService.isDancing()) {
                currentFrame = (currentFrame + 1) % 4;  // 0~3 순환
            }
            // 항상 화면 갱신
            repaint();
        });
        animationTimer.start();
    }

    /**
     * 모든 게임 요소를 레이어링하여 렌더링
     * 순서: 배경 → 학생 → 교수님 → UI
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 🎨 [레이어 1] 배경 이미지 그리기
        drawBackground(g);

        // 🎨 [레이어 2] 학생 캐릭터 그리기 (공부 또는 춤)
        drawStudent(g);

        // 🎨 [레이어 3] 교수님 캐릭터 그리기 (상태별)
        drawTeacher(g);

        // 🎨 [레이어 4] UI 그리기 (점수, 버튼 영역 안내 등)
        drawUI(g2d);

        // 🎨 [레이어 5] 게임 오버 오버레이
        if (gameState.isGameOver()) {
            drawGameOverOverlay(g2d);
        }
    }

    /**
     * 배경 이미지 그리기
     */
    private void drawBackground(Graphics g) {
        if (ImageLoader.배경 != null) {
            g.drawImage(ImageLoader.배경, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(222, 184, 135));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 학생 캐릭터 그리기
     * - isDancing() true: 춤 애니메이션 (currentFrame 기반)
     * - isDancing() false: 공부 이미지
     */
    private void drawStudent(Graphics g) {
        BufferedImage studentImage = null;

        if (engineService != null && engineService.isDancing()) {
            // 춤 애니메이션: 현재 프레임에 해당하는 이미지 선택
            if (ImageLoader.학생_춤 != null && ImageLoader.학생_춤.length > currentFrame) {
                studentImage = ImageLoader.학생_춤[currentFrame];
            }
        } else {
            // 공부 상태: 공부 이미지 표시
            studentImage = ImageLoader.학생_공부;
        }

        if (studentImage != null) {
            g.drawImage(studentImage, STUDENT_X, STUDENT_Y, STUDENT_WIDTH, STUDENT_HEIGHT, null);
        }
    }

    /**
     * 교수님 캐릭터 그리기
     * TeacherState(TEACHING/WARNING/LOOKING)에 따라 이미지 선택
     */
    private void drawTeacher(Graphics g) {
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
    }

    /**
     * UI 렌더링: 점수, 하이스코어, 버튼 안내 텍스트
     */
    private void drawUI(Graphics2D g2d) {
        // 현재 점수 업데이트
        if (gameState.getCurrentScore() > highScore) {
            highScore = gameState.getCurrentScore();
        }

        // 점수 표시 (상단 좌측)
        g2d.setFont(new Font("Courier New", Font.BOLD, 18));
        g2d.setColor(Color.GREEN);
        String scoreText = String.format("SCORE: %04d", gameState.getCurrentScore());
        g2d.drawString(scoreText, 20, 530);

        // 하이스코어 표시 (상단 우측)
        g2d.setColor(Color.ORANGE);
        String hiScoreText = String.format("HI-SCORE: %04d", highScore);
        g2d.drawString(hiScoreText, 280, 530);

        // 상태 표시 (중앙 하단)
        g2d.setFont(new Font("나눔고딕", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String statusText = (engineService != null && engineService.isDancing()) ? "🎶 춤 중..." : "대기 중";
        g2d.drawString(statusText, 200, 555);
    }

    /**
     * 게임 오버 시 표시할 오버레이
     */
    private void drawGameOverOverlay(Graphics2D g2d) {
        // 반투명 검정색 오버레이
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 게임 오버 텍스트
        g2d.setFont(new Font("나눔고딕", Font.BOLD, 48));
        g2d.setColor(Color.RED);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        g2d.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2 - 40);

        // 최종 점수 표시
        g2d.setFont(new Font("Courier New", Font.BOLD, 28));
        g2d.setColor(Color.YELLOW);
        String finalScoreText = String.format("FINAL SCORE: %04d", gameState.getCurrentScore());
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(finalScoreText);
        g2d.drawString(finalScoreText, (getWidth() - textWidth) / 2, getHeight() / 2 + 20);
    }

    /**
     * 애니메이션 타이머 정지 (프레임 클리어)
     */
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        currentFrame = 0;
    }

    /**
     * 애니메이션 타이머 재개
     */
    public void resumeAnimation() {
        if (animationTimer != null && !animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    /**
     * 하이스코어 외부 업데이트
     */
    public void updateHighScore(int newHighScore) {
        this.highScore = newHighScore;
    }
}
