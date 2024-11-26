package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import kr.genti.core.base.BaseFragment
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentNumberBinding

class NumberFragment() : BaseFragment<FragmentNumberBinding>(R.layout.fragment_number) {
    private val viewModel by activityViewModels<CreateViewModel>()


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

    }
}