package com.kamer.builder

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import easydone.core.domain.DomainRepository
import easydone.feature.quickcreatetask.QuickCreateTaskScreen
import org.koin.android.ext.android.inject


class TransparentActivity : AppCompatActivity() {

    private val repository: DomainRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.attributes = window.attributes.apply {
            height = WindowManager.LayoutParams.MATCH_PARENT
        }

        setContent {
            QuickCreateTaskScreen(
                repository = repository,
                closeScreen = { finishAffinity() }
            )
        }
    }

}
