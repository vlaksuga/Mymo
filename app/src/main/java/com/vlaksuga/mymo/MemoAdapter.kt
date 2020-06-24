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

class MemoAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<MemoAdapter.MemoViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var memos = emptyList<Memo>()

    private lateinit var listener: OnItemClickListener

    internal var filterMemoListResult: List<Memo> = memos

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.holder_cardView)
        val titleItemView: TextView = itemView.findViewById(R.id.title_textView)
        val contentItemView: TextView = itemView.findViewById(R.id.content_textView)
        val initDateItemView: TextView = itemView.findViewById(R.id.initDate_textView)
        val initTimeItemView: TextView = itemView.findViewById(R.id.initTime_textView)
        val barColorItemView: TextView = itemView.findViewById(R.id.colorBar_textView)
        val importantItemView: ImageView = itemView.findViewById(R.id.important_imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val itemView = inflater.inflate(R.layout.list_item, parent, false)
        return MemoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val currentMemo = filterMemoListResult[position]
        holder.cardItemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filterMemoListResult[position])
            }
        }
        if (currentMemo.isImportant) {
            holder.importantItemView.visibility = View.VISIBLE
        } else {
            holder.importantItemView.visibility = View.GONE
        }

        val dateSimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ROOT)
        val timeSimpleDateFormat = SimpleDateFormat("a hh:mm", Locale.ROOT)

        holder.titleItemView.text = currentMemo.memoTitle
        holder.contentItemView.text = currentMemo.memoContent
        holder.initDateItemView.text =
            dateSimpleDateFormat.format(Timestamp(currentMemo.initTime)).toString()
        holder.initTimeItemView.text =
            timeSimpleDateFormat.format(Timestamp(currentMemo.initTime)).toString()
        holder.barColorItemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(currentMemo.groupColor))
    }


    override fun getItemCount() = filterMemoListResult.size

    internal fun setMemos(memos: List<Memo>) {
        this.memos = memos
        filterMemoListResult = memos
        notifyDataSetChanged()
    }

    fun getMemoAt(position: Int): Memo {
        return filterMemoListResult[position]
    }

    public interface OnItemClickListener {
        fun onItemClick(memo: Memo) {}
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    // 필터
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charString: CharSequence?): FilterResults {

                // 검색으로 인한 필터
                val charSearch: String = charString.toString().trim()
                filterMemoListResult = if (charSearch.isEmpty()) {
                    memos
                } else {
                    val resultList = ArrayList<Memo>()
                    for (row in memos) {
                        if (row.memoTitle.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        )
                            resultList.add(row)
                    }
                    resultList
                }

                val filterResults = FilterResults()
                filterResults.values = filterMemoListResult
                return filterResults
            }


            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSquence: CharSequence?, filterResults: FilterResults?) {
                filterMemoListResult = filterResults!!.values as List<Memo>
                notifyDataSetChanged()
            }

        }
    }


    // 컬러로 필터링
    fun getListByGroupId(GroupId: Int) {
        val resultList = ArrayList<Memo>()

        for (row in memos) {
            if (row.groupId == GroupId) {
                resultList.add(row)
            }
        }

        filterMemoListResult = resultList
        notifyDataSetChanged()
    }

    fun getAllList() {
        filterMemoListResult = memos
        notifyDataSetChanged()
    }
}