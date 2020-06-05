package com.vlaksuga.mymo

import android.app.Activity
import android.content.Intent
import android.graphics.Color

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import com.google.android.material.snackbar.Snackbar


import kotlinx.android.synthetic.main.activity_add.*
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
        const val COLOR_RED = "#FF0000"
        const val COLOR_ORANGE = "#FF8800"
        const val COLOR_GREEN = "#00AA00"
        const val COLOR_BLUE = "#0000CC"
        const val COLOR_BLACK = "#000000"
    }

    //
    private var currentColor: String
    init {
        currentColor = COLOR_BLACK
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // 인텐트 받아옴
        intent

        // 메뉴에 뒤로가기를 X로
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        // 컬러바 버튼 안보이기
        button_color_red.visibility = View.GONE
        button_color_orange.visibility = View.GONE
        button_color_green.visibility = View.GONE
        button_color_blue.visibility = View.GONE
        button_color_black.visibility = View.GONE

        // 인텐트에 ID가 있을시(EDIT)의 화면 구성
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            title_editText.setText(intent.getStringExtra(EXTRA_REPLY_TITLE))
            content_editText.setText(intent.getStringExtra(EXTRA_REPLY_CONTENT))
            supportActionBar!!.title = null
            when (intent.getStringExtra(EXTRA_REPLY_COLOR)) {
                "#FF0000" -> changeBarColor(COLOR_RED, "RED")
                "#FF8800" -> changeBarColor(COLOR_ORANGE, "ORANGE")
                "#00AA00" -> changeBarColor(COLOR_GREEN, "GREEN")
                "#0000CC" -> changeBarColor(COLOR_BLUE, "BLUE")
                else -> changeBarColor(COLOR_BLACK, "BLACK")
            }
        }

        // 인텐트에 ID가 없을시(ADD)의 화면 구성
        else {
            supportActionBar!!.title = "NEW"
        }

        // 컬러바 클릭시 컬러 아이콘 토글
        currentColor_textView.setOnClickListener {
            colorIconViewVisibilityToggle()
        }

        // 컬러 아이콘 클릭
        button_color_red.setOnClickListener { changeBarColor(COLOR_RED, "RED") }
        button_color_orange.setOnClickListener { changeBarColor(COLOR_ORANGE, "ORANGE") }
        button_color_green.setOnClickListener { changeBarColor(COLOR_GREEN, "GREEN") }
        button_color_blue.setOnClickListener { changeBarColor(COLOR_BLUE, "BLUE") }
        button_color_black.setOnClickListener { changeBarColor(COLOR_BLACK, "BLACK") }

    }

    // 컬러바 버튼 클릭시 토글
    private fun colorIconViewVisibilityToggle() {
        if(button_color_red.visibility == View.VISIBLE) {
            button_color_red.visibility = View.GONE
            button_color_orange.visibility = View.GONE
            button_color_green.visibility = View.GONE
            button_color_blue.visibility = View.GONE
            button_color_black.visibility = View.GONE
        } else {
            button_color_red.visibility = View.VISIBLE
            button_color_orange.visibility = View.VISIBLE
            button_color_green.visibility = View.VISIBLE
            button_color_blue.visibility = View.VISIBLE
            button_color_black.visibility = View.VISIBLE
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
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    // 해당 메모 공유 인텐트
    private fun shareMemo() {
        if (title_editText.text.isEmpty() or content_editText.text.isEmpty()) {
            Snackbar.make(findViewById(R.id.add_layout), "MEMO IS EMPTY", Snackbar.LENGTH_SHORT)
                .show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        val titleText = title_editText?.text.toString()
        val contentText = content_editText?.text.toString()
        val shareContent = titleText + contentText
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareContent)
        val shareMemoIntent = Intent.createChooser(intent, "MEMO 공유")
        startActivity(shareMemoIntent)
    }

    // 저장하기 인텐트
    private fun saveMemo() {
        val resultTitle = title_editText?.text
        val resultContent = content_editText?.text
        val initTime: Long = Date().time
        val replyIntent = Intent()

        if (TextUtils.isEmpty(resultTitle)) {
            Toast.makeText(this, getString(R.string.insert_title), Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(resultContent)) {
            Toast.makeText(this, getString(R.string.insert_memo), Toast.LENGTH_SHORT).show()
            return
        }

        val title = resultTitle.toString()
        val content = resultContent.toString()
        replyIntent.putExtra(EXTRA_REPLY_TITLE, title)
        replyIntent.putExtra(EXTRA_REPLY_CONTENT, content)
        replyIntent.putExtra(EXTRA_REPLY_INIT_TIME, initTime)
        replyIntent.putExtra(EXTRA_REPLY_COLOR, currentColor)

        // Edit로 ID값을 인텐트로 받아왔을 때 아이디 덮어쓰기
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
        currentColor_textView.text = "${colorName}"
        currentColor_textView.setBackgroundColor(Color.parseColor(currentColor))
    }
}
