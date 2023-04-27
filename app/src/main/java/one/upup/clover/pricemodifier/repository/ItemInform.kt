package one.upup.clover.pricemodifier.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_inform")
data class ItemInform(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: String,
    val itemId: String,
    val oldPrice: Long,
    val newPrice: Long,
    val date: Long
)
