package com.fiserv.commercehub.androidttp.ui.main.screen.test.viewModel


import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.fiserv.commercehub.androidttp.R
import com.fiserv.commercehub.androidttp.data.Constant.TIME_FORMAT
import com.fiserv.commercehub.ttp.provider.FiservTTPCardReader
import com.fiserv.commercehub.ttp.provider.constants.Currency
import com.fiserv.commercehub.ttp.provider.constants.Environment
import com.fiserv.commercehub.ttp.provider.constants.PaymentTransactionType
import com.fiserv.commercehub.ttp.provider.constants.RefundTransactionType
import com.fiserv.commercehub.ttp.provider.exception.FiservTTPCardReaderException
import com.fiserv.commercehub.ttp.provider.model.AccountVerificationResponse
import com.fiserv.commercehub.ttp.provider.model.Card
import com.fiserv.commercehub.ttp.provider.model.ChargesResponse
import com.fiserv.commercehub.ttp.provider.model.FiservTTPConfig
import com.fiserv.commercehub.ttp.provider.model.PaymentToken
import com.fiserv.commercehub.ttp.provider.model.ReadCardDetailsResponse
import com.fiserv.commercehub.ttp.provider.model.ReferenceTransactionDetails
import com.fiserv.commercehub.ttp.provider.model.RefundResponse
import com.fiserv.commercehub.ttp.provider.model.TokenizationResponse
import com.fiserv.commercehub.ttp.provider.model.TransactionDetailsRequest
import com.fiserv.commercehub.ttp.provider.model.VoidResponse
import com.fiserv.commercehub.ttp.provider.util.roundUpTwoScale
import com.google.gson.Gson
import com.magiccube.acceptance.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel for managing TTP SDK test operations and UI state.
 * Handles SDK initialization, payment transactions, card operations, and logging functionality.
 */
class TestSDKMainViewModel() : ViewModel() {

    var channel = Channel<String>()
    // Token state fields
    var tokenData: MutableState<String> = mutableStateOf("")
    var tokenExpMonth: MutableState<String> = mutableStateOf("")
    var tokenExpYear: MutableState<String> = mutableStateOf("")

    // Transaction/order state fields
    var payAmount: MutableState<String> = mutableStateOf("1.0")
    var voidAmount: MutableState<String> = mutableStateOf("1.0")
    var refundPayAmount: MutableState<String> = mutableStateOf("1.0")
    var refundTransactionId: MutableState<String> = mutableStateOf("")
    var voidTransactionId: MutableState<String> = mutableStateOf("")
    var refundReferenceTransactionId: MutableState<String> = mutableStateOf("")
    var inquiryTransactionId: MutableState<String> = mutableStateOf("")
    var payReferenceTransactionId: MutableState<String> = mutableStateOf("")
    var refundOrderId: MutableState<String> = mutableStateOf("")
    var inquiryReferenceOrderId: MutableState<String> = mutableStateOf("")
    var payReferenceOrder: MutableState<String> = mutableStateOf("")
    var inquiryReferenceMerchantOrderId: MutableState<String> = mutableStateOf("")


    // UI state fields
    private val _logData = SnapshotStateList<String>()
    private var _isLogPageVisible = MutableStateFlow(false)
    private val _isLoadingInit = MutableStateFlow(false)
    private val _isLoadingReadCard= MutableStateFlow(false)
    private val _isLoadingInquiry = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _isLoadingVoid = MutableStateFlow(false)
    private val _isLoadingPay = MutableStateFlow(false)
    private val _isLoadingTokenize = MutableStateFlow(false)
    private val _isLoadingRefund = MutableStateFlow(false)
    private val _isLoadingVerifyCard = MutableStateFlow(false)
    private var _isPaymentSuccessAlert = MutableStateFlow(false)
    private var _isNFCEnabledAlert = MutableStateFlow(true)
    private var _isNFCSupportedAlert = MutableStateFlow(true)

