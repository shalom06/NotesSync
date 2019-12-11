package com.shalom.classnotes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.shalom.classnotes.MainActivity.Companion.NOTE
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.viewmodels.NoteViewModel
import kotlinx.android.synthetic.main.add_item.*
import java.util.*


class AddItemActivity : AppCompatActivity() {
    //class variables are initialized
    lateinit var noteViewModel: NoteViewModel
    private var editNoteMode: Boolean = false
    lateinit var note: Note

    companion object {
        const val ITEM = "ITEM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item)
        //initialize view-model
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        //initializes edit mode
        if (intent.hasExtra(NOTE)) {
            title = "Edit Note"
            editNoteMode = true
            note = intent.getParcelableExtra(NOTE)!!
            updateTextFields(note)
        } else {
            //initializes new note mode
            title = "Add Note"
            editNoteMode = false
            colorSpinner.setSelection(4)
        }
    }

    private fun updateTextFields(note: Note) {
        //updates the edit text fields with data if in edit notes mode
        noteTitleEditText.setText(note.noteName)
        noteClass.setText(note.className)
        noteDescription.setText(note.noteDetail)
        colorSpinner.setSelection(note.color)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //creates menu options
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    //update or inserts the note into database
    private fun saveNote()
    {
        //create note object to update or inset
        val note = Note().apply {
            this.noteName = noteTitleEditText.text.toString()
            this.date = Date().toString()
            this.noteDetail = noteDescription.text.toString()
            this.className = noteClass.text.toString()
            if (editNoteMode) this.id = note.id
            this.color=colorSpinner.selectedItemPosition

        }
        //updates or inserts the note into database
        if (editNoteMode) noteViewModel.update(note)
        else noteViewModel.insert(note)


        finish()
    }


}