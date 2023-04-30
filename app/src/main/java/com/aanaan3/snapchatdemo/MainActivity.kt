package com.aanaan3.snapchatdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aanaan3.snapchatdemo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null){
            logIn()
        }

    }

    fun goClicked(view:View){
            mAuth.signInWithEmailAndPassword(
                binding.editTextTextPersonName.text.toString(),
                binding.editTextTextPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        logIn()
                    } else {
                        mAuth.createUserWithEmailAndPassword(
                            binding.editTextTextPersonName.text.toString(),
                            binding.editTextTextPassword.text.toString()
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    FirebaseDatabase.getInstance().getReference().child("users").
                                    child(task.result.user!!.uid).child("email").setValue(binding.editTextTextPersonName.text.toString())
                                    logIn()
                                } else {
                                    Toast.makeText(this,"login failed. try again",Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
    }

    private fun logIn() {

        val intent = Intent(this,SnapActivity::class.java)
        startActivity(intent)
    }


}