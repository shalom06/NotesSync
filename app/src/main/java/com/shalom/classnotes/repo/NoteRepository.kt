package com.shalom.classnotes.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.shalom.classnotes.dao.NotesDatabaseDao
import com.shalom.classnotes.database.NoteDatabase
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NoteRepository(application: Application) : CoroutineScope {
    private val job = Job()

    private var noteDao: NotesDatabaseDao

    //live data object which flows to view
    private var allNotes: LiveData<List<Note>>


    init {
        val database: NoteDatabase = NoteDatabase.getInstance(
            application.applicationContext
        )!!
        noteDao = database.noteDao()
        allNotes = noteDao.getAllNotes()

    }

    //inserts note into database
    fun insert(note: Note) {
        this.launch {
            noteDao.insertNote(
                note
            )
        }
    }

    //update note into database
    fun update(note: Note) {
        this.launch {
            noteDao.updateNote(note)
        }
    }

    //delete note  from database
    fun delete(note: Note) {
        this.launch {
            noteDao.delete(note)
        }
    }

    //deletes all notes from database
    fun deleteAllNotes() {
        this.launch {
            noteDao.deleteAllNotes()
        }
    }
    //gets all notes from database
    fun getAllNotes(): LiveData<List<Note>> {
        return allNotes
    }

    //loads firebase data to room
    fun loadSyncedDataToRoomDatabase(student: Student) {

        student.notes.map { note ->

            note.id = null
            this.launch {
                noteDao.insertNote(note)
            }
        }


    }
    //defines the scope of repository and lifecycle
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default


}