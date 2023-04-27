package one.upup.clover.pricemodifier.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import one.upup.clover.pricemodifier.R
import one.upup.clover.pricemodifier.repository.ItemInform
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemsListAdapter(var itemsList: List<ItemInform>) :
    RecyclerView.Adapter<ItemsListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemBind(itemsList[position])
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        private val tvOrderId = itemView.findViewById<TextView>(R.id.tvOrderId)
        private val tvItemId = itemView.findViewById<TextView>(R.id.tvItemId)
        private val tvOldPrice = itemView.findViewById<TextView>(R.id.tvOldPrice)
        private val tvNewPrice = itemView.findViewById<TextView>(R.id.tvNewPrice)

        @SuppressLint("SetTextI18n")
        fun itemBind(itemInform: ItemInform) {
            val date = Date(itemInform.date)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            tvTime.text = format.format(date)
            tvOrderId.text = "Order Id: ${itemInform.orderId}"
            tvItemId.text = "Item Id: ${itemInform.itemId}"
            tvOldPrice.text =
                "Old Price: ${String.format("%.2f$", (itemInform.oldPrice.toDouble() / 100))}"
            tvNewPrice.text =
                "New Price: ${String.format("%.2f$", (itemInform.newPrice.toDouble() / 100))}"
        }
    }
}