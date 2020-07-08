package com.elkiplangat.barcodescanner.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.elkiplangat.barcodescanner.barcode.BarCode

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBarcodeToDb(barcode:BarCode)

    @Delete
    fun deleteBarCode(barcode: BarCode)

    @Query("SELECT * from barcodes_table ORDER BY timeTaken")
    fun getAllBarCodesFromDb():LiveData<List<BarCode>>
}