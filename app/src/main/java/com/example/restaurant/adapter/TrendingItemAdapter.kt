package com.example.restaurant.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.model.TrendingItems

class TrendingItemAdapter(private val trendingList: List<TrendingItems>) :
    RecyclerView.Adapter<TrendingItemAdapter.TrendingViewHolder>() {

    class TrendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgTrending)
        val nameTextView: TextView = view.findViewById(R.id.tvTrendingItemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trending_item_layout, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val item = trendingList[position]
        holder.nameTextView.text = item.name

        Glide.with(holder.imageView.context).load(item.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int = trendingList.size
}
