package easydone.feature.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kamer.home.R


sealed class Tab(
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int
)

object InboxTab : Tab(R.string.inbox_tab, android.R.drawable.ic_delete)

object TodoTab : Tab(R.string.todo_tab, android.R.drawable.ic_lock_lock)