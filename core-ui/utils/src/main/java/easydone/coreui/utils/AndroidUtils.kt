package easydone.coreui.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun EditText.showKeyboard() =
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)