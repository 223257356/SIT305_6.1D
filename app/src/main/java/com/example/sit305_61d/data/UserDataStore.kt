package com.example.sit305_61d.data

import com.example.sit305_61d.data.model.User

// Simple in-memory store for demonstration purposes.
// Data is lost when the app closes.
object UserDataStore {

    // Store a Pair: User object and Password String
    private val users = mutableMapOf<String, Pair<User, String>>() // Map username to Pair(User, Password)

    // Updated function to accept User and Password separately
    fun registerUser(user: User, password: String): Boolean {
        if (users.containsKey(user.username)) {
            return false // Username already exists
        }
        users[user.username] = Pair(user, password)
        return true
    }

    // Updated function to return the Pair
    fun findUserByUsername(username: String): Pair<User, String>? {
        return users[username]
    }

    // Add other necessary functions, e.g., for updating user data if needed
}
