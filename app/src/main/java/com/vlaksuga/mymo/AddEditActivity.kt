package com.vlaksuga.mymo

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.lifecycle.Observer
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


import kotlinx.android.synthetic.main.activity_add_edit.*
import java.text.SimpleDateFormat
import java.util.*


class AddEditActivity : AppCompatActivity() {

    companion object {
        // EXTRA KEYS
        const val EXTRA_REPLY_THEME_COLOR = "com.vlaksuga.mymo.THEME_COLOR"
        const val EXTRA_REPLY_FILTER_STATE = "com.vlaksuga.mymo.FILTER_STATE"
        const val EXTRA_REPLY_ID = "com.vlaksuga.mymo.ID"
        const val EXTRA_REPLY_TITLE = "com.vlaksuga.mymo.TITLE"
        const val EXTRA_REPLY_CONTENT = "com.vlaksuga.mymo.CONTENT"
        const val EXTRA_REPLY_INIT_TIME = "com.vlaksuga.mymo.INIT_TIME"
        const val EXTRA_REPLY_IMPORTANCE = "com.vlaksuga.mymo.IMPORTANCE"
        const val EXTRA_REPLY_GROUP_ID = "com.vlaksuga.mymo.GROUP_ID"
        const val EXTRA_REPLY_GROUP_COLOR = "com.vlaksuga.mymo.GROUP_COLOR"
        const val EXTRA_REPLY_GROUP_NAME = "com.vlaksuga.mymo.GROUP_NAME"

        // COLORS
        const val COLOR_DEFAULT = "#292B2C"

        // DEFAULT
        const val GROUP_NAME_DEFAULT = "그룹 없음"

    }

    private lateinit var viewModel: ViewModel
    private lateinit var menu: Menu
    lateinit var adapter: GroupSelectListAdapter

    private var currentFilterState: Int
    private var currentThemeColor: String
    private var currentTime: Long
    private var currentImportance: Boolean
    private var currentGroupId: Int
    private var currentGroupName: String
    private var currentGroupColor: String


