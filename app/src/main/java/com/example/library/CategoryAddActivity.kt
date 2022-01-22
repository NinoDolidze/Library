package com.example.library

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.library.databinding.ActivityCategoryAddBinding
import com.example.library.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard_admin.*

class CategoryAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("გთხოვთ მოიცადოთ")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            validateData()
        }
    }

    private var category = ""

    private fun validateData() {
        category = binding.categoryEt.text.toString()

        if (category.isEmpty()) {
            Toast.makeText(this, "შეიყვანეთ კატეგორია", Toast.LENGTH_SHORT).show()
        }

        else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        Toast.makeText(this, "ვერ მოხერხდა დამატება", Toast.LENGTH_SHORT).show()
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "$(firebaseAuth.uid)"
        hashMap["userType"] = "user"

        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "წარმატებით დაემატა", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "ვერ მოხდა დამატება", Toast.LENGTH_SHORT).show()
            }
    }
}