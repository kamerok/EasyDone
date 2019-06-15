package com.kamer.setupflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setup.*


class SetupFragment : Fragment() {

    private lateinit var finishListener: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_setup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testView.setOnClickListener { finishListener() }
    }

    data class Dependencies(
        val finishSetupListener: () -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SetupFragment().apply {
            finishListener = dependencies.finishSetupListener
        }
    }

}