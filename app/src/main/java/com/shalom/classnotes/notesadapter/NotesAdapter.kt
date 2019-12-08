package com.mogalabs.tagnotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shalom.classnotes.R
import com.shalom.classnotes.models.Note
import kotlinx.android.synthetic.main.listem_note.view.*

class ClassNoteAdapter(val context: Context) :
    ListAdapter<Note, ClassNoteAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.id == newNote.id
            }

            override fun areContentsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.noteName == newNote.noteName && oldNote.noteDetail == newNote.noteDetail
                        && oldNote.date == newNote.date
            }
        }
    }

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.listem_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote: Note = getItem(position)

        holder.noteTile.text = currentNote.noteName
        holder.noteDate.text = currentNote.date
        holder.className.text = currentNote.className
        holder.background.setCardBackgroundColor(getColorForNote(currentNote.color))

    }

    private fun getColorForNote(color: Int): Int {
        return when (color) {
            0 -> ContextCompat.getColor(context, R.color.indian_red)
            1 -> ContextCompat.getColor(context, R.color._light_green)
            2 -> ContextCompat.getColor(context, R.color.light_yellow)
            3 -> ContextCompat.getColor(context, R.color.light_blue)
            else -> return ContextCompat.getColor(context, R.color.light_grey)
        }
    }

    fun getNoteAt(position: Int): Note {
        return getItem(position)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener?.onItemClick(getItem(position))

            }
        }

        var noteTile: TextView = itemView.noteTitle
        var noteDate: TextView = itemView.noteDate
        var className: TextView = itemView.className
        val background: CardView = itemView.noteCard
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun removeItem(postion:Int){

    }


}
