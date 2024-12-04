package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class InputFormActivity : AppCompatActivity() {

    // Declare the views
    private lateinit var spinner: Spinner
    private lateinit var levelInput: EditText
    private lateinit var locationText: TextView
    private lateinit var submitButton: Button

    // Location client
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_form)

        // Initialize views
        spinner = findViewById(R.id.spinner_pokemon)
        levelInput = findViewById(R.id.level_input)
        locationText = findViewById(R.id.location_text)
        submitButton = findViewById(R.id.submit_button)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val pokemonList = MainActivity.SharedData.pokemonList
        val pokemonNames = pokemonList?.map { it.name } ?: emptyList() // Use an empty list if null

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pokemonNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Autofill the location field with GPS coordinates
        fetchLocation()

        val db = FirebaseFirestore.getInstance()
        val myCollectionRef = db.collection("/test")
        // Handle Submit button click
        submitButton.setOnClickListener {
            val selectedPokemon = spinner.selectedItem.toString()
            val level = levelInput.text.toString()
            val location = locationText.text.toString()

            if (level.isEmpty()) {
                Toast.makeText(this, "Please enter a level", Toast.LENGTH_SHORT).show()
            } else {
                myCollectionRef.add(mapOf("name" to selectedPokemon, "level" to level, "location" to location))
                Toast.makeText(this, "Submitted: $selectedPokemon - Level: $level - Location: $location", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun fetchLocation() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Display the location in the TextView
                    val locationTextString = "Lat: ${it.latitude}, Lon: ${it.longitude}"
                    locationText.text = locationTextString
                }
            }
        } else {
            // Request permission if not granted
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Request permission result handler
    private val requestLocationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun basicReadWrite() {
        val db = FirebaseFirestore.getInstance()
        val myCollectionRef = db.collection("/test")
        myCollectionRef.add(mapOf("name" to "value1", "field2" to "value2"))
    }
}
