package com.woowacourse.ody.presentation.common.capture

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import com.woowacourse.ody.domain.apiresult.ApiResult
import java.io.IOException

class FirebaseImageStorage : ImageStorage {
    private val storageReference: StorageReference by lazy { FirebaseStorage.getInstance().reference }

    override suspend fun upload(
        byteArray: ByteArray,
        fileName: String
    ): ApiResult<String> {
        val imageRef = storageReference.child("images/$fileName")

        return try {
            imageRef.putBytes(byteArray).await()
            val downloadUrl = imageRef.downloadUrl.await()
            ApiResult.Success(downloadUrl.toString())
        } catch (exception: IOException) {
            ApiResult.NetworkError(exception)
        } catch (exception: Exception) {
            ApiResult.Unexpected(exception)
        }
    }
}
