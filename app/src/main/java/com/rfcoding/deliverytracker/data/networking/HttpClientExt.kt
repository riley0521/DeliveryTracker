package com.rfcoding.deliverytracker.data.networking

import com.rfcoding.deliverytracker.domain.DataError
import com.rfcoding.deliverytracker.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified Request, reified Response: Any> HttpClient.post(
    route: String,
    body: Request
): Result<Response, DataError.Remote> {
    val res = post(
        urlString = createRoute(route),
        block = {
            setBody(body)
        }
    )

    return responseToResult(res)
}

suspend inline fun <reified Response: Any> HttpClient.get(
    route: String,
    queryParams: Map<String, Any> = mapOf()
): Result<Response, DataError.Remote> {
    val res = get(
        urlString = createRoute(route),
        block = {
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    )

    return responseToResult(res)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body())
            } catch (_: NoTransformationFoundException) {
                Result.Failure(DataError.Remote.SERIALIZATION)
            }
        }
        404 -> Result.Failure(DataError.Remote.NOT_FOUND)
        else -> Result.Failure(DataError.Remote.UNKNOWN)
    }
}

fun createRoute(route: String): String {
    val baseUrl = "https://6a7acaa08c69b3eb4a17864b.mockapi.io/api"

    return when {
        route.startsWith(baseUrl) -> route
        route.startsWith("/") -> "$baseUrl$route"
        else -> "$baseUrl/$route"
    }
}