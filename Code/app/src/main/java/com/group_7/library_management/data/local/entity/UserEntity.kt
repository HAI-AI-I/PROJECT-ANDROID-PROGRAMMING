package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val joinDate: String,
    val isActive:Boolean=true,
    val isDeleted:Boolean=false,
    val updateAt:String,
)