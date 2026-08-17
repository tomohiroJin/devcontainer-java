package buckpal.application.port.in;

public record SendMoneyCommand(AccountId sourceAccountId, AccountId targetAccountId, Money money) {
    public SendMoneyCommand(AccountId sourceAccountId, AccountId targetAccountId, Money money) {

        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        requireNonNull(money);
        requireGreaterThan(money, 0);

        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
    }

}
