package com.vlaksuga.mymo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "trash")
data class Trash (
    @PrimaryKey val trashId : Int,
    val trashTitle : String,
    val trashContent : String,
    val trashInitTime : Long,
    val trashExpireTime : Long
)