package one.upup.clover.pricemodifier.presenter

import android.accounts.Account
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.room.Room
import com.clover.sdk.util.CloverAccount
import com.clover.sdk.v1.Intents
import com.clover.sdk.v3.inventory.InventoryConnector
import com.clover.sdk.v3.inventory.Modifier
import com.clover.sdk.v3.inventory.ModifierGroup
import com.clover.sdk.v3.order.OrderConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import one.upup.clover.pricemodifier.MainActivity
import one.upup.clover.pricemodifier.adapter.ItemsListAdapter
import one.upup.clover.pricemodifier.repository.ItemInform
import one.upup.clover.pricemodifier.repository.ItemInformDatabase
import kotlin.random.Random

class MainPresenter : IMainPresenter {

    private val lineItemAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intents.ACTION_LINE_ITEM_ADDED && updatePriceState) {
                val orderId = intent.getStringExtra(Intents.EXTRA_CLOVER_ORDER_ID)

                coroutineScope.launch {
                    val order = orderConnector?.getOrder(orderId)
                    if (order != null) {
                        if (order.lineItems != null) {
                            val linerItem = order.lineItems.last()
                            if (linerItem.alternateName != null) {
                                val newPrice = (linerItem.price * percent).toLong()

                                changePrice(
                                    newPrice,
                                    orderId!!,
                                    linerItem.id
                                )

                                saveItemInform(
                                    linerItem.createdTime,
                                    orderId,
                                    linerItem.id,
                                    linerItem.price,
                                    linerItem.price + newPrice
                                )
                            }
                        }
                    }
                }

            }
        }
    }

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    private var cloverAccount: Account? = null
    private var inventoryConnector: InventoryConnector? = null
    private var orderConnector: OrderConnector? = null
    private var saveJob: Job? = null
    private var getItemJob: Job? = null
    private var percent: Double = 0.0
    private var updatePriceState = false
    private var itemsList: ArrayList<ItemInform> = ArrayList()

    private lateinit var context: Context
    private lateinit var itemDb: ItemInformDatabase

    override fun updatePercentText(text: String): String {
        return "$text ${(percent * 100).toInt()}%"
    }

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun initDb() {
        itemDb = Room.databaseBuilder(
            context,
            ItemInformDatabase::class.java,
            "item-inform-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun randomPercent() {
        percent = 0.05 + (0.2) * Random.nextDouble()
    }

    override fun connectToCloverAccount() {
        if (cloverAccount == null) {
            cloverAccount = CloverAccount.getAccount(context)

            if (cloverAccount == null) {
                return
            }
        }
    }

    override fun connectToInventoryConnector() {
        disconnectToInventoryConnector()
        if (cloverAccount != null) {
            inventoryConnector = InventoryConnector(context, cloverAccount, null)
            inventoryConnector!!.connect()
        }
    }

    override fun connectToOrderConnector() {
        disconnectToOrderConnector()
        if (cloverAccount != null) {
            orderConnector = OrderConnector(context, cloverAccount, null)
            orderConnector?.connect()
        }
    }

    override fun disconnectToInventoryConnector() {
        inventoryConnector?.disconnect()
        inventoryConnector = null
    }

    override fun disconnectToOrderConnector() {
        orderConnector?.disconnect()
        orderConnector = null
    }

    override fun getUpdatePriceState(): Boolean {
        return updatePriceState
    }

    override fun setUpdatePriceState(boolean: Boolean) {
        updatePriceState = boolean
    }

    override fun resentPercent() {
        percent = 0.0
    }

    override fun orderListener() {
        val intentFilter = IntentFilter(Intents.ACTION_LINE_ITEM_ADDED)
        context.registerReceiver(lineItemAddedReceiver, intentFilter)
    }

    override fun unregisterOrderListener() {
        context.unregisterReceiver(lineItemAddedReceiver)
    }

    private fun changePrice(price: Long, orderId: String, lineItemId: String) {
        val name = "Update price by ${(percent * 100).toInt()}%"
        val modifierGroup = inventoryConnector!!.createModifierGroup(ModifierGroup().setName(name))
        val modifier = inventoryConnector!!.createModifier(
            modifierGroup.id,
            Modifier().setName(name).setPrice(price)
        )
        orderConnector!!.addLineItemModification(orderId, lineItemId, modifier)
    }

    private fun saveItemInform(
        time: Long,
        orderId: String,
        itemId: String,
        oldPrice: Long,
        newPrice: Long
    ) {
        val itemInform = ItemInform(
            orderId = orderId,
            itemId = itemId,
            oldPrice = oldPrice,
            newPrice = newPrice,
            date = time
        )

        itemsList.add(itemInform)

        saveJob = coroutineScope.launch {
            itemDb.itemInformDao().insertItemInform(itemInform)
        }.apply {
            start()
        }
    }

    override fun prepareItemListAdapter() {
        getItemJob = coroutineScope.launch {
            itemsList = if (itemsList.isEmpty()) {
                ArrayList(itemDb.itemInformDao().getAllItemInform())
            } else {
                itemsList.clear()
                ArrayList(itemDb.itemInformDao().getAllItemInform())
            }
            withContext(Dispatchers.Main) {
                (context as MainActivity)
                    .showRvItemsList(ItemsListAdapter(itemsList.sortedByDescending { it.date }))
            }
        }.apply { start() }
    }

    override fun updateItemListToAdapter(): ArrayList<ItemInform> {
        return itemsList
    }

    override fun closeAllThread() {
        saveJob?.cancel()
        getItemJob?.cancel()
        coroutineScope.cancel()
    }
}