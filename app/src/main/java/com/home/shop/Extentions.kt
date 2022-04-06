package com.home.shop

import android.app.Activity
import android.content.Context

fun Activity.setNickname(nickname: String) {
    getSharedPreferences("shop", Context.MODE_PRIVATE)
        .edit()
        .putString("NICKNAME", nickname)
        .apply()
}

fun Activity.getNickname(): String {
return getSharedPreferences("shop", Context.MODE_PRIVATE)
    .getString("NICKNAME", "")
}