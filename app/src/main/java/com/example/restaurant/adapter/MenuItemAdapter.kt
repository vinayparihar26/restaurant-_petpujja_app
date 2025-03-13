package com.example.restaurant.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.example.restaurant.model.MenuItem
import com.example.restaurant.model.WishlistResponse
import com.example.restaurant.utils.HeartView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuItemAdapter(
    private val context: Context,
    private val menuList: List<MenuItem>,
    private val userId: String
    //private val onFavoriteClick: (String) -> Unit
) :
    RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imgMenuItem)
        val nameTextView: TextView = view.findViewById(R.id.tvMenuItemName)
        val priceTextView: TextView = view.findViewById(R.id.tvMenuItemPrice)
        //val addToWishlistButton: TextView = view.findViewById(R.id.btnWishlist)
        val descriptionTextView: TextView = view.findViewById(R.id.menu_description)
        val restaurantNameTextView: TextView = view.findViewById(R.id.restaurant_name)
        val restaurantAddressTextView: TextView = view.findViewById(R.id.restaurant_address)
        val restaurantImageView: ImageView = view.findViewById(R.id.restaurant_image)
        val imageHeart:HeartView=view.findViewById(R.id.imgHeart)
        val imgShareDetails:ImageView=view.findViewById(R.id.imgShareDetails)
        val distance: TextView=view.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.nameTextView.text = menuItem.menuName
        holder.priceTextView.text = "₹${menuItem.menuPrice}"
        holder.descriptionTextView.text = menuItem.menuDescription
        holder.restaurantNameTextView.text = menuItem.restaurantName
        holder.restaurantAddressTextView.text = menuItem.restaurantAddress
        holder.distance.text=menuItem.distance
       // holder.restaurantImageView.setImageResource(R.drawable.ic_launcher_background)
        Glide.with(context)
            .load(menuItem.menuImage)
            .placeholder(R.drawable.notfound)
            .into(holder.imageView)

        Glide.with(context)
            .load(menuItem.menuImage)
            .placeholder(R.drawable.notfound)
            .into(holder.restaurantImageView)

        holder.imgShareDetails.setOnClickListener {
            shareItemDetails(menuItem)
        }
        holder.imageHeart.setOnClickListener {
            addToWishlist(
                menuItem.menuId.toString(),
                heartButton = holder.imageHeart
            )
        }
      /*  holder.addToWishlistButton.setOnClickListener {
            addToWishlist("userId", menuItem.menuId.toString(), holder)
            Log.d("userId", menuItem.menuId.toString())
        }*/
    }

    private fun addToWishlist(menuId: String, heartButton: HeartView) {
        val method = RequestBody.create("text/plain".toMediaTypeOrNull(), "favorites")
        val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId)
        val menuIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId)

        val call = RetrofitClient.apiService.manageWishlist(method, userIdBody, menuIdBody)

        call.enqueue(object : Callback<WishlistResponse> {
            override fun onResponse(call: Call<WishlistResponse>, response: Response<WishlistResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val wishlistResponse = response.body()!!
                    if (wishlistResponse.status == 200) {
                        heartButton.setBackgroundColor(R.drawable.heart)
                        Toast.makeText(context, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, wishlistResponse.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to update Wishlist!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                Log.e("WISHLIST_ERROR", "Error: ${t.message}")
                Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun shareItemDetails(menuItem: MenuItem) {
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
    }

   /* private fun addToWishlist(userId: String, menuId: String, holder: MenuItemViewHolder) {
        val call = RetrofitClient.apiService.manageWishlist(favorites, userId, menuId)

        call.enqueue(object : Callback<WishlistResponse> {
            override fun onResponse(
                call: Call<WishlistResponse>,
                response: Response<WishlistResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val commonResponse = response.body()!!
                    if (commonResponse.status == 200) {
                        Toast.makeText(context, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to add!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show()
            }
        })
    }*/

    override fun getItemCount(): Int = menuList.size
}

