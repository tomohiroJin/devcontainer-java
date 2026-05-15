
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StringBuilderTest {

    //- [ ] "hello" を渡すと "olleh" が返す
    @Test
    void helloを渡すとollehを返す() {
        assertEquals("olleh", StringBuilder.reverse("hello"), "\"hello\" should be reversed to \"olleh\"");
    }

    //"world" を渡すと "dlrow" が返す
    @Test
    void worldを渡すとdlrowを返す() {
        assertEquals("dlrow", StringBuilder.reverse("world"), "\"world\" should be reversed to \"dlrow\"");
    }
}
