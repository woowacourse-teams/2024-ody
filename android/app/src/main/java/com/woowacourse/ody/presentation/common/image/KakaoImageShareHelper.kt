package com.woowacourse.ody.presentation.common.image

import android.content.Context
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.woowacourse.ody.domain.apiresult.ApiResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoImageShareHelper(private val context: Context) : ImageShareHelper {
    override suspend fun share(imageShareContent: ImageShareContent): ApiResult<Unit> {
        val feed = createFeedTemplate(imageShareContent)
        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            return shareFeedByApp(feed)
        }
        return shareFeedByWeb(feed)
    }

    private fun createFeedTemplate(imageShareContent: ImageShareContent): FeedTemplate {
        val link: Link =
            Link(
                webUrl = imageShareContent.link,
                mobileWebUrl = imageShareContent.link,
            )
        return FeedTemplate(
            content =
            Content(
                title = imageShareContent.description,
                imageUrl = imageShareContent.imageUrl,
                imageWidth = imageShareContent.imageWidthPixel,
                imageHeight = imageShareContent.imageHeightPixel,
                link = link,
            ),
            buttons =
            listOf(
                Button(
                    imageShareContent.buttonTitle,
                    link,
                ),
            ),
        )
    }

    private suspend fun shareFeedByApp(feed: FeedTemplate): ApiResult<Unit> {
        return suspendCoroutine { continuation ->
            ShareClient.instance.shareDefault(context, feed) { sharingResult, error ->
                if (error != null) {
                    continuation.resume(ApiResult.Unexpected(Throwable()))
                } else if (sharingResult != null) {
                    context.startActivity(sharingResult.intent)
                    continuation.resume(ApiResult.Success(Unit))
                }
            }
        }
    }

    private suspend fun shareFeedByWeb(feed: FeedTemplate): ApiResult<Unit> {
        return suspendCoroutine { continuation ->
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(feed)
            try {
                KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
                continuation.resume(ApiResult.Success(Unit))
            } catch (e: UnsupportedOperationException) {
                continuation.resume(ApiResult.Unexpected(Throwable()))
            }
        }
    }
}
