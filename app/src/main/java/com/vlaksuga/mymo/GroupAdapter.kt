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
import java.util.*
import kotlin.collections.ArrayList


class GroupAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var groups = emptyList<Group>()
    var memos = emptyList<Memo>()


    private lateinit var listener: OnItemClickListener
    internal var filterListResult: List<Group> = groups
    private var memoListResult: List<Memo> = memos


    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.holder_group_cardView)
        val titleItemView: TextView = itemView.findViewById(R.id.group_title_textView)
        val barColorItemView: ImageView = itemView.findViewById(R.id.group_colorBar_textView)
        val countItemView: TextView = itemView.findViewById(R.id.group_count_memo_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.group_list_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        if (position == 0) {
            holder.cardItemView.visibility = View.GONE
            holder.titleItemView.visibility = View.GONE
            holder.barColorItemView.visibility = View.GONE
            holder.itemView.layoutParams.width = 0
            holder.itemView.layoutParams.height = 0
        }
        val current = filterListResult[position]
        holder.cardItemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filterListResult[position])
            }
        }
        holder.titleItemView.text = current.groupName
        holder.barColorItemView.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(current.groupColor))
        holder.countItemView.text = getMemoCountByGroupId(current.groupId).toString()
    }

    override fun getItemCount() = filterListResult.size

    internal fun setGroups(groups: List<Group>) {
        this.groups = groups
        filterListResult = groups
        notifyDataSetChanged()
    }

    internal fun setMemos(memos: List<Memo>) {
        this.memos = memos
        memoListResult = memos
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(group: Group) {}
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun getMemoCountByGroupId(id: Int): Int {
        val memoListByGroupId = ArrayList<Memo>()
        for (row in memoListResult) {
            if (row.groupId == id) {
                memoListByGroupId.add(row)
            }
        }
        return memoListByGroupId.size
    }


    // 필터
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charString: CharSequence?): FilterResults {

                // 검색으로 인한 필터
                val charSearch: String = charString.toString().trim()
                filterListResult = if (charSearch.isEmpty()) {
                    groups
                } else {
                    val resultList = ArrayList<Group>()
                    for (row in groups) {
                        if (row.groupName.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        )
                            resultList.add(row)
                    }
                    resultList
                }

                val filterResults = FilterResults()
                filterResults.values = filterListResult
                return filterResults
            }


            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSquence: CharSequence?, filterResults: FilterResults?) {
                filterListResult = filterResults!!.values as List<Group>
                notifyDataSetChanged()
            }

        }
    }


}