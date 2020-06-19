package com.vlaksuga.mymo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "group_table",
    indices = arrayOf(Index(value = ["groupId"], unique = true), Index(value = ["group_color"], unique = true), Index(value = ["group_name"], unique = true))
)

data class Group(
    @PrimaryKey(autoGenerate = true) val groupId: Int,
    @ColumnInfo(name = "group_name") val groupName: String,
    @ColumnInfo(name = "group_desc") val groupDesc: String,
    @ColumnInfo(name = "group_color") val groupColor: String
)