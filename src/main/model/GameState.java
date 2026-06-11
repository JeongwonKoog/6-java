package main.model;

public class GameState {
    public enum TeacherState {
        TEACHING, // 칠판 보는 중 (안전)
        WARNING,  // 돌아보기 직전 (경고)
        LOOKING   // 뒤돌아봄 (위험)
    }

    private TeacherState currentTeacherState = TeacherState.TEACHING;
    private int currentScore = 0;
    private boolean isGameOver = false;

    // Getters and Setters
    public TeacherState getCurrentTeacherState() { return currentTeacherState; }
    public void setCurrentTeacherState(TeacherState state) { this.currentTeacherState = state; }
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int score) { this.currentScore = score; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
}