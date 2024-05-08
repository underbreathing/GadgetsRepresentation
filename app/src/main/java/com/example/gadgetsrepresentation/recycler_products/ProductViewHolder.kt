package com.example.gadgetsrepresentation.recycler_products

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.gadgetsrepresentation.R

class ProductViewHolder(parentView: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(parentView.context).inflate(
    R.layout.product_item,parentView,false)) {
    private val thumbnail: ImageView = itemView.requireViewById(R.id.thumbnail)
    private val title: TextView = itemView.findViewById(R.id.title)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val price: TextView = itemView.findViewById(R.id.price)

    fun bind(model: Product){
        title.text = model.title
        description.text = model.description
        price.text = "${model.price}.00"
        Glide.with(itemView)
            .load(model.thumbnail)
            .centerCrop()
            .transform(RoundedCorners(12))
            .placeholder(R.drawable.placeholder_product)
            .into(thumbnail)
    }
}