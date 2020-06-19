package com.vlaksuga.mymo

import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_group_list.*


class GroupConfigActivity : AppCompatActivity() {
    companion object {
        const val ADD_GROUP_REQUEST: Int = 1
        const val EDIT_GROUP_REQUEST: Int = 2
    }

    private lateinit var viewModel: ViewModel
    lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        // 액션 바
        supportActionBar!!.title = "그룹"

        // 어뎁터
        adapter = GroupAdapter(this)

        // 리사이클러뷰
        val recyclerView = findViewById<RecyclerView>(R.id.group_recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 뷰 모델
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allGroups.observe(this, Observer { groups ->
            groups?.let {
                adapter.setGroups(it)
            }
        })

        // 아이템 클릭 -> 편집 인텐트
        adapter.setOnItemClickListener(object : GroupAdapter.OnItemClickListener {
            override fun onItemClick(group: Group) {
                val intent: Intent = Intent(this@GroupConfigActivity, GroupAddEditActivity::class.java)
                intent.putExtra(GroupAddEditActivity.EXTRA_GROUP_ID, group.groupId)
                intent.putExtra(GroupAddEditActivity.EXTRA_GROUP_NAME, group.groupName)
                intent.putExtra(GroupAddEditActivity.EXTRA_GROUP_DESC, group.groupDesc)
                intent.putExtra(GroupAddEditActivity.EXTRA_GROUP_COLOR, group.groupColor)
                startActivityForResult(intent, EDIT_GROUP_REQUEST)
            }
        })
    }


    // 결과에 따라 인텐트 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        // 새로 만들기 인텐트로 부터
        if (requestCode == ADD_GROUP_REQUEST && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val group = Group(
                    0,
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_NAME)!!,
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_DESC)!!,
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_COLOR)!!
                )
                viewModel.groupInsert(group)
                Unit
            }
        }

        // 편집하기 인텐트로 부터
        else if (requestCode == EDIT_GROUP_REQUEST && resultCode == Activity.RESULT_OK) {
            val id: Int = intentData!!.getIntExtra(GroupAddEditActivity.EXTRA_GROUP_ID, -1)
            if (id == -1) {
                Snackbar.make(
                    group_recyclerView,
                    getString(R.string.update_fail),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return
            }

            // 편집된 데이터 적용
            intentData.let { data ->
                val group = Group(
                    data.getIntExtra(GroupAddEditActivity.EXTRA_GROUP_ID,-1),
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_NAME)!!,
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_DESC)!!,
                    data.getStringExtra(GroupAddEditActivity.EXTRA_GROUP_COLOR)!!
                )
                viewModel.groupUpdate(group)
                Unit
            }
        }

        // 모두 아닌 경우
        else {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_group_menu -> addNewGroup()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewGroup() {
        val intent = Intent(this, GroupAddEditActivity::class.java)
        startActivityForResult(intent, ADD_GROUP_REQUEST)
    }
}
