package service;

import java.util.Random;

public class EventService {

    private final Random random = new Random();

    // 이벤트 발생 여부
    private boolean eventActive = false;

    // 다음 이벤트까지 대기 시간(ms)
    private int nextEventTime;

    public EventService() {
        generateNextEventTime();
    }

    // 랜덤 이벤트 시간 생성 (3~8초)
    public void generateNextEventTime() {
        nextEventTime = random.nextInt(5000) + 3000;
    }

    // 다음 이벤트 시간 반환
    public int getNextEventTime() {
        return nextEventTime;
    }

    // 이벤트 시작
    public void startEvent() {
        eventActive = true;
    }

    // 이벤트 종료
    public void endEvent() {
        eventActive = false;
        generateNextEventTime();
    }

    // 현재 이벤트 발생 여부
    public boolean isEventActive() {
        return eventActive;
    }

    // UI 경고 메시지
    public String getWarningMessage() {
        return "⚠ 뒤돌아봅니다!";
    }
}