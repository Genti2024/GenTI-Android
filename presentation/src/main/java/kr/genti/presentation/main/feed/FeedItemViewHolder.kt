package kr.genti.presentation.main.feed

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.genti.core.extension.breakLines
import kr.genti.domain.entity.response.FeedItemModel
import kr.genti.domain.enums.PictureRatio
import kr.genti.presentation.databinding.ItemFeedItemBinding

class FeedItemViewHolder(
    val binding: ItemFeedItemBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: FeedItemModel) {
        with(binding) {
            tvFeedItemDescription.text = item.prompt.breakLines()
            if (item.picture.pictureRatio == PictureRatio.RATIO_GARO) {
                (cvFeedItemImage.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "3:2"
            } else {
                (cvFeedItemImage.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "2:3"
            }
            ivFeedItemImage.load(item.picture.url)
        }
    }
}
