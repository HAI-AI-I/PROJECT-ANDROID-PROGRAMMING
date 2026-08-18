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
        if(!isValidEmail(emailOrPhone)){
            return false
        }
        else if(!isValidPassword(password)){
            return false
        }
        else if(emailOrPhone!="hai@gmail.com"){
            return false
        }
        else if(password!="123456") return false
        return true
    }
    fun isValidRegistration(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if(!isValidName(name)){
            return false
        }
        else if(!isValidEmail(email)){
            return false
        }
        else if(!isValidPhone(phone)) return false
        else if(!isValidPassword(password)) return false
        else if(!confirmPassword(password,confirmPassword)) return false
        return true
    }
    fun isValidResetPassword(newPassword: String, confirmNewPassword: String): Boolean {
        return isValidPassword(newPassword) && confirmPassword(newPassword, confirmNewPassword)
    }

    fun checkExitsEmail(email:String): Boolean{
        if(!isValidEmail(email)){
            return false
        }
        if(email!="hai@gmail.com"){
            return false
        }
        return true
    }
    fun checkExitsPhone(phone:String): Boolean{
        if(!isValidPhone(phone)){
            return false
        }
        if(phone!="0345115421"){
            return false
        }
        return true
    }
}
