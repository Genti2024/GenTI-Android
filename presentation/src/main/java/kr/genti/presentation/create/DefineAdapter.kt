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

    override fun getItemCount(): Int = TOTAL_VIEW_COUNT

    companion object {
        const val TOTAL_VIEW_COUNT = 5

        private val diffUtil =
            ItemDiffCallback<String>(
                onItemsTheSame = { old, new -> old == new },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}