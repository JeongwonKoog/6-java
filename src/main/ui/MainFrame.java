package main.ui;

import main.model.GameState;
import main.model.Player;
import main.service.EngineService;
import main.service.EventService;
import main.service.RankingService;
import main.storage.CsvStorage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 폰트 깨짐 없이 HTML 기반 졸라맨(사람 모양) 캐릭터가 움직이는 메인 GUI
 */
public class MainFrame extends JFrame {
    private final GameState gameState;
    private final Player player;
    private final CsvStorage csvStorage;
    private final RankingService rankingService;

    private EngineService engineService;
    private EventService eventService;

    // UI 컴포넌트
    private JPanel teacherPanel;
    private JLabel teacherStatusLabel;
    private JLabel teacherSignLabel;
    private JLabel studentEmojiLabel;  // 🟢 HTML 졸라맨이 그려질 레이블
    private JLabel studentStatusLabel;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JButton danceButton;

    private int highScore = 0;

    public MainFrame(GameState gameState, Player player, CsvStorage csvStorage, RankingService rankingService) {
        this.gameState = gameState;
        this.player = player;
        this.csvStorage = csvStorage;
        this.rankingService = rankingService;

        this.highScore = rankingService.getHighScore(csvStorage.loadPlayers());

        setTitle("뒤돌아보지 마! - 아케이드 에디션");
        setSize(480, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout(15, 15));

        initComponents();
        initServices();
        startGame();
    }

