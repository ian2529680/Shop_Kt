package com.home.shop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_singup.*

class SingupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)
        btn_singup.setOnClickListener {
            val email = ed_email.text.toString()
            val password = ed_password.text.toString()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        AlertDialog.Builder(this).setTitle("Sing up")
                            .setMessage("Account created")
                            .setPositiveButton("OK") { dialog, which ->
                                setResult(Activity.RESULT_OK)
                                finish()
                            }.show()
                    } else {
                        AlertDialog.Builder(this).setTitle("Sing up")
                            .setMessage(it.exception?.message)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
        }
    }
}
