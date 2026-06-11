package main.storage;

import main.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 파일 I/O 및 데이터 동기화를 담당하는 클래스
 * [F-01] 데이터 파일 저장
 * [F-08] 데이터 파일 로드
 * [F-10] 데이터 정합성 및 예외 처리
 */
public class CsvStorage {
    private final String filePath;

    /**
     * @param filePath 저장할 CSV 파일의 경로 (예: "data/rankings.csv")
     */
    public CsvStorage(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * [F-01] 현재 플레이어 리스트를 CSV 파일에 덮어씌워 저장합니다.
     */
    public void savePlayers(List<Player> players) {
        // Try-with-resources 구문으로 BufferedWriter를 자동으로 close 해줍니다.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                writer.write(player.toCsvRow());
                writer.newLine(); // 줄바꿈
            }
        } catch (IOException e) {
            System.err.println("❌ [CsvStorage] 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * [F-08] CSV 파일로부터 플레이어 데이터를 읽어와 리스트로 반환합니다.
     */
    public List<Player> loadPlayers() {
        List<Player> players = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return players; // 파일이 없으면 빈 리스트 반환
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // 빈 줄은 패스

                // [F-10] 데이터 정합성 체크 및 파싱
                try {
                    Player player = parseCsvLine(line);
                    players.add(player);
                } catch (Exception e) {
                    // 한 줄이 깨졌다고 프로그램이 멈추지 않도록 예외를 잡고 다음 줄로 진행
                    System.err.println("⚠️ [CsvStorage] 손상된 데이터 라인 건너뜀: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ [CsvStorage] 파일 읽기 중 오류 발생: " + e.getMessage());
        }

        return players;
    }

    /**
     * CSV 한 줄을 읽어서 Player 객체로 변환하는 헬퍼 메소드
     */
    private Player parseCsvLine(String line) {
        String[] tokens = line.split(",");

        // 데이터 개수가 맞지 않으면 예외 발생 (id, name, score, timestamp 총 4개 필요)
        if (tokens.length < 4) {
            throw new IllegalArgumentException("데이터 컬럼 부족");
        }

        String id = tokens[0].trim();
        String name = tokens[1].trim();
        int score = Integer.parseInt(tokens[2].trim());
        long timestamp = Long.parseLong(tokens[3].trim());

        return new Player(id, name, score, timestamp);
    }

    /**
     * 파일이나 상위 디렉토리가 없으면 자동으로 생성해 주는 헬퍼 메소드
     */
    private void ensureFileExists() {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        // data/ 폴더 같은 상위 폴더가 없다면 생성
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
}