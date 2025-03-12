package com.example.restaurant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.model.drawerModel

class DrawerAdapter(private val items: List<drawerModel>, private val onItemClick: (drawerModel) -> Unit) :
    RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder>() {

    inner class DrawerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.drawerItemIcon)
        val title: TextView = view.findViewById(R.id.drawerItemTitle)

        fun bind(item: drawerModel) {
            icon.setImageResource(item.icon)
            title.text = item.title
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_drawer, parent, false)
        return DrawerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
