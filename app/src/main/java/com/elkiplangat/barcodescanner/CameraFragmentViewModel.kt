package com.elkiplangat.barcodescanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elkiplangat.barcodescanner.barcode.BarCode
import com.elkiplangat.barcodescanner.db.BarCodeHistoryDb
import com.elkiplangat.barcodescanner.db.BarcodeHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CameraFragmentViewModel(application: Application):AndroidViewModel(application) {
    private val repository: BarcodeHistoryRepository

    init {
        val historyDao =
            BarCodeHistoryDb.getDatabaseInstance(application.applicationContext, viewModelScope)
                .historyDao()
        repository = BarcodeHistoryRepository(historyDao)
    }

    fun insertBarCode(barcode: BarCode) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                repository.insertBarCodeToDb(barcode)


            }
        }
    }
}