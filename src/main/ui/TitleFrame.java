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
 * 
 * 이미지 자동 감지:
 * - src/main/resources 폴더를 스캔하여 첫 번째 이미지 파일(.png, .jpg, .jpeg, .gif)을 자동으로 감지하고 로드
 * - 파일명을 하드코딩하지 않으므로, 리소스 폴더에 이미지 파일을 아무 이름으로 넣어도 자동 인식
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

        // 투명한 시작 버튼: 칠판 전체 영역을 덮도록 배치
        JButton startButton = new JButton();
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 버튼 위치와 크기 설정 (칠판 전체 영역)
        int bx = 20;     // 시작 X 좌표
        int by = 60;     // 시작 Y 좌표
        int bw = 440;    // 너비
        int bh = 500;    // 높이
        startButton.setBounds(bx, by, bw, bh);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // JOptionPane으로 플레이어 이름 입력받기
                String name = JOptionPane.showInputDialog(
                        TitleFrame.this,
                        "교실에서 몰래 춤출 학생의 이름을 입력하세요:",
                        "게임 입학 신청서",
                        JOptionPane.QUESTION_MESSAGE
                );

                // 입력 취소 또는 빈 입력 처리
                if (name == null || name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                            TitleFrame.this,
                            "이름이 입력되지 않아 게임을 종료합니다.",
                            "안내",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                // Main에 플레이어 이름을 설정하고 게임 세션 시작
                Main.setPlayerName(name);
                TitleFrame.this.dispose();
                Main.startNewGameSession();
            }
        });

        bgPanel.add(startButton);
    }

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
     * 지원 형식: .png, .jpg, .jpeg, .gif
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

