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

class GroupFilterAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<GroupFilterAdapter.GroupViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var groups = emptyList<Group>()
    var memos = emptyList<Memo>()


    private lateinit var listener: OnItemClickListener
    internal var filterListResult: List<Group> = groups
    internal var memoListResult: List<Memo> = memos

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.holder_group_filter_cardView)
        val titleItemView: TextView = itemView.findViewById(R.id.group_filter_title_textView)
        val barColorItemView: ImageView = itemView.findViewById(R.id.group_filter_colorBar_textView)
        val countView : TextView = itemView.findViewById(R.id.count_memos_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = inflater.inflate(R.layout.group_filter_list_item, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val current = filterListResult[position]
        holder.cardItemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filterListResult[position])
            }
        }
        holder.titleItemView.text = current.groupName
        holder.barColorItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(current.groupColor))
        holder.countView.text = getMemoCountByGroupId(current.groupId).toString()

        if(position == filterListResult.size) {
            holder.titleItemView.setTextColor(ColorStateList.valueOf(Color.parseColor("#DDDDDD")))
            holder.barColorItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#DDDDDD"))
        }
    }

    override fun getItemCount() = filterListResult.size

    internal fun setGroups(groups: List<Group>) {
        this.groups = groups
        filterListResult = groups
        notifyDataSetChanged()
    }

    public interface OnItemClickListener {
        fun onItemClick(group: Group) {}
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun setMemos(memos: List<Memo>) {
        this.memos = memos
        memoListResult = memos
        notifyDataSetChanged()
    }

    private fun getMemoCountByGroupId(id : Int) : Int {
        val memoListByGroupId = ArrayList<Memo>()
        for(row in memoListResult) {
            if(row.groupId == id) {
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