package one.upup.clover.pricemodifier.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemInform::class], version = 1, exportSchema = false)
abstract class ItemInformDatabase: RoomDatabase() {
    abstract fun itemInformDao(): ItemInformDAO
}