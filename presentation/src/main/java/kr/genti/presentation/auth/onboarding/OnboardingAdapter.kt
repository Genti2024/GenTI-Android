package kr.genti.presentation.auth.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.genti.presentation.databinding.ItemOnboardingBinding

class OnboardingAdapter : RecyclerView.Adapter<OnboardingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OnboardingViewHolder {
        val inflater by lazy { LayoutInflater.from(parent.context) }
        val binding = ItemOnboardingBinding.inflate(inflater, parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OnboardingViewHolder,
        position: Int,
    ) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = TOTAL_VIEW_COUNT

    companion object {
        const val TOTAL_VIEW_COUNT = 3
    }
}
