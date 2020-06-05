package com.vlaksuga.mymo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class Memo (@PrimaryKey(autoGenerate = true) var _id:Int,
                 @ColumnInfo(name = "memo_title") var memoTitle: String,
                 @ColumnInfo(name = "memo_content") var memoContent: String,
                 @ColumnInfo(name = "init_time") var initTime: Long,
                 @ColumnInfo(name = "bar_color") var barColor: String
)
