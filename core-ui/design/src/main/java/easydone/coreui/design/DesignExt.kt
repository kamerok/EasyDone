package easydone.coreui.design

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


fun Fragment.setupToolbar() =
    (requireActivity() as AppCompatActivity).setSupportActionBar(view?.findViewById(R.id.toolbar))
