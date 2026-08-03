package buckpal.application.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final UpdateAccountStatePort updateAccountStatePort;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        // TODO: ビジネスルールに関する妥当性確認を行う
        // TODO: モデルの状態を変える
        // TODO: 処理結果を返す
    }
}
