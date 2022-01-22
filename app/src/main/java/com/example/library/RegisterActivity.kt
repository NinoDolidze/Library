package com.example.library

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import com.example.library.databinding.ActivityMainBinding
import com.example.library.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("გთხოვთ მოიცადოთ")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.registerButton.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "შეიყვანეთ თქვენი სახელი", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "შეიყვანეთ სწორი ელ-ფოსტის მისამართი", Toast.LENGTH_SHORT).show()
        }
        else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "შეიყვანეთ პაროლი", Toast.LENGTH_SHORT).show()
        }
        else if (password != confirmPassword) {
            Toast.makeText(this, "პაროლები არ ემთხვევა", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("მიმდინარეობს პროფილის შექმნა...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this,"პროფილის შექმნა ვერ მოხერხდა", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("მიმდინარეობს მონაცემების შენახვა...")
        val timestamp = System.currentTimeMills()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["timestamp"] = timestamp

        val ref = firebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "ანგარიში წარმატებით შეიქმნა", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
    }
}