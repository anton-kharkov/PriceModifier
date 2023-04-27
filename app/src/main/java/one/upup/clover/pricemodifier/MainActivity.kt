package one.upup.clover.pricemodifier

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import one.upup.clover.pricemodifier.adapter.ItemsListAdapter
import one.upup.clover.pricemodifier.databinding.ActivityMainBinding
import one.upup.clover.pricemodifier.presenter.IMainPresenter
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val presenter by inject<IMainPresenter>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.setContext(this)
        presenter.initDb()
        presenter.prepareItemListAdapter()

        val permission = "com.clover.permission.ORDER"
        val checkSelfPermission = ContextCompat.checkSelfPermission(this, permission)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }

        presenter.connectToCloverAccount()
        presenter.connectToInventoryConnector()
        presenter.connectToOrderConnector()

        btnUpdateOnOffListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRestart() {
        super.onRestart()
        (binding.rvItemsList.adapter as ItemsListAdapter).itemsList =
            presenter.updateItemListToAdapter().sortedByDescending { it.date }
        binding.rvItemsList.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.disconnectToInventoryConnector()
        presenter.disconnectToOrderConnector()
        presenter.closeAllThread()
    }

    private fun btnUpdateOnOffListener() {
        binding.btnUpdateOnOff.setOnClickListener {
            if (!presenter.getUpdatePriceState()) {
                presenter.setUpdatePriceState(true)
                presenter.randomPercent()

                binding.tvText.text =
                    presenter.updatePercentText(getString(R.string.update_price_by))

                binding.btnUpdateOnOff.text = getString(R.string.update_off)

                presenter.orderListener()
            } else {
                presenter.setUpdatePriceState(false)
                presenter.resentPercent()

                binding.tvText.text = getString(R.string.click_the_button_to_update_price)

                binding.btnUpdateOnOff.text = getString(R.string.update_on)

                presenter.unregisterOrderListener()
            }
        }
    }

    fun showRvItemsList(adapter: ItemsListAdapter) {
        binding.rvItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvItemsList.adapter = adapter
    }
}