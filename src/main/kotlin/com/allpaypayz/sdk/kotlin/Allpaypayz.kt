@file:JvmName("AllpaypayzKt")

package com.allpaypayz.sdk.kotlin

import com.allpaypayz.sdk.Allpaypayz
import com.allpaypayz.sdk.http.RetryOptions
import com.allpaypayz.sdk.resources.OrdersResource
import com.allpaypayz.sdk.resources.P2PTransfersResource
import com.allpaypayz.sdk.resources.PaymentsResource
import com.allpaypayz.sdk.resources.PayoutsResource
import com.allpaypayz.sdk.resources.TerminalResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration

/**
 * DSL builder for [Allpaypayz] — usage:
 *
 * ```kotlin
 * val client = allpaypayz {
 *     apiKey = System.getenv("ALLPAYPAYZ_API_KEY")
 *     baseUrl = "https://staging-api4.allpaypayz.com"
 * }
 * ```
 */
public class AllpaypayzBuilder {
    public var apiKey: String? = null
    public var baseUrl: String? = null
    public var apiVersion: String? = null
    public var userAgent: String? = null
    public var requestTimeout: Duration? = null
    public var retry: RetryOptions? = null
    public var httpClient: java.net.http.HttpClient? = null

    public fun build(): Allpaypayz {
        val b = Allpaypayz.builder().apiKey(requireNotNull(apiKey) { "apiKey is required" })
        baseUrl?.let { b.baseUrl(it) }
        apiVersion?.let { b.apiVersion(it) }
        userAgent?.let { b.userAgent(it) }
        requestTimeout?.let { b.requestTimeout(it) }
        retry?.let { b.retry(it) }
        httpClient?.let { b.httpClient(it) }
        return b.build()
    }
}

/** DSL entry point — `val client = allpaypayz { apiKey = "..." }`. */
public fun allpaypayz(configure: AllpaypayzBuilder.() -> Unit): Allpaypayz =
    AllpaypayzBuilder().apply(configure).build()

// --- Suspend wrappers around the synchronous Java surface.
//
// Each maps 1:1 to the underlying Java SDK method, dispatched on
// Dispatchers.IO so the caller's coroutine isn't blocked. Method names match
// the Java SDK; the package-level functions add coroutine-friendly versions.

public suspend fun PaymentsResource.createSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { create(body, idempotencyKey) }

public suspend fun PaymentsResource.createRedirectSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { createRedirect(body, idempotencyKey) }

public suspend fun PaymentsResource.recurrentSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { recurrent(body, idempotencyKey) }

public suspend fun PaymentsResource.finish3dsSuspend(id: String, body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { finish3ds(id, body, idempotencyKey) }

public suspend fun PaymentsResource.getSuspend(id: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { get(id) }

public suspend fun PaymentsResource.findByReferenceSuspend(merchantReference: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { findByReference(merchantReference) }

public suspend fun PaymentsResource.createRefundSuspend(paymentId: String, body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { createRefund(paymentId, body, idempotencyKey) }

public suspend fun PaymentsResource.getRefundSuspend(paymentId: String, refundId: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { getRefund(paymentId, refundId) }

public suspend fun PayoutsResource.createSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { create(body, idempotencyKey) }

public suspend fun PayoutsResource.getSuspend(id: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { get(id) }

public suspend fun PayoutsResource.findByReferenceSuspend(merchantReference: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { findByReference(merchantReference) }

public suspend fun P2PTransfersResource.createSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { create(body, idempotencyKey) }

public suspend fun P2PTransfersResource.confirmSuspend(id: String, body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { confirm(id, body, idempotencyKey) }

public suspend fun P2PTransfersResource.getSuspend(id: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { get(id) }

public suspend fun P2PTransfersResource.findByReferenceSuspend(merchantReference: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { findByReference(merchantReference) }

public suspend fun OrdersResource.createSuspend(body: Map<String, Any?>, idempotencyKey: String? = null): Map<String, Any?> =
    withContext(Dispatchers.IO) { create(body, idempotencyKey) }

public suspend fun OrdersResource.getSuspend(id: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { get(id) }

public suspend fun OrdersResource.findByReferenceSuspend(merchantReference: String): Map<String, Any?> =
    withContext(Dispatchers.IO) { findByReference(merchantReference) }

public suspend fun TerminalResource.getSuspend(): Map<String, Any?> =
    withContext(Dispatchers.IO) { get() }
