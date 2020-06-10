package com.vlaksuga.mymo

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar


import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*


class AddEditActivity : AppCompatActivity() {

    companion object {
        // EXTRA KEYS
        const val EXTRA_REPLY_ID = "com.vlaksuga.mymo.ID"
        const val EXTRA_REPLY_TITLE = "com.vlaksuga.mymo.TITLE"
        const val EXTRA_REPLY_CONTENT = "com.vlaksuga.mymo.CONTENT"
        const val EXTRA_REPLY_INIT_TIME = "com.vlaksuga.mymo.INIT_TIME"
        const val EXTRA_REPLY_COLOR = "com.vlaksuga.mymo.COLOR"


        // COLORS
        const val COLOR_DEFAULT = "#333333"
        const val COLOR_IMPORTANT = "#0275d8"
        const val COLOR_SUCCESS = "#5cb85c"
        const val COLOR_INFORMATION = "#5bc0de"
        const val COLOR_WARNING = "#f0ad4e"
        const val COLOR_DANGER = "#d9534f"
        const val COLOR_PLAIN = "#000000"
    }

    private lateinit var memoViewModel: MemoViewModel

    private var currentColor: String
    init {
        currentColor = COLOR_PLAIN
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // 뷰 모델델
       memoViewModel = ViewModelProvider(this).get(MemoViewModel::class.java)

        // 인텐트 받아옴
        intent

        // 컬러 감지해서 적용하기
        when (intent.getStringExtra(EXTRA_REPLY_COLOR)) {
            COLOR_IMPORTANT -> changeBarColor(COLOR_IMPORTANT, getString(R.string.color_name_important))
            COLOR_SUCCESS -> changeBarColor(COLOR_SUCCESS, getString(R.string.color_name_success))
            COLOR_INFORMATION -> changeBarColor(COLOR_INFORMATION, getString(R.string.color_name_information))
            COLOR_WARNING -> changeBarColor(COLOR_WARNING, getString(R.string.color_name_warning))
            COLOR_DANGER -> changeBarColor(COLOR_DANGER, getString(R.string.color_name_danger))
            else -> changeBarColor(COLOR_PLAIN, getString(R.string.color_name_plain))
        }


        // 메뉴에 뒤로가기를 X로
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)

        // 컬러바 버튼 안보이기
        button_color_important.visibility = View.GONE
        button_color_success.visibility = View.GONE
        button_color_information.visibility = View.GONE
        button_color_warning.visibility = View.GONE
        button_color_danger.visibility = View.GONE
        button_color_plain.visibility = View.GONE

