package com.vlaksuga.mymo


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class GroupSelectListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<GroupSelectListAdapter.GroupViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var groups = emptyList<Group>()

    private lateinit var listener: OnItemClickListener
    internal var listResult: List<Group> = groups

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.holder_group_list_cardView)
        val barColorItemView: ImageView = itemView.findViewById(R.id.group_color_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.group_select_list_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val current = listResult[position]
        holder.cardItemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(listResult[position])
            }
        }
        holder.barColorItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(current.groupColor))
    }

    override fun getItemCount() = listResult.size

    internal fun setGroups(groups: List<Group>) {
        this.groups = groups
        listResult = groups
        notifyDataSetChanged()
    }


    fun getListAt(position: Int): Group {
        return groups[position]
    }

    public interface OnItemClickListener {
        fun onItemClick(group: Group) {}
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}