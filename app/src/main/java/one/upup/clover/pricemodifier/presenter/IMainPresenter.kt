package one.upup.clover.pricemodifier.presenter

import android.content.Context
import one.upup.clover.pricemodifier.repository.ItemInform

interface IMainPresenter {
    fun updatePercentText(text: String): String
    fun setContext(context: Context)
    fun initDb()
    fun randomPercent()
    fun connectToCloverAccount()
    fun connectToInventoryConnector()
    fun connectToOrderConnector()
    fun disconnectToInventoryConnector()
    fun disconnectToOrderConnector()
    fun getUpdatePriceState(): Boolean
    fun setUpdatePriceState(boolean: Boolean)
    fun resentPercent()
    fun orderListener()
    fun unregisterOrderListener()
    fun prepareItemListAdapter()
    fun updateItemListToAdapter(): ArrayList<ItemInform>
    fun closeAllThread()
}