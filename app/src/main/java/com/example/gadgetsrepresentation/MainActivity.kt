package com.example.gadgetsrepresentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gadgetsrepresentation.recycler_products.DummyApi
import com.example.gadgetsrepresentation.recycler_products.PAGE_PRODUCT_LIMIT
import com.example.gadgetsrepresentation.recycler_products.Product
import com.example.gadgetsrepresentation.recycler_products.ProductAdapter
import com.example.gadgetsrepresentation.recycler_products.SearchProductsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val DUMMY_BASE_URL = "https://dummyjson.com"

class MainActivity : AppCompatActivity() {

    private val products = arrayListOf<Product>()
    private lateinit var productAdapter: ProductAdapter
    private val retrofit = Retrofit.Builder()
        .baseUrl(DUMMY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val dummyService = retrofit.create(DummyApi::class.java)
    private lateinit var titleProblem: TextView
    private lateinit var additionalMessage: TextView
    private lateinit var buttonUpdate: Button
    private lateinit var placeholder: ImageView
    private lateinit var layoutPlaceholders: LinearLayout
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonUpdate = findViewById(R.id.button_update)
        titleProblem = findViewById(R.id.problem_title)
        additionalMessage = findViewById(R.id.problem_additional_message)
        placeholder = findViewById(R.id.problem_image)
        layoutPlaceholders = findViewById(R.id.layout_placeholders)
        progressBar = findViewById(R.id.progressBar)

        productAdapter = ProductAdapter(products)

        val recyclerProducts = findViewById<RecyclerView>(R.id.recyclerProducts)
        recyclerProducts.adapter = productAdapter
        recyclerProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            //знаю что пагинация выглядит отвратительно, и надо делать Redux, но я не успеваю (((((
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()


                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_PRODUCT_LIMIT
                ) {
                    loadMoreProducts()
                }

            }
        })


        buttonUpdate.setOnClickListener {
            getProducts() //?
        }
        getProducts()
    }

    private fun loadMoreProducts() {
        dummyService.getProducts(skip = products.size).enqueue(object : Callback<SearchProductsResponse>{
            override fun onResponse(
                call: Call<SearchProductsResponse>,
                response: Response<SearchProductsResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.total!! > 0) {
                        products.addAll(response.body()?.products!!)
                        productAdapter.notifyItemRangeInserted(products.size,
                            PAGE_PRODUCT_LIMIT)
                    } else {
                        Toast.makeText(applicationContext,getString(R.string.search_not_found),Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext,getString(R.string.search_internet_problem),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchProductsResponse>, t: Throwable) {
                Toast.makeText(applicationContext,getString(R.string.search_internet_problem),Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getProducts() {

        progressBar.isVisible = true

        dummyService.getProducts().enqueue(object : Callback<SearchProductsResponse> {
            override fun onResponse(
                call: Call<SearchProductsResponse>,
                response: Response<SearchProductsResponse>
            ) {
                progressBar.isVisible = false
                if (response.code() == 200) {
                    if (response.body()?.total!! > 0) {
                        products.addAll(response.body()?.products!!)
                        productAdapter.notifyDataSetChanged()
                    } else {
                        showMessage(
                            getString(R.string.search_not_found),
                            "",
                            R.drawable.placeholder_not_found
                        )
                    }
                } else {
                    showMessage(
                        getString(R.string.search_internet_problem),
                        getString(R.string.search_internet_problem_additional),
                        R.drawable.placeholder_no_internet_connection,
                        true
                    )
                }
            }

            override fun onFailure(call: Call<SearchProductsResponse>, t: Throwable) {
                showMessage(
                    getString(R.string.search_internet_problem),
                    getString(R.string.search_internet_problem_additional),
                    R.drawable.placeholder_no_internet_connection,
                    true
                )
            }
        })
    }

    private fun showMessage(
        title: String,
        additional: String,
        imageId: Int,
        internetProblem: Boolean = false
    ) {
        progressBar.isVisible = false
        if (title.isNotEmpty()) {
            layoutPlaceholders.isVisible = true
            products.clear()
            productAdapter.notifyDataSetChanged()
            titleProblem.text = title
            placeholder.setImageResource(imageId)
            addPlaceholdersVisibility(titlesVisibility = true)
            if (additional.isNotEmpty()) {
                additionalMessage.text = additional
                addPlaceholdersVisibility(additionalVisibility = true)
            }
            if (internetProblem) {
                addPlaceholdersVisibility(buttonUpdateVisibility = true)
            }
        }
    }

    private fun addPlaceholdersVisibility(
        titlesVisibility: Boolean = false,
        additionalVisibility: Boolean = false,
        buttonUpdateVisibility: Boolean = false
    ) {
        if (titlesVisibility) {
            titleProblem.isVisible = true
            placeholder.isVisible = true
        }
        if (additionalVisibility) {
            additionalMessage.isVisible = true
        }
        if (buttonUpdateVisibility) {
            buttonUpdate.isVisible = true
        }
    }


}