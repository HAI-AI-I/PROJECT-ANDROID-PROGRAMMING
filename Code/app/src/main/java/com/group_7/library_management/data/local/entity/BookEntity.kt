package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.processing.Generated

@Entity(tableName="books")
data class BookEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val borrowFee: Long,
    val availableCopies: Int,
    val rating: Double,
    val createdAt: Long,
    val viewCount: Int
)