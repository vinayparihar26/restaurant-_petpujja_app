package com.example.restaurant.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.CartItem
import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.MenuItem
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>,
    private val removeCartItem: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val userId: String = sharedPreferences.getString("user_id", null) ?: ""


    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuImage: ImageView = itemView.findViewById(R.id.menuImage)
        val menuName: TextView = itemView.findViewById(R.id.menuName)
        val increment: ImageView = itemView.findViewById(R.id.increment)
        val decrement: ImageView = itemView.findViewById(R.id.decrement)
        val menuDescription: TextView = itemView.findViewById(R.id.menuItemDescription)
        val totalPrice: TextView = itemView.findViewById(R.id.totalMenuPrice)
        val menuQuantity: TextView = itemView.findViewById(R.id.menuQuantity)
        val deleteCart: MaterialButton = itemView.findViewById(R.id.deleteCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        var currentQuantity = item.quantity
        val itemPrice = item.menuPrice

        holder.menuName.text = item.menuName
        holder.menuDescription.text=item.menuDescription
        holder.menuQuantity.text = currentQuantity.toString()
        holder.totalPrice.text = (currentQuantity * itemPrice).toString()

        Glide.with(holder.itemView.context)
            .load(item.menuImg)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.menuImage)

        holder.deleteCart.setOnClickListener {
            removeCartItem(item.cartId, position)
        }

        holder.increment.setOnClickListener {
            currentQuantity++
            val updatedPrice = currentQuantity * itemPrice
            holder.menuQuantity.text = currentQuantity.toString()
            holder.totalPrice.text = updatedPrice.toString()

            updateCartItem(userId, item.menuId.toInt() ,currentQuantity)
        }


        holder.decrement.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                val updatedPrice = currentQuantity * itemPrice
                holder.menuQuantity.text = currentQuantity.toString()
                holder.totalPrice.text = updatedPrice.toString()

                updateCartItem(userId, item.menuId.toInt() ,currentQuantity)

            }
        }



    }

    private fun updateCartItem(userId: String, menuId: Int, newQuantity: Int) {
        val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "cart")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId.toString())
        val quantityBody = RequestBody.create("text/plain".toMediaTypeOrNull(), newQuantity.toString())

        RetrofitClient.apiService.addToCart(methodBody, userIdBody, menuIdBody, quantityBody)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        Log.d("CartUpdate", "Cart updated successfully")
                    } else {
                        Log.e("CartUpdate", "Failed to update cart: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Log.e("CartUpdateError", t.message.toString())
                }
            })
    }

    override fun getItemCount(): Int = cartItems.size
}



