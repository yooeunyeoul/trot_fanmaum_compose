package com.trotfan.trot.ui.signup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trotfan.trot.repository.SignUpRepository
import com.trotfan.trot.ui.components.input.SearchStatus
import com.trotfan.trot.ui.signup.Sample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarSelectViewModel @Inject constructor(
    private val repository: SignUpRepository
) : ViewModel() {
    private val _testData = MutableStateFlow<List<Sample>>(emptyList())

    //    private val _testData = MutableStateFlow<List<Sample>>(emptyList())
    val testData: StateFlow<List<Sample>>
        get() = _testData

    private val _searchState = MutableStateFlow(SearchStatus.TrySearch)
    val searchStatus: StateFlow<SearchStatus>
        get() = _searchState

    private val _requestComplete = MutableStateFlow(false)
    val requestComplete: StateFlow<Boolean>
        get() = _requestComplete


    init {
//        getRestApiTest()
        initSampleData()
        Log.d("Initializing", "MainViewModel")
    }

    private fun initSampleData() {
        viewModelScope.launch {
            val sampleList = mutableListOf<Sample>()
            repeat(30) {
                sampleList.add(Sample(id = it))
            }
            _testData.emit(sampleList)
        }
    }


    fun searchStar(keyword: String) {
        val sampleList = mutableListOf<Sample>()
        viewModelScope.launch {
            when (keyword) {
                "" -> {
                    _testData.emit(listOf())
                    _searchState.emit(SearchStatus.TrySearch)
                }
                "empty" -> {
                    _testData.emit(listOf())
                    _searchState.emit(SearchStatus.NoResult)
                }
                "search"->{
                    for (i in 0..100) {
                        sampleList.add(Sample(id = i))
                    }
                    _testData.emit(sampleList)
                    _searchState.emit(SearchStatus.SearchResult)

                }
                else -> {
                    for (i in 0..10) {
                        sampleList.add(Sample(id = i))
                    }
                    _testData.emit(sampleList)
                    _searchState.emit(SearchStatus.SearchResult)
                }

            }
        }

    }

    fun requestStar(starName: String) {
        viewModelScope.launch {
            _requestComplete.emit(true)
        }
    }

    fun dismissCompleteDialog() {
        viewModelScope.launch {
            _requestComplete.emit(false)
        }

    }

    fun selectStar(selectedItem: Sample?) {
        viewModelScope.launch {

        }
    }
}