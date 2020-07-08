package com.elkiplangat.barcodescanner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.elkiplangat.barcodescanner.barcode.BarCode
import com.elkiplangat.barcodescanner.db.BarCodeHistoryDb
import com.elkiplangat.barcodescanner.db.BarcodeHistoryRepository
import kotlinx.coroutines.launch

class ScanHistoryFragmentViewModel(application: Application):AndroidViewModel(application) {

    private val repository:BarcodeHistoryRepository

    val allHistoryItems:LiveData<List<BarCode>>
    var isEmpty:Boolean?

    init {
        val historyDao = BarCodeHistoryDb.getDatabaseInstance(application.applicationContext, viewModelScope).historyDao()
        repository = BarcodeHistoryRepository(historyDao)
        allHistoryItems = repository.allHistoryItems
       isEmpty = allHistoryItems.value?.isEmpty()

    }
    fun insertBarCode(barcode:BarCode){
        viewModelScope.launch {
            repository.insertBarCodeToDb(barcode)
        }
    }
}