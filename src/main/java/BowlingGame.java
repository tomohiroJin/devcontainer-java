public class BowlingGame { 

    int _score;

    public BowlingGame() {
        _score = 0;
    }

    public void roll(int pins) {
        _score += pins;
    }

    public int score() {
        return _score;
    }
}