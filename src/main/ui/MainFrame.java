package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JLabel scoreLabel;
    private JLabel stateLabel;
    private JButton pauseButton;

    private int score = 0;

    public MainFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Don't Look Back");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // 배경 비율 고정을 위해 창 크기 고정

        // 🎨 1. 순수 Swing Graphics로 교실 배경을 그리는 커스텀 패널 생성
        JPanel classroomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // 부드러운 그래픽 처리

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

// ----------------------------------------------------
// 또는 방법 2: 만약 속도를 가장 최우선으로 하고 싶다면 기능을 끄는 조합
// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

// 또는 방법 3: 렌더링 힌트 자체를 설정하지 않고 아예 이 줄을 삭제하기 (기본값 작동)
                int w = getWidth();
                int h = getHeight();

                // [A] 교실 벽면 (황토색)
                g2.setColor(new Color(225, 169, 23));
                g2.fillRect(0, 0, w, h);

                // [B] 하단 뒷벽/책상 라인 (갈색)
                g2.setColor(new Color(133, 23, 23));
                g2.fillRect(0, (int)(h * 0.55), w, (int)(h * 0.45));

                // 경계선
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(0, (int)(h * 0.55), w, (int)(h * 0.55));

                // [C] 중앙 대형 칠판 (짙은 녹색)
                int boardX = (int)(w * 0.15);
                int boardY = (int)(h * 0.05);
                int boardW = (int)(w * 0.65);
                int boardH = (int)(h * 0.42);

                g2.setColor(new Color(100, 100, 100)); // 테두리 회색
                g2.fillRect(boardX - 5, boardY - 5, boardW + 10, boardH + 10);
                g2.setColor(new Color(16, 122, 49));   // 칠판 녹색
                g2.fillRect(boardX, boardY, boardW, boardH);
                g2.setColor(Color.BLACK);
                g2.drawRect(boardX - 5, boardY - 5, boardW + 10, boardH + 10);

                // [D] 왼쪽 알림판 원형 아이콘 3개
                g2.setColor(Color.BLACK);
                int radius = 25;
                g2.fillOval((int)(w * 0.03), (int)(h * 0.20), radius, radius);
                g2.fillOval((int)(w * 0.03), (int)(h * 0.28), radius, radius);
                g2.fillOval((int)(w * 0.03), (int)(h * 0.36), radius, radius);

                // [E] 칠판 위 판서 내용 렌더링 (한글 깨짐 방지)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
                g2.drawString("구석기 시대", boardX + 20, boardY + 40);
                g2.drawString("- 동굴 생활", boardX + 20, boardY + 60);
                g2.drawString("- 뗀석기", boardX + 20, boardY + 80);

                g2.drawString("신석기 시대", boardX + 130, boardY + 40);
                g2.drawString("- 빗살무늬 토기", boardX + 130, boardY + 60);
                g2.drawString("- 움집", boardX + 130, boardY + 80);

                g2.drawString("청동기 시대", boardX + 260, boardY + 40);
                g2.drawString("- 청동무기 사용", boardX + 260, boardY + 60);
                g2.drawString("- 벼농사", boardX + 260, boardY + 80);
            }
        };

        // ⭐ 핵심: 칠판 패널의 레이아웃을 해제하고, 이 패널을 창의 주 무대(ContentPane)로 설정합니다!
        classroomPanel.setLayout(null);
        setContentPane(classroomPanel);

        // 🎨 2. 컴포넌트들을 JFrame이 아니라 무조건 classroomPanel에 직접 .add() 해줍니다!

        // [상단 점수] 우측 상단 배치
        scoreLabel = new JLabel("Score : 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 32));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(560, 20, 200, 50);
        classroomPanel.add(scoreLabel);

        // [중앙 상태] 칠판 중앙 배치
        stateLabel = new JLabel("게임 시작", SwingConstants.CENTER);
        stateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 28));
        stateLabel.setForeground(Color.WHITE);
        stateLabel.setBounds(200, 150, 400, 50);
        classroomPanel.add(stateLabel);

        // [파란색 상태 바] 원래 기획안처럼 좌측 상단 배치
        JPanel gaugeBar = new JPanel();
        gaugeBar.setBackground(new Color(45, 0, 241));
        gaugeBar.setBounds(20, 30, 180, 25);
        classroomPanel.add(gaugeBar);

        // [하단 일시정지 버튼] 위치 조정 및 이벤트 연결
        pauseButton = new JButton("일시정지");
        pauseButton.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        pauseButton.setFocusPainted(false);
        pauseButton.setBackground(Color.WHITE);
        pauseButton.setBounds(340, 510, 120, 35);

        pauseButton.addActionListener(e -> {
            stateLabel.setText("일시정지");
            stateLabel.setForeground(Color.YELLOW);
        });

        classroomPanel.add(pauseButton);
    }

    // ⭐ 원래 사용하던 비즈니스 로직 메서드들 변경 없이 그대로 유지!
    public void updateScore(int score) {
        this.score = score;
        scoreLabel.setText("Score : " + score);
    }

    public void showWarning() {
        stateLabel.setText("⚠ 뒤돌아봅니다!");
        stateLabel.setForeground(Color.RED);
    }

    public void showSafe() {
        stateLabel.setText("생존!");
        stateLabel.setForeground(Color.GREEN);
    }

    public void gameOver() {
        ResultDialog dialog = new ResultDialog(this, score);
        dialog.setVisible(true);
    }
}