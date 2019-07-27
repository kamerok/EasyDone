package easydone.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.StartFlow


class TransparentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent)
        window.attributes = window.attributes.apply {
            height = WindowManager.LayoutParams.MATCH_PARENT
        }

        StartFlow.startCreate(this, R.id.containerView)
    }

}