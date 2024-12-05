package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        adapter = DocumentAdapter(
            this,
            documentList as ArrayList<DocumentModel>,
            onDelete = { document ->
                // Delete document from Firestore
                FirebaseFirestore.getInstance().collection("collection")
                    .whereEqualTo("name", document.name)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (doc in querySnapshot.documents) {
                            doc.reference.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                                    fetchDocuments() // Refresh the list
                                }
                        }
                    }
            },
            onUpdate = { document ->
                // Show a dialog or activity to update the document
                showUpdateDialog(document)
            }
        )
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

    private fun showUpdateDialog(document: DocumentModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_document, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
        val levelEditText = dialogView.findViewById<EditText>(R.id.editTextLevel)
        val locationEditText = dialogView.findViewById<EditText>(R.id.editTextLocation)

        // Pre-fill the fields with the current values
        nameEditText.setText(document.name)
        levelEditText.setText(document.level)
        locationEditText.setText(document.location)

        AlertDialog.Builder(this)
            .setTitle("Update Document")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedDocument = DocumentModel(
                    nameEditText.text.toString(),
                    levelEditText.text.toString(),
                    locationEditText.text.toString()
                )

                // Update Firestore
                FirebaseFirestore.getInstance().collection("collection")
                    .whereEqualTo("name", document.name)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (doc in querySnapshot.documents) {
                            doc.reference.set(updatedDocument)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show()
                                    fetchDocuments() // Refresh the list
                                }
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