    // Callable UI states
    val logData: SnapshotStateList<String> get() = _logData
    val isLogPageVisible: StateFlow<Boolean> = _isLogPageVisible
    val isLoadingInit: StateFlow<Boolean> = _isLoadingInit
    val isLoadingReadCard: StateFlow<Boolean> = _isLoadingReadCard
    val isLoadingInquiry: StateFlow<Boolean> = _isLoadingInquiry
    val isLoading: StateFlow<Boolean> = _isLoading
    val isLoadingVoid: StateFlow<Boolean> = _isLoadingVoid
    val isLoadingPay: StateFlow<Boolean> = _isLoadingPay
    val isLoadingTokenize: StateFlow<Boolean> = _isLoadingTokenize
    val isLoadingRefund: StateFlow<Boolean> = _isLoadingRefund
    val isLoadingVerifyCard: StateFlow<Boolean> = _isLoadingVerifyCard
    val isPaymentSuccessAlert: StateFlow<Boolean> get() = _isPaymentSuccessAlert
    val isNFCEnabledAlert: StateFlow<Boolean> get() = _isNFCEnabledAlert
    val isNFCSupportedAlert: StateFlow<Boolean> get() = _isNFCSupportedAlert

    fun visibleLogPage() {
        _isLogPageVisible.value = true
    }

    fun disableLogPage() {
        _isLogPageVisible.value = false
    }

    fun startLoadingInit() {
        _isLoadingInit.value = true
    }

    fun stopLoadingInit() {
        _isLoadingInit.value = false
    }

    fun startLoadingInquiry() {
        _isLoadingInquiry.value = true
    }

    fun stopLoadingInquiry() {
        _isLoadingInquiry.value = false
    }

    fun startLoading() {
        _isLoading.value = true
    }

    fun stopLoading() {
        _isLoading.value = false
    }

    fun startLoadingVoid() {
        _isLoadingVoid.value = true
    }

    fun stopLoadingVoid() {
        _isLoadingVoid.value = false
    }

    fun startLoadingPay() {
        _isLoadingPay.value = true
    }

    fun stopLoadingPay() {
        _isLoadingPay.value = false
    }

