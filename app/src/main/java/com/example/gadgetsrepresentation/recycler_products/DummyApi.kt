package com.example.gadgetsrepresentation.recycler_products

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val PAGE_PRODUCT_LIMIT = 20

interface DummyApi {

    @GET("/products")
    fun getProducts(@Query("skip") skip: Int = 0, @Query("limit") limit: Int = PAGE_PRODUCT_LIMIT): Call<SearchProductsResponse>
}