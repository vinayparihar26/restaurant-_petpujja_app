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
    private val userId: String
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.findViewById(R.id.menuName)
        val menuPrice: TextView = view.findViewById(R.id.menuPrice)
        val menuImg: ImageView = view.findViewById(R.id.img)
        val removeIcon: ImageView = view.findViewById(R.id.closeButton)
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

        if (!item.menuImg.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(item.menuImg).into(holder.menuImg)
        } else {
            holder.menuImg.setImageResource(R.drawable.ic_launcher_foreground) // Default image
        }

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
                override fun onResponse(call: Call<WishlistResponse>, response: Response<WishlistResponse>) {
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
