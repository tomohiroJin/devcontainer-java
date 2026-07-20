package buckpal.application.domain.model;

public class Account {
    private AccountId id;
    private Money baselineBalance; // 基準日が始まる時点の残高
    private ActivityWindow activityWindow; // 取引の履歴
    
    //　コンストラクタやGetter


    // 残高を計算する
    public Money calculateBalance() {
        return Money.add(
            this.baselineBalance,
            this.activityWindow.calculateBalance(this.id)

        );
    }
}