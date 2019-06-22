package com.kamer.easydone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamer.builder.StartFlow

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StartFlow.start(this, R.id.containerView)
    }
}
