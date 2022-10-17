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

package com.google.samples.apps.sunflower.utils

import android.content.Context
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/**
 * This is used as a workaround to preserve rounded corners when bottomSheet is expanded.
 * Behavior was updated in 1.8.0-alpha01 version of the material-components library by using: `shouldRemoveExpandedCorners`
 * attribute. Until that version becomes stable, we can use this workaround.
 * @param onlyWhenExpandedToMax - whenever to consider expanded only when sheet height matches parent height.
 */
class UpdateSheetBackgroundOnExpandedStateAction : BottomSheetCallback() {

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        // no-op
    }

    override fun onStateChanged(sheet: View, @BottomSheetBehavior.State newState: Int) {
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            // In the EXPANDED state apply a new MaterialShapeDrawable with rounded corners
            val sheetBackgroundMaterialDrawable = sheet.background as? MaterialShapeDrawable
            if (sheetBackgroundMaterialDrawable != null) {
                val newMaterialShapeDrawable: MaterialShapeDrawable =
                    sheetBackgroundMaterialDrawable.copy(sheet.context)
                sheet.background = newMaterialShapeDrawable
            }
        }
    }

    private fun MaterialShapeDrawable.copy(context: Context): MaterialShapeDrawable {
        val shapeAppearanceModel = ShapeAppearanceModel.Builder(this.shapeAppearanceModel).build()

        return MaterialShapeDrawable(shapeAppearanceModel).apply {
            // Copy the attributes in the new MaterialShapeDrawable
            initializeElevationOverlay(context)
            fillColor = this@copy.fillColor
            tintList = this@copy.tintList
            elevation = this@copy.elevation
            strokeWidth = this@copy.strokeWidth
            strokeColor = this@copy.strokeColor
        }
    }
}
