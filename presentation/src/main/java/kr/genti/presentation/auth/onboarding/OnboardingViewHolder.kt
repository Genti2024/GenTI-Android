package kr.genti.presentation.auth.onboarding

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ItemOnboardingBinding

class OnboardingViewHolder(
    val binding: ItemOnboardingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(position: Int) {
        with(binding) {
            ivOnboardingFirst.isVisible = position == 0
            ivOnboardingSecond.isVisible = position == 1
            if (position == 1) {
                tvOnboardingSubtitle.text = "얼굴 사진 3장만 골라요."
                tvOnboardingBody.text = "사진 생성에 이용할 본인 사진 3장을 골라주세요."
            }
            if (position == 2) {
                tvOnboardingSubtitle.text = "나만의 특별한 사진 완성!"
                tvOnboardingBody.text = "지금 젠티하러 가볼까요?"
                root.background = AppCompatResources.getDrawable(root.context, R.color.transparent)
            }
        }
    }
}
