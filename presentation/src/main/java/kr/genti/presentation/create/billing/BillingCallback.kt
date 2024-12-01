package kr.genti.presentation.create.billing

import com.android.billingclient.api.Purchase

interface BillingCallback {
    fun onBillingSuccess(purchase: Purchase)
    fun onBillingFailure(responseCode: Int)
}