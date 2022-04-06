package com.home.shop

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        btn_nickname_finish.setOnClickListener {
            val nick = ed_nickname.text.toString()
            setNickname(nick)
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("nickname")
                .setValue(nick)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
