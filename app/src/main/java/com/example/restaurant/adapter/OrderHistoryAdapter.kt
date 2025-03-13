package com.example.restaurant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.model.OrderedMenuItem

class OrderHistoryAdapter(private var orderedItems: MutableList<OrderedMenuItem>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_order_history, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val item = orderedItems[position]
        holder.menuName.text = item.menuName
        holder.menuPrice.text = "₹${item.menuPrice}"
        holder.orderDateTime.text = "Ordered on: ${item.orderCreatedAt}"

        // Ensure the image URL is valid
        val imageUrl = if (!item.menuImg.isNullOrEmpty() && item.menuImg.startsWith("http")) {
            item.menuImg
        } else {
            "https://yourserver.com/${item.menuImg ?: "default_image.jpg"}"
        }



        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.menuImage)
    }

    override fun getItemCount(): Int = orderedItems.size

    // Update data method
    fun updateData(newItems: List<OrderedMenuItem>) {
        orderedItems.clear()
        orderedItems.addAll(newItems)
        notifyDataSetChanged()
    }

    class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuName: TextView = itemView.findViewById(R.id.menuNameTextView)
        val menuPrice: TextView = itemView.findViewById(R.id.menuPriceTextView)
        val menuImage: ImageView = itemView.findViewById(R.id.menuImageView)
        val orderDateTime: TextView = itemView.findViewById(R.id.orderDateTimeTextView)
    }
}
