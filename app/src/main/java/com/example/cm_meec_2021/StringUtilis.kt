package com.example.cm_meec_2021

import android.util.Patterns

class StringUtilis {
    companion object {
        fun validateEmail(email: String): Boolean {
            return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePassword(password: String): Boolean {
            return !password.isNullOrEmpty() && password.length >= 6;
        }
    }
}