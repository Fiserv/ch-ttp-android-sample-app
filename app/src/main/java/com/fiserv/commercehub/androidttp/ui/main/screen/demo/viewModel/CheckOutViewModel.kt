package com.fiserv.commercehub.androidttp.ui.main.screen.demo.viewModel

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModel
import com.fiserv.commercehub.androidttp.data.Constant.DATE_TIME_FORMAT
import com.fiserv.commercehub.androidttp.data.Constant.TIME_FORMAT
import com.fiserv.commercehub.ttp.provider.FiservTTPCardReader
import com.fiserv.commercehub.ttp.provider.constants.PaymentTransactionType
import com.fiserv.commercehub.ttp.provider.constants.RefundTransactionType
import com.fiserv.commercehub.ttp.provider.exception.FiservTTPCardReaderException
import com.fiserv.commercehub.ttp.provider.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Handler

/**
 * ViewModel for managing checkout flow and payment processing.
 * Handles payment transactions, refunds, void operations, and logging.
 */
class CheckOutViewModel() : ViewModel() {

    val channel = Channel<String>()

    // Transaction-related states
    var transactionId: MutableState<String> = mutableStateOf("")
    var orderId: MutableState<String> = mutableStateOf("")
    var merchantId: MutableState<String> = mutableStateOf("")

    private val amount: MutableState<Double> = mutableStateOf(0.0)

    // UI-related states and data
    val logData: SnapshotStateList<String> get() = _logData
    val isPaymentSuccessAlert: StateFlow<Boolean> get() = _isPaymentSuccessAlert
    val isCheckOutPage: StateFlow<Boolean> get() = _isCheckOutPage
    val isLogPageVisible: StateFlow<Boolean> get() = _isLogPageVisible
    val isLoadingVoid: StateFlow<Boolean> get() = _isLoadingVoid
    val isLoadingRefund: StateFlow<Boolean> get() = _isLoadingRefund
    val isLoadingPay: StateFlow<Boolean> get() = _isLoadingPay

    private val _logData = SnapshotStateList<String>()
    private var _isPaymentSuccessAlert = MutableStateFlow(false)
    private var _isCheckOutPage = MutableStateFlow(true)
    private var _isLogPageVisible = MutableStateFlow(false)
    private val _isLoadingVoid = MutableStateFlow(false)
    private val _isLoadingRefund = MutableStateFlow(false)
    private val _isLoadingPay = MutableStateFlow(false)

    /**
     * Initializes ViewModel by setting up logging channel/initial state.
     */
    fun initViewModel() {
        FiservTTPCardReader.setLoggingChannel(channel)
        listenLogChannel(channel)
        "*** Payment proceed ***".addLog()
    }

    fun visibleLogPage() {
        _isLogPageVisible.value = true
    }

    fun disableLogPage() {
        _isLogPageVisible.value = false
    }

    private fun showCheckoutPage() {
        _isCheckOutPage.value = true
    }

    fun dismiss() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            _isPaymentSuccessAlert.value = false
        }
    }

    private fun stopCheckoutPage() {
        _isCheckOutPage.value = false
    }

    private fun startLoadingVoid() {
        _isLoadingVoid.value = true
    }

    private fun stopLoadingVoid() {
        _isLoadingVoid.value = false
    }

    fun startLoadingPay() {
        _isLoadingPay.value = true
    }

    fun stopLoadingPay() {
        _isLoadingPay.value = false
    }

    fun startLoadingRefund() {
        _isLoadingRefund.value = true
    }

    fun stopLoadingRefund() {
        _isLoadingRefund.value = false
    }

    private fun listenLogChannel(channel: Channel<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            channel.consumeEach { it.addLog() }
        }
    }

    private fun getRefundAmountOrNull() = amount.toString().toBigDecimalOrNull()

    private fun getReferenceTransactionDetailsOrNull(): ReferenceTransactionDetails? {
        return transactionId.value.toString()
            ?.takeIf { !TextUtils.isEmpty(it) }
            ?.let { ReferenceTransactionDetails(transactionId.value.toString()) }
    }

    private fun getTransactionDetailsRequestForRefund() = TransactionDetailsRequest(
        true,
        transactionId.value.toString(),
        merchantId.value.toString(),
        false
    )

    private fun getRefundType(flag: Int): RefundTransactionType {
        return if (flag == 1) {
            RefundTransactionType.TAGGED
        } else {
            RefundTransactionType.OPEN
        }

    }

    /**
     * Processes a refund transaction using stored transaction details.
     *
     * @param context: application context for showing Toast messages
     */
    fun onRefund(context: Context) {
        startLoadingRefund()
        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.refunds(
                amount = getRefundAmountOrNull(),
                refundTransactionType = getRefundType(1),
                transactionDetailsRequest = getTransactionDetailsRequestForRefund(),
                referenceTransactionDetails = getReferenceTransactionDetailsOrNull()
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingRefund() }
                .single()
                .onSuccess {
                    if (it.gatewayResponse?.transactionState.equals("FAILED")) {
                        "Refund Failed".addLog()
                        Toast.makeText(context, "Refund Failed", Toast.LENGTH_SHORT).show()
                    } else {
                        "Refund Success".addLog()
                        Toast.makeText(context, "Refund Success", Toast.LENGTH_SHORT).show()
                    }

                }
                .onFailure {
                    Toast.makeText(context, "Refund Failed", Toast.LENGTH_SHORT).show()
                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }
        }
    }

    /**
     * Processes a payment transaction with the given amount.
     *
     * @param context: application context for showing Toast messages
     * @param totalAmount: total amount to be charged
     */
    fun onPay(context: Context, totalAmount: Double) {
        amount.value = totalAmount
        startLoadingPay()
        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.charges(
                totalAmount.toString().toBigDecimal(),
                PaymentTransactionType.SALE,
                TransactionDetailsRequest(captureFlag = false, createToken = false),
                null,
                null
            )
                .flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingPay() }
                .single()
                .onSuccess { chargesResponse: ChargesResponse? ->
                    postSuccess()
                    chargesResponse?.gatewayResponse?.transactionProcessingDetails?.transactionId
                        ?.apply { transactionId.value = this }
                    chargesResponse?.gatewayResponse?.transactionProcessingDetails?.orderId
                        ?.apply { orderId.value = this }
                    chargesResponse?.transactionDetails?.merchantOrderId
                        ?.apply { merchantId.value = this }
                }
                .onFailure {
                    Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT).show()
                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }
        }
    }

    private fun postSuccess() {
        _isPaymentSuccessAlert.value = true
        _isCheckOutPage.value = false
    }

    /**
     * Voids/cancels the current transaction.
     *
     * @param context: application context for showing messages
     */
    fun voidTransaction(context: Context) {
        startLoadingVoid()
        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.cancels(
                amount.toString().toBigDecimalOrNull(),
                ReferenceTransactionDetails(transactionId.value)
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingVoid() }
                .single()
                .onSuccess {

                    if (it!!.gatewayResponse?.transactionState.equals("FAILED")) {
                        "Void Transaction Failed".addLog()
                        Toast.makeText(context, "Void Transaction Failed", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        "Void Transaction Success".addLog()
                        Toast.makeText(context, "Void Transaction Success", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                .onFailure {
                    Toast.makeText(context, "Void Transaction Failed", Toast.LENGTH_SHORT).show()

                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }
        }

    }

    private fun String.addLog() {
        _logData.add(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT)) + "-" + this
        )
    }

    fun getCurrentDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
    }
}