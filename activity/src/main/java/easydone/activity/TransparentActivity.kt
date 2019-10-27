package easydone.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.ActivityHolder
import com.kamer.builder.ActivityNavigator
import com.kamer.builder.CustomFragmentFactory
import com.kamer.builder.StartFlow
import org.koin.android.ext.android.inject


class TransparentActivity : AppCompatActivity(R.layout.activity_transparent) {

    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory()

        super.onCreate(savedInstanceState)
        window.attributes = window.attributes.apply {
            height = WindowManager.LayoutParams.MATCH_PARENT
        }

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) {
            StartFlow.startCreate()
        }
    }

}
