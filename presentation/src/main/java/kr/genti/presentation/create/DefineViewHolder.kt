package kr.genti.presentation.create

import androidx.recyclerview.widget.RecyclerView
import kr.genti.presentation.databinding.ItemDefineExampleBinding

class DefineViewHolder(
    val binding: ItemDefineExampleBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: String) {
        with(binding) {
            tvDefineExample.text = item
        }
    }
}
