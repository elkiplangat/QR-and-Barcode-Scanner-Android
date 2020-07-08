package com.elkiplangat.barcodescanner.db

import androidx.lifecycle.LiveData
import com.elkiplangat.barcodescanner.barcode.BarCode

class BarcodeHistoryRepository(private val dao: HistoryDao) {
    val allHistoryItems:LiveData<List<BarCode>> = dao.getAllBarCodesFromDb()

    suspend fun insertBarCodeToDb(barCode: BarCode){
        dao.addBarcodeToDb(barCode)
    }
}