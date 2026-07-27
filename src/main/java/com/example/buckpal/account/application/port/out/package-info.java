/**
 * 出力ポート (永続化などの外部依存を抽象化したインターフェース) を置く。
 *
 * <p>サービスはここを介してのみ外界に出る。実装は adapter.out が担う。 例: {@code LoadAccountPort}, {@code
 * UpdateAccountStatePort}.
 */
package com.example.buckpal.account.application.port.out;
