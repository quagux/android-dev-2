package com.example.myapplication

class DocumentModel() { // No-argument constructor

    var level: String? = null
        private set
    var location: String? = null
        private set
    var name: String? = null
        private set

    // Secondary constructor (optional for manual instantiation)
    constructor(name: String?, level: String?, location: String?) : this() {
        this.name = name
        this.level = level
        this.location = location
    }
}
