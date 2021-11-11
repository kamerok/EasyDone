package easydone.feature.waiting

import androidx.lifecycle.ViewModel
import easydone.core.domain.DomainRepository


internal class WaitingViewModel(
    repository: DomainRepository,
    navigator: WaitingNavigator
) : ViewModel() {
}
