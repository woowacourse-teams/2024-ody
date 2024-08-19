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
            content = Content(
                title = imageShareContent.title,
                description = imageShareContent.description,
                imageUrl = imageShareContent.imageUrl,
                link = link,
            ),
            buttons = listOf(
                Button(
                    imageShareContent.buttonTitle,
                    link,
                )
            )
        )
    }

    private fun shareFeedByApp(feed: FeedTemplate): ApiResult<Unit> {
        var isSuccess = false
        ShareClient.instance.shareDefault(context, feed) { sharingResult, error ->
            if (error != null) {
                return@shareDefault
            } else if (sharingResult != null) {
                isSuccess = true
                context.startActivity(sharingResult.intent)
            }
        }

        if (isSuccess) ApiResult.Success(Unit)
        return ApiResult.Unexpected(Throwable())
    }

    private fun shareFeedByWeb(feed: FeedTemplate): ApiResult<Unit> {
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(feed)
        return try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
            ApiResult.Success(Unit)
        } catch (e: UnsupportedOperationException) {
            ApiResult.Unexpected(Throwable())
        }
    }
}
