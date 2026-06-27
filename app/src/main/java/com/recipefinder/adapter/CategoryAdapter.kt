package com.recipefinder.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.recipefinder.model.Category
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.recipefinder.R
import com.recipefinder.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition = 0

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            binding.apply {
                tvCategoryName.text = category.strCategory

                Glide.with(itemView.context)
                    .load(category.strCategoryThumb)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(ivCategoryImage)

                // Change visual state based on selection
                if (isSelected) {
                    cardCategory.setCardBackgroundColor(
                        itemView.context.getColor(R.color.colorPrimary)
                    )
                    tvCategoryName.setTextColor(
                        itemView.context.getColor(R.color.white)
                    )
                } else {
                    cardCategory.setCardBackgroundColor(
                        itemView.context.getColor(R.color.cardBackground)
                    )
                    tvCategoryName.setTextColor(
                        itemView.context.getColor(R.color.textPrimary)
                    )
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onCategoryClick(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun resetSelection() {
        selectedPosition = 0
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.idCategory == newItem.idCategory
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}