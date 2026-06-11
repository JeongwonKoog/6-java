package test;

import main.model.Player;
import main.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogicTest {

    private RankingService rankingService;
    private List<Player> dummyPlayers;

    @BeforeEach
    void setUp() {
        rankingService = new RankingService();
        dummyPlayers = new ArrayList<>();

        // 테스트용 더미 데이터 세팅 (ID, 이름, 점수, 타임스탬프)
        // 타임스탬프가 작을수록 먼저 달성한 유저임
        dummyPlayers.add(new Player("p01", "김철수", 150, 1000L));
        dummyPlayers.add(new Player("p02", "이영희", 300, 2000L));
        dummyPlayers.add(new Player("p03", "박민수", 300, 1500L)); // 이영희와 동점이지만 더 먼저 달성함
        dummyPlayers.add(new Player("p04", "최데브", 50, 3000L));
    }

    @Test
    @DisplayName("[F-07] 플레이어 생성 시 8자리 고유 ID가 자동으로 발급되는가")
    void testPlayerIdGeneration() {
        Player newPlayer = new Player("정스윙");

        assertNotNull(newPlayer.getId(), "ID는 null일 수 없습니다.");
        assertEquals(8, newPlayer.getId().length(), "자동 발급된 ID는 8자리여야 합니다.");
        assertNotNull(newPlayer.getName());
        assertEquals(0, newPlayer.getScore(), "초기 점수는 0점이어야 합니다.");
    }

    @Test
    @DisplayName("[F-09] 랭킹 정렬 시 점수 내림차순 및 동점자 타임스탬프 오름차순이 적용되는가")
    void testTopRankingsSorting() {
        // 상위 3명 추출
        List<Player> topRank = rankingService.getTopRankings(dummyPlayers, 3);

        assertEquals(3, topRank.size(), "요청한 limit만큼 리스트가 반환되어야 합니다.");

        // 1등 검증: 300점 중 타임스탬프가 더 빠른(작은) 박민수(1500L)가 와야 함
        assertEquals("박민수", topRank.get(0).getName(), "동점자일 경우 먼저 달성한 사람이 1등이어야 합니다.");

        // 2등 검증: 300점 중 타임스탬프가 늦은 이영희(2000L)
        assertEquals("이영희", topRank.get(1).getName());

        // 3등 검증: 150점 김철수
        assertEquals("김철수", topRank.get(2).getName());
    }

    @Test
    @DisplayName("[F-09] 통계 기능 - 최고 점수와 평균 점수가 올바르게 계산되는가")
    void testStatistics() {
        int highScore = rankingService.getHighScore(dummyPlayers);
        double averageScore = rankingService.getAverageScore(dummyPlayers);

        // 최고 점수 검증 (300점)
        assertEquals(300, highScore, "최고 점수는 300점이어야 합니다.");

        // 평균 점수 검증 ((150 + 300 + 300 + 50) / 4 = 200.0)
        assertEquals(200.0, averageScore, 0.001, "평균 점수는 200점이어야 합니다.");
    }

    @Test
    @DisplayName("[F-09] 특정 플레이어의 ID를 기반으로 등수가 정확히 조회되는가")
    void testPlayerRankRetrieval() {
        // 동점자 룰에 의해 박민수가 1위, 이영희가 2위여야 함
        int rankMinSu = rankingService.getPlayerRank(dummyPlayers, "p03"); // 박민수 ID
        int rankYeongHui = rankingService.getPlayerRank(dummyPlayers, "p02"); // 이영희 ID
        int rankMissing = rankingService.getPlayerRank(dummyPlayers, "unknown_id");

        assertEquals(1, rankMinSu, "박민수는 동점자 우선순위에 의해 1위여야 합니다.");
        assertEquals(2, rankYeongHui, "이영희는 2위여야 합니다.");
        assertEquals(-1, rankMissing, "존재하지 않는 ID는 -1을 반환해야 합니다.");
    }
}