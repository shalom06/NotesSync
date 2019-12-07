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
    lateinit var noteViewModel: NoteViewModel
    private var editNoteMode: Boolean = false
    lateinit var note: Note

    companion object {
        const val ITEM = "ITEM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_item)
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        if (intent.hasExtra(NOTE)) {
            title = "Edit Note"
            editNoteMode = true
            note = intent.getParcelableExtra(NOTE)!!
            updateTextFields(note)
        } else {
            title = "Add Note"
            editNoteMode = false
        }
    }

    private fun updateTextFields(note: Note) {
        noteTitleEditText.setText(note.noteName)
        noteClass.setText(note.className)
        noteDescription.setText(note.noteDetail)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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

    private fun saveNote() {
        val note = Note().apply {
            this.noteName = noteTitleEditText.text.toString()
            this.date = Date().toString()
            this.noteDetail = noteDescription.text.toString()
            this.className = noteClass.text.toString()
            if (editNoteMode) this.id = note.id

        }
        if (editNoteMode) noteViewModel.update(note)
        else noteViewModel.insert(note)


        finish()
    }
}