    init {
        currentFilterState = 0
        currentThemeColor = COLOR_DEFAULT
        currentTime = Date().time
        currentImportance = false
        currentGroupId = 1
        currentGroupName = GROUP_NAME_DEFAULT
        currentGroupColor = COLOR_DEFAULT
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        Log.d("onCreate.add.App.prefs.instantMemoTitle.rockteki", App.prefs.instantMemoTitle!!)
        Log.d("onCreate.add.App.prefs.instantMemoContent.rockteki", App.prefs.instantMemoContent!!)
        Log.d("onCreate.add.App.prefs.instantMemoGroupId.rockteki", App.prefs.instantMemoGroupId.toString())
        Log.d("onCreate.add.currentFilterState.rockteki", currentFilterState.toString())
        Log.d("onCreate.add.currentGroupId.rockteki", currentGroupId.toString())
        // 타이틀
        supportActionBar!!.title = null
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor(currentThemeColor)))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)


        // 뷰 모델
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allGroups.observe(this, Observer { groups ->
            groups?.let {
                adapter.setGroups(it)
            }
        })

        // 어댑터
        adapter = GroupSelectListAdapter(this)

        // 리사이클러뷰
        val groupListRecyclerView = findViewById<RecyclerView>(R.id.group_list_recyclerView)
        groupListRecyclerView.setHasFixedSize(true)
        groupListRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        groupListRecyclerView.adapter = adapter
        groupListRecyclerView.visibility = View.GONE


        // 아이템 클릭 -> 편집 인텐트
        adapter.setOnItemClickListener(object : GroupSelectListAdapter.OnItemClickListener {
            override fun onItemClick(group: Group) {
                currentGroupId = group.groupId
                currentThemeColor = group.groupColor
                currentGroupName = group.groupName
                currentGroupColor = group.groupColor
                currentColor_textView.apply {
                    text = currentGroupName
                    background = ColorDrawable(Color.parseColor(currentThemeColor))
                    supportActionBar!!.setBackgroundDrawable(
                        ColorDrawable(
                            Color.parseColor(
                                currentThemeColor
                            )
                        )
                    )
                }
                Log.d("onItemClick.add.currentFilterState.rockteki", currentFilterState.toString())
                Log.d("onItemClick.add.currentGroupId.rockteki", currentGroupId.toString())
            }
        })


        // 편집 화면 구성
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            val dateSimpleDateFormat =
                SimpleDateFormat(getString(R.string.date_format), Locale.ROOT)
            val latestDate: String =
                dateSimpleDateFormat.format(intent.getLongExtra(EXTRA_REPLY_INIT_TIME, 0))
                    .toString()

            title_editText.setText(intent.getStringExtra(EXTRA_REPLY_TITLE))
            content_editText.setText(intent.getStringExtra(EXTRA_REPLY_CONTENT))
            latestDate_textView.text = latestDate
            currentFilterState = intent.getIntExtra(EXTRA_REPLY_FILTER_STATE, 0)
            currentThemeColor = intent.getStringExtra(EXTRA_REPLY_GROUP_COLOR)!!
            currentImportance = intent.getBooleanExtra(EXTRA_REPLY_IMPORTANCE, false)
            currentGroupId = intent.getIntExtra(EXTRA_REPLY_GROUP_ID, 1)
            currentGroupColor = intent.getStringExtra(EXTRA_REPLY_GROUP_COLOR)!!
            currentGroupName = intent.getStringExtra(EXTRA_REPLY_GROUP_NAME)!!

            // 아래 컬러바
            currentColor_textView.apply {
                text = currentGroupName
                background = ColorDrawable(Color.parseColor(currentThemeColor))
            }
            // 액션바
            supportActionBar!!.title = ""
            supportActionBar!!.setBackgroundDrawable(object :
                ColorDrawable(Color.parseColor(currentThemeColor)) {})

            Log.d("editmode.add.currentFilterState.rockteki", currentFilterState.toString())
            Log.d("editmode.add.currentGroupId.rockteki", currentGroupId.toString())
        }

        // 추가 화면 구성
        if (currentFilterState != 0) {
            currentGroupId = 1
            Log.d(
                "newmode.currentFilterState !=0.add.currentFilterState.rockteki",
                currentFilterState.toString()
            )
            Log.d(
                "newmode.currentFilterState !=0.add.currentGroupId.rockteki",
                currentGroupId.toString()
            )
        }


        val dateSimpleDateFormat =
            SimpleDateFormat(getString(R.string.date_format), Locale.ROOT)
        val nowDate: String =
            dateSimpleDateFormat.format(currentTime)
                .toString()
        latestDate_textView.text = nowDate
        currentFilterState = intent.getIntExtra(EXTRA_REPLY_FILTER_STATE, 0)
        currentThemeColor = intent.getStringExtra(EXTRA_REPLY_GROUP_COLOR)!!
        currentGroupId = intent.getIntExtra(EXTRA_REPLY_GROUP_ID, 1)
        currentGroupColor = intent.getStringExtra(EXTRA_REPLY_GROUP_COLOR)!!
        currentGroupName = intent.getStringExtra(EXTRA_REPLY_GROUP_NAME)!!


        // 아래 컬러바
        currentColor_textView.apply {
            text = currentGroupName
            background = ColorDrawable(Color.parseColor(currentThemeColor))
        }

        // 액션바
        supportActionBar!!.setBackgroundDrawable(object :
            ColorDrawable(Color.parseColor(currentThemeColor)) {})
        Log.d("newmode.add.currentFilterState.rockteki", currentFilterState.toString())
        Log.d("newmode.add.currentGroupId.rockteki", currentGroupId.toString())


        // 컬러바 클릭시 컬러 아이콘 토글

        currentColor_textView.setOnClickListener {
            if (groupListRecyclerView.visibility == View.VISIBLE) {
                groupListRecyclerView.visibility = View.GONE
                color_toggle_icon.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                groupListRecyclerView.visibility = View.VISIBLE
                color_toggle_icon.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
    }


    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.add_note_menu, menu)
        this.menu = menu!!
        if (intent.getBooleanExtra(EXTRA_REPLY_IMPORTANCE, false)) {
            menu.getItem(1).icon = ContextCompat.getDrawable(this, R.drawable.ic_lock)
        } else {
            menu.getItem(1).icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open)
        }
        return true
    }

    // 메뉴 아이템 선택시
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> apply {
                onBackPressed()
                return true
            }
            R.id.save_memo -> saveMemo()
            R.id.important_memu -> apply {
                if (!currentImportance) {
                    item.setIcon(R.drawable.ic_lock)
                    currentImportance = true
                    Toast.makeText(this@AddEditActivity, "삭제되지 않는 메모가 되었습니다..", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    item.setIcon(R.drawable.ic_lock_open)
                    currentImportance = false
                    Toast.makeText(this@AddEditActivity, "잠금 상태에서 해제되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            R.id.share_memo -> shareMemo()
            R.id.delete_this -> deleteThisMemo()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteThisMemo() {
        if(currentImportance) {
            Toast.makeText(this, "잠금상태에선 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.delete_this_message))
            builder.setPositiveButton(getString(R.string.positive_button)) { _: DialogInterface, _: Int ->
                viewModel.delete(
                    Memo(
                        intent.getIntExtra(EXTRA_REPLY_ID, -1),
                        intent.getStringExtra(EXTRA_REPLY_TITLE)!!,
                        intent.getStringExtra(EXTRA_REPLY_CONTENT)!!,
                        intent.getLongExtra(EXTRA_REPLY_INIT_TIME, 0),
                        intent.getBooleanExtra(EXTRA_REPLY_IMPORTANCE, false),
                        intent.getIntExtra(EXTRA_REPLY_GROUP_ID, 1),
                        intent.getStringExtra(EXTRA_REPLY_GROUP_COLOR)!!,
                        intent.getStringExtra(EXTRA_REPLY_GROUP_NAME)!!
                    )
                )
                finish()
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton(getString(R.string.negative_button)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            builder.show()
        } else {
            finish()
        }
    }

    // 해당 메모 공유 인텐트
    private fun shareMemo() {
        if (title_editText.text.isEmpty() or content_editText.text.isEmpty()) {
            Snackbar.make(
                findViewById(R.id.add_layout),
                getString(R.string.memo_is_empty),
                Snackbar.LENGTH_SHORT
            )
                .show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        val titleText = title_editText?.text.toString()
        val contentText = content_editText?.text.toString()
        val shareContent = titleText + contentText
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
        val shareMemoIntent = Intent.createChooser(intent, getString(R.string.share_title))
        startActivity(shareMemoIntent)
    }

    // 저장하기 인텐트
    private fun saveMemo() {
        val resultTitle = title_editText?.text
        var title = resultTitle.toString()

        val resultContent = content_editText?.text
        val content = resultContent.toString()

        val initTime: Long = Date().time
        val replyIntent = Intent()

        if (TextUtils.isEmpty(resultTitle)) {
            title = "제목없음"
        }
        if (TextUtils.isEmpty(resultContent)) {
            Snackbar.make(add_layout, getString(R.string.insert_memo), Snackbar.LENGTH_SHORT).show()
            return
        }


        // Memo()를 생성하기 위한 엑스트라(memo_id는 자동으로 생성됨)
        replyIntent.putExtra(EXTRA_REPLY_TITLE, title) // memoTitle
        replyIntent.putExtra(EXTRA_REPLY_CONTENT, content) // memoContent
        replyIntent.putExtra(EXTRA_REPLY_INIT_TIME, initTime) // initTime
        replyIntent.putExtra(EXTRA_REPLY_IMPORTANCE, currentImportance) // isImportant
        replyIntent.putExtra(EXTRA_REPLY_GROUP_ID, currentGroupId) // groupId
        replyIntent.putExtra(EXTRA_REPLY_GROUP_COLOR, currentGroupColor) // groupColor
        replyIntent.putExtra(EXTRA_REPLY_GROUP_NAME, currentGroupName) // groupName
        replyIntent.putExtra(
            EXTRA_REPLY_FILTER_STATE,
            currentFilterState
        ) // filterStateFrom MainActivity

        // 편집으로 아이디 값을 인텐트로 받아왔을 때 아이디 덮어쓰기
        val id: Int = intent.getIntExtra(EXTRA_REPLY_ID, -1)
        if (id != -1) {
            replyIntent.putExtra(EXTRA_REPLY_ID, id)
        }

        setResult(Activity.RESULT_OK, replyIntent)
        Log.d("save.add.currentFilterState.rockteki", currentFilterState.toString())
        Log.d("save.add.currentGroupId.rockteki", currentGroupId.toString())
        finish()


    }

    private fun askModifiedWhenLeave() {

        val askDialog = AlertDialog.Builder(this)
        askDialog.setMessage("변경사항을 저장할까요?")
            .setPositiveButton("저장"
            ) { _, _ -> saveMemo() }

            .setNeutralButton("저장 안 함"
            ) { _, _ -> finish() }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onBackPressed() {
        // 쉐어드 프리퍼런스에 동일
        if(App.prefs.instantMemoTitle != title_editText.text.toString() || App.prefs.instantMemoImportance != currentImportance || App.prefs.instantMemoContent != content_editText.text.toString()) {
            askModifiedWhenLeave()
            if(App.prefs.instantMemoGroupId != currentGroupId) {
                askModifiedWhenLeave()
            }

            Log.d("onBackPressed.main.App.prefs.instantMemoTitle.rockteki", App.prefs.instantMemoTitle!!)
            Log.d("onBackPressed.main.App.prefs.instantMemoContent.rockteki", App.prefs.instantMemoContent!!)
            Log.d("onBackPressed.main.App.prefs.instantMemoImportance.rockteki", App.prefs.instantMemoImportance.toString())
            Log.d("onBackPressed.main.App.prefs.instantMemoGroupId.rockteki", App.prefs.instantMemoGroupId.toString())
            return
        }
        super.onBackPressed()
    }

    override fun onStop() {
        App.prefs.instantMemoTitle = ""
        App.prefs.instantMemoContent = ""
        App.prefs.instantMemoImportance = false

        Log.d("onStop.main.App.prefs.instantMemoTitle.rockteki", App.prefs.instantMemoTitle!!)
        Log.d("onStop.main.App.prefs.instantMemoContent.rockteki", App.prefs.instantMemoContent!!)
        Log.d("onStop.main.App.prefs.instantMemoImportance.rockteki", App.prefs.instantMemoImportance.toString())
        Log.d("onStop.main.App.prefs.instantMemoGroupId.rockteki", App.prefs.instantMemoGroupId.toString())
        super.onStop()
    }
}
