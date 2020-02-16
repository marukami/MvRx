package com.example.mvrx_navigation_example

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.navigation.MvRxNavDirections
import com.airbnb.mvrx.navigation.MvRxNavDirections.Companion.KEY_ARG_TITLE
import com.airbnb.mvrx.navigation.navGraphViewModel
import com.airbnb.mvrx.withState

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseMvRxFragment() {

    private val viewModel: NavigationViewModel by navGraphViewModel(R.id.nav_graph_viewModel)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            withState(viewModel) {
                findNavController().navigate(
                        R.id.action_FirstFragment_to_SecondFragment,
                        bundleOf(KEY_ARG_TITLE to it.powerLevel.toString())
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun invalidate() {
        withState(viewModel) {
            view!!.findViewById<TextView>(R.id.textview_first).text = "${it.name} ${it.powerLevel}"
        }
    }
}

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class StartFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_firstFragment)
        }
    }

}
