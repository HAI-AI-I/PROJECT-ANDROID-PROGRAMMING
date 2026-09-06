package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.UserEntity
import com.group_7.library_management.models.User

fun UserEntity.toUserModel(): User {
    return User(
        id = id.toString(),
        name = name,
        phone = phone,
        qrCodeData = "USER_$id"
    )
}

fun User.toUserEntity(
    email: String = "",
    password: String = "",
    joinDate: String = "",
    updateAt: String = ""
): UserEntity {
    return UserEntity(
        id = id.toLongOrNull() ?: 0L,
        name = name,
        email = email,
        phone = phone,
        password = password,
        joinDate = joinDate,
        updateAt = updateAt
    )
}
