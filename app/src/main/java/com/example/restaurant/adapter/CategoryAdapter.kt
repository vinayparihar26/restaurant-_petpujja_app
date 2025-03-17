package com.example.restaurant.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.model.CategoryModel
import android.content.Context
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.load.engine.DiskCacheStrategy

class CategoryAdapter(
    private val context: Context, private var categoriesList: List<CategoryModel>,
    private val onCategoryClick: (CategoryModel) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.categoryImage)
        val textView: TextView = view.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_category, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val category = categoriesList[position]
        holder.textView.text = category.categoryName
        Log.d("CategoryAdapter", "Binding item at position $position: $category")

        Log.d("CategoryAdapter", "Loading Image: ${category.categoryImg}")

        val imageUrl = category.categoryImg
        Log.d("GlideDebug", "Image URL: $imageUrl")

        Glide.with(context).load(category.categoryImg)
            .placeholder(R.drawable.ic_launcher_background) // Use a placeholder image
            .error(R.drawable.ic_launcher_background) // Use an error image in case of failure
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache for performance
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onCategoryClick(category)
        }/*   holder.apply {
               Glide.with(context).load(item.categoryImg).into(holder.imageView)
               textView.text = item.categoryName*/

        /*        Glide.with(holder.itemView.context)
        .load(imageUrl)
        .error(R.drawable.ic_launcher_background)
        .into(holder.imageView)*/
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<CategoryModel>) {
        Log.d("CategoryAdapter", "Updating data: $newList")
        categoriesList = newList
        notifyDataSetChanged()
    }
}