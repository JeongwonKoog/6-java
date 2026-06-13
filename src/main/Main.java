package main;

import main.model.GameState;
import main.model.Player;
import main.service.RankingService;
import main.storage.CsvStorage;
import main.ui.MainFrame;

import javax.swing.*;

/**
 * 프로그램의 진입점
 */
public class Main {
    private static CsvStorage csvStorage;
    private static RankingService rankingService;
    private static String playerName;
    private static String difficulty = "Normal"; // 👈 난이도 변수 합체 완료!

    public static void setPlayerName(String name) {
        playerName = name;
    }

    // 👈 난이도를 저장하고 불러오는 메서드 합체 완료!
    public static void setDifficulty(String diff) {
        difficulty = diff;
        System.out.println("★ 난이도가 변경되었습니다: " + diff);
    }

    public static String getDifficulty() {
        return difficulty;
    }

    public static void main(String[] args) {
        // 스윙 UI 스레드 안전하게 실행
        SwingUtilities.invokeLater(() -> {
            // 인프라 서비스 초기화
            csvStorage = new CsvStorage("data/rankings.csv");
            rankingService = new RankingService();

            // 타이틀 화면을 먼저 보여주고, 타이틀에서 이름을 입력받으면 게임을 시작합니다.
            main.ui.TitleFrame titleFrame = new main.ui.TitleFrame();
            titleFrame.setVisible(true);
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