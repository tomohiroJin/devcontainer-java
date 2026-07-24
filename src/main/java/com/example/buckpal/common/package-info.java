/**
 * 層をまたいで使う共通のステレオタイプ注釈などを置く。
 *
 * <p>書籍では {@code @UseCase}, {@code @WebAdapter}, {@code @PersistenceAdapter} といった 独自注釈を定義し、Spring の
 * {@code @Component} を意味付けしつつ ArchUnit の注釈ベース検査に使う。 これらは写経で自分の手で追加していく。
 */
package com.example.buckpal.common;
