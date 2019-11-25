package com.shalom.classnotes.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.shalom.classnotes.dao.NotesDatabaseDao
import com.shalom.classnotes.database.NoteDatabase
import com.shalom.classnotes.models.Note
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoteRepository(application: Application) {

    private var noteDao: NotesDatabaseDao

    private lateinit var allNotes: LiveData<List<Note>>

    init {
        val database: NoteDatabase = NoteDatabase.getInstance(
            application.applicationContext
        )!!
        noteDao = database.noteDao()
        GlobalScope.launch { allNotes = noteDao.getAllNotes() }

    }

    fun insert(note: Note) {
        val insertNoteAsyncTask =
            GlobalScope.launch {
                noteDao.insertNote(
                    note
                )
            }
    }

    fun update(note: Note) {
        GlobalScope.launch {
            noteDao.updateNote(note)
        }
    }


    fun delete(note: Note) {
        GlobalScope.launch {
            noteDao.delete(note)
        }
    }

    fun deleteAllNotes() {
        GlobalScope.launch {
            noteDao.deleteAllNotes()
        }
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return allNotes
    }


}