package kr.genti.presentation.main.profile

import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.domain.entity.response.ImageModel
import kr.genti.presentation.databinding.ItemProfileImageBinding

class ProfileItemViewHolder(
    val binding: ItemProfileImageBinding,
    val imageClick: (ImageModel) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: ImageModel) {
        with(binding) {
            ivProfileItemImage.load(item.url)
            root.setOnSingleClickListener {
                imageClick(item)
            }
        }
    }
}
