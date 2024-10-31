package kr.genti.presentation.auth.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.genti.core.state.UiState
import kr.genti.domain.entity.request.SignupRequestModel
import kr.genti.domain.entity.response.SignUpUserModel
import kr.genti.domain.enums.Gender
import kr.genti.domain.repository.InfoRepository
import kr.genti.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignupViewModel
@Inject
constructor(
    private val infoRepository: InfoRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val selectedGender = MutableLiveData<Gender>(Gender.NONE)
    private val isGenderSelected = MutableLiveData<Boolean>(false)

    val selectedYear = MutableLiveData<String>()
    val isYearSelected = MutableLiveData<Boolean>(false)

    private val _isYearAllSelected = MutableStateFlow<Boolean>(false)
    val isYearAllSelected: StateFlow<Boolean> = _isYearAllSelected

    val isAllSelected = MutableLiveData<Boolean>(false)

    private val _postSignupState = MutableStateFlow<UiState<SignUpUserModel>>(UiState.Empty)
    val postSignupState: StateFlow<UiState<SignUpUserModel>> = _postSignupState

    fun selectGender(gender: Gender) {
        selectedGender.value = gender
        isGenderSelected.value = true
        checkAllSelected()
    }

    fun checkYear() {
        isYearSelected.value = selectedYear.value?.isNotEmpty()
        _isYearAllSelected.value = selectedYear.value?.length == 4 &&
                selectedYear.value?.toIntOrNull()?.let { it in 1950..2025 } == true
        checkAllSelected()
    }

    private fun checkAllSelected() {
        isAllSelected.value = isGenderSelected.value == true && isYearAllSelected.value == true
    }

    fun postSignupDataToServer() {
        _postSignupState.value = UiState.Loading
        viewModelScope.launch {
            infoRepository.postSignupData(
                SignupRequestModel(
                    selectedYear.value.toString(),
                    selectedGender.value.toString(),
                ),
            ).onSuccess {
                userRepository.setUserRole(ROLE_USER)
                _postSignupState.value = UiState.Success(it)
            }.onFailure {
                _postSignupState.value = UiState.Failure(it.message.orEmpty())
            }
        }
    }

    companion object {
        private const val ROLE_USER = "USER"
    }
}
