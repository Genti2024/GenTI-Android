package kr.genti.presentation.create.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType.INAPP
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class BillingManager(private val context: Context, private val callback: BillingCallback) {

    private lateinit var billingClient: BillingClient
    private var purchasesUpdatedListener: PurchasesUpdatedListener? = null

    private var inAppProductDetails: ProductDetails? = null

    init {
        initBillingClient()
    }

    private fun initBillingClient() {
        // TODO : 추후 purchasesUpdatedListener 등록
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        ).build()
            .apply {
                connectBillingClient()
            }
    }

    private fun connectBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Timber.d("Billing Service Disconnected")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    CoroutineScope(Dispatchers.IO).launch {
                        getInAppProductDetails()
                    }
                } else {
                    callback.onBillingFailure(billingResult.responseCode)
                }
            }
        })
    }

    private suspend fun getInAppProductDetails() {
        val inAppProduct =
            Product.newBuilder().setProductId(PRODUCT_GENTI_PAID).setProductType(INAPP).build()
        inAppProductDetails = billingClient.queryProductDetails(
            QueryProductDetailsParams.newBuilder().setProductList(listOf(inAppProduct)).build()
        ).productDetailsList?.firstOrNull()
    }

    companion object {
        const val PRODUCT_GENTI_PAID = "genti_paid_picture"
        const val PRICE_PAID = "3300"
    }
}