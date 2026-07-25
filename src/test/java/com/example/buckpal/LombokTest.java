package com.example.buckpal;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Lombok の注釈処理 (annotation processing) が効いていることを確認するテスト。
 *
 * <p>Lombok はコンパイル時にコードを生成するため、注釈処理が動いていなければ getter や
 * コンストラクタが「存在しない」となり、このテストは<b>実行以前にコンパイルで失敗する</b>。
 * つまりコンパイルが通ること自体が第一の検証で、各テストは生成されたコードの<b>挙動</b>を確認する。
 *
 * <p>ここで使っている注釈は buckpal の写経で実際に多用するものに絞ってある。
 */
@Slf4j
@DisplayName("Lombok の動作確認")
class LombokTest {

  @Nested
  @DisplayName("@Getter / @Setter / @RequiredArgsConstructor")
  class Accessors {

    @Test
    @DisplayName("final フィールドのコンストラクタと getter が生成される")
    void generatesConstructorAndGetter() {
      Account account = new Account(42L);

      assertThat(account.getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("@Setter で可変フィールドの setter が生成される")
    void generatesSetter() {
      Account account = new Account(42L);
      account.setName("振替口座");

      assertThat(account.getName()).isEqualTo("振替口座");
    }
  }

  @Nested
  @DisplayName("@Value (不変オブジェクト)")
  class ImmutableValue {

    @Test
    @DisplayName("フィールドが private final になり、クラスが final になる")
    void makesClassImmutable() throws NoSuchFieldException {
      Field amount = Money.class.getDeclaredField("amount");

      assertThat(Modifier.isPrivate(amount.getModifiers())).isTrue();
      assertThat(Modifier.isFinal(amount.getModifiers())).isTrue();
      assertThat(Modifier.isFinal(Money.class.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("値が等しければ equals / hashCode も等しい (値オブジェクトとして使える)")
    void generatesEqualsAndHashCode() {
      Money a = new Money(1000L, "JPY");
      Money b = new Money(1000L, "JPY");
      Money c = new Money(2000L, "JPY");

      assertThat(a).isEqualTo(b).isNotEqualTo(c);
      assertThat(a).hasSameHashCodeAs(b);
      // equals/hashCode が正しく生成されていれば、HashSet 上で a と b は同一視される
      assertThat(new HashSet<>(List.of(a, b, c))).hasSize(2);
    }

    @Test
    @DisplayName("toString が生成され、フィールド値を含む")
    void generatesToString() {
      assertThat(new Money(1000L, "JPY").toString()).contains("1000").contains("JPY");
    }
  }

  @Nested
  @DisplayName("@Builder")
  class BuilderPattern {

    @Test
    @DisplayName("builder で組み立てられる")
    void buildsWithBuilder() {
      Activity activity = Activity.builder().ownerAccountId(1L).amount(500L).build();

      assertThat(activity.getOwnerAccountId()).isEqualTo(1L);
      assertThat(activity.getAmount()).isEqualTo(500L);
    }

    @Test
    @DisplayName("@Builder.Default で未指定フィールドの既定値が使われる")
    void appliesBuilderDefault() {
      Activity activity = Activity.builder().ownerAccountId(1L).build();

      assertThat(activity.getAmount()).isZero();
    }
  }

  @Nested
  @DisplayName("@Slf4j")
  class Logging {

    @Test
    @DisplayName("log フィールドが生成され、クラス名のロガーになっている")
    void generatesLogger() {
      assertThat(log).isNotNull();
      assertThat(log.getName()).isEqualTo(LombokTest.class.getName());
    }
  }

  /** {@code @Getter} / {@code @Setter} / {@code @RequiredArgsConstructor} の検証用。 */
  @Getter
  @RequiredArgsConstructor
  static class Account {

    /** final なので @RequiredArgsConstructor の引数になる。 */
    private final Long id;

    /** final ではないので setter 経由で変更する。 */
    @Setter private String name;
  }

  /**
   * {@code @Value} の検証用 (不変の値オブジェクト)。
   *
   * <p>注意: ここでの {@code @Value} は {@code lombok.Value}。Spring の {@code
   * org.springframework.beans.factory.annotation.Value} と名前が衝突するため、 import を取り違えないこと。
   */
  @Value
  static class Money {

    long amount;
    String currency;
  }

  /** {@code @Builder} の検証用。 */
  @Getter
  @Builder
  static class Activity {

    private final Long ownerAccountId;

    @Builder.Default private final long amount = 0L;
  }
}
