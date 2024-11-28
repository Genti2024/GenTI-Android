package kr.genti.presentation.create.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType.INAPP
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
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

class BillingManager(private val activity: Activity, private val callback: BillingCallback) {

    private lateinit var billingClient: BillingClient
    private lateinit var purchasesUpdatedListener: PurchasesUpdatedListener

    private var inAppProductDetails: ProductDetails? = null

    init {
        initPurchasesUpdatedListener()
        initBillingClient()
    }

    private fun initBillingClient() {
        val pendingPurchasesParams =
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        billingClient =
            BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases(pendingPurchasesParams)
                .build()
                .apply { connectBillingClient() }
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
            Product.newBuilder()
                .setProductId(PRODUCT_GENTI_PAID)
                .setProductType(INAPP)
                .build()
        inAppProductDetails =
            billingClient.queryProductDetails(
                QueryProductDetailsParams.newBuilder()
                    .setProductList(listOf(inAppProduct))
                    .build()
            ).productDetailsList?.firstOrNull()
    }

    fun purchaseProduct() {
        inAppProductDetails?.let { productDetail ->
            val offerToken =
                productDetail.subscriptionOfferDetails?.getOrNull(0)?.offerToken
            val productDetailsParams =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetail)
                    .setOfferToken(offerToken.orEmpty())
                    .build()
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productDetailsParams))
                    .build()
            billingClient.launchBillingFlow(activity, billingFlowParams).apply {
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    callback.onBillingFailure(responseCode)
                }
            }
        }
    }

    private fun initPurchasesUpdatedListener() {
        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) confirmPurchase(purchase)
            } else {
                callback.onBillingFailure(billingResult.responseCode)
            }
        }
    }

    companion object {
        const val PRODUCT_GENTI_PAID = "genti_paid_picture"
        const val PRICE_PAID = "3300"
    }
}