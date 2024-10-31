package kr.genti.presentation.create

import androidx.recyclerview.widget.RecyclerView
import coil.load
import kr.genti.core.extension.breakLines
import kr.genti.domain.entity.response.PromptExampleModel
import kr.genti.presentation.databinding.ItemDefineExampleBinding

class DefineViewHolder(
    val binding: ItemDefineExampleBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: PromptExampleModel) {
        with(binding) {
            ivDefineExample.load(item.url)
            tvDefineExample.text = item.prompt.breakLines()
        }
    }
}
