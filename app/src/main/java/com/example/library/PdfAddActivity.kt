package com.example.library

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.appcompat.app.AlertDialog
import com.example.library.databinding.ActivityPdfAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private var pdfUri: Uri? = null

    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("გთხოვთ მოიცადოთ")
        progressDialog.setCanceledOnTouchOutside(false)
        
        binding.categoryTv.setOnClickListener { 
            categoryPickDialog()
        }
    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories")
        categoryArrayList = ArrayList()


        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: Showing pdf category pick dialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ამოირჩიეთ კატეგორია")
            .setItems(categoriesArray){dialog, which ->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
                
                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected Category Title: $selectedCategoryTitle")

            }
            .show()
    }
    private fun pdfPickIntent(){
        Log.d(TAG, "pdfPickIntent: ")
    }
}