import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

/**
 * Mockito の3種類のテストダブルの使い方の例。ドメインコードに依存させず
 * (かつリファクタリングで壊れないようにするため)、協働オブジェクトには
 * JDK 標準の型だけを使う。
 *
 * <ul>
 *   <li><b>Stub</b> — 決まった値を返すだけの偽物。テスト対象に、本来なら
 *       得られない入力を与えるために使う。
 *   <li><b>Mock</b> — やりとりの記録を後から {@code verify(...)} で
 *       確認するための偽物。
 *   <li><b>Spy</b> — 本物のオブジェクトを包んだもの。明示的にスタブしない
 *       限り本物のメソッドが動くので、一部のメソッドだけ差し替えられる。
 * </ul>
 */
class MockitoExamplesTest {

    // ---- Stub: 決まった値を返す -----------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void stub_returnsCannedValues() {
        List<String> stubbedList = mock(List.class);
        when(stubbedList.get(0)).thenReturn("first");
        when(stubbedList.size()).thenReturn(3);

        assertEquals("first", stubbedList.get(0));
        assertEquals(3, stubbedList.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void stub_returnsDifferentValuesOnSuccessiveCallsThenThrows() {
        Iterator<String> stubbedIterator = mock(Iterator.class);
        when(stubbedIterator.hasNext()).thenReturn(true, true, false);
        when(stubbedIterator.next()).thenReturn("a", "b").thenThrow(new NoSuchElementException());

        List<String> collected = new ArrayList<>();
        while (stubbedIterator.hasNext()) {
            collected.add(stubbedIterator.next());
        }

        assertEquals(List.of("a", "b"), collected);
    }

    // ---- Mock: テスト対象が協働オブジェクトをどう使ったか確認する -----------

    @Test
    @SuppressWarnings("unchecked")
    void mock_verifiesEachElementWasPassedToTheCollaborator() {
        Consumer<String> mockConsumer = mock(Consumer.class);

        List.of("Alice", "Bob", "Carol").forEach(mockConsumer);

        verify(mockConsumer).accept("Alice");
        verify(mockConsumer).accept("Bob");
        verify(mockConsumer, times(3)).accept(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void mock_verifiesCollaboratorWasConsultedForEveryElement() {
        Predicate<Integer> mockPredicate = mock(Predicate.class);
        when(mockPredicate.test(anyInt())).thenReturn(false);

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));
        numbers.removeIf(mockPredicate);

        verify(mockPredicate, times(3)).test(anyInt());
        assertEquals(3, numbers.size()); // mock が常に false を返すので、何も削除されない
    }

    // ---- Spy: 本物のオブジェクトを包み、一部のメソッドだけ差し替える -------

    @Test
    void spy_realMethodsRunUnlessStubbed() {
        List<String> spiedList = spy(new ArrayList<>());

        spiedList.add("real");

        assertEquals(1, spiedList.size()); // 本物の ArrayList の動作
        assertEquals("real", spiedList.get(0)); // 本物の ArrayList の動作
        verify(spiedList).add("real");
    }

    @Test
    void spy_stubsOneMethodWhileOthersStayReal() {
        List<String> spiedList = spy(new ArrayList<>());
        spiedList.add("a");
        spiedList.add("b");

        // when(spiedList.size()) と書くと本物の size() が先に呼ばれてしまうため、
        // spy に対しては doReturn(...).when(...) の形で書く必要がある。
        doReturn(100).when(spiedList).size();

        assertEquals(100, spiedList.size()); // スタブした値
        assertEquals("a", spiedList.get(0)); // ここは本物のまま
    }
}
