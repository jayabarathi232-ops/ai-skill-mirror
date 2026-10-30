package com.simats.aiskillmirror.data.network

object UserSession {
    var userId: Int = 1
    var userName: String = "Test Student"
    var userEmail: String = "teststudent@example.com"

    fun update(id: Int, name: String, email: String) {
        userId = id
        userName = name
        userEmail = email
    }
}
