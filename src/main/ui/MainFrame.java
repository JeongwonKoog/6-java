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

        setLayout(new BorderLayout());

        // 상단 점수
        JPanel topPanel = new JPanel();

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));

        topPanel.add(scoreLabel);

        // 중앙 상태
        stateLabel = new JLabel("게임 시작", SwingConstants.CENTER);
        stateLabel.setFont(new Font("Arial", Font.BOLD, 32));

        // 하단 버튼
        JPanel bottomPanel = new JPanel();

        pauseButton = new JButton("일시정지");

        pauseButton.addActionListener(e ->
                stateLabel.setText("일시정지"));

        bottomPanel.add(pauseButton);

        add(topPanel, BorderLayout.NORTH);
        add(stateLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

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