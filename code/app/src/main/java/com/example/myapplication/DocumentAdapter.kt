package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView


class DocumentAdapter(
    private val context: Context,
    private var documentList: List<DocumentModel>,
    private val onDelete: (DocumentModel) -> Unit,
    private val onUpdate: (DocumentModel) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documentList[position]
        holder.nameTextView.text = document.name
        holder.levelTextView.text = document.level
        holder.locationTextView.text = document.location

        // Set long press listener
        holder.itemView.setOnLongClickListener {
            showOptionsDialog(document)
            true
        }
    }

    override fun getItemCount(): Int = documentList.size

    fun updateList(newList: List<DocumentModel>) {
        documentList = newList
        notifyDataSetChanged()
    }

    private fun showOptionsDialog(document: DocumentModel) {
        val options = arrayOf("Update", "Delete")
        AlertDialog.Builder(context)
            .setTitle("Choose an action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> onUpdate(document) // Update action
                    1 -> onDelete(document) // Delete action
                }
            }
            .show()
    }

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val levelTextView: TextView = itemView.findViewById(R.id.levelTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }
}
