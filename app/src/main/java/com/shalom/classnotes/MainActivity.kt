package com.shalom.classnotes

import SwipeToDeleteCallback
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mogalabs.tagnotes.adapters.ClassNoteAdapter
import com.shalom.classnotes.AddItemActivity.Companion.ITEM
import com.shalom.classnotes.databinding.ActivityMainBinding
import com.shalom.classnotes.login.LoginDialogFragment
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import com.shalom.classnotes.viewmodels.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


class MainActivity : AppCompatActivity(), ClassNoteAdapter.OnItemClickListener {

    companion object {
        const val ADD_TASK = 1
        const val EDIT_TASK = 2
        const val PRIVATE_MODE = 0
        const val PREF_NAME = "com.shalom.notesapp"
        const val ID = "ID"
        const val NAME = "NAME"
        const val NOTE = "NOTE"
        const val SEEN_HINT = "SEEN_HINT"
    }

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesAdapter: ClassNoteAdapter
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBarGradient()
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(toolbar)
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        DataBindingUtil.bind<ActivityMainBinding>(binding.root).apply {
            this?.lifecycleOwner = this@MainActivity
            this?.vm = noteViewModel
        }


        notesAdapter = ClassNoteAdapter(this).apply {
            this.setOnItemClickListener(this@MainActivity)

        }

        notesRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = notesAdapter.getNoteAt(viewHolder.adapterPosition)
                noteViewModel.delete(noteToDelete)
            }

        }


        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(notesRecycler)

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
            binding.invalidateAll()
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

    private fun setupStatusBarGradient() {
        val window: Window = window
        val background: Drawable = ContextCompat.getDrawable(this, R.drawable.gradient)!!
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.transparent)
        window.setBackgroundDrawable(background)
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

                showSnackBar("Welcome Back we got your notes back!")
                val student = it.toObject(Student::class.java)
                student?.let { studentExists -> noteViewModel.syncFireBaseToLocal(studentExists) }
                onComplete.invoke()
                showHints()
            } else {
                saveValuesToSharedRepo(id)
                showSnackBar("Looks like we do not have any data to sync!")
                onComplete.invoke()
                showHints()
            }
        }.addOnFailureListener {
            saveValuesToSharedRepo(id)
            onComplete.invoke()
            showHints()
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

            Intent(this, AddItemActivity::class.java).putExtra(NOTE, note)

        )
    }

    private fun showHints() {
        if (sharedPreferences.getBoolean(SEEN_HINT, false)) {
            return
        }
        val config = ShowcaseConfig()
        config.delay = 500 // half second between each showcase view


        val sequence = MaterialShowcaseSequence(this, "test")

        sequence.setConfig(config)

        sequence.addSequenceItem(
            fab,
            "Sync your Notes online so that you can get them whenever and on any device !\n" +
                    "It will get it even if you reinstall the application", "GOT IT"
        )

        sequence.addSequenceItem(
            addNote,
            "Add a Note", "GOT IT"
        )

        sequence.addSequenceItem(
            titleText,
            "Swipe on the notes to delete them or click on them to view/edit them", "Got it"
        )


        sequence.start()

        sharedPreferences.edit().putBoolean(SEEN_HINT, true).apply()
    }
}
