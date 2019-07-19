package easydone.coreui.design

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment


fun Fragment.setupToolbar() = (requireActivity() as AppCompatActivity).run {
    setSupportActionBar(view?.findViewById(R.id.toolbar))
}

fun Fragment.setupToolbar(titleId: Int, isBackEnabled: Boolean = true) {
    view?.findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
        toolbar.setTitle(titleId)
        (requireActivity() as AppCompatActivity).run {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackEnabled)
            supportActionBar?.setDisplayShowHomeEnabled(isBackEnabled)
        }
    }
}
