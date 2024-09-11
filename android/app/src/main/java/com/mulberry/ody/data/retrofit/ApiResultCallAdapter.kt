package com.mulberry.ody.data.retrofit

import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Call<ApiResult<R>>> {
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
                "리턴 타입은 Call<ApiResult<Foo>> 또는 Call<ApiResult<out Foo>>로 매개변수화 되어야 합니다."
            }
            val responseType = getParameterUpperBound(0, returnType)

            if (getRawType(responseType) != ApiResult::class.java) {
                return null
            }

            check(responseType is ParameterizedType) {
                "리턴 타입은 ApiResult<Foo> 또는 ApiResult<out Foo>로 매개변수화되어야 합니다."
            }

            val successBodyType = getParameterUpperBound(0, responseType)

            return ApiResultCallAdapter<Any>(successBodyType)
        }
    }
}
