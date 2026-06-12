package main.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 게임 리소스 이미지 매니저
 * static 블록을 활용하여 클래스 로드 시점에 모든 이미지를 미리 메모리에 로드
 * 외부에서는 ImageLoader.배경, ImageLoader.학생_공부 등으로 접근
 */
public class ImageLoader {
    // ═══════════════════════════════════════════════════════════
    // 배경 이미지
    // ═══════════════════════════════════════════════════════════
    public static BufferedImage 배경;  // 빈강의실1.png

    // ═══════════════════════════════════════════════════════════
    // 교수님 이미지 (3종 상태)
    // ═══════════════════════════════════════════════════════════
    public static BufferedImage 상태1;    // 상태1.png (선생님 필기 중)
    public static BufferedImage 상태2;    // 상태2.png (싸한 느낌)
    public static BufferedImage 상태3;    // 상태3.png (응시)

    // ═══════════════════════════════════════════════════════════
    // 학생 이미지
    // ═══════════════════════════════════════════════════════════
    public static BufferedImage 학생_공부;     // studyy.png
    public static BufferedImage[] 학생_춤;     // dan1.png, dan2.png, dan3.png, dan4.png

    // static 블록: 클래스 로드 시 자동으로 실행
    static {
        try {
            // 배경 로드
            배경 = loadImage("빈강의실1.png");

            // 교수님 이미지 로드
            상태1 = loadImage("상태1.png");
            상태2 = loadImage("상태2.png");
            상태3 = loadImage("상태3.png");

            // 학생 공부 이미지 로드 (파일명: studyy.png)
            학생_공부 = loadImage("studyy.png");

            // 학생 춤 애니메이션 프레임 로드 (4프레임)
            학생_춤 = new BufferedImage[4];
            학생_춤[0] = loadImage("dan1.png");
            학생_춤[1] = loadImage("dan2.png");
            학생_춤[2] = loadImage("dan3.png");
            학생_춤[3] = loadImage("dan4.png");

            System.out.println("✓ 모든 이미지 리소스 로드 완료");
        } catch (Exception e) {
            System.err.println("✗ 이미지 로드 중 오류 발생:");
            e.printStackTrace();
        }
    }

    /**
     * src/main/resources 폴더에서 이미지를 로드하는 헬퍼 메서드
     * @param filename 로드할 이미지 파일명
     * @return 로드된 BufferedImage 객체
     */
    private static BufferedImage loadImage(String filename) {
        File[] candidates = {
                new File("src/main/resources/" + filename),
                new File("./src/main/resources/" + filename),
                new File("../src/main/resources/" + filename),
                new File("../../src/main/resources/" + filename)
        };

        for (File candidate : candidates) {
            if (candidate.exists()) {
                try {
                    BufferedImage image = ImageIO.read(candidate);
                    System.out.println("  ✓ 로드됨: " + filename);
                    return image;
                } catch (Exception e) {
                    System.err.println("  ✗ 읽기 실패: " + filename + " (" + e.getMessage() + ")");
                }
            }
        }

        System.err.println("  ✗ 파일을 찾을 수 없음: " + filename);
        return null;
    }
}

