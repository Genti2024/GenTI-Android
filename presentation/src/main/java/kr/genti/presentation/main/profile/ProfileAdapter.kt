package kr.genti.presentation.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.genti.core.util.ItemDiffCallback
import kr.genti.domain.entity.response.ImageModel
import kr.genti.presentation.databinding.ItemProfileImageBinding

class ProfileAdapter(
    private val imageClick: (ImageModel) -> Unit,
    private val moveClick: (Boolean) -> Unit
) : ListAdapter<ImageModel, RecyclerView.ViewHolder>(diffUtil) {

    var isMaking: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(currentList.size)
        }

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProfileImageBinding.inflate(inflater, parent, false)

        return when (viewType) {
            VIEW_TYPE_ITEM -> ProfileItemViewHolder(binding, imageClick)
            VIEW_TYPE_FOOTER -> ProfileMoveViewHolder(binding, moveClick)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ProfileItemViewHolder) {
            val item = getItem(position) ?: return
            holder.onBind(item)
        } else if (holder is ProfileMoveViewHolder) {
            holder.onBind(isMaking)
        }
    }

    override fun getItemCount(): Int {
        return if (currentList.isNotEmpty()) currentList.size + 1 else currentList.size
    }

    fun addList(newItems: List<ImageModel>) {
        val currentItems = currentList.toMutableList()
        currentItems.addAll(newItems)
        submitList(currentItems)
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1

        private val diffUtil =
            ItemDiffCallback<ImageModel>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}
