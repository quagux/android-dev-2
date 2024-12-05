package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView


class DocumentAdapter(context: Context, documentList: List<DocumentModel>) :
    RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {
    private var documentList: List<DocumentModel>
    private val context: Context = context

    init {
        this.documentList = documentList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: DocumentViewHolder, position: Int) {
        val document = documentList[position]
        holder.nameTextView.text = document.name
        holder.levelTextView.text = document.level
        holder.locationTextView.text = document.location
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    fun updateList(newList: List<DocumentModel>) {
        documentList = newList
        notifyDataSetChanged()
    }

    class DocumentViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        var levelTextView: TextView = itemView.findViewById(R.id.levelTextView)
        var locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }
}
