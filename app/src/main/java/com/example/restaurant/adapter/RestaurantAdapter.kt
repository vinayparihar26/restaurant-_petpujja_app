package com.example.restaurant.adapter


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.activities.MenuItemsActivity
import com.example.restaurant.activities.RestaurantMenuActivity
import com.example.restaurant.model.CategoryModel
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.Restaurant


class RestaurantAdapter(private val restaurantList: List<Restaurant>, val context: Context) :
    RecyclerView.Adapter<RestaurantAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantImageView: ImageView = view.findViewById(R.id.image_restaurant)
        val restaurantName: TextView = view.findViewById(R.id.name_restaurant)
        val restaurantPhone: TextView = view.findViewById(R.id.number_restaurant)
        val restaurantAddress: TextView = view.findViewById(R.id.address_restaurant)
        val restaurantRating: TextView = view.findViewById(R.id.rating_restaurant)
        val restaurantDesc: TextView = view.findViewById(R.id.restaurant_desc)
        val share: TextView = view.findViewById(R.id.share)
        val call: ImageView = view.findViewById(R.id.imgBtnCall)

        val context: Context = view.context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_restaurant, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.restaurantName.text = restaurant.restaurantName
        holder.restaurantPhone.text = restaurant.restaurantPhone
        holder.restaurantAddress.text = restaurant.restaurantAddress
       holder.restaurantRating.text = restaurant.avgRating.toString()
       holder.restaurantDesc.text = restaurant.reviewCount
//        holder.share.text = restaurant.share
        //holder.call.setImageResource(restaurant.call)
        //holder.btnAddToWishList.setImageResource(restaurant.btnAddToWishList)

        Glide.with(holder.restaurantImageView.context)
            .load(restaurant.restaurantImg)
            .placeholder(R.drawable.notfound)
            .into(holder.restaurantImageView)

        holder.restaurantImageView.setOnClickListener {
            val intent = Intent(holder.context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId", restaurant.restaurantId?:"")
            Log.d("restaurantId", restaurant.restaurantId.toString())
            holder.context.startActivity(intent)
            //onRestaurantClick(restaurant)
        }

        holder.share.setOnClickListener {
            shareItemDetails(restaurant)
        }

        holder.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${restaurant.restaurantPhone}")
            context.startActivity(intent)
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




