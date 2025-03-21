package com.example.restaurant.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurant.R
import com.example.restaurant.api.RetrofitClient
import com.example.restaurant.model.CartResponse
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.WishlistResponse
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuItemAdapter(
    private val context: Context,
    private val menuList: MutableList<MenuItem>,
) :
    RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {
    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val userId: String = sharedPreferences.getString("user_id", null) ?: ""


    class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgMenuItem)
        val nameTextView: TextView = view.findViewById(R.id.tvMenuItemName)
        val priceTextView: TextView = view.findViewById(R.id.tvMenuItemPrice)
        val addToCartButton: TextView = view.findViewById(R.id.btnAddToCart)
        val descriptionTextView: TextView = view.findViewById(R.id.menu_description)
        val imageHeart: ShapeableImageView = view.findViewById(R.id.imgHeart1)
        val imgShareDetails: TextView = view.findViewById(R.id.imgShareDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        try {
            val menuItem = menuList[position]
            holder.nameTextView.text = menuItem.menuName
            holder.priceTextView.text = "₹${menuItem.menuPrice}"
            holder.descriptionTextView.text = menuItem.menuDescription
            // holder.restaurantImageView.setImageResource(R.drawable.ic_launcher_background)
            Glide.with(context)
                .load(menuItem.menuImage)
                .placeholder(R.drawable.notfound)
                .into(holder.imageView)


            holder.imgShareDetails.setOnClickListener {
                shareItemDetails(menuItem)
            }
            var isItemAddedToCart = false

            holder.addToCartButton.setOnClickListener {

                if (isItemAddedToCart) {
                    // If the item is already in the cart, remove it from the cart
                    holder.addToCartButton.text =
                        "Add To Cart"  // Change button text back to "Add To Cart"
                    holder.addToCartButton.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )  // Reset button color

                    addToCart(menuItem.menuId, false)  // Removed from cart
                } else {
                    // If the item is not in the cart, add it to the cart
                    holder.addToCartButton.text =
                        "Added To Cart"  // Change button text to "Added To Cart"
                    holder.addToCartButton.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.orange
                        )
                    )  // Change button color to green

                    addToCart(menuItem.menuId, true)  // Added to cart
                }

                // Toggle the cart state
                isItemAddedToCart = !isItemAddedToCart
            }
            var isHeartRed = false  // Track if the heart is red (liked) or not

            holder.imageHeart.setOnClickListener {
                // Get the current drawable (VectorDrawable) from the ImageView
                val currentDrawable = holder.imageHeart.drawable

                // Check if the drawable is a VectorDrawable
                if (currentDrawable is VectorDrawable) {
                    // Toggle the heart's color
                    if (isHeartRed) {
                        // If it's red (liked), set it back to the default color
                        DrawableCompat.setTint(
                            currentDrawable,
                            ContextCompat.getColor(context, R.color.white)
                        ) // default color
                        addToWishlist(menuItem.menuId.toString(), false)  // Removed from wishlist
                    } else {
                        // If it's not red, set it to red (liked)
                        DrawableCompat.setTint(currentDrawable, Color.RED)
                        addToWishlist(menuItem.menuId.toString(), true)  // Added to wishlist
                    }

                    // Set the modified drawable back to the ImageView
                    holder.imageHeart.setImageDrawable(currentDrawable)

                    // Toggle the heart state
                    isHeartRed = !isHeartRed
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addToCart(menuId: String?, addToCartButton: Boolean) {
        try {
            val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "cart")
            val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId.toString())
            val quantityIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "1")

            val call = RetrofitClient.apiService.addToCart(
                method = method,
                userId = userIdBody,
                menuId = menuIdBody,
                quantity = quantityIdBody
            )
            call.enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val cartResponse = response.body()!!
                        Log.d("wish", "$cartResponse")
                        if (cartResponse.status == 200) {
                            if (addToCartButton) {
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Removed from Cart!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, cartResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to update Wishlist!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


                override fun onFailure(p0: Call<CartResponse>, p1: Throwable) {


                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Toast.makeText(
                context,
                "Error While adding item into cart: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun addToWishlist(menuId: String, heartButton: Boolean) {
        try {
            val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "favorites")
            val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
            val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId)

            val call = RetrofitClient.apiService.manageWishlist(method, userIdBody, menuIdBody)

            call.enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val wishlistResponse = response.body()!!
                        Log.d("wish", "$wishlistResponse")
                        if (wishlistResponse.status == 200) {
                            if (heartButton) {
                                Toast.makeText(context, "Added to Wishlist!", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Removed from Wishlist!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            Log.d("userrr", "$userId, $menuId, $method")
                        } else {
                            Toast.makeText(context, wishlistResponse.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to update Wishlist!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.e("WISHLIST_ERROR", "Error: ${t.message}")
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Toast.makeText(
                context,
                "Error while adding item in wishlist: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun shareItemDetails(menuItem: MenuItem) {
        try {
            val shareText = """
            🍽️ *${menuItem.menuName}*
            💰 Price: ₹${menuItem.menuPrice}
            📍 *${menuItem.restaurantName}*
            📌 Address: ${menuItem.restaurantAddress}
            ℹ️ Description: ${menuItem.menuDescription}
            
            🔗 Check it out now!
        """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            context.startActivity(Intent.createChooser(intent, "Share via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Error while Sharing Details: ${e.message}", Toast.LENGTH_SHORT)
                .show()

        }
    }
    override fun getItemCount(): Int = menuList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMenuList(newList: List<MenuItem>) {
        menuList.clear()
        menuList.addAll(newList)
        notifyDataSetChanged()
    }
}


