package com.group_7.library_management.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun isValidPhone(phone: String): Boolean {
        return phone.length >= 10 && phone.all { it.isDigit() }
    }
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    fun confirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
    fun isValidVerificationCode(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
    fun isValidLoginInput(emailOrPhone: String, password: String): Boolean {
        return (isValidEmail(emailOrPhone) || isValidPhone(emailOrPhone)) && password.isNotBlank()
    }
    fun isValidRegistration(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return isValidName(name) &&
                isValidEmail(email) &&
                isValidPhone(phone) &&
                isValidPassword(password) &&
                confirmPassword(password, confirmPassword)
    }
    fun isValidResetPassword(newPassword: String, confirmNewPassword: String): Boolean {
        return isValidPassword(newPassword) && confirmPassword(newPassword, confirmNewPassword)
    }
}
