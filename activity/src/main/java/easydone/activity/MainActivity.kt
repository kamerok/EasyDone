package easydone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.ActivityHolder
import com.kamer.builder.ActivityNavigator
import com.kamer.builder.CustomFragmentFactory
import com.kamer.builder.StartFlow
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CustomFragmentFactory()

        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(R.color.background)

        ActivityHolder.setActivity(this)
        navigator.init(this, R.id.containerView)
        if (savedInstanceState == null) {
            StartFlow.start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
