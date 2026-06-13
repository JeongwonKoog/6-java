package main.ui;

import main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 게임 시작 타이틀 화면: 배경 이미지 자동 감지 및 투명한 시작 버튼
 */
public class TitleFrame extends JFrame {
    private BufferedImage backgroundImage;
    private static final String[] SUPPORTED_FORMATS = {".png", ".jpg", ".jpeg", ".gif"};

    public TitleFrame() {
        setTitle("게임 타이틀");
        setSize(480, 650);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 배경 이미지 자동 감지 및 로드
        loadBackgroundImage();

        // 배경을 그리는 패널
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // 이미지를 창 크기에 맞춰 꽉 차게 렌더링
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // 이미지가 없을 때는 기본 배경색
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setLayout(null); // 절대 위치로 버튼 배치
        setContentPane(bgPanel);

        // ---------------------------------------------------------------------
        // 1. 투명한 시작 버튼: 칠판 영역 배치
        // ---------------------------------------------------------------------
        JButton startButton = new JButton();
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int bx = 180;    // 시작 X 좌표
        int by = 120;    // 시작 Y 좌표
        int bw = 270;    // 너비
        int bh = 320;    // 높이
        startButton.setBounds(bx, by, bw, bh);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        TitleFrame.this,
                        "교실에서 몰래 춤출 학생의 이름을 입력하세요:",
                        "게임 입학 신청서",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (name == null || name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            TitleFrame.this,
                            "이름이 입력되지 않아 게임을 종료합니다.",
                            "안내",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                Main.setPlayerName(name);
                TitleFrame.this.dispose();
                Main.startNewGameSession();
            }
        });
        bgPanel.add(startButton);

        // ---------------------------------------------------------------------
        // 2. ⬜ 깔끔한 네모칸 형태의 "설정" 버튼 추가 (생성자 안으로 안전하게 진입!)
        // ---------------------------------------------------------------------
        JButton settingsButton = new JButton("설정"); // 주석 해제 완료
        settingsButton.setFont(new Font("나눔고딕", Font.BOLD, 15));
        settingsButton.setBackground(Color.WHITE);
        settingsButton.setForeground(Color.BLACK);

        // 두께 2짜리 깔끔한 검은색 네모 테두리 선 적용
        settingsButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Swing에서 버튼 배경색과 테두리가 강제로 보이도록 설정
        settingsButton.setOpaque(true);
        settingsButton.setContentAreaFilled(true);
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 우측 하단 빈 공간에 배치
        int sx = 365;
        int sy = 540;
        int sw = 85;
        int sh = 40;
        settingsButton.setBounds(sx, sy, sw, sh);

        // 설정 버튼 클릭 이벤트 (팝업 창 띄우기)
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsDialog settingsDialog = new SettingsDialog(TitleFrame.this);
                settingsDialog.setVisible(true);
            }
        });

        bgPanel.add(settingsButton); // 패널에 설정 버튼 최종 추가

    } // <-- TitleFrame() 생성자가 닫히는 중괄호 위치가 여기가 맞습니다!

    /**
     * src/main/resources 폴더에서 첫 번째 이미지 파일을 자동으로 찾아서 로드
     */
    private void loadBackgroundImage() {
        File imageFile = findImageFileInResources();

        if (imageFile != null && imageFile.exists()) {
            try {
                backgroundImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.err.println("✗ 이미지 파일 읽기 오류: " + imageFile.getName());
                backgroundImage = null;
            }
        } else {
            System.err.println("✗ src/main/resources 폴더에서 이미지 파일을 찾을 수 없습니다.");
            backgroundImage = null;
        }
    }

    /**
     * src/main/resources 폴더에서 첫 번째 이미지 파일을 찾아 반환
     */
    private File findImageFileInResources() {
        File resourcesDir = new File("src/main/resources");

        if (!resourcesDir.exists()) {
            File[] candidates = {
                    new File("src/main/resources"),
                    new File("./src/main/resources"),
                    new File("../src/main/resources"),
                    new File("../../src/main/resources")
            };

            for (File candidate : candidates) {
                if (candidate.exists() && candidate.isDirectory()) {
                    resourcesDir = candidate;
                    break;
                }
            }
        }

        if (!resourcesDir.exists() || !resourcesDir.isDirectory()) {
            return null;
        }

        File[] imageFiles = resourcesDir.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            for (String format : SUPPORTED_FORMATS) {
                if (lowerName.endsWith(format)) {
                    return true;
                }
            }
            return false;
        });

        if (imageFiles != null && imageFiles.length > 0) {
            return imageFiles[0];
        }

        return null;
    }
}