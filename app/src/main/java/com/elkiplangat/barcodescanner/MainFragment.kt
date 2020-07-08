package com.elkiplangat.barcodescanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.elkiplangat.barcodescanner.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var mainFragmentBinding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflater = LayoutInflater.from(requireContext())
        mainFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        return mainFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainFragmentBinding.materialCardViewBarCode.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_permissionsFragment)
           //
        }
        mainFragmentBinding.materialCardViewHistory.setOnClickListener {

            view.findNavController().navigate(R.id.action_mainFragment_to_scanHistoryFragment)
        }

    }
}
