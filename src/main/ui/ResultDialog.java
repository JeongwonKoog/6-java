package main.ui;

import main.model.Player;
import main.service.RankingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 게임 종료 후 결과 리포트 및 재시작을 제어하는 다이얼로그
 * [F-14] 결과 리포트 출력 (현재 등수, 최고 점수, 랭킹 리스트)
 * [F-15] 재시작 및 종료 제어
 */
public class ResultDialog extends JDialog {
    private final RankingService rankingService;
    private final Player currentPlayer;
    private final List<Player> allPlayers;

    private boolean restartRequested = false; // 재시작 여부를 프레임에 전달할 플래그

    /**
     * @param parent         부모 프레임 (MainFrame)
     * @param currentPlayer  방금 게임을 끝낸 플레이어 객체
     * @param allPlayers     전체 플레이어 기록 리스트 (CsvStorage에서 로드한 데이터)
     * @param rankingService 랭킹 계산용 서비스
     */
    public ResultDialog(JFrame parent, Player currentPlayer, List<Player> allPlayers, RankingService rankingService) {
        super(parent, "게임 결과 (Game Over)", true); // true로 설정하여 모달창으로 구동
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        this.rankingService = rankingService;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        initComponents();
    }

    private void initComponents() {
        // --- 1. 상단 배너 영역 ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(231, 76, 60)); // 빨간색 알림 배너
        JLabel titleLabel = new JLabel("📊 GAME OVER REPORT 📊");
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. 중앙 컨텐츠 영역 (현재 성적 + 리더보드) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // [F-14] 현재 플레이어 성적 요약
        int myRank = rankingService.getPlayerRank(allPlayers, currentPlayer.getId());
        int highScore = rankingService.getHighScore(allPlayers);

        JLabel scoreSummaryLabel = new JLabel("<html>" +
                "<h3>플레이어: <font color='blue'>" + currentPlayer.getName() + "</font></h3>" +
                "<h3>이번 점수: <font color='red'>" + currentPlayer.getScore() + " 점</font></h3>" +
                "<h3>현재 순위: <b>" + (myRank == -1 ? "집계 중" : myRank + " 위") + "</b></h3>" +
                "<h3>역대 최고 점수: " + highScore + " 점</h3>" +
                "</html>");
        scoreSummaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(scoreSummaryLabel);
        centerPanel.add(Box.createVerticalStrut(20)); // 간격 띄우기

        // [F-14] 역대 상위 5명 리더보드 출력
        JLabel rankTitle = new JLabel("🏆 실시간 TOP 5 랭킹 🏆");
        rankTitle.setFont(new Font("나눔고딕", Font.BOLD, 14));
        rankTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(rankTitle);
        centerPanel.add(Box.createVerticalStrut(5));

        // 랭킹 리스트를 텍스트 영역에 예쁘게 포맷팅
        JTextArea rankArea = new JTextArea();
        rankArea.setEditable(false);
        rankArea.setBackground(new Color(245, 245, 245));
        rankArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        rankArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Player> top5 = rankingService.getTopRankings(allPlayers, 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < top5.size(); i++) {
            Player p = top5.get(i);
            sb.append(String.format(" %d위.  %-10s \t %5d 점\n", (i + 1), p.getName(), p.getScore()));
        }
        if (top5.isEmpty()) {
            sb.append(" 아직 등록된 랭킹 데이터가 없습니다.");
        }
        rankArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(rankArea);
        scrollPane.setPreferredSize(new Dimension(340, 150));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. 하단 버튼 영역 [F-15] ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton restartButton = new JButton("다시 하기 (Retry)");
        restartButton.setFont(new Font("나눔고딕", Font.BOLD, 14));
        restartButton.addActionListener(e -> {
            restartRequested = true; // 재시작 플래그 ON
            dispose();               // 다이얼로그 닫기
        });

        JButton exitButton = new JButton("게임 종료 (Exit)");
        exitButton.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        exitButton.addActionListener(e -> {
            restartRequested = false;
            dispose();
        });

        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 프레임(MainFrame)에서 유저가 재시작 버튼을 눌렀는지 확인할 수 있게 해주는 메소드
     */
    public boolean isRestartRequested() {
        return restartRequested;
    }
}