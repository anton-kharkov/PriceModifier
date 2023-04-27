package one.upup.clover.pricemodifier.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemInformDAO {
    @Query("SELECT * FROM item_inform")
    fun getAllItemInform(): List<ItemInform>

    @Insert
    fun insertItemInform(itemInform: ItemInform)
}