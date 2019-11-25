package com.shalom.classnotes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shalom.classnotes.models.Note

@Dao
interface NotesDatabaseDao {


    @Insert
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM notes")
    fun deleteAllNotes()

    @Query("SELECT * FROM notes ")
    fun getAllNotes(): LiveData<List <Note>>


}