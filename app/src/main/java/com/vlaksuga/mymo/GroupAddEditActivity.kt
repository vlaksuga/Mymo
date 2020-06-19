package com.vlaksuga.mymo

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_edit.*
import kotlinx.android.synthetic.main.activity_group_add_edit.*
import petrov.kristiyan.colorpicker.ColorPicker


class GroupAddEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GROUP_ID = "com.vlaksuga.mymo.GROUP_ID"
        const val EXTRA_GROUP_NAME = "com.vlaksuga.mymo.GROUP_NAME"
        const val EXTRA_GROUP_DESC = "com.vlaksuga.mymo.GROUP_DESC"
        const val EXTRA_GROUP_COLOR = "com.vlaksuga.mymo.GROUP_COLOR"
    }

    lateinit var groupAdapter: GroupAdapter
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_add_edit)

        // 시작화면 설정
        supportActionBar!!.title = "그룹 추가"
        if(intent.hasExtra(EXTRA_GROUP_ID)) {
            supportActionBar!!.title = "그룹 편집"
            current_group_color_textView.apply{
                this.text =  intent.getStringExtra(EXTRA_GROUP_COLOR)
                this.backgroundTintList = ColorStateList.valueOf(Color.parseColor(intent.getStringExtra(EXTRA_GROUP_COLOR)))
            }
            group_name_editText.setText(intent.getStringExtra(EXTRA_GROUP_NAME))
            group_desc_editText.setText(intent.getStringExtra(EXTRA_GROUP_DESC))
        }


        // 컬러 선택
        current_group_color_textView.setOnClickListener {
            openColorPicker()
        }


    }

    private fun openColorPicker() {
        val colorPicker = ColorPicker(this)

        val colors : ArrayList<String> = ArrayList()

        // init Color for start
        val initColors = arrayListOf("#FA4659", "#364F6B", "#364F6B", "#3FC1C9", "#FA4659")

        // 이미 사용중인 컬러
        val currentUsingColorList: ArrayList<String>


        // TODO : 사용할 수 없는 컬러 제외하기
        colors.add("#0A7533")
        colors.add("#FF3A2F")
        colors.add("#FA4659")
        colors.add("#59A6E9")
        colors.add("#F0CCBD")

        colors.add("#326AB4")
        colors.add("#798517")
        colors.add("#D5D68A")
        colors.add("#F23AC6")
        colors.add("#FFCB17")

        colors.add("#FE6D71")
        colors.add("#0A53DE")
        colors.add("#FB6F24")
        colors.add("#454545")
        colors.add("#24D024")

        // TODO : 그룹에서 사용된 컬러 제외하기
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)


        val usingColors = arrayListOf<String>()

        // 제외 컬러
        for(color in usingColors) {
            colors.remove(color)
        }



        colorPicker.setColors(colors)
            .setTitle(getString(R.string.choose_color))
            .setColumns(5)
            .setRoundColorButton(true)
            .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    val resultColor = String.format("#%06X", 0xFFFFFF and color)
                    // TODO : 컬러선택 안하고 확인시 #000000 오류
                    current_group_color_textView.text = resultColor
                    current_group_color_textView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(resultColor))
                }

                override fun onCancel() {

                }
            }).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_group -> saveGroup()
        }
        return super.onOptionsItemSelected(item)
    }

    // 그룹저장
    private fun saveGroup() {
        groupAdapter = GroupAdapter(this)
        groupAdapter.notifyDataSetChanged()
        val resultTitle = group_name_editText?.text
        val resultContent = group_desc_editText?.text
        val resultColor = current_group_color_textView?.text
        val groupIntent = Intent()


        val groupName = resultTitle.toString().trim()
        val groupDesc = resultContent.toString().trim()
        val groupColor = resultColor.toString().trim()

// TODO : 여기에 그룹 컬러비교해서 거르기


        if (TextUtils.isEmpty(resultTitle)) {
            Snackbar.make(add_group_layout, getString(R.string.insert_title), Snackbar.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(resultContent)) {
            Snackbar.make(add_group_layout, getString(R.string.insert_memo), Snackbar.LENGTH_SHORT).show()
            return
        }
        if (!groupColor.contains('#')) {
            Snackbar.make(add_group_layout, "색을 선택해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }



        groupIntent.putExtra(EXTRA_GROUP_NAME, groupName)
        groupIntent.putExtra(EXTRA_GROUP_DESC, groupDesc)
        groupIntent.putExtra(EXTRA_GROUP_COLOR, groupColor)

        // 편집으로 아이디 값을 인텐트로 받아왔을 때 아이디 덮어쓰기
        val groupId: Int = intent.getIntExtra(EXTRA_GROUP_ID, -1)
        if (groupId != -1) {
            groupIntent.putExtra(EXTRA_GROUP_ID, groupId)
        }

        setResult(Activity.RESULT_OK, groupIntent)
        finish()
    }
}
