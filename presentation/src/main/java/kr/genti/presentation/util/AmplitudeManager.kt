package kr.genti.presentation.util

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.events.Identify

object AmplitudeManager {
    private lateinit var amplitude: Amplitude

    fun init(
        context: Context,
        apiKey: String,
    ) {
        amplitude =
            Amplitude(
                Configuration(
                    apiKey = apiKey,
                    context = context.applicationContext,
                ),
            )
    }

    fun trackEvent(
        eventName: String,
        properties1: Map<String, Any>? = null,
        properties2: Map<String, Any>? = null,
    ) {
        when {
            properties1 == null && properties2 == null -> {
                amplitude.track(eventName)
            }

            properties1 != null && properties2 == null -> {
                amplitude.track(eventName, properties1)
            }

            properties1 != null && properties2 != null -> {
                val combinedProperties = mutableMapOf<String, Any?>()
                combinedProperties.putAll(properties1)
                combinedProperties.putAll(properties2)
                amplitude.track(eventName, combinedProperties)
            }
        }
    }

    fun updateStringProperties(
        propertyName: String,
        values: String,
    ) {
        amplitude.identify(Identify().set(propertyName, values))
    }

    fun updateIntProperties(
        propertyName: String,
        intValues: Int,
    ) {
        amplitude.identify(Identify().set(propertyName, intValues))
    }

    fun updateBooleanProperties(
        propertyName: String,
        booleanValue: Boolean,
    ) {
        amplitude.identify(Identify().set(propertyName, booleanValue))
    }

    fun plusIntProperties(propertyName: String) {
        amplitude.identify(Identify().add(propertyName, 1))
    }

    const val EVENT_CLICK_BTN = "click_button"

    const val PROPERTY_PAGE = "page_name"
    const val PROPERTY_BTN = "button_name"
    const val PROPERTY_TYPE = "picdone_type"

    const val TYPE_ORIGINAL = "original"
    const val TYPE_PARENT = "parents"
}
