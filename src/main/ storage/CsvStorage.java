package storage;

import model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {

    private static final String FILE_NAME = "players.csv";

    public CsvStorage() {
        // Milestone 1: 프로그램 시작 시 초기 빈 파일 생성 및 예외 처리
        ensureFileExists();
    }

    /**
     * 파일이 존재하지 않으면 안전하게 새로 생성합니다. (Milestone 1 요구사항)
     */
    private void ensureFileExists() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("[Storage] 초기 데이터 파일(" + FILE_NAME + ")이 생성되었습니다.");
                }
            } catch (IOException e) {
                System.err.println("[Storage] 초기 파일 생성 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * CSV 저장 - 실시간 동기화 및 멀티스레드 안전성 확보 (F-10, Milestone 1)
     */
    public synchronized void savePlayer(Player player) {
        // 데이터 오염 방지: 이름에 포함된 쉼표(,)를 공백으로 치환
        String safeName = player.getPlayerName().replace(",", " ");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(player.getPlayerId() + "," + safeName + "," + player.getScore());
            writer.newLine();
            // 실시간으로 디스크에 기록 보장 (동기화 안정성 확보)
            writer.flush();
        } catch (IOException e) {
            System.err.println("[Storage] 저장 오류 발생 (권한 또는 I/O 오류)");
            e.printStackTrace();
        }
    }

    /**
     * CSV 전체 읽기 - RankingService(Stream API)가 바로 사용할 수 있도록 List<Player>로 반환 구조 변경
     */
    public synchronized List<Player> loadPlayers() {
        List<Player> players = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return players;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // 빈 줄 스킵

                String[] data = line.split(",");
                if (data.length == 3) {
                    try {
                        String id = data[0];
                        String name = data[1];
                        int score = Integer.parseInt(data[2]);

                        // 변환 후 리스트에 담기
                        players.add(new Player(id, name, score));
                    } catch (NumberFormatException e) {
                        System.err.println("[Storage] 데이터 파싱 실패 (점수 포맷 오류): " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Storage] 읽기 오류 발생");
            e.printStackTrace();
        }

        return players;
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean exists() {
        return new File(FILE_NAME).exists();
    }

    /**
     * 파일 초기화 (테스트 및 초기화용, 스레드 안전성 확보)
     */
    public synchronized void clear() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("[Storage] 파일 초기화 오류 발생");
            e.printStackTrace();
        }
    }
}