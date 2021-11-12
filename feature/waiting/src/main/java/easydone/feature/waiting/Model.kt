package easydone.feature.waiting

import easydone.coreui.design.UiTask
import java.time.LocalDate


data class State(
    val tasks: Map<LocalDate, List<UiTask>>
)
