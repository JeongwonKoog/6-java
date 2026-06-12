package main.ui;

import main.model.GameState;
import main.model.Player;
import main.service.EngineService;
import main.service.EventService;
import main.service.RankingService;
import main.storage.CsvStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 게임 메인 프레임: 단일 캔버스 방식으로 게임 화면 렌더링
 * (기존 상/중/하 패널 분할 방식에서 단일 통합 캔버스 방식으로 개편)
 */
public class MainFrame extends JFrame {
    private final GameState gameState;
    private final Player player;
    private final CsvStorage csvStorage;
    private final RankingService rankingService;

    private EngineService engineService;
    private EventService eventService;

    // UI 컴포넌트
    private GameCanvasPanel gameCanvasPanel;
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
        setLayout(new BorderLayout());

        initComponents();
        initServices();
        startGame();
    }

    private void initComponents() {
        // 게임 캔버스 패널 생성 (메인 렌더링 영역)
        gameCanvasPanel = new GameCanvasPanel(gameState, null, highScore); // engineService는 나중에 주입
        add(gameCanvasPanel, BorderLayout.CENTER);

        // 춤 버튼 (하단 고정)
        danceButton = new JButton("PUSH TO DANCE!");
        danceButton.setFont(new Font("Courier New", Font.BOLD, 22));
        danceButton.setBackground(new Color(231, 76, 60));
        danceButton.setForeground(Color.WHITE);
        danceButton.setFocusable(false);
        danceButton.setPreferredSize(new Dimension(0, 70));
        danceButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        danceButton.setBorder(BorderFactory.createRaisedBevelBorder());

        add(danceButton, BorderLayout.SOUTH);
    }

    private void initServices() {
        // EngineService 생성 (uiRefreshCallback으로 gameCanvasPanel.repaint() 호출)
        this.engineService = new EngineService(
                gameState,
                player,
                () -> gameCanvasPanel.repaint(), // 캔버스 갱신
                this::handleGameOver              // 게임 오버 처리
        );

        // GameCanvasPanel에 engineService 주입 (이미지 상태 변경용)
        gameCanvasPanel = new GameCanvasPanel(gameState, engineService, highScore);
        getContentPane().removeAll();
        add(gameCanvasPanel, BorderLayout.CENTER);
        add(danceButton, BorderLayout.SOUTH);

        // EventService는 선생님 상태 변화 담당 (수정 없음)
        this.eventService = new EventService(gameState, () -> gameCanvasPanel.repaint());

        // 춤 버튼 마우스 이벤트 리스너
        danceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                engineService.startDancing();
                danceButton.setBackground(new Color(192, 57, 43));
                gameCanvasPanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                engineService.stopDancing();
                danceButton.setBackground(new Color(231, 76, 60));
                gameCanvasPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                engineService.stopDancing();
                danceButton.setBackground(new Color(231, 76, 60));
                gameCanvasPanel.repaint();
            }
        });
    }

    private void startGame() {
        engineService.startEngine();
        eventService.startTeacherRoutine();
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