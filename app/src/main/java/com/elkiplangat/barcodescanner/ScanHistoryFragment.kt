package com.elkiplangat.barcodescanner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.elkiplangat.barcodescanner.BarcodeHistoryRecyclerAdapter
import com.elkiplangat.barcodescanner.R
import com.elkiplangat.barcodescanner.barcode.BarCode
import com.elkiplangat.barcodescanner.databinding.FragmentScanHistoryBinding


class ScanHistoryFragment : Fragment() {
    private lateinit var scanHistoryFragmentBinding:FragmentScanHistoryBinding
    //private lateinit var scanHistoryFragmentViewModel: ScanHistoryFragmentViewModel
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(ScanHistoryFragmentViewModel::class.java) }
    val adapter = BarcodeHistoryRecyclerAdapter()
    private lateinit var localList: List<BarCode>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflater = LayoutInflater.from(requireContext())
        scanHistoryFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_history, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        scanHistoryFragmentBinding.recyclerView.adapter = adapter
        scanHistoryFragmentBinding.recyclerView.layoutManager = layoutManager
       

        return scanHistoryFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allHistoryItems.observe(viewLifecycleOwner, Observer {barcodeslist ->
            barcodeslist?.let {
                adapter.submitList(it)
                localList = it

            }
            if (localList.isNotEmpty()){

               scanHistoryFragmentBinding.linearLayoutNothing.visibility = View.GONE
            }

        })
    }
}