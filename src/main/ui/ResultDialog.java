package ui;

import javax.swing.*;
import java.awt.*;

public class ResultDialog extends JDialog {

    public ResultDialog(JFrame parent, int score) {

        super(parent, "게임 결과", true);

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel resultLabel =
                new JLabel("최종 점수 : " + score,
                        SwingConstants.CENTER);

        resultLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        JPanel buttonPanel = new JPanel();

        JButton restartButton = new JButton("재시작");
        JButton exitButton = new JButton("종료");

        restartButton.addActionListener(e -> {

            dispose();

            parent.dispose();

            MainFrame newGame = new MainFrame();
            newGame.setVisible(true);
        });

        exitButton.addActionListener(e ->
                System.exit(0));

        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        add(resultLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}