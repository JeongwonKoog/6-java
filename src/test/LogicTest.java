import model.GameState;
import model.Player;
import service.EngineService;
import service.RankingService;

import java.util.Arrays;
import java.util.List;

public class LogicTest {

    public static void main(String[] args) {

        testDeathLogic();
        testRankingLogic();
        testStatisticsLogic();

    }

    // F-03 사망 판정 테스트
    private static void testDeathLogic() {

        System.out.println("===== 사망 판정 테스트 =====");

        Player player = new Player("Tester");
        GameState gameState = new GameState();

        EngineService engine =
                new EngineService(player, gameState);

        boolean result =
                engine.checkDeath(true);

        System.out.println("사망 여부: " + result);

        if (result) {
            System.out.println("테스트 성공");
        } else {
            System.out.println("테스트 실패");
        }

        System.out.println();
    }

    // F-09 랭킹 정렬 테스트
    private static void testRankingLogic() {

        System.out.println("===== 랭킹 정렬 테스트 =====");

        Player p1 = new Player("국정원");
        Player p2 = new Player("엄태준");
        Player p3 = new Player("Player3");

        p1.addScore(100);
        p2.addScore(300);
        p3.addScore(200);

        List<Player> players =
                Arrays.asList(p1, p2, p3);

        RankingService rankingService =
                new RankingService();

        List<Player> ranking =
                rankingService.sortRanking(players);

        ranking.forEach(player ->
                System.out.println(
                        player.getPlayerName()
                                + " : "
                                + player.getScore()
                ));
    }

    // F-09 통계 테스트
    private static void testStatisticsLogic() {

        System.out.println();
        System.out.println("===== 통계 테스트 =====");

        Player p1 = new Player("국정원");
        Player p2 = new Player("엄태준");

        p1.addScore(100);
        p2.addScore(300);

        List<Player> players =
                Arrays.asList(p1, p2);

        RankingService rankingService =
                new RankingService();

        double average =
                rankingService.getAverageScore(players);

        int highest =
                rankingService.getHighestScore(players);

        System.out.println("평균 점수 : " + average);
        System.out.println("최고 점수 : " + highest);
    }
}