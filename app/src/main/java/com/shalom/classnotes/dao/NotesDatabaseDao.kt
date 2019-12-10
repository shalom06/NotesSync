package com.shalom.classnotes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shalom.classnotes.models.Note

@Dao
interface NotesDatabaseDao {


    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE noteName LIKE :search")
    fun searchNotes(search: String): LiveData<List<Note>>
}