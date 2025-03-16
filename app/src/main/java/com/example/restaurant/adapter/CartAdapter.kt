package com.example.restaurant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.model.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val removeCartItem: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuImage: ImageView = itemView.findViewById(R.id.menuImage)
        val menuName: TextView = itemView.findViewById(R.id.menuName)
        val menuPrice: TextView = itemView.findViewById(R.id.menuPrice)
        val menuQuantity: TextView = itemView.findViewById(R.id.menuQuantity)
        val deleteCart: ImageView = itemView.findViewById(R.id.deleteCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_cart, parent, false)
        return CartViewHolder(view)

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.menuName.text = item.menuName
        holder.menuPrice.text = "₹${item.menuPrice}"
        holder.menuQuantity.text = "Qty: ${item.quantity}"

        Glide.with(holder.itemView.context)
            .load(item.menuImg)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.menuImage)

        holder.deleteCart.setOnClickListener {
            removeCartItem(item.cartId, position)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
