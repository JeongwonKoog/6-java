package main.ui;

import main.Main;
import javax.swing.*;
import java.awt.*;

/**
 * 발전된 게임 설정 팝업 창 (난이도 및 조작 가이드 추가)
 */
public class SettingsDialog extends JDialog {
    // 난이도를 기억할 콤보박스를 멤버 변수로 선언
    private JComboBox<String> diffCombo;

    public SettingsDialog(JFrame parent) {
        super(parent, "게임 설정", true);

        setSize(340, 360); // 컴포넌트가 늘어나서 세로 크기를 360으로 키웠습니다.
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // 메인 컨텐츠 패널
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 245, 245));

        // [제목]
        JLabel titleLabel = new JLabel("⚙️ GAME SETTINGS");
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // [기능 1] 배경음악 볼륨 조절
        JLabel volumeLabel = new JLabel("배경음악(BGM) 볼륨");
        volumeLabel.setFont(new Font("나눔고딕", Font.BOLD, 13));
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(volumeLabel);

        JSlider volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setBackground(new Color(245, 245, 245));
        volumeSlider.setMaximumSize(new Dimension(250, 40));
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(volumeSlider);
        contentPanel.add(Box.createVerticalStrut(15));

        // [선택 1] 교실 난이도 설정 (교수님 뒤돌아보기 속도용)
        JLabel diffLabel = new JLabel("교수님 감시 난이도");
        diffLabel.setFont(new Font("나눔고딕", Font.BOLD, 13));
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(diffLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        String[] difficulties = {"순한맛 (Easy)", "평범한맛 (Normal)", "매운맛 (Hard)"};
        diffCombo = new JComboBox<>(difficulties);

        // 현재 Main에 저장된 난이도 불러와서 기본값으로 세팅 (초기값 Normal)
        String currentDiff = Main.getDifficulty();
        if (currentDiff.equals("Easy")) diffCombo.setSelectedIndex(0);
        else if (currentDiff.equals("Hard")) diffCombo.setSelectedIndex(2);
        else diffCombo.setSelectedIndex(1); // Normal

        diffCombo.setMaximumSize(new Dimension(250, 30));
        diffCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(diffCombo);
        contentPanel.add(Box.createVerticalStrut(25));

        // [선택 3] 하단 조작 가이드 안내창 (회색 박스 형태)
        JPanel guidePanel = new JPanel();
        guidePanel.setBackground(new Color(230, 235, 240));
        guidePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        guidePanel.setMaximumSize(new Dimension(270, 45));
        guidePanel.setLayout(new GridBagLayout()); // 중앙 정렬용 레이아웃

        JLabel guideLabel = new JLabel("💡 조작법: SPACE 키를 누르면 춤을 춥니다!");
        guideLabel.setFont(new Font("나눔고딕", Font.BOLD, 11));
        guideLabel.setForeground(new Color(70, 80, 95));
        guidePanel.add(guideLabel);

        contentPanel.add(guidePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // [닫기 버튼] 클릭 시 선택한 난이도를 Main에 저장하고 닫힘
        JButton closeButton = new JButton("설정 저장 및 닫기");
        closeButton.setFont(new Font("나눔고딕", Font.BOLD, 13));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        closeButton.addActionListener(e -> {
            // 선택된 아이템에 따라 Main의 난이도 세팅 변경
            int selectedIndex = diffCombo.getSelectedIndex();
            if (selectedIndex == 0) Main.setDifficulty("Easy");
            else if (selectedIndex == 2) Main.setDifficulty("Hard");
            else Main.setDifficulty("Normal");

            dispose(); // 창 닫기
        });
        contentPanel.add(closeButton);

        add(contentPanel, BorderLayout.CENTER);
    }
}