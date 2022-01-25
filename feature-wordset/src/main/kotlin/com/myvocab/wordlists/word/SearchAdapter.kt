package com.myvocab.wordlists.word

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.core.util.AnimatorListenerAdapter
import com.myvocab.wordlists.R
import com.myvocab.wordlists.SortType
import com.myvocab.wordlists.databinding.ItemSearchWordBinding
import com.myvocab.wordlists.databinding.SelectWordSortTypeBinding

class SearchAdapter(
    private val onTextChanged: (String) -> Unit,
    private val onSortTypeChanged: (SortType) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    var searchText = ""
        set(value) {
            if (value != field) {
                field = value
                notifyItemChanged(0)
            }
        }

    var selectedSortType = SortType.BY_DEFAULT
        set(value) {
            if (value != field) {
                field = value
                notifyItemChanged(0)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding, onTextChanged, onSortTypeChanged)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchText, selectedSortType)
    }

    override fun getItemCount(): Int = 1
}

class SearchViewHolder(
    private val binding: ItemSearchWordBinding,
    private val onTextChanged: (String) -> Unit,
    private val onSortTypeChanged: (SortType) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val SORT_SELECTOR_ANIM_DURATION = 200L
    }

    init {
        with(binding) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    onTextChanged(newText ?: "")
                    return true
                }
            })
        }
    }


    fun bind(searchText: String, selectedSortType: SortType) {
        with(binding) {
            if (searchView.query.toString() != searchText) {
                searchView.setQuery(searchText, true)
            }
            if (selectedSortType == SortType.BY_DEFAULT) {
                sortBtn.clearColorFilter()
            } else {
                sortBtn.setColorFilter(ContextCompat.getColor(root.context, R.color.colorAccent))
            }
            sortBtn.setOnClickListener {
                val location = IntArray(2)
                sortBtn.getLocationOnScreen(location)
                openSortTypeSelector(
                    location[0],
                    location[1] + sortBtn.height - sortBtn.paddingBottom,
                    selectedSortType
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun openSortTypeSelector(x: Int, y: Int, selectedSortType: SortType) {
        PopupWindow(binding.root.context).apply {
            val popupBinding = SelectWordSortTypeBinding.inflate(LayoutInflater.from(binding.root.context))
            contentView = popupBinding.root

            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT

            isFocusable = true


            with(popupBinding) {
                val colorAccent = ContextCompat.getColor(root.context, R.color.colorAccent)
                val inactiveTextColor = ContextCompat.getColor(root.context, R.color.secondaryTextColor)

                if (selectedSortType == SortType.BY_DEFAULT) {
                    byDefaultIcon.setColorFilter(colorAccent)
                    byDefaultLabel.setTextColor(colorAccent)
                } else {
                    byDefaultIcon.clearColorFilter()
                    byDefaultLabel.setTextColor(inactiveTextColor)
                }

                if (selectedSortType == SortType.ALPHABETICALLY) {
                    alphabeticallyIcon.setColorFilter(colorAccent)
                    alphabeticallyLabel.setTextColor(colorAccent)
                } else {
                    alphabeticallyIcon.clearColorFilter()
                    alphabeticallyLabel.setTextColor(inactiveTextColor)
                }

                if (selectedSortType == SortType.BY_PROGRESS_LEVEL) {
                    byKnowledgeLevelIcon.setColorFilter(colorAccent)
                    byKnowledgeLevelLabel.setTextColor(colorAccent)
                } else {
                    byKnowledgeLevelIcon.clearColorFilter()
                    byKnowledgeLevelLabel.setTextColor(inactiveTextColor)
                }

                byDefaultBtn.setOnClickListener {
                    onSortTypeChanged(SortType.BY_DEFAULT)
                    animateSelectorDisappearing()
                }

                alphabeticallyBtn.setOnClickListener {
                    onSortTypeChanged(SortType.ALPHABETICALLY)
                    animateSelectorDisappearing()
                }

                byKnowledgeLevelBtn.setOnClickListener {
                    onSortTypeChanged(SortType.BY_PROGRESS_LEVEL)
                    animateSelectorDisappearing()
                }

                animateSelectorAppearing()
            }

            setBackgroundDrawable(null)

            showAtLocation(contentView, Gravity.NO_GRAVITY, x, y)
        }
    }

    private fun PopupWindow.animateSelectorAppearing() {
        contentView.alpha = 0f
        contentView.animate().alpha(1f).setDuration(SORT_SELECTOR_ANIM_DURATION).start()
    }

    private fun PopupWindow.animateSelectorDisappearing() {
        contentView.animate()
            .setListener(AnimatorListenerAdapter(onEnd = { dismiss() }))
            .setDuration(SORT_SELECTOR_ANIM_DURATION)
            .alpha(0f)
            .start()
    }

}