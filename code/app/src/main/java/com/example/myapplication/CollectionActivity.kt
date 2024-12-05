package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Locale

class CollectionActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var adapter: DocumentAdapter? = null
    private var documentList: MutableList<DocumentModel>? = null
    private var firestore: FirebaseFirestore? = null
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        searchEditText = findViewById<EditText>(R.id.searchEditText)

        documentList = ArrayList()
        adapter = DocumentAdapter(this, documentList as ArrayList<DocumentModel>)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)

        firestore = FirebaseFirestore.getInstance()

        fetchDocuments()

        // Implement search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchDocuments(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })



        // Reference to the BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_input_form

        // Set up navigation logic
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Navigate to Home
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_input_form -> {
                    // Navigate to Input Form
                    startActivity(Intent(this, InputFormActivity::class.java))
                    true
                }
                R.id.nav_collection -> {
                    // Navigate to Collection
                    startActivity(Intent(this, CollectionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchDocuments() {
        firestore!!.collection("collection")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                documentList!!.clear()
                for (snapshot in queryDocumentSnapshots) {
                    val document = snapshot.toObject(
                        DocumentModel::class.java
                    )
                    documentList!!.add(document)
                }
                adapter!!.notifyDataSetChanged()
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "Firestore",
                    "Error fetching documents",
                    e
                )
            }
    }

    private fun searchDocuments(query: String) {
        val filteredList: MutableList<DocumentModel> = ArrayList()
        for (document in documentList!!) {
            if (document.name!!.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault())) ||
                document.level!!.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(document)
            }
        }
        adapter!!.updateList(filteredList)
    }
}
