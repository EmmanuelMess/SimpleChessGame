package com.emmanuelmess.simplechess.net

interface Networking {
    fun logIn(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit)
    fun undo()
    fun draw()
    fun surrender()
}