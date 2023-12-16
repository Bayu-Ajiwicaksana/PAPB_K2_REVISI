package com.example.papb_k2_revisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

class LoginActivity : AppCompatActivity() {

    private lateinit var emailFL: TextInputLayout
    private lateinit var emailInput: TextInputEditText

    private lateinit var passFL: TextInputLayout
    private lateinit var passInput: TextInputEditText

    private lateinit var loginBtn: Button

    private lateinit var toRegistration: TextView

    private var emailOk = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailFL = findViewById(R.id.emailLogin_Layout)
        emailInput = findViewById(R.id.emailLogin)

        passFL = findViewById(R.id.passwordLogin_Layout)
        passInput = findViewById(R.id.passwordLogin)

        loginBtn = findViewById(R.id.loginBtn)

        toRegistration = findViewById(R.id.toRegistration)

        formValidation()

        loginBtn.setOnClickListener {
            login(emailOk)
        }

        toRegistration.setOnClickListener {
            val intentToRegistration = Intent(this, RegistrationActivity::class.java)
            startActivity(intentToRegistration.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    private fun login(isValid: Boolean) {

        if (isValid) {
            auth.signInWithEmailAndPassword(emailInput.text.toString(), sha256(passInput.text.toString()).toHex())
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Welcome !", Toast.LENGTH_SHORT).show()
                        val intentToMain = Intent(this, MainActivity::class.java)
                        startActivity(intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }
                }
        }

    }

    private fun formValidation(){
        emailInput.doOnTextChanged { text, _, _, _ ->
            if (!text?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
                emailFL.error = "Email address not valid"
                emailOk = false
            } else {
                emailFL.error = null
                emailOk = true
            }
        }
    }

    private fun sha256(password: String): ByteArray = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(UTF_8))
    private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }
}