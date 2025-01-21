package com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.fiserv.commercehub.androidttp.BuildConfig
import com.fiserv.commercehub.androidttp.data.Constant.TIME_FORMAT
import com.fiserv.commercehub.androidttp.data.model.Product
import com.fiserv.commercehub.ttp.provider.FiservTTPCardReader
import com.fiserv.commercehub.ttp.provider.constants.Currency
import com.fiserv.commercehub.ttp.provider.constants.Environment
import com.fiserv.commercehub.ttp.provider.exception.FiservTTPCardReaderException
import com.fiserv.commercehub.ttp.provider.model.FiservTTPConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductViewModel() : ViewModel() {

    private val channel = Channel<String>()
    private val _products = SnapshotStateList<Product>()
    private val _cartSize = MutableStateFlow(0)
    private var _isLogPageVisible = MutableStateFlow(false)
    private val _isLoadingInit = MutableStateFlow(false)
    private val _logData = SnapshotStateList<String>()

    private val _log = mutableStateOf("Please wait. SDK init will take some time")
    val logData: SnapshotStateList<String> get() = _logData
    val products: SnapshotStateList<Product> get() = _products
    val cartSize: StateFlow<Int> get() = _cartSize
    val log: MutableState<String> = _log
    val isLogPageVisible: StateFlow<Boolean> get() = _isLogPageVisible
    val isLoadingInit: StateFlow<Boolean> = _isLoadingInit

    init {
        addProduct(Product(id = 1, productName = "Book", productCost = 12.00, type = 1))
        addProduct(Product(id = 2, productName = "Phone Case", productCost = 20.00, type = 2))
        addProduct(Product(id = 3, productName = "Smart Phone", productCost = 250.0, type = 3))
    }

    fun initFiservTTPSession(context: Context) {

        FiservTTPCardReader.setLoggingChannel(channel)
        listenLogChannel(channel)

        init(
            context = context,
            secret = BuildConfig.apiSecret,
            apiKey = BuildConfig.apiKey,
            environment = BuildConfig.env,
            currency = Currency.USD.toString(),
            merchantId = BuildConfig.mid,
            terminalId = BuildConfig.tid,
            ppid = BuildConfig.ppid,
            hostPort = BuildConfig.host_port
        )
    }

    fun visibleLogPage() {
        _isLogPageVisible.value = true
    }

    fun disableLogPage() {
        _isLogPageVisible.value = false
    }

    private fun init(
        context: Context,
        secret: String,
        apiKey: String,
        environment: String,
        currency: String,
        merchantId: String,
        terminalId: String,
        ppid: String,
        hostPort: String,
    ) {

        if (!TextUtils.isEmpty(secret)
            && !TextUtils.isEmpty(apiKey)
            && !TextUtils.isEmpty(environment)
            && !TextUtils.isEmpty(currency)
            && !TextUtils.isEmpty(merchantId)
            && !TextUtils.isEmpty(terminalId)
        ) {

            startLoadingInit()
            CoroutineScope(Dispatchers.Main).launch {
                FiservTTPCardReader.initializeSession(
                    context = context,
                    FiservTTPConfig(
                        merchantId,
                        terminalId,
                        apiKey,
                        secret,
                        ppid,
                        hostPort,
                        Environment.valueOf(environment),
                        Currency.valueOf(currency)
                    )
                ).flowOn(Dispatchers.IO)
                    .single()
                    .onSuccess {
                        "Ready to Accept Payment".addLog()
                        stopLoadingInit()
                    }
                    .onFailure {
                        if (it is FiservTTPCardReaderException) {
                            "Type: ${it.type} Code: ${it.code} Message:  ${it.message}".addLogWithDate()
                        } else {
                            "Message: ${it.message}".addLogWithDate()
                        }
                        stopLoadingInit()
                    }
            }
        } else {
            Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
        }
    }

    fun startLoadingInit() {
        _isLoadingInit.value = true
    }

    fun stopLoadingInit() {
        _isLoadingInit.value = false
    }

    fun addProduct(products: Product) {
        _products.add(products)
    }

    fun updateProductCost(product: Product, cost: String) {
        _products.findLast { it.id == product.id }
            ?.let { it.productCost = cost.toDouble() }
    }

    fun updateProductQty(product: Product, flag: Boolean) {
        var productUpdate = product.copy()
        _products.findLast { it.id == product.id }
            ?.apply { productUpdate.qty = if (flag) this.qty + 1 else this.qty - 1 }
            ?.apply { _products[_products.indexOf(product)] = productUpdate }
        updateCart()
    }

    fun updateCart(): List<Product> {
        return _products.toList()
            .filter { it.qty > 0 }
            .apply { _cartSize.value = this.size }
    }

    private fun String.addLog() {
        _log.value = this
    }

    private fun String.addLogWithDate() {
        _logData.add(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT)) + "-" + this
        )
    }

    private fun listenLogChannel(channel: Channel<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            channel.consumeEach { it.addLogWithDate() }
        }
    }

}