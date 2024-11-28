package kr.genti.presentation.create.billing

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
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

    /**
     * BillingClient 초기화 및 PurchasesUpdatedListener 등록
     */
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

    /**
     * BillingClient 상태 확인 후 서비스와 연결
     */
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

    /**
     * Google Play 콘솔에서 등록된 상품 정보 조회 및 저장
     */
    private suspend fun getInAppProductDetails() {
        val inAppProduct =
            Product.newBuilder()
                .setProductId(PRODUCT_GENTI_PAID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        val productDetails =
            billingClient.queryProductDetails(
                QueryProductDetailsParams.newBuilder()
                    .setProductList(listOf(inAppProduct))
                    .build()
            ).productDetailsList?.firstOrNull()
        if (productDetails == null) {
            callback.onBillingFailure(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)
        } else {
            inAppProductDetails = productDetails
        }
    }

    /**
     * 지정된 상품에 대해 Google Play 결제 UI 호출 및 결제 요청 실행
     */
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

    /**
     * 결제 상태 변경 이벤트를 처리하는 리스너 초기화
     */
    private fun initPurchasesUpdatedListener() {
        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) confirmPurchase(purchase)
            } else {
                callback.onBillingFailure(billingResult.responseCode)
            }
        }
    }

    /**
     * 구매 상태 확인 후, 완료되었지만 승인되지 않은 경우 승인 처리
     */
    private fun confirmPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val ackPurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            CoroutineScope(Dispatchers.Main).launch {
                billingClient.acknowledgePurchase(ackPurchaseParams) {
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        consumePurchase(purchase)
                    } else {
                        callback.onBillingFailure(it.responseCode)
                    }
                }
            }
        }
    }

    /**
     * 이후 재구매 위해, 소비성 아이템의 소비 완료 표사
     */
    private fun consumePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                checkConsumable(purchase)
            } else {
                callback.onBillingFailure(billingResult.responseCode)
            }
        }
    }

    /**
     * 구매 표시 안된 소비성 아이템 찾아 소비 완료 다시 실행, 모두 완료 확인 후 성공 콜백 전송
     */
    private fun checkConsumable(purchasedItem: Purchase) {
        val queryPurchasesParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchaseList.isNotEmpty()) {
                    purchaseList.forEach { purchase -> consumePurchase(purchase) }
                } else {
                    callback.onBillingSuccess(purchasedItem)
                }
            } else {
                callback.onBillingFailure(billingResult.responseCode)
            }
        }
    }

    companion object {
        const val PRODUCT_GENTI_PAID = "genti_paid_picture"
    }
}