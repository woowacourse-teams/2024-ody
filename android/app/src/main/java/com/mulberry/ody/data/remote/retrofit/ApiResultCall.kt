package com.mulberry.ody.data.remote.retrofit

import com.mulberry.ody.data.remote.core.entity.ErrorResponse
import com.mulberry.ody.domain.apiresult.ApiResult
import kotlinx.serialization.json.Json
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ApiResultCall<T>(private val call: Call<T>) : Call<ApiResult<T>> {
    override fun clone(): Call<ApiResult<T>> = ApiResultCall(call.clone())

    override fun execute(): Response<ApiResult<T>> {
        throw UnsupportedOperationException("ApiResultCall은 execute를 지원하지 않습니다.")
    }

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        call.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    if (response.isSuccessful) {
                        val body: T = response.body() ?: Unit as T

                        callback.onResponse(
                            this@ApiResultCall,
                            Response.success(ApiResult.Success(body)),
                        )
                    } else {
                        val json =
                            Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            }
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = errorBody?.let { Json.decodeFromString<ErrorResponse>(it) }

                        callback.onResponse(
                            this@ApiResultCall,
                            Response.success(ApiResult.Failure(errorResponse?.status, errorResponse?.detail)),
                        )
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    val networkResponse =
                        when (t) {
                            is IOException -> ApiResult.NetworkError(t)
                            else -> ApiResult.Unexpected(t)
                        }
                    callback.onResponse(this@ApiResultCall, Response.success(networkResponse))
                }
            },
        )
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}
