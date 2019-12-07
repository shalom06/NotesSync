package com.shalom.classnotes

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mogalabs.tagnotes.adapters.ClassNoteAdapter
import com.shalom.classnotes.AddItemActivity.Companion.ITEM
import com.shalom.classnotes.login.LoginDialogFragment
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import com.shalom.classnotes.viewmodels.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), ClassNoteAdapter.OnItemClickListener {

    companion object {
        const val ADD_TASK = 1
        const val EDIT_TASK = 2
        const val PRIVATE_MODE = 0
        const val PREF_NAME = "com.shalom.notesapp"
        const val ID = "ID"
        const val NAME = "NAME"
        const val NOTE = "NOTE"
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesAdapter: ClassNoteAdapter
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var studentName: String
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)



        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        notesAdapter = ClassNoteAdapter(this).apply {
            this.setOnItemClickListener(this@MainActivity)

        }

        notesRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }

        fab.setOnClickListener { view ->
            //            noteViewModel.getAllNotes().value?.get(0)?.let { noteViewModel.delete(it) }
            val firebaseDb = FirebaseFirestore.getInstance()
            val users: CollectionReference = firebaseDb.collection("users")
            val id = sharedPreferences.getString(ID, null) ?: ""
            val name = sharedPreferences.getString(NAME, null) ?: ""
            val student = Student().apply {
                this.id = id
                this.name = name
                this.notes = noteViewModel.getAllNotes().value ?: emptyList()
            }

            users.document(id)
                .set(student)
            Snackbar.make(view, "Done Syncing ! ", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        addNote.setOnClickListener {
            startActivityForResult(
                Intent(this, AddItemActivity::class.java), 1
            )
        }

        noteViewModel.getAllNotes().observe(this, Observer<List<Note>> {
            notesAdapter.submitList(it)
        })

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            fragmentTransaction.remove(prev)
        }
        fragmentTransaction.addToBackStack(null)
        val id = sharedPreferences.getString(ID, null)
        if (id.isNullOrBlank()) {
            val dialogFragment = LoginDialogFragment() //here MyDialog is my custom dialog
            dialogFragment.show(fragmentTransaction, "dialog")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun checkIfUserExistsInFirebaseDatabase(
        id: String,
        onComplete: () -> Unit
    ) {
        val firebaseDb = FirebaseFirestore.getInstance()
        val checkIfExists = firebaseDb.collection("users").document(id)
        checkIfExists.get().addOnSuccessListener {
            if (it.data != null) {
                saveValuesToSharedRepo(id)

                showSnackBar("We got them Synced")
                val student = it.toObject(Student::class.java)
                student?.let { studentExists -> noteViewModel.syncFireBaseToLocal(studentExists) }
                onComplete.invoke()
            } else {
                saveValuesToSharedRepo(id)
                showSnackBar("Looks like we do not have any data to sync!")
                onComplete.invoke()
            }
        }.addOnFailureListener {
            saveValuesToSharedRepo(id)
            onComplete.invoke()
        }

    }

    fun saveValuesToSharedRepo(id: String) {
        sharedPreferences.edit().putString(ID, id).apply()
    }

    private fun showSnackBar(msg: String) {
        Snackbar.make(toolbar, msg, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TASK && resultCode == Activity.RESULT_OK) {
            val note: Note? = data?.getParcelableExtra(ITEM)
            note?.let { noteViewModel.insert(it) }

            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()


        }
    }

    override fun onItemClick(note: Note) {
        startActivity(

            Intent(this, AddItemActivity::class.java).putExtra(NOTE,note)

        )
    }
}
