package easydone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.StartFlow


class TransparentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent)

        StartFlow.startCreate(this, R.id.containerView)
    }

}