package com.kamer.easydone


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private val loginHolder by lazy { LoginHolder(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!loginHolder.hasToken()) {
            findNavController().navigate("easydone://login".toUri())
        }
    }

}
