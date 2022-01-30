package com.example.library

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        fun formatTimestamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView){
            val TAG = "PDF_SIZE_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {storageMetaData->
                    Log.d(TAG, "loadPdfSize: got metadata")
                    val bytes = storageMetaData.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    val kb = bytes/1024
                    val mb = kb/1024
                    if (mb>=1){
                        sizeTv.text = "${String.format("$.2f", mb)} MB"
                    }
                    else if (kb>=1){
                        sizeTv.text = "${String.format("$.2f", kb)} KB"
                    }
                    else{
                        sizeTv.text = "${String.format("$.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener {e->
                    Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
                }
        }
    }
}