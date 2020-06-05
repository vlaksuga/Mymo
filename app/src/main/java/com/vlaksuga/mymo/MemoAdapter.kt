package com.vlaksuga.mymo



import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MemoAdapter internal constructor(context: Context)  :
    RecyclerView.Adapter<MemoAdapter.MemoViewHolder> (), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var memos = emptyList<Memo>()

    private lateinit var listener: onItemClickListener
    internal var filterListResult : List<Memo> = memos

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardItemView: CardView = itemView.findViewById(R.id.holder_cardView)
        val titleItemView: TextView = itemView.findViewById(R.id.title_textView)
        val contentItemView: TextView = itemView.findViewById(R.id.content_textView)
        val initDateItemView: TextView = itemView.findViewById(R.id.initDate_textView)
        val initTimeItemView: TextView = itemView.findViewById(R.id.initTime_textView)
        val barColorItemView: TextView = itemView.findViewById(R.id.colorBar_textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val itemView = inflater.inflate(R.layout.list_item, parent, false)
        return MemoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val current = filterListResult[position]
        holder.cardItemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filterListResult[position])
            }
        }
        val dateSimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.ROOT)
        val timeSimpleDateFormat = SimpleDateFormat("a hh:mm", Locale.ROOT)

        holder.titleItemView.text = current.memoTitle
        holder.contentItemView.text = current.memoContent
        holder.initDateItemView.text =
            dateSimpleDateFormat.format(Timestamp(current.initTime)).toString()
        holder.initTimeItemView.text =
            timeSimpleDateFormat.format(Timestamp(current.initTime)).toString()
        holder.barColorItemView.setBackgroundColor(Color.parseColor(current.barColor))
    }

    override fun getItemCount() = filterListResult.size

    internal fun setMemos(memos: List<Memo>) {
        this.memos = memos
        filterListResult = memos
        notifyDataSetChanged()
    }

    fun getMemoAt(position: Int): Memo {
        return memos[position]
    }

    public interface onItemClickListener {
        fun onItemClick(memo: Memo) {}
    }

    public fun setOnItemClickListener(listener: onItemClickListener) {
        this.listener = listener
    }

    // 필터
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charString: CharSequence?): FilterResults {

                // 검색으로 인한 필터
                val charSearch : String = charString.toString().trim()
                filterListResult = if (charSearch.isEmpty()) {
                    memos
                } else {
                    val resultList = ArrayList<Memo>()
                    for(row in memos) {
                        if(row.memoTitle.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT
                            )
                            )
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
                filterListResult = filterResults!!.values as List<Memo>
                notifyDataSetChanged()
            }

        }
    }

}