package kr.genti.presentation.main.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseBottomSheet
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.BottomSheetFeedInfoBinding

@AndroidEntryPoint
class FeedInfoBottomSheet :
    BaseBottomSheet<BottomSheetFeedInfoBinding>(R.layout.bottom_sheet_feed_info) {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initMoreBtnListener()

    }

    private fun initMoreBtnListener() {
        binding.btnMoreInfo.setOnSingleClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEB_GENFLUENCER)))
            dismiss()
        }
    }

    companion object {
        private const val WEB_GENFLUENCER =
            "https://stealth-goose-156.notion.site/57a00e1d610b4c1786c6ab1fdb4c4659?pvs=4"
    }
}