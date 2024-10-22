package kr.genti.presentation.main.profile

import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ItemProfileImageBinding

class ProfileMoveViewHolder(
    val binding: ItemProfileImageBinding,
    val moveClick: (Boolean) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(isMaking: Boolean) {
        with(binding) {
            if (isMaking) {
                ivProfileItemImage.load(R.drawable.img_profile_create_inactive)
            } else {
                ivProfileItemImage.load(R.drawable.img_profile_create_active)
            }
            root.setOnSingleClickListener {
                moveClick(isMaking)
            }
        }
    }
}