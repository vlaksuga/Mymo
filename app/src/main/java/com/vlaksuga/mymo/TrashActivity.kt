package com.vlaksuga.mymo

import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_trash.*
import java.util.*

class TrashActivity : AppCompatActivity() {

    lateinit var trashAdapter: TrashAdapter
    private lateinit var viewModel: ViewModel

    private lateinit var recoverIcon: Drawable
    private lateinit var expireIcon: Drawable
    private var swipeBackGroundExpire: ColorDrawable =
        ColorDrawable(Color.parseColor("#FF0000"))

    private var swipeBackGroundRecover: ColorDrawable =
        ColorDrawable(Color.parseColor("#0000AA"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        // 액션바 타이틀
        supportActionBar!!.title = getString(R.string.trash_title)

        // 어댑터
        trashAdapter = TrashAdapter(this)

        // 뷰 모델
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)
        viewModel.allTrash.observe(this, Observer { trash ->
            trash?.let {
                trashAdapter.setTrash(it)
                val currentTime = Date().time
                viewModel.deleteAllTimeOverTrash(currentTime)
                checkEmptyView()
            }
        })

        // 리사이클러 뷰
        val trashRecyclerView = findViewById<RecyclerView>(R.id.trash_rv)
        trashRecyclerView.layoutManager = LinearLayoutManager(this)
        trashRecyclerView.setHasFixedSize(true)
        trashRecyclerView.adapter = trashAdapter

        // 스와이프
        expireIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_forever)!!
        recoverIcon = ContextCompat.getDrawable(this, R.drawable.ic_assignment_return)!!
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
                val selectedTrash = trashAdapter.getTrashAt(viewHolder.adapterPosition)
                val swipeDeleteBuilder = AlertDialog.Builder(this@TrashActivity)

                swipeDeleteBuilder.apply {
                    setMessage(getString(R.string.empty_trash))
                    setPositiveButton(
                        getString(R.string.positive_button)
                    ) { _, _ ->
                        viewModel.trashDelete(selectedTrash)
                        Snackbar.make(trashRecyclerView, getString(R.string.enempty_trash), Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton(
                        getString(R.string.negative_button)
                    ) { _, _ ->
                        viewModel.trashDelete(selectedTrash)
                        viewModel.trashInsert(selectedTrash)
                    }
                    setOnCancelListener {
                        viewModel.trashDelete(selectedTrash)
                        viewModel.trashInsert(selectedTrash)
                    }
                    show()
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
                val expireIconMargin = (itemView.height - expireIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackGroundExpire.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    expireIcon.setBounds(
                        itemView.left + expireIconMargin,
                        itemView.top + expireIconMargin,
                        itemView.left + expireIconMargin + expireIcon.intrinsicWidth,
                        itemView.bottom - expireIconMargin
                    )
                } else {
                    swipeBackGroundExpire.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    expireIcon.setBounds(
                        itemView.right - expireIconMargin - expireIcon.intrinsicWidth,
                        itemView.top + expireIconMargin,
                        itemView.right - expireIconMargin,
                        itemView.bottom - expireIconMargin
                    )
                }

                swipeBackGroundExpire.draw(c)
                expireIcon.draw(c)

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
        itemTouchHelperRight.attachToRecyclerView(trashRecyclerView)

        val itemTouchHelperLeftCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val selectedTrash = trashAdapter.getTrashAt(viewHolder.adapterPosition)
                val swipeRecoverBuilder = AlertDialog.Builder(this@TrashActivity)

                swipeRecoverBuilder.apply {
                    setMessage(getString(R.string.ask_recover_trash))
                    setPositiveButton(
                        getString(R.string.positive_button)
                    ) { _, _ ->
                        viewModel.recoverTrash(selectedTrash)
                        Snackbar.make(trashRecyclerView, getString(R.string.recover_trash_message), Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton(
                        getString(R.string.negative_button)
                    ) { _, _ ->
                        viewModel.recoverTrash(selectedTrash)
                        viewModel.undoTrash(selectedTrash)
                    }
                    setOnCancelListener {
                        viewModel.recoverTrash(selectedTrash)
                        viewModel.undoTrash(selectedTrash)
                    }
                    show()
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
                val recoverIconMargin = (itemView.height - recoverIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackGroundExpire.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    expireIcon.setBounds(
                        itemView.left + recoverIconMargin,
                        itemView.top + recoverIconMargin,
                        itemView.left + recoverIconMargin + recoverIcon.intrinsicWidth,
                        itemView.bottom - recoverIconMargin
                    )
                } else {
                    swipeBackGroundRecover.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    recoverIcon.setBounds(
                        itemView.right - recoverIconMargin - recoverIcon.intrinsicWidth,
                        itemView.top + recoverIconMargin,
                        itemView.right - recoverIconMargin,
                        itemView.bottom - recoverIconMargin
                    )
                }

                swipeBackGroundRecover.draw(c)
                recoverIcon.draw(c)

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

        val itemTouchHelperLeft = ItemTouchHelper(itemTouchHelperLeftCallback)
        itemTouchHelperLeft.attachToRecyclerView(trashRecyclerView)
    }

    // 리스트 없음
    private fun checkEmptyView() {
        if (trashAdapter.itemCount < 1) {
            trash_is_empty_view.visibility = View.VISIBLE
        } else {
            trash_is_empty_view.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trash_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.trash_delete_all -> apply{
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.ask_trash_remove_forever))
                    .setPositiveButton(getString(R.string.positive_button)
                    ) { _, _ ->
                        viewModel.deleteAllTrash()
                        Snackbar.make(trash_layout, getString(R.string.remove_all_trash), Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(getString(R.string.negative_button)) {dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}
