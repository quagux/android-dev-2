package com.example.myapplication

import PokemonAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

//import com.google.firebase.referencecode.database.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.pokemonRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPokemonData()

        // Reference to the BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

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

    object SharedData {
        var pokemonList: List<Pokemon>? = null
    }
    private fun fetchPokemonData() {
        val url = "https://pokeapi.co/api/v2/pokemon?limit=50"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    parsePokemonJson(json) { pokemonList ->
                        // Update the RecyclerView adapter on the main thread
                        runOnUiThread {
                            recyclerView.adapter = PokemonAdapter(pokemonList)
                        }
                        SharedData.pokemonList = pokemonList
                    }
                }
            }
        })
    }

    data class PokemonsApiResponse(
        val results: List<PokemonsResult>
    )
    data class PokemonsResult(
        val name: String,
        val url: String
    )

    data class Pokemon(
        val name: String,
        val spriteUrl: String,
        val flavorText: String
    )
    private fun parsePokemonJson(json: String, callback: (List<Pokemon>) -> Unit){
        val pokemonList = mutableListOf<Pokemon>()
        val gson = Gson()
        val jsonObject = gson.fromJson(json, PokemonsApiResponse::class.java)
        var pendingRequests = jsonObject.results.size

        for (pokemon in jsonObject.results){
            fetchPokemonSprite(pokemon.url) { spriteUrl, speciesUrl ->
                fetchPokemonFlavorText(speciesUrl) { flavorText ->
                    val pokemonName = pokemon.name.replaceFirstChar { it.uppercase() }
                    val pokemonObject = Pokemon(pokemonName, spriteUrl, flavorText)
                    pokemonList.add(pokemonObject)
                    // Decrement the counter and check if all requests are done
                    pendingRequests--
                    if (pendingRequests == 0) {
                        // All sprites fetched, invoke the callback with the final list
                        //Log.d("Pokemon", "Final List: $pokemonList")
                        callback(pokemonList)
                    }
                }
            }
        }
    }

    data class PokemonApiResponse(
        val sprites: Sprites,
        val species: Species
    )
    data class Sprites(
        val front_default: String
    )
    data class Species(
        val url: String
    )

    private fun fetchPokemonSprite(url: String, callback: (String, String) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val gson = Gson()
                    val pokemonApiResponse  = gson.fromJson(json, PokemonApiResponse::class.java)
                    val spriteUrl = pokemonApiResponse.sprites.front_default
                    val speciesUrl = pokemonApiResponse.species.url
                    //Log.d("Pokemon", "Sprite URL: $spriteUrl")
                    //Log.d("Pokemon", "Species URL: $speciesUrl")
                    callback(spriteUrl, speciesUrl)
                }
            }
        })
    }

    data class PokemonFlavorTextResponse(
        val flavor_text_entries: List<FlavorTextEntry>
    )
    data class FlavorTextEntry(
        val flavor_text: String,
        val language: Language,
        val version: Version
    )
    data class Language(
        val name: String,
        val url: String
    )
    data class Version(
        val name: String,
        val url: String
    )
    private fun fetchPokemonFlavorText(url: String, callback: (String) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val gson = Gson()
                    val responseSpecies  = gson.fromJson(json, PokemonFlavorTextResponse::class.java)
                    val flavor_text_entry = responseSpecies.flavor_text_entries.first { it.language.name == "en" }
                    val flavor_text = flavor_text_entry.flavor_text
                    //Log.d("Pokemon", "Flavor text: $flavor_text")
                    callback(flavor_text)
                }
            }
        })
    }
}