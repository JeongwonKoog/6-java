package main.ui; // 본인의 패키지 경로에 맞게 적어주세요 (ui 또는 service)

import javax.sound.sampled.*;
import java.io.File;

/**
 * 게임 사운드 매니저 (프로젝트 폴더 직접 탐색 버전)
 */
public class SoundManager {
    private static Clip bgmClip; // 배경음악을 담을 클립 객체

    /**
     * 배경음악 재생 (무한 반복)
     */
    public static void playBGM(String filename) {
        try {
            // ImageLoader 클래스처럼 프로젝트 폴더 구조에서 직접 파일을 찾습니다.
            File audioFile = new File("src/main/resources/" + filename);

            if (!audioFile.exists()) {
                System.err.println("✗ 오디오 파일을 찾을 수 없음: " + audioFile.getAbsolutePath());
                return;
            }

            // 오디오 스트림 및 Clip 생성
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);

            // 무한 반복 설정 후 재생 시작
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
            System.out.println("✓ BGM 재생 시작: " + filename);

        } catch (Exception e) {
            System.err.println("✗ 오디오 재생 중 오류 발생:");
            e.printStackTrace();
        }
    }

    /**
     * 배경음악 정지
     */
    public static void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
            System.out.println("✓ BGM 재생 정지");
        }
    }
}