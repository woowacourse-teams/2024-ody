package com.woowacourse.ody.presentation.meetinglog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityLogsBinding

class LogsActivity : AppCompatActivity(), CodeCopyListener, LogsListener {
    private val binding: ActivityLogsBinding by lazy {
        ActivityLogsBinding.inflate(layoutInflater)
    }
    private val bottomSheetLayout by lazy { findViewById<ConstraintLayout>(R.id.cl_bottom_sheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
        initializePersistentBottomSheet()
    }

    override fun copy() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val inviteCode = "ABCD1234"
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(INVITE_CODE_LABEL, inviteCode)
        clipboard.setPrimaryClip(clip)

        showSnackBar(R.string.logs_copy_code_guide)
    }

    override fun share() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initializeDataBinding() {
        binding.logsListener = this
        binding.codeCopyListener = this
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
    }

    private fun showSnackBar(messageId: Int) =
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .apply {
                // 날짜 위에 나오게 변경 setAnchorView(binding.tvDate)
            }
            .show()

    companion object {
        private const val INVITE_CODE_LABEL = "inviteCode"
    }
}
