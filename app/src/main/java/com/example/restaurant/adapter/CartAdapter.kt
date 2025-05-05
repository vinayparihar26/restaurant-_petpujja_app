package com.example.restaurant.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import com.example.restaurant.databinding.ItemViewCartBinding
import com.example.restaurant.model.CartItem
import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.OrdersItem
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val removeCartItem: (String, Int) -> Unit,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var grandTotal: Int = 0
    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val userId: String = sharedPreferences.getString("user_id", null) ?: ""

    class CartViewHolder(val binding: ItemViewCartBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            ItemViewCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        var currentQuantity = item.quantity
        val itemPrice = item.menuPrice

        with(holder.binding) {
            menuName.text = item.menuName
            menuItemDescription.text = item.menuDescription
            menuQuantity.text = currentQuantity.toString()
            totalMenuPrice.text = (currentQuantity * itemPrice).toString()

            Glide.with(context)
                .load(item.menuImg)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.notfound)
                .into(menuImage)

            deleteCart.setOnClickListener {
                removeCartItem(item.cartId, position)
            }

            increment.setOnClickListener {
                currentQuantity++
                val updatedPrice = currentQuantity * itemPrice
                menuQuantity.text = currentQuantity.toString()
                totalMenuPrice.text = updatedPrice.toString()

                updateCartItem(userId, item.menuId.toInt(), currentQuantity)
            }


            // holder.tvTotalPayment.text = item.totalAmount
            decrement.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    val updatedPrice = currentQuantity * itemPrice
                    menuQuantity.text = currentQuantity.toString()
                    totalMenuPrice.text = updatedPrice.toString()
                    updateCartItem(userId, item.menuId.toInt(), currentQuantity)

                }
            }
        }


    }

    private fun updateCartItem(userId: String, menuId: Int, newQuantity: Int) {
        try {
            val methodBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "cart")
            val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId.toString())
            val quantityBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), newQuantity.toString())

            RetrofitClient.apiService.addToCart(methodBody, userIdBody, menuIdBody, quantityBody)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>,
                    ) {
                        if (response.isSuccessful) {
                            Log.d("CartUpdate", "Cart updated successfully")
                        } else {
                            Log.e(
                                "CartUpdate",
                                "Failed to update cart: ${response.errorBody()?.string()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        Log.e("CartUpdateError", t.message.toString())
                    }
                })
        } catch (e: Exception) {
            Toast.makeText(this.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int = cartItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMenuList(newList: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }

}



