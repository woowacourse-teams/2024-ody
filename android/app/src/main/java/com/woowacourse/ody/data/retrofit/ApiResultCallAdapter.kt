package com.woowacourse.ody.data.retrofit

import com.woowacourse.ody.domain.apiresult.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapter<R : Any>(private val responseType: Type) : CallAdapter<R, Call<ApiResult<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Call<ApiResult<R>> {
        return ApiResultCall(call)
    }

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit,
        ): CallAdapter<*, *>? {
            if (returnType == ApiResult::class.java) {
                return null
            }
            check(returnType is ParameterizedType) {
                "return type must be parameterized as Call<ApiResult<Foo>> or Call<ApiResult<out Foo>>"
            }
            val responseType = getParameterUpperBound(0, returnType)

            if (getRawType(responseType) != ApiResult::class.java) {
                return null
            }

            check(responseType is ParameterizedType) {
                "return type must be parameterized as ApiResult<Foo> or ApiResult<out Foo>"
            }

            val successBodyType = getParameterUpperBound(0, responseType)

            return ApiResultCallAdapter<Any>(successBodyType)
        }
    }
}
