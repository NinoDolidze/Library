package com.example.library

import android.app.Application
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.library.databinding.ActivityPdfAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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

        binding.attachPdfButton.setOnClickListener {
            pdfPickIntent()
        }

        binding.submitButton.setOnClickListener {
            vaidateData()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun vaidateData() {
        Log.d(TAG, "vaidateData: validating data")

        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        if (title.isEmpty()){
            Toast.makeText(this, "შეიყვანეთ სახელი...", Toast.LENGTH_SHORT).show()
        }

        else if (description.isEmpty()){
            Toast.makeText(this, "შეიყვანეთ აღწერა...", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()){
            Toast.makeText(this, "აირჩიეთ კატეგორია...", Toast.LENGTH_SHORT).show()
        }

        else if (pdfUri == null){
            Toast.makeText(this, "აირჩიეთ PDF ფაილი...", Toast.LENGTH_SHORT).show()
        }

        else {
            uploadPdfToStorage()
        }


    }

    private fun uploadPdfToStorage() {

        Log.d(TAG, "uploadPdfToStorage: uploading to storage...")

        progressDialog.setMessage("მიმდინარეობს ატვირთვა")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

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
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF არჩეულია ")
                pdfUri = result.data!!.data
            }
            else{
                Log.d(TAG, "PDF არჩევა შეწყვეტილია ")
                Toast.makeText(this, "პროცესი შეჩერებულია", Toast.LENGTH_SHORT).show()
            }
        }
    )
}