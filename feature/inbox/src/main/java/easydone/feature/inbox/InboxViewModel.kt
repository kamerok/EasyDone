package easydone.feature.inbox

import androidx.lifecycle.ViewModel
import easydone.core.domain.DomainRepository


internal class InboxViewModel(
    repository: DomainRepository,
    navigator: InboxNavigator
) : ViewModel() {
}
