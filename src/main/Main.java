package main;

import main.model.GameState;
import main.model.Player;
import main.service.RankingService;
import main.storage.CsvStorage;
import main.ui.MainFrame;

import javax.swing.*;

/**
 * 프로그램의 진입점 (Main 클래스에는 MainFrame 코드가 섞여 있으면 안 됩니다!)
 */
public class Main {
    // 🟢 [해결 포인트 1] static 메소드에서 쓰려면 변수들도 반드시 static이어야 합니다.
    private static CsvStorage csvStorage;
    private static RankingService rankingService;
    private static String playerName;

    public static void main(String[] args) {
        // 스윙 UI 스레드 안전하게 실행
        SwingUtilities.invokeLater(() -> {
            // 인프라 서비스 초기화
            csvStorage = new CsvStorage("data/rankings.csv");
            rankingService = new RankingService();

            // 플레이어 이름 입력 받기
            playerName = JOptionPane.showInputDialog(
                    null,
                    "교실에서 몰래 춤출 학생의 이름을 입력하세요:",
                    "게임 입학 신청서",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (playerName == null || playerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "이름이 입력되지 않아 게임을 종료합니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

            // 게임 세션 시작
            startNewGameSession();
        });
    }

    /**
     * [F-15] 새로운 게임 세션을 시작하는 메소드
     */
    public static void startNewGameSession() {
        GameState gameState = new GameState();
        Player player = new Player(playerName);

        // 의존성 주입 (4개 인자 전달)
        MainFrame mainFrame = new MainFrame(gameState, player, csvStorage, rankingService);
        mainFrame.setVisible(true);
    }
}