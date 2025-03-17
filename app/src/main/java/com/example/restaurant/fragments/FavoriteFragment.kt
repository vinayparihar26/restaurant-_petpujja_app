package com.example.restaurant.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.adapter.WishlistAdapter
import com.example.restaurant.model.WishlistItem


class FavoriteFragment : Fragment() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var emptyWishlistTextView: TextView
    private var wishlistItems: MutableList<WishlistItem> = mutableListOf()
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }


}