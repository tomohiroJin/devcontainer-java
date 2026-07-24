/**
 * 出力アダプタ (永続化)。JPA エンティティ / リポジトリ / マッパーを置き、出力ポートを実装する。
 *
 * <p>依存ルール: adapter.in には依存しないこと。application.port.out を実装する。 例: {@code AccountPersistenceAdapter},
 * {@code AccountJpaEntity}, {@code SpringDataAccountRepository}.
 */
package com.example.buckpal.account.adapter.out.persistence;
