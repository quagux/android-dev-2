package com.example.myapplication

import PokemonAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val spriteUrl: String
    )
    private fun parsePokemonJson(json: String, callback: (List<Pokemon>) -> Unit){
        val pokemonList = mutableListOf<Pokemon>()
        val gson = Gson()
        val jsonObject = gson.fromJson(json, PokemonsApiResponse::class.java)
        var pendingRequests = jsonObject.results.size

        for (pokemon in jsonObject.results){
            fetchPokemonSprite(pokemon.url) { spriteUrl ->
                val pokemonObject = Pokemon(pokemon.name, spriteUrl)
                pokemonList.add(pokemonObject)
                // Decrement the counter and check if all requests are done
                pendingRequests--
                if (pendingRequests == 0) {
                    // All sprites fetched, invoke the callback with the final list
                    Log.d("Pokemon", "Final List: $pokemonList")
                    callback(pokemonList)
                }
            }
        }
    }

    data class PokemonApiResponse(
        val sprites: Sprites
    )
    data class Sprites(
        val front_default: String
    )
    private fun fetchPokemonSprite(url: String, callback: (String) -> Unit) {
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
                    Log.d("Pokemon", "Sprite URL: $spriteUrl")
                    callback(spriteUrl)
                }
            }
        })
    }

    fun basicReadWrite() {
        val db = FirebaseFirestore.getInstance()
        val myCollectionRef = db.collection("/test")
        myCollectionRef.add(mapOf("field1" to "value1", "field2" to "value2"))
    }
}