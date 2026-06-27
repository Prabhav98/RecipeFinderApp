package com.recipefinder.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.recipefinder.adapter.CategoryAdapter
import com.recipefinder.adapter.MealAdapter
import com.recipefinder.databinding.ActivityHomeBinding
import com.recipefinder.repository.MealRepository
import com.recipefinder.utils.Resource
import com.recipefinder.viewmodel.HomeViewModel
import com.recipefinder.viewmodel.HomeViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(MealRepository())
    }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var mealAdapter: MealAdapter

    // Flag to know if user is searching
    private var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupSearchView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Category RecyclerView (Horizontal)
        categoryAdapter = CategoryAdapter { category ->
            isSearching = false
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            viewModel.fetchMealsByCategory(category.strCategory)
        }

        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }

        // Meals RecyclerView (Grid 2 columns)
        mealAdapter = MealAdapter { meal ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("MEAL_ID", meal.idMeal)
            intent.putExtra("MEAL_NAME", meal.strMeal)
            startActivity(intent)
        }

        binding.rvMeals.apply {
            adapter = mealAdapter
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        isSearching = true
                        viewModel.searchMeals(it)
                    }
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isBlank()) {
                        isSearching = false
                        viewModel.refreshCurrentCategory()
                    } else {
                        isSearching = true
                        viewModel.searchMeals(it)
                    }
                }
                return true
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshCurrentCategory()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeViewModel() {
        // Observe Categories
        viewModel.categories.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarCategories.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBarCategories.visibility = View.GONE
                    resource.data?.let { categories ->
                        categoryAdapter.submitList(categories)
                    }
                }
                is Resource.Error -> {
                    binding.progressBarCategories.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe Meals (by category)
        viewModel.meals.observe(this) { resource ->
            if (!isSearching) {
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBarMeals.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBarMeals.visibility = View.GONE
                        resource.data?.let { meals ->
                            if (meals.isEmpty()) {
                                binding.tvEmpty.visibility = View.VISIBLE
                                binding.tvEmpty.text = "No meals found"
                            } else {
                                binding.tvEmpty.visibility = View.GONE
                            }
                            mealAdapter.submitList(meals)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBarMeals.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = "Error: ${resource.message}"
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Observe Search Results
        viewModel.searchResults.observe(this) { resource ->
            if (isSearching) {
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBarMeals.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBarMeals.visibility = View.GONE
                        resource.data?.let { meals ->
                            if (meals.isEmpty()) {
                                binding.tvEmpty.visibility = View.VISIBLE
                                binding.tvEmpty.text = "No results found"
                            } else {
                                binding.tvEmpty.visibility = View.GONE
                            }
                            mealAdapter.submitList(meals)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBarMeals.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = "Error: ${resource.message}"
                    }
                }
            }
        }

        // Update toolbar subtitle with selected category
        viewModel.selectedCategory.observe(this) { category ->
            binding.tvSelectedCategory.text = category
        }
    }
}