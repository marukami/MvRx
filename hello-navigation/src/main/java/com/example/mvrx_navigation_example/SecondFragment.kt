package com.example.mvrx_navigation_example

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.navigation.MvRxNavDirections
import com.airbnb.mvrx.navigation.navGraphViewModel
import com.airbnb.mvrx.withState

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseMvRxFragment() {

    private val viewModel: NavigationViewModel by navGraphViewModel(R.id.nav_graph_viewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            withState(viewModel) {
                if(it.powerLevel == 9_000) {
                    isEnabled = false
                    findNavController().popBackStack()
                }
                else viewModel.powerDown()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.powerUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            withState(viewModel) {
                findNavController().navigate(
                    R.id.action_SecondFragment_to_FirstFragment,
                    bundleOf(MvRxNavDirections.KEY_ARG_TITLE to it.powerLevel)
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun invalidate() {
        withState(viewModel) {
            view!!.findViewById<TextView>(R.id.textview_second).text =
                "${it.name} ${it.powerLevel}"
        }
    }
}
