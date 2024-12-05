package com.example.myapplication

class DocumentModel() { // No-argument constructor

    var level: String? = null
        private set
    var location: String? = null
        private set
    var name: String? = null
        private set

    // Secondary constructor (optional for manual instantiation)
    constructor(level: String?, location: String?, name: String?) : this() {
        this.level = level
        this.location = location
        this.name = name
    }
}
