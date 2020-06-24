package com.vlaksuga.mymo


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrashAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<TrashAdapter.TrashViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var trash = emptyList<Trash>()

    internal var listResult: List<Trash> = trash

    inner class TrashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItemView: TextView = itemView.findViewById(R.id.trash_title_textView)
        val contentItemView: TextView = itemView.findViewById(R.id.trash_content_textView)
        val exDateItemView: TextView = itemView.findViewById(R.id.trash_exDate_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val itemView = inflater.inflate(R.layout.trash_list_item, parent, false)
        return TrashViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val currentTrash = listResult[position]
        val dateSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd a hh:mm", Locale.ROOT)

        holder.titleItemView.text = currentTrash.trashTitle
        holder.contentItemView.text = currentTrash.trashContent
        holder.exDateItemView.text =
            "EXP : " + dateSimpleDateFormat.format(Timestamp(currentTrash.trashExpireTime)).toString()
    }


    override fun getItemCount() = listResult.size

    internal fun setTrash(trash: List<Trash>) {
        this.trash = trash
        listResult = trash
        notifyDataSetChanged()
    }

    fun getTrashAt(position: Int): Trash {
        return listResult[position]
    }

}