    private void initComponents() {
        // --- 1. 상단: 선생님 영역 (칠판 & 텍스트 경고창) ---
        teacherPanel = new JPanel(new GridLayout(2, 1));
        teacherPanel.setPreferredSize(new Dimension(480, 140));
        teacherPanel.setBackground(new Color(46, 204, 113));
        teacherPanel.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20), 5));

        teacherSignLabel = new JLabel("( -_-)📝", SwingConstants.CENTER);
        teacherSignLabel.setFont(new Font("Courier New", Font.BOLD, 36));
        teacherSignLabel.setForeground(Color.WHITE);

        teacherStatusLabel = new JLabel("선생님이 칠판을 보고 있습니다.", SwingConstants.CENTER);
        teacherStatusLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        teacherStatusLabel.setForeground(Color.WHITE);

        teacherPanel.add(teacherSignLabel);
        teacherPanel.add(teacherStatusLabel);
        add(teacherPanel, BorderLayout.NORTH);

        // --- 2. 중앙: 교실 영역 (HTML 고해상도 졸라맨 배치) ---
        JPanel classroomPanel = new JPanel(new BorderLayout());
        classroomPanel.setBackground(new Color(222, 184, 135));
        classroomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 30, 20), 6),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // 🟢 초기 상태: 단정하게 서 있는 사람 모양 (HTML + 공백 정렬 활용)
        studentEmojiLabel = new JLabel(getStandingStatus(), SwingConstants.CENTER);
        studentEmojiLabel.setForeground(new Color(40, 30, 20));

        studentStatusLabel = new JLabel("얌전히 펜을 잡고 공부하는 척...", SwingConstants.CENTER);
        studentStatusLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        studentStatusLabel.setForeground(new Color(60, 40, 20));

        classroomPanel.add(studentEmojiLabel, BorderLayout.CENTER);
        classroomPanel.add(studentStatusLabel, BorderLayout.SOUTH);
        add(classroomPanel, BorderLayout.CENTER);

        // --- 3. 하단: 게임 HUD 및 제어판 ---
        JPanel bottomContainer = new JPanel(new BorderLayout(10, 10));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JPanel hudPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        hudPanel.setBackground(Color.BLACK);
        hudPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        scoreLabel = new JLabel("SCORE: 0000", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Courier New", Font.BOLD, 22));
        scoreLabel.setForeground(Color.GREEN);
        scoreLabel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "NOW", TitledBorder.CENTER, TitledBorder.TOP, null, Color.GRAY));

        highScoreLabel = new JLabel(String.format("HI-SCORE: %04d", highScore), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Courier New", Font.BOLD, 22));
        highScoreLabel.setForeground(Color.ORANGE);
        highScoreLabel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "BEST", TitledBorder.CENTER, TitledBorder.TOP, null, Color.GRAY));

        hudPanel.add(scoreLabel);
        hudPanel.add(highScoreLabel);
        bottomContainer.add(hudPanel, BorderLayout.NORTH);

        danceButton = new JButton("PUSH TO DANCE!");
        danceButton.setFont(new Font("Courier New", Font.BOLD, 22));
        danceButton.setBackground(new Color(231, 76, 60));
        danceButton.setForeground(Color.WHITE);
        danceButton.setFocusable(false);
        danceButton.setPreferredSize(new Dimension(0, 70));
        danceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        danceButton.setBorder(BorderFactory.createRaisedBevelBorder());

        bottomContainer.add(danceButton, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private void initServices() {
        this.engineService = new EngineService(gameState, player, this::refreshUI, this::handleGameOver);
        this.eventService = new EventService(gameState, this::refreshUI);

        danceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                engineService.startDancing();
                danceButton.setBackground(new Color(192, 57, 43));
                refreshUI();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                engineService.stopDancing();
                danceButton.setBackground(new Color(231, 76, 60));
                refreshUI();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                engineService.stopDancing();
                danceButton.setBackground(new Color(231, 76, 60));
                refreshUI();
            }
        });
    }

    private void startGame() {
        engineService.startEngine();
        eventService.startTeacherRoutine();
    }

    /**
     * 실시간 화면 동기화 및 졸라맨 댄스 애니메이션 연출
     */
    private void refreshUI() {
        scoreLabel.setText(String.format("SCORE: %04d", gameState.getCurrentScore()));

        if (gameState.getCurrentScore() > highScore) {
            highScore = gameState.getCurrentScore();
            highScoreLabel.setText(String.format("HI-SCORE: %04d", highScore));
            highScoreLabel.setForeground(Color.CYAN);
        }

        // 1. 게임 오버 연출 (졸라맨 쓰러짐)
        if (gameState.isGameOver()) {
            teacherPanel.setBackground(Color.BLACK);
            teacherSignLabel.setText("( O_O)!! ");
            teacherStatusLabel.setText("!!! 거기 너 뒤로 나와 !!!");

            studentEmojiLabel.setText(getGameOverStatus());
            studentStatusLabel.setText("교무실로 끌려가는 중...");
            danceButton.setEnabled(false);
            danceButton.setBackground(Color.DARK_GRAY);
            return;
        }

        // 2. 학생 움직임 프레임 제어 (점수에 맞춰 모션 교차 변경)
        if (engineService.isDancing()) {
            if (gameState.getCurrentScore() % 2 == 0) {
                studentEmojiLabel.setText(getDanceFrameA());
            } else {
                studentEmojiLabel.setText(getDanceFrameB());
            }
            studentStatusLabel.setText("🎶 신나게 몰래 춤추는 중!! 🎶");
        } else {
            studentEmojiLabel.setText(getStandingStatus());
            studentStatusLabel.setText("얌전히 펜을 잡고 공부하는 척...");
        }

        // 3. 선생님 상태 연출
        switch (gameState.getCurrentTeacherState()) {
            case TEACHING:
                teacherPanel.setBackground(new Color(46, 204, 113));
                teacherSignLabel.setText("( -_-)📝");
                teacherStatusLabel.setText("선생님이 열심히 칠판에 필기 중입니다.");
                break;
            case WARNING:
                teacherPanel.setBackground(new Color(241, 196, 15));
                teacherSignLabel.setText("(-_-; )💦");
                teacherStatusLabel.setText("싸한 느낌... 곧 뒤를 돌아보십니다!");
                break;
            case LOOKING:
                teacherPanel.setBackground(new Color(231, 76, 60));
                teacherSignLabel.setText("(👁__👁)");
                teacherStatusLabel.setText("!!! 선생님이 뒤를 주시하고 있습니다 !!!");
                break;
        }
    }

    // ─────────────────────────────────────────────────────────
    // 🟢 고정폭 폰트와 HTML을 이용한 사람 모양(졸라맨) 그래픽 생성 서브 메소드들
    // ─────────────────────────────────────────────────────────
    private String getStandingStatus() {
        return "<html><div style='font-family:Courier New; font-size:32px; font-weight:bold; text-align:center;'>"
                + "&nbsp;&nbsp;O&nbsp;&nbsp;<br>"
                + "&nbsp;/|\\<br>"
                + "&nbsp;&nbsp;|&nbsp;&nbsp;<br>"
                + "&nbsp;/&nbsp;\\</div></html>";
    }

    private String getDanceFrameA() { // 만세 포즈
        return "<html><div style='font-family:Courier New; font-size:32px; font-weight:bold; text-align:center;'>"
                + "\\&nbsp;O&nbsp;/<br>"
                + "&nbsp;&nbsp;|&nbsp;&nbsp;<br>"
                + "&nbsp;&nbsp;|&nbsp;&nbsp;<br>"
                + "&nbsp;/&nbsp;\\</div></html>";
    }

    private String getDanceFrameB() { // 꺾기 포즈
        return "<html><div style='font-family:Courier New; font-size:32px; font-weight:bold; text-align:center;'>"
                + "&nbsp;&nbsp;O_/<br>"
                + "&nbsp;/|&nbsp;&nbsp;<br>"
                + "&nbsp;&nbsp;|&nbsp;&nbsp;<br>"
                + "&nbsp;/&nbsp;\\</div></html>";
    }

    private String getGameOverStatus() { // 바닥에 쓰러진 모습
        return "<html><div style='font-family:Courier New; font-size:32px; font-weight:bold; text-align:center;'>"
                + "<br><br>_\\_X_/_&nbsp;</div></html>";
    }

    private void handleGameOver() {
        java.util.List<Player> allPlayers = csvStorage.loadPlayers();
        allPlayers.add(player);
        csvStorage.savePlayers(allPlayers);

        ResultDialog dialog = new ResultDialog(this, player, allPlayers, rankingService);
        dialog.setVisible(true);

        if (dialog.isRestartRequested()) {
            this.dispose();
            main.Main.startNewGameSession();
        } else {
            System.exit(0);
        }
    }
}