package com.shalom.classnotes

import SwipeToDeleteCallback
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
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
import com.shalom.classnotes.databinding.ActivityMainBinding
import com.shalom.classnotes.login.LoginDialogFragment
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import com.shalom.classnotes.notesadapter.ClassNoteAdapter
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
        setSupportActionBar(toolbar)
        //
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        //data binding viewmodel to view
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        DataBindingUtil.bind<ActivityMainBinding>(binding.root).apply {
            this?.lifecycleOwner = this@MainActivity
            this?.vm = noteViewModel
        }
        //set toolbar


        //initialise shared preferences
        sharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)





        //set up on click action of notes recycler
        notesAdapter = ClassNoteAdapter(this).apply {
            this.setOnItemClickListener(this@MainActivity)

        }
        //set adapter and layout manager
        notesRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }

        //swipe to delete handler
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = notesAdapter.getNoteAt(viewHolder.adapterPosition)
                noteViewModel.delete(noteToDelete)
            }

        }

        //attach swipe
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(notesRecycler)

        fab.setOnClickListener { view ->
            //sync to firebase
            //            noteViewModel.getAllNotes().value?.get(0)?.let { noteViewModel.delete(it) }
            val firebaseDb = FirebaseFirestore.getInstance()
            //get reference from firebase
            val users: CollectionReference = firebaseDb.collection("users")
            val id = sharedPreferences.getString(ID, null) ?: ""
            val name = sharedPreferences.getString(NAME, null) ?: ""
            //create student object to send to firebase
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
            //move to  add note activity
            startActivityForResult(
                Intent(this, AddItemActivity::class.java), 1
            )
        }


        //add notes to notes adapter
        noteViewModel.getAllNotes().observe(this, Observer<List<Note>> {
            notesAdapter.submitList(it)
            binding.invalidateAll()
        })


        //show login fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            fragmentTransaction.remove(prev)
        }
        fragmentTransaction.addToBackStack(null)
        //if id is set means user has logged in
        val id = sharedPreferences.getString(ID, null)
        if (id.isNullOrBlank()) {
            val dialogFragment = LoginDialogFragment() //here MyDialog is my custom dialog
            dialogFragment.show(fragmentTransaction, "dialog")
        }
    }

    private fun setupStatusBarGradient() {
        //sets the gradient status bar
        val window: Window = window
        val background: Drawable = ContextCompat.getDrawable(this, R.drawable.gradient)!!
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }

        //sync old notes when user logs in
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
                //invokes the on complete function specified in function
                onComplete.invoke()
                //shows hints the first time
                showHints()
            }
        }.addOnFailureListener {
            saveValuesToSharedRepo(id)
            onComplete.invoke()
            showHints()
        }

    }

    fun saveValuesToSharedRepo(id: String) {
        //saves id to shared preferences
        sharedPreferences.edit().putString(ID, id).apply()
    }

    private fun showSnackBar(msg: String) {
        //show snackbar
        Snackbar.make(toolbar, msg, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }


    override fun onItemClick(note: Note) {
        startActivity(
            //opens edit note activity
            Intent(this, AddItemActivity::class.java).putExtra(NOTE, note)

        )
    }

    private fun showHints() {
        //shows hints on first time
        if (sharedPreferences.getBoolean(SEEN_HINT, false)) {
            return
        }
        val config = ShowcaseConfig()
        config.delay = 500 // half second between each showcase view

        //defines a sequence of hints
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
