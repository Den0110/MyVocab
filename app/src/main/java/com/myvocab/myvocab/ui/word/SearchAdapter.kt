package com.myvocab.myvocab.ui.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.databinding.ItemSearchWordBinding

class SearchAdapter(private val onTextChanged: (String) -> Unit) : RecyclerView.Adapter<SearchViewHolder>() {

    var searchText = ""
        set(value) {
            if (value != field) {
                field = value
                notifyItemChanged(0)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding, onTextChanged)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchText)
    }

    override fun getItemCount(): Int = 1
}

class SearchViewHolder(
    private val binding: ItemSearchWordBinding,
    private val onTextChanged: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onTextChanged(newText ?: "")
                return true
            }
        })
    }


    fun bind(searchText: String) {
        if (binding.searchView.query.toString() != searchText) {
            binding.searchView.setQuery(searchText, true)
        }
    }

}