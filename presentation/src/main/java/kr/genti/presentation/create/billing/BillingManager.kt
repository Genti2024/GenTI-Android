package kr.genti.presentation.create.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import timber.log.Timber

class BillingManager(private val context: Context, private val callback: BillingCallback) {

    private lateinit var billingClient: BillingClient
    private var purchasesUpdatedListener: PurchasesUpdatedListener? = null

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
                    // 상품 정보 받아오는 함수 실행
                } else {
                    callback.onBillingFailure(billingResult.responseCode)
                }
            }
        })
    }

}