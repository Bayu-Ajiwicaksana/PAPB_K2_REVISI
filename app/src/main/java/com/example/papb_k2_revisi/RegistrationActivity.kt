package com.example.papb_k2_revisi

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.util.regex.Pattern
import kotlin.text.Charsets.UTF_8

class RegistrationActivity : AppCompatActivity() {

    private lateinit var nameFL: TextInputLayout
    private lateinit var nameInput: TextInputEditText

    private lateinit var emailFL: TextInputLayout
    private lateinit var emailInput: TextInputEditText

    private lateinit var passFL: TextInputLayout
    private lateinit var passInput: TextInputEditText

    private lateinit var cPassFL: TextInputLayout
    private lateinit var cPassInput: TextInputEditText

    private lateinit var toLogin: TextView

    private var nameOk = false
    private var emailOk = false
    private var passOk = false
    private var cPassOk = false

    private lateinit var submitBtn: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        nameFL = findViewById(R.id.nameField_layout)
        nameInput = findViewById(R.id.nameField)

        emailFL = findViewById(R.id.emailField_layout)
        emailInput = findViewById(R.id.emailField)

        passFL = findViewById(R.id.passwordField_layout)
        passInput = findViewById(R.id.passwordField)

        cPassFL = findViewById(R.id.confirmPasswordField_layout)
        cPassInput = findViewById(R.id.confirmPasswordField)

        submitBtn = findViewById(R.id.submitBtn)

        toLogin = findViewById(R.id.toLogin)
        formValidation()
        submitBtn.setOnClickListener {
            val isValid = nameOk && emailOk && passOk && cPassOk
            register(isValid)
        }
        toLogin.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java )
            startActivity(intentToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    private fun register(isValid: Boolean) {
        if (isValid) {
            auth.createUserWithEmailAndPassword(emailInput.text.toString(), sha256(passInput.text.toString()).toHex())
                .addOnCompleteListener(this@RegistrationActivity) { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Pengguna/" + FirebaseAuth.getInstance().currentUser!!.uid).setValue(
                        UsersClass(
                            nameInput.text.toString(),
                            emailInput.text.toString(),
                            sha256(passInput.text.toString()).toHex()
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Registration success", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))

                        finish()
                    }
                }
            }
        }
    }

    private fun formValidation(){

        nameInput.doOnTextChanged { text, _, _, _ ->
            if (!text?.let { Pattern.compile("^[ A-Za-z]+$").matcher(it).matches() }!!) {
                nameFL.error = "Alphabets only !!!"
                nameOk = false
            } else {
                nameFL.error = null
                nameOk = true
            }
        }

        emailInput.doOnTextChanged { text, _, _, _ ->
            if (!text?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
                emailFL.error = "Email address not valid"
                emailOk = false
            } else {
                emailFL.error = null
                emailOk = true
            }
        }

        passInput.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 6) {
                passFL.error = "Minimum password length is 6"
                passOk = false
            } else {
                passFL.error = null
                passOk = true
            }
        }

        cPassInput.doOnTextChanged { text, _, _, _ ->
            if (passInput.text.toString() != text.toString() || text.isNullOrEmpty()) {
                cPassFL.helperText = "Password didn't match"
                cPassFL.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.red)))
                cPassOk = false
            } else {
                cPassFL.helperText = "Password match"
                cPassFL.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.green)))
                cPassOk = true
            }
        }
    }

    private fun sha256(password: String): ByteArray = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(UTF_8))
    private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }
}