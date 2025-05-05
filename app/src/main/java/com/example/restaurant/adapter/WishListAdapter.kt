package com.example.restaurant.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.databinding.ItemViewWishlistBinding
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

    class WishlistViewHolder(val binding: ItemViewWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding = ItemViewWishlistBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WishlistViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item = wishlist[position]

        with(holder.binding){
            wishListMenuName.text = item.menuName
            wishListMenuPrice.text = "₹${item.menuPrice}"
            wishListMenuDesc.text = item.menuDescription

            wishListRemove.setOnClickListener {
                removeFromWishlist(item.menuId, position, holder.itemView.context)
            }

            Glide.with(holder.binding.wishListMenuImg.context)
                .load(item.menuImg)
                .placeholder(R.drawable.notfound)
                .into(holder.binding.wishListMenuImg)

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
                    } else {
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
