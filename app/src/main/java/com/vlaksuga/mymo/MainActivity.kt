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
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
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
        const val TAG : String = "MainActivity"
    }

    lateinit var memoAdapter: MemoAdapter
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var groupFilterAdapter: GroupFilterAdapter
    private lateinit var viewModel: ViewModel
    private lateinit var searchView: SearchView
    private lateinit var fab: FloatingActionButton
    private lateinit var deleteIcon: Drawable

    private var swipeBackGround: ColorDrawable =
        ColorDrawable(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
    private var currentThemeColor: String = AddEditActivity.COLOR_DEFAULT
    private var currentFilterState: Int = 0
    private var currentGroupId: Int = -1
    private var currentGroupName: String = "ETC"
    private var currentGroupColor: String = "#000000"

    // 시작
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 인트로 패스 체크
        val prefsState : Boolean = App.prefs.introPassed

        if(!prefsState) {
            startActivity(Intent(this, SwipeTutorial::class.java))
        }


        // 툴바
        this.setSupportActionBar(toolbar)
        toolbar_layout.apply {
            setExpandedTitleTextAppearance(R.style.CollapsedAppBar_Expand)
            title = getString(R.string.app_name)
        }

        // 앱 부제
        desc_textView.text = getString(R.string.subtitle)

        // 어댑터
        memoAdapter = MemoAdapter(this)
        groupAdapter = GroupAdapter(this)
        groupFilterAdapter = GroupFilterAdapter(this)

        // 뷰 모델
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allMemos.observe(this, Observer { memos ->
            memos?.let {
                memoAdapter.setMemos(it)
                if (currentFilterState != 0) {
                    memoAdapter.getListByGroupId(currentGroupId)
                }
                checkEmptyView()
                val memoCounter = memoAdapter.itemCount.toString() + getString(R.string.main_note_count)
                count_textView.text = memoCounter
            }
        })

        viewModel.allGroups.observe(this, Observer { groups ->
            groups?.let {
                groupFilterAdapter.setGroups(it)
                getAllMemo()
            }
        })

        // 엠티뷰 숨기기
        list_is_empty_view.visibility = View.GONE


        // 리사이클러뷰
        val mainRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = memoAdapter


        // 플로팅 액션 버튼
        fab = findViewById(R.id.fab_add)
        fab.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        fab.setOnClickListener {
            selectGroupDialog()
        }


        // 리스트 스와이프 -> 삭제하기 콜백
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
                // 선택된 메모의 포지션과 다른 것이 삭제됨
                val selectedMemo = memoAdapter.getMemoAt(viewHolder.adapterPosition)
                val swipeDeleteBuilder = AlertDialog.Builder(this@MainActivity)


                swipeDeleteBuilder.apply {
                    setMessage(getString(R.string.delete_this_message))
                    setPositiveButton(
                        getString(R.string.positive_button)
                    ) { _, _ ->
                        viewModel.delete(selectedMemo)
                        Snackbar.make(
                            recyclerView,
                            getString(R.string.onswiped_delete),
                            Snackbar.LENGTH_LONG
                        )

                            .setAction(getString(R.string.undo), View.OnClickListener {
                                viewModel.undoMemo(selectedMemo)
                            })
                            .setActionTextColor(Color.parseColor(getString(R.string.undo_text_color)))
                            .show()
                    }
                    setNegativeButton(
                        getString(R.string.negative_button)
                    ) { _, _ ->
                        viewModel.delete(selectedMemo)
                        viewModel.undoMemo(selectedMemo)
                    }
                    setOnCancelListener {
                        viewModel.delete(selectedMemo)
                        viewModel.undoMemo(selectedMemo)
                    }
                    show()
                }


                // 검색 중에 삭제했다면 검색결과 없애버리기
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
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

            // 잠금 스와이프 제약
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder is MemoAdapter.MemoViewHolder && viewHolder.importantItemView.visibility == View.VISIBLE) {
                    return 0
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }

        val itemTouchHelperRight = ItemTouchHelper(itemTouchHelperRightCallback)
        itemTouchHelperRight.attachToRecyclerView(mainRecyclerView)


        // 아이템 클릭 -> 편집 인텐트
        memoAdapter.setOnItemClickListener(object : MemoAdapter.OnItemClickListener {
            override fun onItemClick(memo: Memo) {
                if (intent.hasExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE)) {
                    currentFilterState =
                        intent.getIntExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE, 0)
                }

                App.prefs.instantMemoTitle = memo.memoTitle
                App.prefs.instantMemoContent = memo.memoContent
                App.prefs.instantMemoGroupId = memo.groupId
                App.prefs.instantMemoImportance = memo.isImportant



                val editIntent = Intent(this@MainActivity, AddEditActivity::class.java)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_ID, memo._id)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_TITLE, memo.memoTitle)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_CONTENT, memo.memoContent)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_INIT_TIME, memo.initTime)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_IMPORTANCE, memo.isImportant)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_ID, memo.groupId)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_COLOR, memo.groupColor)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_NAME, memo.groupName)
                editIntent.putExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE, currentFilterState)
                startActivityForResult(editIntent, EDIT_MEMO_REQUEST)
            }
        })
    }

    // 인텐트 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        // 메모추가 인텐트로 부터
        if (requestCode == ADD_MEMO_REQUEST && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                // 메모 인서트 처리
                val memo = Memo(
                    0,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_TITLE)!!,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_CONTENT)!!,
                    data.getLongExtra(AddEditActivity.EXTRA_REPLY_INIT_TIME, 0),
                    data.getBooleanExtra(AddEditActivity.EXTRA_REPLY_IMPORTANCE, false),
                    data.getIntExtra(AddEditActivity.EXTRA_REPLY_GROUP_ID, 1),
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_GROUP_COLOR)!!,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_GROUP_NAME)!!
                )
                viewModel.insert(memo)

                // 커런트 그룹 인텐트
                currentFilterState = data.getIntExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE, 0)
                Unit
            }


            // 카테고리 되돌리기
            if (!searchView.isIconified) {
                searchView.isIconified = true
                searchView.isIconified = true
            }

            if (searchView.query.isEmpty()) {
                searchView.isIconified = true
            }
        }

        // 메모편집 인텐트로 부터
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
                    data.getBooleanExtra(AddEditActivity.EXTRA_REPLY_IMPORTANCE, false),
                    data.getIntExtra(AddEditActivity.EXTRA_REPLY_GROUP_ID, 1),
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_GROUP_COLOR)!!,
                    data.getStringExtra(AddEditActivity.EXTRA_REPLY_GROUP_NAME)!!
                )
                viewModel.update(memo)

                // 커런트 그룹 인텐트
                currentFilterState = data.getIntExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE, 0)
                Unit
            }

            // 카테고리 되돌리기
            if (!searchView.isIconified) {
                searchView.isIconified = true
            }

            if (searchView.query.isEmpty()) {
                searchView.isIconified = false
            }
        }
    }

    // 메뉴 설정
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
                memoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                memoAdapter.filter.filter(newText)
                return false
            }
        })

        return true
    }

    // 메뉴 아이템 옵션 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // 모두 삭제
            R.id.delete_all_memos -> appReset()

            // 새로 만들기
            R.id.add_memo_menu -> addNewMemo()

            // 제목 검색
            R.id.action_search -> apply {
                getAllMemo()
                appBarLayout.setExpanded(false)
                return true
            }

            R.id.trash_menu -> apply {
                startActivity(Intent(this, TrashActivity::class.java))
            }

            // 그룹 액티비티 추가 / 편집
            R.id.group_config -> goToGroupAddEditActivity()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }


    /* functions */

    private fun selectGroupDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert_dialog_filter_group,null, false)

        builder.setView(view)
        val alertDialogRecyclerView: RecyclerView =
            view.findViewById(R.id.alert_dialog_recyclerView)
        val groupFilterAdapter = GroupFilterAdapter(this)
        viewModel.allGroups.value?.let { itGroup -> groupFilterAdapter.setGroups(itGroup) }
        viewModel.allMemos.value?.let {memos ->
            groupFilterAdapter.setMemos(memos)
            val groupAllCount : TextView = view.findViewById(R.id.all_group_count_memos_textview)
            groupAllCount.text = groupFilterAdapter.memoListResult.size.toString()
        }
        alertDialogRecyclerView.setHasFixedSize(true)
        alertDialogRecyclerView.layoutManager = LinearLayoutManager(this)
        alertDialogRecyclerView.adapter = groupFilterAdapter
        val newMemoButton: Button = view.findViewById(R.id.ad_new_memo)
        val newButton : Button = view.findViewById(R.id.ad_edit_group_button)
        val allGroupCardView : CardView = view.findViewById(R.id.all_group_cardView)

        val dialog = builder.create()
        groupFilterAdapter.setOnItemClickListener(object :
            GroupFilterAdapter.OnItemClickListener {
            override fun onItemClick(group: Group) {

                setListByGroup(group)
                Snackbar.make(recyclerView, group.groupName, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor(group.groupColor)).show()
                checkEmptyView()
                dialog.dismiss()
            }
        })

        // 전체보기 선택시
        newMemoButton.setOnClickListener {
            addNewMemo()
            dialog.dismiss()
        }

        newButton.setOnClickListener {
            val goGroupConfigIntent = Intent(this, GroupConfigActivity::class.java)
            startActivity(goGroupConfigIntent)
            dialog.dismiss()
        }

        allGroupCardView.setOnClickListener {
            getAllMemo()
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    // 그룹 설정으로 가기
    private fun goToGroupAddEditActivity() {
        val configIntent = Intent(this, GroupConfigActivity::class.java)
        startActivity(configIntent)
    }

    // 전체 메모 삭제
    private fun appReset() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_All_message))
        builder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
            viewModel.deleteAll()
            viewModel.deleteAllGroup(1)
            viewModel.deleteAllTrash()
            Snackbar.make(
                recyclerView,
                getString(R.string.delete_all_memo),
                Snackbar.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton(getString(R.string.negative_button)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setListByGroup(group: Group) {
        memoAdapter.getListByGroupId(group.groupId)
        toolbar_layout.title = group.groupName
        desc_textView.text = group.groupDesc
        currentFilterState = group.groupId
        currentThemeColor = group.groupColor
        currentGroupColor = group.groupColor
        currentGroupName = group.groupName
        currentGroupId = group.groupId

        App.prefs.instantMemoGroupId = currentGroupId

        fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(group.groupColor))



        title_color_view.setBackgroundColor(Color.parseColor(group.groupColor))
        val memoCounter = memoAdapter.itemCount.toString() + getString(R.string.main_note_count)
        count_textView.text = memoCounter
    }

    // 새 메모 만들기
    private fun addNewMemo() {

        val newIntent = Intent(this@MainActivity, AddEditActivity::class.java)
        newIntent.putExtra(AddEditActivity.EXTRA_REPLY_FILTER_STATE, currentFilterState)
        newIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_ID, currentGroupId)
        newIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_NAME, currentGroupName)
        newIntent.putExtra(AddEditActivity.EXTRA_REPLY_GROUP_COLOR, currentGroupColor)
        startActivityForResult(newIntent, ADD_MEMO_REQUEST)
    }

    // 전체 메모 열기
    private fun getAllMemo() {
        memoAdapter.getAllList()
        toolbar_layout.title = getString(R.string.app_name)
        desc_textView.text = getString(R.string.subtitle)
        currentFilterState = 0
        currentThemeColor = AddEditActivity.COLOR_DEFAULT
        currentGroupColor = AddEditActivity.COLOR_DEFAULT
        currentGroupName = getString(R.string.default_currentGroupName)
        currentGroupId = 1
        fab.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        App.prefs.instantMemoGroupId = currentGroupId
        title_color_view.setBackgroundColor(Color.parseColor(AddEditActivity.COLOR_DEFAULT))
        val memoCounter = memoAdapter.itemCount.toString() + getString(R.string.main_note_count)
        count_textView.text = memoCounter
        checkEmptyView()

    }

    // 리스트 없음
    private fun checkEmptyView() {
        if (memoAdapter.itemCount < 1) {
            list_is_empty_view.visibility = View.VISIBLE
        } else {
            list_is_empty_view.visibility = View.GONE
        }
    }


    override fun onBackPressed() {

        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }

        if (currentFilterState != 0){
            getAllMemo()
            return
        }

        else {
            val closeBuilder = AlertDialog.Builder(this)
            closeBuilder.setMessage(getString(R.string.close_app_message))
                .setPositiveButton(getString(R.string.positive_button)
                ) { _, _ ->
                    finish()
                }
                .setNegativeButton(getString(R.string.negative_button)
                ) { dialog, _ -> dialog!!.dismiss() }
                .show()
        }
    }
}
