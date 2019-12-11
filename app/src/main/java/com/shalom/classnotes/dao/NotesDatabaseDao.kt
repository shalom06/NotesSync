package com.shalom.classnotes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shalom.classnotes.models.Note

//describes the functions that can be performed on the database
@Dao
interface NotesDatabaseDao {

    //inserts notes
    @Insert
    suspend fun insertNote(note: Note)

    //updates notes
    @Update
    suspend fun updateNote(note: Note)

    //deletes notes
    @Delete
    suspend fun delete(note: Note)

    //deletes all notes
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    //get all notes
    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    //search
    @Query("SELECT * FROM notes WHERE noteName LIKE :search")
    fun searchNotes(search: String): LiveData<List<Note>>
}