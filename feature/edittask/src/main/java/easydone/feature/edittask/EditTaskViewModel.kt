package easydone.feature.edittask

import androidx.lifecycle.ViewModel
import easydone.core.domain.DomainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


internal class EditTaskViewModel(
    id: String,
    repository: DomainRepository
) : ViewModel() {

    val state: StateFlow<State> = MutableStateFlow(State("", "", "", false, false))

    fun onTypeClick() {}
    fun onTitleChange(title: String) {}
    fun onDescriptionChange(description: String) {}
    fun onUrgentClick() {}
    fun onImportantClick() {}
    fun onSave() {}

}
