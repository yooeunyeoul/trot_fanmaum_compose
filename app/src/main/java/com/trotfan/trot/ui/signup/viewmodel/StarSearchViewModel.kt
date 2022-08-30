package com.trotfan.trot.ui.signup.viewmodel

import android.R.id
import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.trotfan.trot.datasource.GetStarDataSourceForName
import com.trotfan.trot.datastore.userIdStore
import com.trotfan.trot.model.Person
import com.trotfan.trot.repository.SignUpRepository
import com.trotfan.trot.ui.components.input.SearchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StarSearchViewModel @Inject constructor(
    private val repository: SignUpRepository,
    private val dataSource: GetStarDataSourceForName,
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private val _searchState = MutableStateFlow(SearchStatus.TrySearch)
    val searchStatus: StateFlow<SearchStatus>
        get() = _searchState

    private val _requestComplete = MutableStateFlow(false)
    val requestComplete: StateFlow<Boolean>
        get() = _requestComplete

    private val _startListState = mutableStateOf<Flow<PagingData<Person>>?>((null))
    val starListState: State<Flow<PagingData<Person>>?>
        get() = _startListState

    private val _onComplete = MutableStateFlow(false)
    val onComplete: StateFlow<Boolean>
        get() = _onComplete


    fun searchStar(keyword: String) {
        viewModelScope.launch {

            when (keyword) {
                "" -> {
                    _searchState.emit(SearchStatus.TrySearch)
                    _startListState.value = null
                }
                "empty" -> {
                    _searchState.emit(SearchStatus.NoResult)
                }
                else -> {
                    _startListState.value = Pager(PagingConfig(pageSize = 15)) {
                        dataSource.apply { starName = keyword }
                    }.flow
                }
            }
        }

    }

    fun requestStar(starName: String, completeListener: () -> Unit) {
        viewModelScope.launch {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, starName)
            })
            _requestComplete.emit(true)
            completeListener.invoke()
        }
    }

    fun dismissCompleteDialog() {
        viewModelScope.launch {
            _requestComplete.emit(false)
        }

    }

    fun selectStar(selectedItem: Person?) {
        viewModelScope.launch {
            context.userIdStore.data.collect {
                val response = repository.updateUser(
                    userid = it.userId.toString(),
                    starId = selectedItem?.id.toString()
                )
                if (response.code == 200) {
                    _onComplete.emit(true)
                }
            }
        }
    }

    fun changeSearchState(searchState: SearchStatus) {
        viewModelScope.launch {
            _searchState.emit(searchState)
        }

    }
}