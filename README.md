# `com.allpaypayz:sdk-kotlin` (Kotlin)

**[⬇ Download the latest version](https://github.com/allpaypayz/allpaypayz-sdk-kotlin/archive/refs/heads/main.zip)** · [Browse the code](https://github.com/allpaypayz/allpaypayz-sdk-kotlin) · [MIT](LICENSE)

<sub>The archive is a snapshot of `main` — the current state of the SDK. Tagged releases will appear on the Releases page once the code leaves alpha.</sub>


Kotlin-idiomatic facade on top of [`com.allpaypayz:sdk`](https://github.com/allpaypayz/allpaypayz-sdk-java) — adds
a DSL builder for client construction, `suspend` wrappers for every method,
and a Kotlin webhook dispatcher.

> Status: **alpha** (v0.1.0). Requires Kotlin 2.0+, Java 17+.

## Install

### Maven

```xml
<dependency>
  <groupId>com.allpaypayz</groupId>
  <artifactId>sdk-kotlin</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle

```kotlin
implementation("com.allpaypayz:sdk-kotlin:0.1.0")
```

Transitively pulls in `com.allpaypayz:sdk` (Java SDK) and
`kotlinx-coroutines-core`.

## Quick start

```kotlin
import com.allpaypayz.sdk.kotlin.*
import kotlinx.coroutines.runBlocking

val client = allpaypayz {
    apiKey = System.getenv("ALLPAYPAYZ_API_KEY")
}

// Sync (delegates to Java SDK directly):
val payment = client.payments().create(mapOf(
    "merchant_reference" to "ORDER-77",
    "amount" to mapOf("amount_minor" to 1000, "currency" to "USD"),
    "card" to mapOf("pan" to "4111111111111111", "exp_month" to 12, "exp_year" to 2029, "cvc" to "123"),
))

// Suspend (coroutine-friendly):
runBlocking {
    val p = client.payments().createSuspend(mapOf(
        "merchant_reference" to "ORDER-78",
        "amount" to mapOf("amount_minor" to 1000, "currency" to "USD"),
        "card" to mapOf("pan" to "4111111111111111", "exp_month" to 12, "exp_year" to 2029, "cvc" to "123"),
    ))
    println(p["id"])
}
```

## DSL builder

```kotlin
val client = allpaypayz {
    apiKey = "sk_test_..."
    baseUrl = "https://staging-api4.allpaypayz.com"
    apiVersion = "2026-05-20"
    userAgent = "MyApp/2.0"
    requestTimeout = Duration.ofSeconds(30)
    retry = RetryOptions(3, Duration.ofMillis(250), Duration.ofSeconds(4), Duration.ofMillis(250))
}
```

## Suspend functions

Every Java SDK method has a `*Suspend` extension defined on the resource
class. The body runs on `Dispatchers.IO` so the caller's coroutine stays
non-blocking.

| Resource | Methods (suspend) |
|---|---|
| `client.payments()` | `createSuspend`, `createRedirectSuspend`, `recurrentSuspend`, `finish3dsSuspend`, `getSuspend`, `findByReferenceSuspend`, `createRefundSuspend`, `getRefundSuspend` |
| `client.payouts()`  | `createSuspend`, `getSuspend`, `findByReferenceSuspend` |
| `client.p2p()`      | `createSuspend`, `confirmSuspend`, `getSuspend`, `findByReferenceSuspend` |
| `client.orders()`   | `createSuspend`, `getSuspend`, `findByReferenceSuspend` |
| `client.terminal()` | `getSuspend` |

## Errors

All errors come from the Java SDK — see
[sdk-java/README.md](https://github.com/allpaypayz/allpaypayz-sdk-java) for the full hierarchy
(`AllpaypayzException` + 7 typed subclasses, all in
`com.allpaypayz.sdk.exception`).

```kotlin
try {
    client.payments().createSuspend(req)
} catch (e: ConflictException) {
    if (e.errorCode == "duplicate_reference") {
        // merchant_reference already used on this terminal
    }
}
```

## Webhooks

```kotlin
import com.allpaypayz.sdk.kotlin.*

val dispatcher = WebhookDispatcher()
    .on("payment.succeeded") { event ->
        @Suppress("UNCHECKED_CAST")
        val res = event["resource"] as Map<String, Any?>
        markOrderPaid(res["merchant_reference"] as String)
    }

post("/webhooks/allpaypayz") { req, res ->
    try {
        val event = verifyWebhook(
            req.body.readAllBytes(),
            req.header("Callback-Signature"),
            System.getenv("ALLPAYPAYZ_SIGN_KEY"),
        )
        dispatcher.dispatch(event)
        res.status(200)
    } catch (e: WebhookException) {
        res.status(400).send(e.code)
    }
}
```

`verifyWebhook` is a thin Kotlin wrapper around
`com.allpaypayz.sdk.Webhooks.verify` — same constant-time HMAC check, same
300 s tolerance window, returns the same `Map<String, Any?>`.

## Tests

```bash
mvn test
```

`WebhooksTest.kt` runs the spec-vector contract against the Kotlin facade,
`ClientTest.kt` boots the JDK-built-in `HttpServer` to exercise both the
sync and `*Suspend` paths.

## License

MIT
