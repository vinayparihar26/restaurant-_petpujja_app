package com.example.restaurant.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.activities.RestaurantMenuActivity
import com.example.restaurant.databinding.ItemViewRestaurantBinding
import com.example.restaurant.model.Restaurant


class RestaurantAdapter(private val restaurantList: List<Restaurant>, val context: Context) :
    RecyclerView.Adapter<RestaurantAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemViewRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemViewRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        with(holder.binding) {
            nameRestaurant.text = restaurant.restaurantName
            numberRestaurant.text = restaurant.restaurantPhone
            addressRestaurant.text = restaurant.restaurantAddress
            ratingRestaurant.text = restaurant.avgRating.toString()
            restaurantDesc.text = restaurant.reviewCount

            Glide.with(imageRestaurant.context)
                .load(restaurant.restaurantImg)
                .placeholder(R.drawable.notfound)
                .into(imageRestaurant)

            imageRestaurant.setOnClickListener {
                val intent = Intent(holder.context, RestaurantMenuActivity::class.java)
                intent.putExtra("restaurantId", restaurant.restaurantId ?: "")
                holder.context.startActivity(intent)
                //onRestaurantClick(restaurant)
            }

            share.setOnClickListener {
                shareItemDetails(restaurant)
            }

            imgBtnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${restaurant.restaurantPhone}")
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = restaurantList.size

    private fun shareItemDetails(restaurant: Restaurant) {
        val shareText = """
            🍽️ restaurantName*${restaurant.restaurantName}*
            📍 avgRating*${restaurant.avgRating}*
            📌 Address: ${restaurant.restaurantAddress}
            ℹ️ reviewCount: ${restaurant.reviewCount}
            
            🔗 Check it out now!
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}




