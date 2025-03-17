package com.example.restaurant.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.model.Restaurant

class RestaurantAdapter(private val restaurantList: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantImageView: ImageView = view.findViewById(R.id.image_restaurant)
        val restaurantName: TextView = view.findViewById(R.id.name_restaurant)
        val restaurantPhone: TextView = view.findViewById(R.id.number_restaurant)
        val restaurantAddress: TextView = view.findViewById(R.id.address_restaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_restaurant, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.restaurantName.text = restaurant.restaurantName
        holder.restaurantPhone.text = restaurant.restaurantPhone
        holder.restaurantAddress.text = restaurant.restaurantAddress

        Glide.with(holder.restaurantImageView.context).load(restaurant.restaurantImg)
            .into(holder.restaurantImageView)
    }

    override fun getItemCount(): Int = restaurantList.size
}
