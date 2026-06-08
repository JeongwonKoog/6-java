package storage;

import model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {

    private static final String FILE_NAME = "players.csv";

    // CSV 저장
    public void savePlayer(Player player) {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(FILE_NAME, true))) {

            writer.write(
                    player.getPlayerId() + "," +
                            player.getPlayerName() + "," +
                            player.getScore()
            );

            writer.newLine();

        } catch (IOException e) {
            System.out.println("저장 오류 발생");
            e.printStackTrace();
        }
    }

    // CSV 전체 읽기
    public List<String> loadPlayers() {

        List<String> players = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return players;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(FILE_NAME))) {

            String line;

            while ((line = reader.readLine()) != null) {
                players.add(line);
            }

        } catch (IOException e) {
            System.out.println("읽기 오류 발생");
            e.printStackTrace();
        }

        return players;
    }

    // 파일 존재 여부 확인
    public boolean exists() {
        return new File(FILE_NAME).exists();
    }

    // 파일 초기화
    public void clear() {

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter(FILE_NAME))) {

            writer.write("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}