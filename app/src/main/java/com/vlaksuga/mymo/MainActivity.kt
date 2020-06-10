package com.vlaksuga.mymo

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        const val DELETE_MEMO_REQUEST: Int = 3
    }

    private lateinit var memoViewModel: MemoViewModel
    private lateinit var searchView: SearchView
    private lateinit var fab: FloatingActionButton
    lateinit var adapter: MemoAdapter


    private var swipeBackGround: ColorDrawable =
        ColorDrawable(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
    private lateinit var deleteIcon: Drawable
    private var barColorCurrent: String = AddEditActivity.COLOR_PLAIN


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 툴바
        setSupportActionBar(toolbar)
        toolbar_layout.apply {
            setExpandedTitleTextAppearance(R.style.CollapsedAppBar_Expand)
            title = "BY COLORS"
        }

        // 리스트 없을 때 안 보이기 우선 처리
        list_is_empty_view.visibility = View.GONE

        // 어댑터
        adapter = MemoAdapter(this)

        // 리사이클러뷰
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        // 플로팅 액션 버튼
        fab = findViewById(R.id.fab_add)
        fab.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        fab.setOnClickListener {
            colorFilter()
        }

        // 뷰 모델
        memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        memoViewModel.allMemos.observe(this, Observer { memos ->
            memos?.let {
                adapter.setMemos(it)
                // 메모가 없을 때 보여주기
                if (adapter.itemCount < 1) {
                    list_is_empty_view.visibility = View.VISIBLE
                } else {
                    list_is_empty_view.visibility = View.GONE
                }
            }
        })

        // 스와이프로 삭제하기 콜백
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
                val selectedMemo = adapter.getMemoAt(viewHolder.adapterPosition)
                memoViewModel.delete(selectedMemo)

                // 검색 중에 삭제했다면 검색결과 없애버리기
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }

                Snackbar.make(
                    recyclerView,
                    getString(R.string.onswiped_delete),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo), View.OnClickListener {
                        memoViewModel.insert(selectedMemo)
                    })
                    .setActionTextColor(Color.parseColor(getString(R.string.undo_text_color)))
                    .show()
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

        // 스와이프 콜백 뷰에 붙이기
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
                intent.putExtra(AddEditActivity.EXTRA_REPLY_INIT_TIME, memo.initTime)
                startActivityForResult(intent, EDIT_MEMO_REQUEST)
            }
        })

    }

    // 새로 만들기 인텐트
    private fun addNewMemo() {
        val intent = Intent(this@MainActivity, AddEditActivity::class.java)
        intent.putExtra(AddEditActivity.EXTRA_REPLY_COLOR, barColorCurrent)
        startActivityForResult(intent, ADD_MEMO_REQUEST)
    }

    // 결과에 따라 인텐트 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        // 새로 만들기 인텐트로 부터
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

            // 카테고리 되돌리기
            toolbar_layout.title = "BY COLORS"
            fab.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
            title_color_view.setBackgroundColor(Color.parseColor(AddEditActivity.COLOR_DEFAULT))


            if (!searchView.isIconified) {
                searchView.isIconified = true
                searchView.isIconified = true
            }

            if (searchView.query.isEmpty()) {
                searchView.isIconified = true
            }
        }

        // 편집하기 인텐트로 부터
        else if (requestCode == EDIT_MEMO_REQUEST && resultCode == Activity.RESULT_OK) {
            val id: Int = intentData!!.getIntExtra(AddEditActivity.EXTRA_REPLY_ID, -1)
            if (id == -1) {
                Snackbar.make(recyclerView, getString(R.string.update_fail), Snackbar.LENGTH_SHORT)
                    .show()
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

            // 카테고리 되돌리기
            toolbar_layout.title = "BY COLORS"

            fab.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
            title_color_view.setBackgroundColor(Color.parseColor(AddEditActivity.COLOR_DEFAULT))

            if (!searchView.isIconified) {
                searchView.isIconified = true
            }

            if (searchView.query.isEmpty()) {
                searchView.isIconified = false
            }
        }

        // 메모를 바로 삭제하고 액티비티 종료됨
        else if (requestCode == DELETE_MEMO_REQUEST) {
            // 카테고리 되돌리기

            Snackbar.make(recyclerView, getString(R.string.memo_deleted), Snackbar.LENGTH_SHORT)
                .show()
        }

        // 모두 아닌 경우
        else {

        }
    }

    // 메뉴 바 설정
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

    // 메뉴 아이템 옵션 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // 모두 삭제
            R.id.delete_all_memos -> apply {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.delete_All_message))
                builder.setPositiveButton("확인") { dialogInterface, i ->
                    memoViewModel.deleteAll()
                    Snackbar.make(
                        recyclerView,
                        getString(R.string.delete_all_memo),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                builder.setNegativeButton("취소") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            R.id.add_memo_menu -> addNewMemo()

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
                getAllMemo()
                return true
            }

            R.id.memo_color_filter -> colorFilter()

            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAllMemo() {
        adapter.getColor("")
        barColorCurrent = AddEditActivity.COLOR_PLAIN
        toolbar_layout.title = "BY COLORS"
        fab.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        title_color_view.setBackgroundColor(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        Snackbar.make(recyclerView, getString(R.string.filter_all), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor(AddEditActivity.COLOR_DEFAULT)).show()
    }

    private fun colorFilter() {
        val colorSelectBuilder = AlertDialog.Builder(this)
        val colorNames = arrayOf(
            "전체",
            getString(R.string.color_name_important),
            getString(R.string.color_name_success),
            getString(R.string.color_name_information),
            getString(R.string.color_name_warning),
            getString(R.string.color_name_danger),
            getString(R.string.color_name_plain)
        )
        val colors = arrayOf(
            "",
            AddEditActivity.COLOR_IMPORTANT,
            AddEditActivity.COLOR_SUCCESS,
            AddEditActivity.COLOR_INFORMATION,
            AddEditActivity.COLOR_WARNING,
            AddEditActivity.COLOR_DANGER,
            AddEditActivity.COLOR_PLAIN
        )
        colorSelectBuilder
            .setTitle("카테고리 선택")
            .setItems(
                colorNames
            ) { dialog, position ->
                if (position == 0) {
                    getAllMemo()
                } else {
                    listChangeByColor(colors[position], colorNames[position])
                }
            }

            .setNegativeButton(
                getString(R.string.negative_button)
            ) { dialog, which -> dialog?.dismiss() }

        colorSelectBuilder.show()
    }

    private fun listChangeByColor(color: String, name: String) {
        adapter.getColor(color)
        barColorCurrent = color
        toolbar_layout.title = name
        fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        title_color_view.setBackgroundColor(Color.parseColor(color))
        Snackbar.make(recyclerView, name, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor(color)).show()
    }

    // 뒤로가기
    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
        super.onBackPressed()
    }
}
