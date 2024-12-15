
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    @Test
    void testAdd() {
        assertEquals(5, HelloWorld.add(2, 3), "2 + 3 should equal 5");
    }
}