    fun dismiss() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            _isPaymentSuccessAlert.value = false
        }

    }

    fun startLoadingCardRead() {
        _isLoadingReadCard.value = true
    }

    fun stopLoadingCardRead() {
        _isLoadingReadCard.value = false
    }

    fun disableNFCAlert()
    {
        _isNFCEnabledAlert.value=true
    }

    fun startLoadingRefund() {
        _isLoadingRefund.value = true
    }

    fun stopLoadingRefund() {
        _isLoadingRefund.value = false
    }

    fun startLoadingTokenize() {
        _isLoadingTokenize.value = true
    }

    fun stopLoadingTokenize() {
        _isLoadingTokenize.value = false
    }


    fun startLoadingVerifyCard() {
        _isLoadingVerifyCard.value = true
    }

    fun stopLoadingVerifyCard() {
        _isLoadingVerifyCard.value = false
    }

    fun initChannel()
    {
        FiservTTPCardReader.setLoggingChannel(channel)
        listenLogChannel(channel)
    }
    fun initViewModel(
        context: Context,
        apiKey: String,
        secret: String,
        environment: String,
        currency: String,
        merchantId: String,
        terminalId: String,
        ppid: String,
        hostPort: String,
    ) {


        init(
            context = context,
            secret = secret,
            apiKey = apiKey,
            environment = environment,
            currency = currency,
            merchantId = merchantId,
            terminalId = terminalId,
            ppid = ppid,
            hostPort = hostPort
        )

    }


    private fun channelClose() {
        channel.close()
    }

    private fun listenLogChannel(channel: Channel<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            channel.consumeEach {
                it.addLog() }
        }
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

            _isLoadingInit.value = true

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
                    .onCompletion {
                        _isLoadingInit.value = false
                    }
                    .single()
                    .onSuccess { initStatus: Boolean -> }
                    .onFailure {
                        if (it is FiservTTPCardReaderException) {
                            "Type: ${it.type} Code: ${it.code} Message:  ${it.message}".addLog()
                            println("## ${it.type} Code: ${it.code} Message:  ${it.message}")
                            if (it.code=="270")
                            {
                                _isNFCEnabledAlert.value=false
                            }else if (it.code=="271")
                            {
                                _isNFCSupportedAlert.value=false
                            }
                        } else {
                            "Message: ${it.message}".addLog()
                        }
                    }
            }
        } else {
            Toast.makeText(context, "Invalid input", Toast.LENGTH_LONG).show()
        }
    }



    private fun getRefundAmountOrNull() = refundPayAmount.value.toBigDecimalOrNull()

    private fun getReferenceTransactionDetailsOrNull(): ReferenceTransactionDetails? {
        return refundReferenceTransactionId.value.toString()
            ?.takeIf { !TextUtils.isEmpty(it) }
            ?.let { ReferenceTransactionDetails(refundReferenceTransactionId.value.toString()) }
    }

    private fun getTransactionDetailsRequestForRefund() = TransactionDetailsRequest(
        true,
        refundTransactionId.value.toString(),
        refundOrderId.value.toString(),
        false
    )


    private fun getRefundType(context: Context, flag: String): RefundTransactionType {
        return if (flag == context.getString(R.string.tagged)) {
            RefundTransactionType.TAGGED
        } else {
            RefundTransactionType.OPEN
        }
    }

    /**
     * Processes a refund transaction with specified amount and type.
     * Supports tagged and open refunds.
     *
     * @param context: the application context for the transaction
     * @param refundType: the type of refund, either "TAGGED" or "OPEN"
     */
    fun onRefund(context: Context, refundType: String) {

        /*if (!TextUtils.isEmpty(refundPayAmount.value.toString()) && refundPayAmount.value.toDouble() > 0.0) {*/
        startLoadingRefund()

        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.refunds(
                amount = getRefundAmountOrNull(),
                refundTransactionType = getRefundType(context, refundType),
                transactionDetailsRequest = getTransactionDetailsRequestForRefund(),
                referenceTransactionDetails = getReferenceTransactionDetailsOrNull()
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingRefund() }
                .single()
                .onSuccess { refundResponse: RefundResponse? ->
                    // Toast.makeText(context, "Refund success", Toast.LENGTH_SHORT).show()
                    if (refundResponse!!.gatewayResponse?.transactionState.equals("FAILED")) {
                        "Refund Failed".addLog()
                        Toast.makeText(context, "Refund Process Failed", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        "Refund Success".addLog()
                        Toast.makeText(context, "Refund Success", Toast.LENGTH_SHORT)
                            .show()
                    }

                    refundResponse?.gatewayResponse?.transactionProcessingDetails?.transactionId
                        ?.apply {
                            voidTransactionId.value = this
                        }
                        ?.apply {
                            refundReferenceTransactionId.value = this
                        }
                        ?.apply {
                            inquiryTransactionId.value = this
                        }
                        ?.apply {
                            payReferenceTransactionId.value = this
                        }
                    refundResponse?.gatewayResponse?.transactionProcessingDetails?.orderId
                        ?.apply {
                            refundOrderId.value = this
                        }
                        ?.apply {
                            inquiryReferenceOrderId.value = this
                        }
                        ?.apply {
                            payReferenceOrder.value = this
                        }
                    refundResponse?.transactionDetails?.merchantOrderId
                        ?.apply {
                            inquiryReferenceMerchantOrderId.value = this
                        }
                }
                .onFailure {
                    Toast.makeText(context, "Refund Process Failed", Toast.LENGTH_SHORT).show()
                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }
        }
        /* } else {
             Toast.makeText(context, context.getString(R.string.invalidInput), Toast.LENGTH_LONG)
                 .show()
         }*/
    }

    private fun getTransactionTypeOnUserSelection(selectedOption: String): PaymentTransactionType {
        return when (selectedOption) {
            "Capture" -> PaymentTransactionType.CAPTURE
            "Auth" -> PaymentTransactionType.AUTH
            else -> PaymentTransactionType.SALE
        }
    }


    /**
     * Processes a payment transaction with specified parameters.
     * Handles Sale/Auth/Capture and tokenization.
     *
     * @param context: the application context for the transaction
     * @param transactionIdText: the transaction ID for the payment
     * @param orderIdText: the order ID for the payment
     * @param radioButtonSale: the selected transaction type (Sale/Auth/Capture)
     * @param tokenizeCard: whether to tokenize the card
     * @param paymentToken: the payment token if tokenization is enabled
     * @param paymentTokenTextExpYear: the expiration year of the tokenized card
     * @param paymentTokenTextExpMonth: the expiration month of the tokenized card
     */
    fun onPay(
        context: Context,
        transactionIdText: String,
        orderIdText: String,
        radioButtonSale: String,
        tokenizeCard: Boolean,
        paymentToken: String,
        paymentTokenTextExpYear: String,
        paymentTokenTextExpMonth: String,
    ) {

        tokenData.value=""
        tokenExpYear.value=""
        tokenExpMonth.value=""

        if (!TextUtils.isEmpty(payAmount.value.toString())
            && (payAmount.value.toString().toDouble() > 0.0)
        ) {
            startLoadingPay()

            var amount = payAmount.value.toString().toBigDecimal()
            var payReferenceTransactionIdText = payReferenceTransactionId.value
            var paymentTokenText = paymentToken

            var radioButtonCapture = false
            radioButtonCapture =
                getTransactionTypeOnUserSelection(radioButtonSale) == PaymentTransactionType.CAPTURE


            CoroutineScope(Dispatchers.Main).launch {
                FiservTTPCardReader.charges(
                    amount,
                    getTransactionTypeOnUserSelection(radioButtonSale),
                    TransactionDetailsRequest(
                        captureFlag = radioButtonCapture,
                        merchantTransactionId = transactionIdText.toString(),
                        merchantOrderId = orderIdText.toString(),
                        createToken = tokenizeCard
                    ),
                    payReferenceTransactionIdText
                        ?.takeIf { !TextUtils.isEmpty(it) }
                        ?.let {
                            ReferenceTransactionDetails().apply {
                                referenceTransactionId = it
                            }
                        },

                    paymentTokenText.toString()
                        ?.takeIf { !TextUtils.isEmpty(it) }
                        ?.let {

                            PaymentToken()
                                .apply { tokenData = it }
                                .apply { tokenSource = "TRANSARMOR" }
                                .apply {
                                    card = Card()
                                        .apply {
                                            expirationYear = paymentTokenTextExpYear.toString()
                                        }
                                        .apply {
                                            expirationMonth = paymentTokenTextExpMonth.toString()
                                        }
                                }

                        }).flowOn(Dispatchers.IO)
                    .onCompletion {
                        stopLoadingPay()
                    }
                    .single()
                    .onSuccess { chargesResponse: ChargesResponse? ->
                        postSuccess()
                        chargesResponse?.gatewayResponse?.transactionProcessingDetails?.transactionId
                            ?.apply {

                                voidTransactionId.value = this
                            }
                            ?.apply {
                                refundReferenceTransactionId.value = this

                            }
                            ?.apply {
                                inquiryTransactionId.value = this

                            }
                            ?.apply {
                                payReferenceTransactionId.value = this

                            }
                        chargesResponse?.gatewayResponse?.transactionProcessingDetails?.orderId
                            ?.apply {

                                refundOrderId.value = this
                            }
                            ?.apply {
                                inquiryReferenceOrderId.value = this

                            }
                            ?.apply {
                                payReferenceOrder.value = this

                            }
                        chargesResponse?.transactionDetails?.merchantOrderId
                            ?.apply {
                                inquiryReferenceMerchantOrderId.value = this
                            }
                    }
                    .onFailure {
                        Toast.makeText(context, "Payment Process Failed", Toast.LENGTH_SHORT).show()
                        if (it is FiservTTPCardReaderException) {
                            "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                        } else {
                            "Message: ${it.message}".addLog()
                        }
                    }
            }
        } else {
            Toast.makeText(context, context.getString(R.string.invalidInput), Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun postSuccess() {
        _isPaymentSuccessAlert.value = true
        // Show Alert message on main screen like payment completed
    }

    /**
     * Voids/cancels the current transaction.
     *
     * @param context: the application context for messaging
     */
    fun voidTransaction(context: Context) {

        if (!TextUtils.isEmpty(voidAmount.value.toString())
            && !TextUtils.isEmpty(voidTransactionId.value)
            && (voidAmount.value.toString().toDouble() >= 0.0)
        ) {
            startLoadingVoid()

            CoroutineScope(Dispatchers.Main).launch {
                FiservTTPCardReader.cancels(
                    voidAmount.value.toString().toBigDecimalOrNull(),
                    ReferenceTransactionDetails(voidTransactionId.value)
                ).flowOn(Dispatchers.IO)
                    .onCompletion { stopLoadingVoid() }
                    .single()
                    .onSuccess { voidResponse: VoidResponse? ->

                        Toast.makeText(context, "Void Transaction Success", Toast.LENGTH_SHORT)
                            .show()

                        voidResponse?.gatewayResponse?.transactionProcessingDetails?.transactionId
                            ?.apply {
                                voidTransactionId.value = this
                            }
                            ?.apply {
                                refundReferenceTransactionId.value = this
                            }
                            ?.apply {
                                inquiryTransactionId.value = this
                            }
                            ?.apply {
                                payReferenceTransactionId.value = this
                            }
                        voidResponse?.gatewayResponse?.transactionProcessingDetails?.orderId
                            ?.apply {
                                refundOrderId.value = this
                            }
                            ?.apply {
                                inquiryReferenceOrderId.value = this
                            }
                            ?.apply {
                                payReferenceOrder.value = this
                            }
                        voidResponse?.transactionDetails?.merchantOrderId
                            ?.apply {
                                inquiryReferenceMerchantOrderId.value = this
                            }
                    }
                    .onFailure {
                        Toast.makeText(context, "Void Transaction Process Failed", Toast.LENGTH_SHORT)
                            .show()
                        if (it is FiservTTPCardReaderException) {
                            "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                        } else {
                            "Message: ${it.message}".addLog()
                        }
                    }
            }
        } else {
            Toast.makeText(context, context.getString(R.string.invalidInput), Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * Performs transaction inquiry.
     * Retrieves transaction status and details.
     *
     * @param context: the application context for messaging
     */
    fun inquiryTransaction(context: Context) {

        if (!TextUtils.isEmpty(inquiryTransactionId.value)
            && !TextUtils.isEmpty(inquiryReferenceOrderId.value)

        ) {
            startLoadingInquiry()

            CoroutineScope(Dispatchers.Main).launch {
                FiservTTPCardReader.transactionInquiryFlow(
                    referenceTransactionDetails = ReferenceTransactionDetails(
                        referenceTransactionId = inquiryTransactionId.value.toString(),
                        referenceOrderId = inquiryReferenceOrderId.value.toString(),
                        referenceMerchantOrderId = inquiryReferenceMerchantOrderId.value.toString()
                    )
                ).flowOn(Dispatchers.IO)
                    .onCompletion { stopLoadingInquiry() }
                    .collect {
                        it.onSuccess { transactionInquiryResponseList ->
                            ("Size of List = " + transactionInquiryResponseList.size.toString()).addLog()
                        }
                        it.onFailure {
                            if (it is FiservTTPCardReaderException) {
                                "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                            } else {
                                "Message: ${it.message}".addLog()
                            }
                        }
                    }
            }
        } else {
            Toast.makeText(context, context.getString(R.string.invalidInput), Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * Performs card account verification.
     * Validates card details without processing payment.
     *
     * @param context: the application context for messaging
     * @param verificationTransactionIdText: the transaction ID for verification
     * @param verificationOrderId: the order ID for verification
     */
    fun onAccountVerificationCardClick(
        context: Context,
        verificationTransactionIdText: String,
        verificationOrderId: String,
    ) {


        startLoadingVerifyCard()
        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.accountVerification(
                TransactionDetailsRequest(
                    false,
                    verificationTransactionIdText.toString(),
                    verificationOrderId.toString(),
                    false
                ), null
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingVerifyCard() }
                .single()
                .onSuccess { accountVerificationResponse: AccountVerificationResponse? -> }
                .onFailure {
                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }
        }
    }


    /**
     * Calls card reader using NFC/contactless interface.
     * Captures card data for payment processing.
     */
    fun readCardDetails() {

        startLoadingCardRead()
        var amount = payAmount.value.toString().toBigDecimal()

        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.readCardDetails(
                amount.roundUpTwoScale(),
                TransactionType.PURCHASE
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingCardRead() }
                .single()
                .onSuccess { readCardDetailsResponse: ReadCardDetailsResponse ->
                    "Response : ${Gson().toJson(readCardDetailsResponse)}".addLog()
                }
                .onFailure {
                    if (it is FiservTTPCardReaderException) {
                        "Type: ${it.type} Code: ${it.code} Message:  ${it.message} Additional Info: ${it.additionalInfo}".addLog()
                    } else {
                        "Message: ${it.message}".addLog()
                    }
                }



        }

    }

    /**
     * Tokenizes card details for secure storage.
     * Generates payment token for future transactions.
     */
    fun onTokenizeCardClick(
        context: Context,
        tokenizeTransactionIdText: String,
        tokenizeOrderIdText: String,
    ) {


        startLoadingTokenize()
        CoroutineScope(Dispatchers.Main).launch {
            FiservTTPCardReader.tokens(
                TransactionDetailsRequest(
                    true,
                    tokenizeTransactionIdText.toString(),
                    tokenizeOrderIdText.toString(),
                    true
                )
            ).flowOn(Dispatchers.IO)
                .onCompletion { stopLoadingTokenize() }
                .single()
                .onSuccess { tokenizationResponse: TokenizationResponse? ->
                    tokenData.value= tokenizationResponse?.paymentTokens?.get(0)?.tokenData.let { it }?:""
                    tokenExpMonth.value= tokenizationResponse?.source?.card?.expirationMonth.let { it }?:""
                    tokenExpYear.value= tokenizationResponse?.source?.card?.expirationYear.let { it }?:""

                }
                .onFailure {
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
}