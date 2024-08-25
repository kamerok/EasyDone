package com.kamer.builder

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import easydone.core.domain.DomainRepository
import easydone.feature.edittask.EditTaskArgs
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.edittask.EditTaskRoute
import org.koin.android.ext.android.inject


class ShareActivity : AppCompatActivity() {

    private val repository: DomainRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            EditTaskRoute(
                args = EditTaskArgs.Create(
                    text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
                ),
                repository = repository,
                navigator = object : EditTaskNavigator {
                    override fun close() {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            )
        }
    }

}
