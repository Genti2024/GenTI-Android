package kr.genti.presentation.create.example

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kr.genti.core.util.ItemDiffCallback
import kr.genti.domain.entity.response.PromptExampleModel
import kr.genti.presentation.databinding.ItemDefineExampleBinding

class DefineAdapter : ListAdapter<PromptExampleModel, DefineViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DefineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDefineExampleBinding.inflate(inflater, parent, false)
        return DefineViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DefineViewHolder,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        holder.onBind(item)
    }

    companion object {

        private val diffUtil =
            ItemDiffCallback<PromptExampleModel>(
                onItemsTheSame = { old, new -> old.prompt == new.prompt },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}