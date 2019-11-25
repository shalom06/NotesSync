package com.shalom.classnotes.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.shalom.classnotes.R
import com.shalom.classnotes.models.Note
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
            val firebaseDb = FirebaseFirestore.getInstance()
            val users: CollectionReference = firebaseDb.collection("users")

            users.document("320455455").set(Note("200455455", "test", "sgs"))
            this.dismiss()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Hey", "onCreate")
        var setFullScreen = true
        if (arguments != null) {
            setFullScreen = requireNotNull(arguments?.getBoolean("fullScreen"))
        }
        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    interface DialogListener {
        fun onFinishEditDialog(inputText: String)
    }
}