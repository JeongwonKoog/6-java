package main.ui; // 본인의 패키지 경로에 맞게 적어주세요 (ui 또는 service)

import javax.sound.sampled.*;
import java.io.File;

/**
 * 게임 사운드 매니저 (프로젝트 폴더 직접 탐색 버전)
 */
public class SoundManager {
    private static Clip bgmClip; // 배경음악을 담을 클립 객체
    private static float currentVolume = 0.5f; // 🟢 현재 음량 (0.0f: 음소거 ~ 1.0f: 최대 음량)

    /**
     * 배경음악 재생 (무한 반복)
     */
    public static void playBGM(String filename) {
        try {
            // 이미 음악이 재생 중이라면 중복 재생 방지
            if (bgmClip != null && bgmClip.isRunning()) {
                return;
            }

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

            // 🟢 재생 직전에 현재 설정된 음량을 주입합니다.
            applyVolume();

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

    /**
     * 🟢 실시간으로 음량을 설정하는 메서드 (0.0f ~ 1.0f)
     */
    public static void setVolume(float volume) {
        currentVolume = volume;
        if (currentVolume < 0.0f) currentVolume = 0.0f;
        if (currentVolume > 1.0f) currentVolume = 1.0f;

        // 사운드 클립이 켜져 있다면 실시간으로 하드웨어 볼륨 변경
        applyVolume();
    }

    /**
     * 🟢 현재 설정된 음량 수치 반환
     */
    public static float getVolume() {
        return currentVolume;
    }

    /**
     * 🟢 오디오 하드웨어(Clip)에 실제 데시벨(dB) 수치를 적용하는 내부 메서드
     */
    private static void applyVolume() {
        if (bgmClip != null && bgmClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);

            if (currentVolume == 0.0f) {
                // 음량이 0이면 시스템 최저 데시벨로 내려 아예 소리를 끕니다.
                gainControl.setValue(gainControl.getMinimum());
            } else {
                // 선형 수치(0~1)를 하드웨어가 인식하는 데시벨(dB) 공식으로 변환
                float dB = (float) (Math.log10(currentVolume) * 20.0);

                // 데시벨 허용 범위(MIN~MAX) 안전장치 제한
                if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
                if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();

                gainControl.setValue(dB);
            }
        }
    }
}