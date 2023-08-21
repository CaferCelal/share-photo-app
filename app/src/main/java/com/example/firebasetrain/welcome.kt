package com.example.firebasetrain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_welcome.*

class welcome : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        auth = FirebaseAuth.getInstance()


        val currUser = auth.currentUser

        if (currUser != null){
            val intent = Intent(this,feed::class.java)
            startActivity(intent)
            finish()
        }


        signInBtn.setOnClickListener {
            signInAction()
    }

        signUpBtn.setOnClickListener {
            signUpAction()
        }


    }

    private fun signInAction() {
        val mailAddress = mailBox.text.toString()
        val password = passwordBox.text.toString()

        auth.signInWithEmailAndPassword(mailAddress,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val currUser = auth.currentUser?.email.toString()
                Toast.makeText(this, "Hoşgeldiniz ${currUser}",Toast.LENGTH_SHORT).show()

                val intent = Intent(this,feed::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()

        }

    }


    private fun signUpAction() {
        val mailAddress = mailBox.text.toString()
        val password = passwordBox.text.toString()


        auth.createUserWithEmailAndPassword(mailAddress,password).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                val intent = Intent(this,feed::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener{exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()

        }


    }



}