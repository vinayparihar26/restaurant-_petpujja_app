package com.example.restaurant.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.WishlistItem
import com.example.restaurant.model.WishlistResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistAdapter(
    private var wishlist: MutableList<WishlistItem>,
    private val userId: String,
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.findViewById(R.id.wishListMenuName)
        val menuPrice: TextView = view.findViewById(R.id.wishListMenuPrice)
        val menuImg: ImageView = view.findViewById(R.id.wishListMenuImg)
        val menuQuantity: TextView = view.findViewById(R.id.wishListMenuQuantity)
        val removeIcon: ImageView = view.findViewById(R.id.wishListRemove)
        val description: TextView = view.findViewById(R.id.wishListMenuDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item = wishlist[position]

        holder.menuName.text = item.menuName
        holder.menuPrice.text = "₹${item.menuPrice}"
        holder.menuQuantity.text = "1"
        holder.description.text = item.menuDescription

        Glide.with(holder.menuImg.context)
            .load(item.menuImg)
            .placeholder(R.drawable.notfound)
            .into(holder.menuImg)

        holder.removeIcon.setOnClickListener {
            removeFromWishlist(item.menuId, position, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int = wishlist.size

    private fun removeFromWishlist(menuId: String, position: Int, context: Context) {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "favorites")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId)

        RetrofitClient.apiService.manageWishlist(methodBody, userIdBody, menuIdBody)
            .enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>,
                ) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        wishlist.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(
                            context,
                            "Item removed from wishlist",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to remove item",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
