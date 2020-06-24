package com.vlaksuga.mymo

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        supportActionBar!!.title = getString(R.string.group_add_title)
        if (intent.hasExtra(EXTRA_GROUP_ID)) {
            supportActionBar!!.title = getString(R.string.group_edit_title)
            current_group_color_textView.apply {
                this.text = intent.getStringExtra(EXTRA_GROUP_COLOR)
                this.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(intent.getStringExtra(EXTRA_GROUP_COLOR)))
            }
            group_name_editText.setText(intent.getStringExtra(EXTRA_GROUP_NAME))
            group_desc_editText.setText(intent.getStringExtra(EXTRA_GROUP_DESC))
            delete_this_group_button.visibility = View.VISIBLE
        }

        groupAdapter = GroupAdapter(this)

        // 컬러 선택
        current_group_color_textView.setOnClickListener {
            openColorPicker()
        }

        delete_this_group_button.setOnClickListener {
            if(intent.getIntExtra(EXTRA_GROUP_ID, 1) == 1) {
                Toast.makeText(this, getString(R.string.unable_to_delete_default_group), Toast.LENGTH_SHORT).show()
            }
            deleteThisGroup()
        }


    }

    private fun deleteThisGroup() {
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allMemos.observe(this, Observer { memos ->
            memos?.let{
                groupAdapter.setMemos(it)
                val targetId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
                val targetName = intent.getStringExtra(EXTRA_GROUP_NAME)!!
                val targetDesc = intent.getStringExtra(EXTRA_GROUP_DESC)!!
                val targetColor = intent.getStringExtra(EXTRA_GROUP_COLOR)!!

                if(groupAdapter.getMemoCountByGroupId(targetId) == 0) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.delete_group_confirm_message))
                        .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                            if(targetId != -1) {
                                viewModel.groupDelete(group = Group(targetId, targetName, targetDesc, targetColor))
                                Toast.makeText(this, getString(R.string.group_deleted), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .setNegativeButton(getString(R.string.negative_button)
                        ) { dialog, _ ->
                            Snackbar.make(add_group_layout, getString(R.string.cancel_msg), Snackbar.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    Snackbar.make(add_group_layout, getString(R.string.group_has_memos), Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun openColorPicker() {
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allGroups.observe(this, Observer { groups ->
            groups?.let {
                val colorPicker = ColorPicker(this)
                val colors = arrayListOf(
                    "#292B2C", // prime - default

                    "#0065A3", // blue
                    "#008ADF", // sky blue
                    "#54E360", // green
                    "#FFD400", // yellow
                    "#FF9100", // orange
                    "#FF4949", // red

                    // starter
                    "#005183", // dark blue
                    "#0065A3", // dark sky blue
                    "#00AB5E", // dark green
                    "#DAD307", // dark yellow
                    "#0A53DE", // dark orange
                    "#E80048", // dark red

                    // addition 1

                    "#003B5F", // deep blue
                    "#87AABF", // deep sky blue
                    "#015D34", // deep green
                    "#563E17", // deep yellow
                    "#812600", // deep orange
                    "#A90437" // deep red
                    // addition 2
                )

                // 이미 지정된 색 빼기
                groupAdapter.setGroups(it)
                for(row in groups) {
                    colors.remove(row.groupColor)
                }

                // 추가할 색깔이 없을때 막기
                if(colors.size == 0) {
                    Toast.makeText(this, getString(R.string.no_more_colors), Toast.LENGTH_SHORT).show()
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

        if (!groupColor.contains('#') || groupColor == "#000000") {
            Snackbar.make(add_group_layout, getString(R.string.choose_color), Snackbar.LENGTH_SHORT).show()
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
