
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class BowlingGameTest {

	//- [ ] 1投目が1の時に1点
	@Test
	void _1投目が1の時に1点() {
        var sut = new BowlingGame();
        sut.roll(1);
        assertEquals(1, sut.score());
    }

    // 1投目が2で2投目が0の時に2点
    @Test
	void _1投目が2で2投目が0の時に2点() {
        var sut = new BowlingGame();
        sut.roll(2);
        sut.roll(0);
        assertEquals(2, sut.score());
    }

    // _1フレームから10フレームまで1ピンずつ倒すと20点
    @Test
	void _1フレームから10フレームまで1ピンずつ倒すと20点() {
        var sut = new BowlingGame();
        int[] rolls = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        for (int pins : rolls) sut.roll(pins);
        assertEquals(20, sut.score());
    }
}