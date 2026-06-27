package com.recipefinder.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.recipefinder.R
import com.recipefinder.databinding.ActivityDetailBinding
import com.recipefinder.model.MealDetail
import com.recipefinder.repository.MealRepository
import com.recipefinder.utils.Resource
import com.recipefinder.viewmodel.DetailViewModel
import com.recipefinder.viewmodel.DetailViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(MealRepository())
    }

    private var youtubeUrl: String? = null
    private var sourceUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealId = intent.getStringExtra("MEAL_ID") ?: ""
        val mealName = intent.getStringExtra("MEAL_NAME") ?: "Recipe Detail"

        setupToolbar(mealName)
        setupButtons()
        observeViewModel()

        if (mealId.isNotEmpty()) {
            viewModel.fetchMealDetail(mealId)
        }
    }

    private fun setupToolbar(mealName: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = mealName
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupButtons() {
        binding.btnWatchYoutube.setOnClickListener {
            youtubeUrl?.let { url ->
                if (url.isNotBlank()) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this, "No YouTube video available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnViewSource.setOnClickListener {
            sourceUrl?.let { url ->
                if (url.isNotBlank()) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this, "No source available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.mealDetail.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    resource.data?.let { meal ->
                        bindMealDetail(meal)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindMealDetail(meal: MealDetail) {
        youtubeUrl = meal.strYoutube
        sourceUrl = meal.strSource

        // Load meal image
        Glide.with(this)
            .load(meal.strMealThumb)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .into(binding.ivMealDetail)

        // Basic info
        binding.tvMealTitle.text = meal.strMeal
        binding.tvCategory.text = "Category: ${meal.strCategory}"
        binding.tvArea.text = "Cuisine: ${meal.strArea}"

        // Instructions
        binding.tvInstructions.text = meal.strInstructions

        // Build Ingredients list
        val ingredientsList = meal.getIngredientsList()
        val ingredientsText = StringBuilder()
        ingredientsList.forEach { (ingredient, measure) ->
            ingredientsText.append("• ${measure.ifBlank { "" }} $ingredient\n")
        }
        binding.tvIngredients.text = ingredientsText.toString().trim()

        // YouTube button visibility
        if (!meal.strYoutube.isNullOrBlank()) {
            binding.btnWatchYoutube.visibility = View.VISIBLE
        } else {
            binding.btnWatchYoutube.visibility = View.GONE
        }

        // Source button visibility
        if (!meal.strSource.isNullOrBlank()) {
            binding.btnViewSource.visibility = View.VISIBLE
        } else {
            binding.btnViewSource.visibility = View.GONE
        }
    }
}