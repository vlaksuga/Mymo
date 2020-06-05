package com.vlaksuga.mymo

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_MEMO_REQUEST: Int = 1
        const val EDIT_MEMO_REQUEST: Int = 2
    }

    private lateinit var memoViewModel: MemoViewModel
    private lateinit var searchView: SearchView
    lateinit var adapter: MemoAdapter

    private var swipeBackGround: ColorDrawable = ColorDrawable(Color.parseColor("#333333"))
    private lateinit var deleteIcon: Drawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 어댑터
        adapter = MemoAdapter(this)


        // 리사이클러뷰
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 리스트 카운터 달기



        // 플로팅 액션 버튼 -> 새로만들기 인텐트
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditActivity::class.java)
            startActivityForResult(intent, ADD_MEMO_REQUEST)
        }

        // 뷰 모델
        memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        memoViewModel.allMemos.observe(this, Observer { memos ->
            memos?.let {
                list_is_empty_view.visibility = View.GONE
                adapter.setMemos(it)

                // 메모가 없을 때 보여주기
                if(adapter.itemCount < 1) {
                    list_is_empty_view.visibility = View.VISIBLE
                }
            }
        })

        // 스와이프로 삭제하기
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        val itemTouchHelperRightCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                memoViewModel.delete(adapter.getMemoAt(viewHolder.adapterPosition))

                // 검색 중에 삭제했다면 검색결과 없애버리기
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
                Snackbar.make(recyclerView, "삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                // TODO : DIALOG OR CUSTOM SNACK BAR FOR CONFIRM TO DELETE
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val deleteIconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackGround.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + deleteIconMargin,
                        itemView.top + deleteIconMargin,
                        itemView.left + deleteIconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - deleteIconMargin
                    )
                } else {
                    swipeBackGround.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - deleteIconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + deleteIconMargin,
                        itemView.right - deleteIconMargin,
                        itemView.bottom - deleteIconMargin
                    )
                }

                swipeBackGround.draw(c)
                deleteIcon.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelperRight = ItemTouchHelper(itemTouchHelperRightCallback)
        itemTouchHelperRight.attachToRecyclerView(recyclerView)


        // 아이템 클릭 -> 편집 인텐트
        adapter.setOnItemClickListener(object : MemoAdapter.onItemClickListener {
            override fun onItemClick(memo: Memo) {
                val intent: Intent = Intent(this@MainActivity, AddEditActivity::class.java)
                intent.putExtra(AddEditActivity.EXTRA_REPLY_ID, memo._id)
                intent.putExtra(AddEditActivity.EXTRA_REPLY_TITLE, memo.memoTitle)
                intent.putExtra(AddEditActivity.EXTRA_REPLY_CONTENT, memo.memoContent)
                intent.putExtra(AddEditActivity.EXTRA_REPLY_COLOR, memo.barColor)
                startActivityForResult(intent, EDIT_MEMO_REQUEST)
            }
        })

    }

    // 결과에 따라 인텐트 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        // 새로 만들기 인텐트
        if (requestCode == ADD_MEMO_REQUEST && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val memo = Memo(
                    0,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_TITLE)!!,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_CONTENT)!!,
                    data.getLongExtra(AddEditActivity.EXTRA_REPLY_INIT_TIME, 0),
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_COLOR)!!
                )
                memoViewModel.insert(memo)
                Unit
            }
            if (!searchView.isIconified) {
                searchView.isIconified = true
            }
        }

        // 편집하기 인텐트
        else if (requestCode == EDIT_MEMO_REQUEST && resultCode == Activity.RESULT_OK) {
            val id: Int = intentData!!.getIntExtra(AddEditActivity.EXTRA_REPLY_ID, -1)
            if (id == -1) {
                Toast.makeText(this, getString(R.string.update_fail), Toast.LENGTH_SHORT).show()
                return
            }
            // 편집된 데이터 적용
            intentData.let { data ->
                val memo = Memo(
                    data.getIntExtra(AddEditActivity.EXTRA_REPLY_ID, -1),
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_TITLE)!!,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_CONTENT)!!,
                    data.getLongExtra(AddEditActivity.EXTRA_REPLY_INIT_TIME, 0),
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_COLOR)!!
                )
                memoViewModel.update(memo)
                Unit
            }

            // 검색 결과 되돌리기
            if (!searchView.isIconified) {
                searchView.isIconified = true
            }
        }
        // 둘 다 아닌 경우 또는 취소
        else {

        }
    }

    // 메뉴바 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // 검색
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Int.MAX_VALUE


        // 검색 쿼리텍스트 리스너
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    // 메뉴 버튼 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // 모두 삭제
            R.id.delete_all_memos -> apply {
                memoViewModel.deleteAll()
                Toast.makeText(this, getString(R.string.delete_all_memo), Toast.LENGTH_SHORT).show()
            }

            // 앱 공유
            R.id.share_app -> apply {
                val appURLString = "https://play.google.com/store/apps/details?id=com.vlaksuga.mymo"
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "앱이름 \n $appURLString")
                val shareAppIntent = Intent.createChooser(intent, "앱 공유하기")
                startActivity(shareAppIntent)
            }

            // 제목 검색
            R.id.action_search -> apply {
                return true
            }

            // TODO ("컬러별로 나오게 만들기")
            R.id.get_orange_memo -> apply {
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "ORANGE", Toast.LENGTH_SHORT).show()
            }
            // 예외처리
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    // 뒤로가기시 아이콘 돌리기
    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
        super.onBackPressed()
    }
}
