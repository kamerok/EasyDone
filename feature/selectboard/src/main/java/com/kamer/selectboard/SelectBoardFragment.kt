package com.kamer.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_select_board.*


class SelectBoardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectBoardView.setOnClickListener {
            Toast.makeText(requireContext(), "selected", Toast.LENGTH_SHORT).show()
//            findNavController().popBackStack(R.id.loginFragment, true)
        }
    }

}
