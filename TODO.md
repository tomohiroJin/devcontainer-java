## TODO

- [ ] 文字列を逆順にした結果を返す（例："hello" → "olleh"）
    - [x] "hello" を渡すと "olleh" が返す
    - [ ] "world" を渡すと "dlrow" が返す
- [ ] 空文字列 `""` を渡した場合は空文字列 `""` を返す
- [ ] null を渡した場合は空文字列 `""` を返す



## お題
文字列の反転
与えられた文字列を逆順に並び替えるメソッドを実装してください。

📋 要件
メソッド名は `reverse` とし、`String` 型の引数を1つ受け取り、`String` 型を返す
文字列を逆順にした結果を返す（例："hello" → "olleh"）
空文字列 `""` を渡した場合は空文字列 `""` を返す
null を渡した場合は空文字列 `""` を返す
🧪 テスト例
assertEquals("olleh", reverse("hello")); // "hello" を逆順にすると "olleh"
assertEquals("", reverse("")); // 空文字列はそのまま空文字列を返す
assertEquals("", reverse(null)); // null は空文字列を返す
💡 ヒント
・ `StringBuilder` クラスには文字列を逆順にする `reverse()` メソッドがあります
・ null チェックはメソッドの最初に行い、早期リターンするとシンプルに書けます