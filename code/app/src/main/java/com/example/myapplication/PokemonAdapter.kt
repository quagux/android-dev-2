import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.DetailActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class PokemonAdapter(private val pokemonList: List<MainActivity.Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.pokemonName)
        val spriteImageView: ImageView = itemView.findViewById(R.id.pokemonSprite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pokemon_list_item, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.nameTextView.text = pokemon.name
        Glide.with(holder.itemView.context).load(pokemon.spriteUrl).into(holder.spriteImageView)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("POKEMON_NAME", pokemon.name)
            intent.putExtra("POKEMON_SPRITE_URL", pokemon.spriteUrl)
            intent.putExtra("POKEMON_FLAVOR_TEXT", pokemon.flavorText)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pokemonList.size
}