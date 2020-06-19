package com.vlaksuga.mymo

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(
    tableName = "memo_table",
    indices = [Index(value = ["memo_group_id"]), Index(value = ["memo_group_color"]), Index(value = ["memo_group_name"])],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            onUpdate = CASCADE,
            entity = Group::class,
            parentColumns = arrayOf("groupId"),
            childColumns = arrayOf("memo_group_id")
        ),
        ForeignKey(
            onDelete = CASCADE,
            onUpdate = CASCADE,
            entity = Group::class,
            parentColumns = arrayOf("group_color"),
            childColumns = arrayOf("memo_group_color")
        ),
        ForeignKey(
            onDelete = CASCADE,
            onUpdate = CASCADE,
            entity = Group::class,
            parentColumns = arrayOf("group_name"),
            childColumns = arrayOf("memo_group_name")
        )
    ]
)

data class Memo(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "memo_id") val _id: Int,
    @ColumnInfo(name = "memo_title") val memoTitle: String,
    @ColumnInfo(name = "memo_content") val memoContent: String,
    @ColumnInfo(name = "init_time") val initTime: Long,
    @ColumnInfo(name = "is_important") val isImportant: Boolean,
    @ColumnInfo(name = "memo_group_id") val groupId: Int,
    @ColumnInfo(name = "memo_group_color") val groupColor: String,
    @ColumnInfo(name = "memo_group_name") val groupName : String

)
