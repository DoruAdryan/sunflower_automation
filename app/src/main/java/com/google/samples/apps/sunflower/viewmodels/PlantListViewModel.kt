/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * The ViewModel for [PlantListFragment].
 */
@HiltViewModel
class PlantListViewModel @Inject internal constructor(
    plantRepository: PlantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantTypesFilters: MutableStateFlow<Set<String>> = MutableStateFlow(
        savedStateHandle[FILTERS_SAVED_STATE_KEY] ?: emptySet()
    )

    val activeFiltersCount: LiveData<Int> = plantTypesFilters.mapLatest { it.size }.asLiveData()

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow(
        savedStateHandle[SEARCH_QUERY_SAVED_STATE_KEY] ?: ""
    )

    val plants: LiveData<List<Plant>> =
        combine(
            searchQuery.debounce(300),
            plantTypesFilters, ::Pair
        ).flatMapLatest { (query, filters) ->
            val queryInvalid = query.length < 2
            when {
                queryInvalid && filters.isEmpty() -> plantRepository.getPlants()
                queryInvalid -> plantRepository.getPlantsWithTypes(filters)
                filters.isEmpty() -> plantRepository.getPlantsWithName(query)
                else -> plantRepository.getPlantsWithNameAndType(query, filters)
            }
        }.asLiveData()

    init {
        viewModelScope.launch {
            plantTypesFilters.collect { newFilters ->
                savedStateHandle[FILTERS_SAVED_STATE_KEY] = newFilters
            }
        }
    }

    fun search(searchQuery: String) {
        Timber.d("search for: $searchQuery")
        this.searchQuery.update { searchQuery }
    }

    fun isFilterForTypeEnabled(type: String): Boolean = plantTypesFilters.value.contains(type)

    fun toggleFilterForType(type: String) {
        val currFilters = plantTypesFilters.value
        val newFilters = if (currFilters.contains(type)) {
            currFilters - type
        } else {
            currFilters + type
        }
        plantTypesFilters.update { newFilters }
    }

    companion object {
        private const val FILTERS_SAVED_STATE_KEY = "filters_saved_state_key"
        private const val SEARCH_QUERY_SAVED_STATE_KEY = "search_query_saved_state_key"
    }
}
