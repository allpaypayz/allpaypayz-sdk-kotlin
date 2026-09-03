package com.allpaypayz.sdk.kotlin

import com.allpaypayz.sdk.Webhooks as JavaWebhooks
import com.allpaypayz.sdk.WebhookDispatcher as JavaWebhookDispatcher
import com.allpaypayz.sdk.exception.AllpaypayzWebhookException
import java.time.Clock

/**
 * Kotlin-idiomatic facade over [com.allpaypayz.sdk.Webhooks].
 *
 * ```kotlin
 * val event = verifyWebhook(rawBody, signatureHeader, signKey)
 * ```
 */
public fun verifyWebhook(
    rawBody: ByteArray,
    signatureHeader: String,
    signKey: String,
    toleranceSeconds: Int = 300,
    clock: Clock = Clock.systemUTC(),
): Map<String, Any?> = JavaWebhooks.verify(rawBody, signatureHeader, signKey, toleranceSeconds, clock)

public class WebhookDispatcher {
    private val handlers: MutableMap<String, (Map<String, Any?>) -> Unit> = mutableMapOf()

    public fun on(eventType: String, handler: (Map<String, Any?>) -> Unit): WebhookDispatcher {
        handlers[eventType] = handler
        return this
    }

    public fun dispatch(event: Map<String, Any?>) {
        val type = event["type"] as? String ?: return
        handlers[type]?.invoke(event)
    }
}

/** Re-exported for convenience so callers can match on AllpaypayzWebhookException. */
public typealias WebhookException = AllpaypayzWebhookException

/** Re-exported so the Kotlin module can be a drop-in replacement. */
public typealias JavaDispatcher = JavaWebhookDispatcher
