package com.elkiplangat.barcodescanner.barcode

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "barcodes_table")
data class BarCode(val type: Int?, val value: String?, @PrimaryKey val timeTaken: Date) {
    //, val scanTime:Long
}