package kr.genti.presentation.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kr.genti.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _isAutoLogined = MutableSharedFlow<Boolean>()
        val isAutoLogined: SharedFlow<Boolean> = _isAutoLogined

        init {
            getAutoLoginState()
        }

        private fun getAutoLoginState() {
            viewModelScope.launch {
                delay(DELAY_SPLASH)
                if (userRepository.getAccessToken().isEmpty()) {
                    // TODO 다시 원상복귀
                    _isAutoLogined.emit(false)
                } else {
                    _isAutoLogined.emit(false)
                }
            }
        }

        companion object {
            private const val DELAY_SPLASH = 1500L
        }
    }
