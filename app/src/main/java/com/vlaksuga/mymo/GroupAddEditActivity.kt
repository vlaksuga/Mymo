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

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_add_edit)

        // 시작화면 설정
        supportActionBar!!.title = "그룹 추가"
        if (intent.hasExtra(EXTRA_GROUP_ID)) {
            supportActionBar!!.title = "그룹 편집"
            current_group_color_textView.apply {
                this.text = intent.getStringExtra(EXTRA_GROUP_COLOR)
                this.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(intent.getStringExtra(EXTRA_GROUP_COLOR)))
            }
            group_name_editText.setText(intent.getStringExtra(EXTRA_GROUP_NAME))
            group_desc_editText.setText(intent.getStringExtra(EXTRA_GROUP_DESC))
        }

        groupAdapter = GroupAdapter(this)

        // 컬러 선택
        current_group_color_textView.setOnClickListener {
            openColorPicker()
        }


    }

    private fun openColorPicker() {
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allGroups.observe(this, Observer { groups ->
            groups?.let {
                val colorPicker = ColorPicker(this)
                val colors = arrayListOf(
                    "#292B2C",
                    "#000000",
                    "#0A7533",
                    "#FF3A2F",
                    "#FA4659",
                    "#59A6E9",

                    "#326AB4",
                    "#798517",
                    "#D5D68A",
                    "#F23AC6",
                    "#FFCB17",

                    "#0A53DE",
                    "#FB6F24",
                    "#454545",
                    "#24D024",
                    "#4546FF"
                )

                // 이미 지정된 색 빼기
                groupAdapter.setGroups(it)
                for(row in groups) {
                    colors.remove(row.groupColor)
                    Log.d("UsingGroupColors.for.viewModel.allGroups.observe.rockteki", row.groupColor)
                }

                // 추가할 색깔이 없을때 막기
                if(colors.size == 0) {
                    Toast.makeText(this, "그룹 색을 모두 사용하였습니다.", Toast.LENGTH_SHORT).show()
                    return@Observer
                }

                colorPicker.setColors(colors)
                    .setTitle(getString(R.string.choose_color))
                    .setColumns(5)
                    .setRoundColorButton(true)
                    .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                        override fun onChooseColor(position: Int, color: Int) {
                            val resultColor = String.format("#%06X", 0xFFFFFF and color)
                            current_group_color_textView.text = resultColor
                            current_group_color_textView.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor(resultColor))
                        }

                        override fun onCancel() {
                            // TODO : 컬러선택 안하고 확인시 #000000 오류
                        }
                    })
                    .show()
            }

        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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



        if (TextUtils.isEmpty(resultTitle)) {
            Snackbar.make(add_group_layout, getString(R.string.insert_title), Snackbar.LENGTH_SHORT)
                .show()
            return
        }
        if (TextUtils.isEmpty(resultContent)) {
            Snackbar.make(add_group_layout, getString(R.string.insert_memo), Snackbar.LENGTH_SHORT)
                .show()
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
