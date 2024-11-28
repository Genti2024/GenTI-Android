package kr.genti.presentation.create.billing

import com.android.billingclient.api.Purchase

interface BillingCallback {
    fun onBillingClientConnected()
    fun onSuccess(purchase: Purchase)
    fun onFailure(responseCode: Int)
}