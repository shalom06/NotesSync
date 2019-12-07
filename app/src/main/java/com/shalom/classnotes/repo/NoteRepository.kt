package com.shalom.classnotes.repo

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.shalom.classnotes.dao.NotesDatabaseDao
import com.shalom.classnotes.database.NoteDatabase
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoteRepository(application: Application): LifecycleOwner {


    private var noteDao: NotesDatabaseDao

    private var allNotes: LiveData<List<Note>>


    init {
        val database: NoteDatabase = NoteDatabase.getInstance(
            application.applicationContext
        )!!
        noteDao = database.noteDao()
        allNotes = noteDao.getAllNotes()

    }

    fun insert(note: Note) {
        GlobalScope.launch {
            val insert = noteDao.insertNote(
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

    fun loadSyncedDataToRoomDatabase(student: Student) {

        student.notes.map { note ->

            note.id = null
            GlobalScope.launch {
                noteDao.insertNote(note)
            }
        }


    }

    override fun getLifecycle(): Lifecycle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}