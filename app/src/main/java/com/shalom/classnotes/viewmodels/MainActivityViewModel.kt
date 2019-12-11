package com.shalom.classnotes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import com.shalom.classnotes.repo.NoteRepository

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    //initialize note Repository
    private var repository: NoteRepository =
        NoteRepository(application)

    //observe live data object from  repository
    private var allNotes: LiveData<List<Note>> = repository.getAllNotes()

    //tell repository to insert note
    fun insert(note: Note) {
        repository.insert(note)
    }
    //tell repository to update note
    fun update(note: Note) {
        repository.update(note)
    }
    //tell repository to delete note
    fun delete(note: Note) {
        repository.delete(note)
    }
    //tell repository to delete all note
    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }


    //function to observe live data in main activty
    fun getAllNotes(): LiveData<List<Note>> {
        return allNotes
    }

    //tell repository to sync firebase notes
    fun syncFireBaseToLocal(student: Student) {
        repository.loadSyncedDataToRoomDatabase(student)
    }

    //databinding directly to view
    fun getNoOfNotes():String{
        return allNotes.value?.size.toString()
    }



}