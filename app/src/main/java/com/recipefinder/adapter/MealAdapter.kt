package com.recipefinder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.recipefinder.R
import com.recipefinder.databinding.ItemMealBinding
import com.recipefinder.model.Meal

class MealAdapter(
    private val onMealClick: (Meal) -> Unit
) : ListAdapter<Meal, MealAdapter.MealViewHolder>(MealDiffCallback()) {

    inner class MealViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.apply {
                tvMealName.text = meal.strMeal

                Glide.with(itemView.context)
                    .load(meal.strMealThumb)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(ivMealImage)

                root.setOnClickListener {
                    onMealClick(meal)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
    override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem.idMeal == newItem.idMeal
    }

    override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem == newItem
    }
}