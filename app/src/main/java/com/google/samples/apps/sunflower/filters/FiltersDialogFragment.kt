/*
 * Copyright 2022 Google LLC
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

package com.google.samples.apps.sunflower.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.PLANT_TYPE_FLOWER
import com.google.samples.apps.sunflower.data.PLANT_TYPE_FRUIT
import com.google.samples.apps.sunflower.data.PLANT_TYPE_VEGETABLE
import com.google.samples.apps.sunflower.databinding.FragmentFiltersBinding
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: PlantListViewModel by activityViewModels()

    private var _binding: FragmentFiltersBinding? = null
    private val binding: FragmentFiltersBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFiltersBinding.inflate(inflater, null, false).also {
            _binding = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        val bottomSheetDialog = dialog as BottomSheetDialog
        val behavior = bottomSheetDialog.behavior
        behavior.apply {
            isFitToContents = true
//            peekHeight = 0
            skipCollapsed = true
            isHideable = true
        }
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.switchFlowersFilter.isChecked = viewModel.isFilterForTypeEnabled(PLANT_TYPE_FLOWER)
        binding.switchFlowersFilter.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleFilterForType(PLANT_TYPE_FLOWER)
        }

        binding.switchVegetablesFilter.isChecked = viewModel.isFilterForTypeEnabled(PLANT_TYPE_VEGETABLE)
        binding.switchVegetablesFilter.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleFilterForType(PLANT_TYPE_VEGETABLE)
        }

        binding.switchFruitsFilter.isChecked = viewModel.isFilterForTypeEnabled(PLANT_TYPE_FRUIT)
        binding.switchFruitsFilter.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleFilterForType(PLANT_TYPE_FRUIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}