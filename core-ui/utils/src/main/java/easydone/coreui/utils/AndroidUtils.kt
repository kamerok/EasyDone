package easydone.coreui.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager


fun Context.showKeyboard() = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)