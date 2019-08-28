package easydone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.StartFlow

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.background_light)

        StartFlow.start(this, R.id.containerView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
