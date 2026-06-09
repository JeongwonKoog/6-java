package model;

public class GameState {

    private boolean running;
    private boolean paused;
    private boolean gameOver;

    private int score;
    private int level;

    public GameState() {
        this.running = false;
        this.paused = false;
        this.gameOver = false;
        this.score = 0;
        this.level = 1;
    }

    public void startGame() {
        running = true;
        paused = false;
        gameOver = false;
    }

    public void pauseGame() {
        paused = true;
    }

    public void resumeGame() {
        paused = false;
    }

    public void endGame() {
        running = false;
        gameOver = true;
    }

    public void increaseScore() {
        score++;
    }

    public void increaseLevel() {
        level++;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void reset() {
        running = false;
        paused = false;
        gameOver = false;
        score = 0;
        level = 1;
    }
}