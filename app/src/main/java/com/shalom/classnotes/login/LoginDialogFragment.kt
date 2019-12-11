package com.shalom.classnotes.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.shalom.classnotes.MainActivity
import com.shalom.classnotes.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginDialogFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            //login
           checkLogin()


        }
        registerText.setOnClickListener {
            //changes the ui to register
            changeUiToRegister()


        }
        registerButton.setOnClickListener {
            //resgisetrs user
            registerUser()
        }
    }

    private fun registerUser() {
        val fb = FirebaseAuth.getInstance()
        fb.createUserWithEmailAndPassword(emailId.text.toString(), password.text.toString())
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    // register success
                    Toast.makeText(
                        activity as MainActivity, "Registration Successful.",
                        Toast.LENGTH_SHORT

                    ).show()
                    resetToLoginSate()
                } else {
                    // If register in fails
                    Toast.makeText(
                        activity as MainActivity,
                        "Registration failed." +
                                "\n Please use valid Email Id or password more than 6 characters ",
                        Toast.LENGTH_LONG
                    ).show()
//                    updateUI(null)
                }


            }
    }

    private fun resetToLoginSate() {
        //view changes to login
        loginButton.visibility=View.VISIBLE
        registerButton.visibility=View.GONE
        registerText.visibility=View.VISIBLE
    }

    private fun changeUiToRegister() {
        //view changes to register
        loginButton.visibility=View.GONE
        registerButton.visibility=View.VISIBLE
        registerText.visibility=View.GONE
    }

    private fun checkLogin() {
        val fb = FirebaseAuth.getInstance()
        fb.signInWithEmailAndPassword(emailId.text.toString(), password.text.toString())
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(
                        activity as MainActivity, "Authentication Successful.",
                        Toast.LENGTH_SHORT

                    ).show()

                    (activity as MainActivity).checkIfUserExistsInFirebaseDatabase(emailId.text.toString()) { this.dismiss() }
                } else {
                    // If sign in fails
                    Toast.makeText(
                        activity as MainActivity,
                        "Authentication failed. Please Check Id and Password . ",
                        Toast.LENGTH_LONG
                    ).show()
//                    updateUI(null)
                }


            }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //type of dialog fragment
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }



}