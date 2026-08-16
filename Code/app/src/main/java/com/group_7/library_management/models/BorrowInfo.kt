package com.group_7.library_management.models

data class BorrowInfo(
    val bookName: String,
    val borrowerName: String,
    val borrowDate: String,
    val returnDate: String,
    val loanPeriod: String
)
