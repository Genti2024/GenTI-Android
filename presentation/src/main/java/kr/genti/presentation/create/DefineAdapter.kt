package kr.genti.presentation.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kr.genti.core.util.ItemDiffCallback
import kr.genti.presentation.databinding.ItemDefineExampleBinding

class DefineAdapter : ListAdapter<String, DefineViewHolder>(diffUtil) {

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
            ItemDiffCallback<String>(
                onItemsTheSame = { old, new -> old.length == new.length },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}