        // 인텐트에 ID가 있을시(EDIT)의 화면 구성
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            val dateSimpleDateFormat = SimpleDateFormat(getString(R.string.date_format), Locale.ROOT)
            val latestDate : String = dateSimpleDateFormat.format(intent.getLongExtra(EXTRA_REPLY_INIT_TIME, 0)).toString()
            title_editText.setText(intent.getStringExtra(EXTRA_REPLY_TITLE))
            content_editText.setText(intent.getStringExtra(EXTRA_REPLY_CONTENT))
            latestDate_textView.text = latestDate
            latestDate_textView.visibility = View.VISIBLE
            supportActionBar!!.title = null
            supportActionBar!!.setBackgroundDrawable(object : ColorDrawable( Color.parseColor(intent.getStringExtra(EXTRA_REPLY_COLOR))) {})
        }

        // 인텐트에 ID가 없을시(ADD)의 화면 구성
        else {
            supportActionBar!!.title = getString(R.string.add_activity_title)
            supportActionBar!!.setBackgroundDrawable(object : ColorDrawable( Color.parseColor(currentColor)) {})
        }

        // 컬러바 클릭시 컬러 아이콘 토글
        currentColor_textView.setOnClickListener {
            colorIconViewVisibilityToggle()
        }

        // 컬러 아이콘 클릭
        button_color_important.setOnClickListener { changeBarColor(COLOR_IMPORTANT, getString(R.string.color_name_important))}
        button_color_success.setOnClickListener { changeBarColor(COLOR_SUCCESS,  getString(R.string.color_name_success))}
        button_color_information.setOnClickListener { changeBarColor(COLOR_INFORMATION, getString(R.string.color_name_information))}
        button_color_warning.setOnClickListener { changeBarColor(COLOR_WARNING, getString(R.string.color_name_warning))}
        button_color_danger.setOnClickListener { changeBarColor(COLOR_DANGER, getString(R.string.color_name_danger))}
        button_color_plain.setOnClickListener { changeBarColor(COLOR_PLAIN, getString(R.string.color_name_plain)) }

    }

    // 컬러바 버튼 클릭시 토글
    private fun colorIconViewVisibilityToggle() {
        if(button_color_danger.visibility == View.VISIBLE) {
            button_color_important.visibility = View.GONE
            button_color_success.visibility = View.GONE
            button_color_information.visibility = View.GONE
            button_color_warning.visibility = View.GONE
            button_color_danger.visibility = View.GONE
            button_color_plain.visibility = View.GONE
        } else {
            button_color_important.visibility = View.VISIBLE
            button_color_success.visibility = View.VISIBLE
            button_color_information.visibility = View.VISIBLE
            button_color_warning.visibility = View.VISIBLE
            button_color_danger.visibility = View.VISIBLE
            button_color_plain.visibility = View.VISIBLE
        }
    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    // 메뉴 아이템 선택시
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_memo -> saveMemo()
            R.id.share_memo -> shareMemo()
            R.id.delete_this -> deleteThisMemo()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteThisMemo() {
        if(intent.hasExtra(EXTRA_REPLY_ID)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.delete_this_message))
            builder.setPositiveButton(getString(R.string.positive_button)) { dialogInterface: DialogInterface, i: Int ->
                memoViewModel.delete(Memo(
                    intent.getIntExtra(EXTRA_REPLY_ID,-1),
                    intent.getStringExtra(EXTRA_REPLY_TITLE)!!,
                    intent.getStringExtra(EXTRA_REPLY_CONTENT)!!,
                    intent.getLongExtra(EXTRA_REPLY_INIT_TIME, 0),
                    intent.getStringExtra(EXTRA_REPLY_COLOR)!!))
                finish()
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton(getString(R.string.negative_button)) { dialogInterface: DialogInterface, i: Int ->
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
            Snackbar.make(findViewById(R.id.add_layout), getString(R.string.memo_is_empty), Snackbar.LENGTH_SHORT)
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
        val resultContent = content_editText?.text
        val initTime: Long = Date().time
        val replyIntent = Intent()

        if (TextUtils.isEmpty(resultTitle)) {
            Snackbar.make(add_layout,getString(R.string.insert_title), Snackbar.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(resultContent)) {
            Snackbar.make(add_layout,getString(R.string.insert_memo), Snackbar.LENGTH_SHORT).show()
            return
        }

        val title = resultTitle.toString()
        val content = resultContent.toString()
        replyIntent.putExtra(EXTRA_REPLY_TITLE, title)
        replyIntent.putExtra(EXTRA_REPLY_CONTENT, content)
        replyIntent.putExtra(EXTRA_REPLY_INIT_TIME, initTime)
        replyIntent.putExtra(EXTRA_REPLY_COLOR, currentColor)

        // 편집으로 아이디 값을 인텐트로 받아왔을 때 아이디 덮어쓰기
        val id: Int = intent.getIntExtra(EXTRA_REPLY_ID, -1)
        if (id != -1) {
            replyIntent.putExtra(EXTRA_REPLY_ID, id)
        }

        setResult(Activity.RESULT_OK, replyIntent)
        finish()
    }

    // 컬러 바 바꾸기
    private fun changeBarColor(colorCode: String, colorName: String) {
        currentColor = colorCode
        currentColor_textView.text = colorName
        supportActionBar!!.setBackgroundDrawable(object : ColorDrawable( Color.parseColor(colorCode)) {})
        currentColor_textView.setBackgroundColor(Color.parseColor(currentColor))
    }
}
