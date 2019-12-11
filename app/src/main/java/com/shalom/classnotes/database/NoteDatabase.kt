package com.shalom.classnotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shalom.classnotes.dao.NotesDatabaseDao
import com.shalom.classnotes.models.Note
import com.shalom.classnotes.models.Student
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {


    abstract fun noteDao(): NotesDatabaseDao


    companion object {
        private var instance: NoteDatabase? = null
        private const val NOTES_DATABASE = "notes_database"
        //initializes a singleton room database with dao
        fun getInstance(context: Context): NoteDatabase? {
            if (instance == null) {
                synchronized(NoteDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java, NOTES_DATABASE
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                loadSampleData(instance)
            }
        }

        fun loadSampleData(db: NoteDatabase?) {
            val noteDao = db?.noteDao()
            GlobalScope.launch {
//                noteDao?.insertNote(Note(null, "description 1"))
//                noteDao?.insertNote(Note(null, "description 2"))
//                noteDao?.insertNote(Note(null, "description 3"))
            }
        }
    }


}