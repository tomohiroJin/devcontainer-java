package buckpal.application.domain.model;

import java.time.LocalDateTime;

public class Account {
    private AccountId id;
    private Money baselineBalance; // 基準日が始まる時点の残高
    private ActivityWindow activityWindow; // 取引の履歴

    // コンストラクタやGetter

    // 残高を計算する
    public Money calculateBalance() {
        return Money.add(
                this.baselineBalance,
                this.activityWindow.calculateBalance(this.id)

        );
    }

    public boolean withdraw(Money money, AccountId targetAccountId) {
        Activity activity = new Activity(
                this.id, // 自分
                this.id, // 送金元
                targetAccountId, // 送金先
                LocalDateTime.now(),
                money);
        this.activityWindow.addActivity(activity);
        return true;
    }

    // 引き出せる金額か？
    private boolean mayWithdraw(Money money) {
        return Money
                .add(this.caluculateBalance(), money.negate())
                .isPositive();
    }

    // 自身の口座に預け入れる
    // ※送金元の口座から自身の口座に送金された、という取引を取引履歴に追加する
    public boolean deposit(Money money, AccountId sourceAccountId) {
        Activity deposit = new Activity(
                this.id, // 口座所有者の口座ID
                sourceAccountId, // 送金元の口座ID
                this.id, // 送金元のID
                LocalDateTim.now(),
                money);
        this.actibityWindow.addActibity(deposit);
        return true;
    }
}
