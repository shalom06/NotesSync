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
    suspend fun getAllNotes(): LiveData<List <Note>>


}