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

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.samples.apps.sunflower.R
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import timber.log.Timber

interface ToolbarBadgesDelegate {
    fun showBadgeForMenuItem(@IdRes itemId: Int)
    fun showBadgeForMenuItem(@IdRes itemId: Int, number: Int)
    fun hideBadgeForMenuItem(@IdRes itemId: Int, remove: Boolean = false)
}

@SuppressLint("UnsafeOptInUsageError")
class DefaultToolbarBadgesDelegate(toolbar: Toolbar) : ToolbarBadgesDelegate {
    private val toolbar: Toolbar? by weakRef(toolbar)
    private val badgesByMenuItemIds = SparseArray<BadgeDrawable>()

    override fun showBadgeForMenuItem(@IdRes itemId: Int) {
        val toolbar = toolbar
        if (toolbar == null) {
            badgesByMenuItemIds.clear()
            return
        }
        createOrShowBadgeDrawableForItemId(toolbar, itemId)
    }

    override fun showBadgeForMenuItem(itemId: Int, number: Int) {
        val toolbar = toolbar
        if (toolbar == null) {
            badgesByMenuItemIds.clear()
            return
        }
        val badge = createOrShowBadgeDrawableForItemId(toolbar, itemId)
        badge.number = number
    }

    private fun createOrShowBadgeDrawableForItemId(toolbar: Toolbar, @IdRes itemId: Int): BadgeDrawable {
        var badge = badgesByMenuItemIds[itemId]
        if (badge == null) {
            val tmpBadge = createBadgeDrawable(toolbar.context)
            BadgeUtils.attachBadgeDrawable(tmpBadge, toolbar, itemId)
            badgesByMenuItemIds[itemId] = tmpBadge
            badge = tmpBadge
        } else {
            badge.isVisible = true
        }
        return badge
    }

    private fun createBadgeDrawable(context: Context): BadgeDrawable =
        BadgeDrawable.createFromResource(context, R.xml.standalone_badge)

    override fun hideBadgeForMenuItem(@IdRes itemId: Int, remove: Boolean) {
        val toolbar = toolbar
        if (toolbar == null) {
            badgesByMenuItemIds.clear()
            return
        }
        val badge = badgesByMenuItemIds[itemId] ?: return
        if (remove) {
            BadgeUtils.detachBadgeDrawable(badge, toolbar, itemId)
            badgesByMenuItemIds.remove(itemId)
        } else {
            badge.isVisible = false
        }
    }
}

fun ToolbarBadgesDelegate.showOrHideBadgeUsingCountForMenuItem(@IdRes itemId: Int, badgeCount: Int) {
    if (badgeCount > 0) {
        showBadgeForMenuItem(itemId, badgeCount)
    } else {
        hideBadgeForMenuItem(itemId)
    }
}

/**
 * Property delegate which forwards getter / setters calls to a WeakReference.
 * Allows usages like:
 * ```
 *     private val weakReferenceView: View? by weakRef(strongReferenceView)
 * ```
 * @see [weakRef]
 */
class WeakReferenceHolder<T>(private var value: WeakReference<T?>) : ReadWriteProperty<Any?, T?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = value.get()

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = WeakReference(value)
    }
}

/**
 * Convenience function to enable easy use of WeakReferenceHolder.
 */
fun <T> weakRef(value: T) = WeakReferenceHolder(WeakReference(value))
