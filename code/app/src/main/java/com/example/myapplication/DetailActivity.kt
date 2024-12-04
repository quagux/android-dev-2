package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val pokemonName = intent.getStringExtra("POKEMON_NAME") ?: "Unknown"
        val pokemonSpriteUrl = intent.getStringExtra("POKEMON_SPRITE_URL")
        val pokemonFlavorText = intent.getStringExtra("POKEMON_FLAVOR_TEXT")

        val spriteImageView: ImageView = findViewById(R.id.pokemonSprite)
        val nameTextView: TextView = findViewById(R.id.pokemonName)
        val descriptionTestView: TextView = findViewById(R.id.description)

        nameTextView.text = pokemonName
        descriptionTestView.text = pokemonFlavorText
        if (pokemonSpriteUrl != null) {
            Glide.with(this).load(pokemonSpriteUrl).into(spriteImageView)
        }
    }
}
