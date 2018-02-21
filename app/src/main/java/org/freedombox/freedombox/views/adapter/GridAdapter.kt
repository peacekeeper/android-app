/*
 * This file is part of FreedomBox.
 *
 * FreedomBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FreedomBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FreedomBox. If not, see <http://www.gnu.org/licenses/>.
 */

package org.freedombox.freedombox.views.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.app_container.view.*
import org.freedombox.freedombox.R
import org.freedombox.freedombox.models.Shortcut
import org.freedombox.freedombox.utils.ImageRenderer
import org.freedombox.freedombox.utils.network.launchApp
import org.freedombox.freedombox.utils.network.urlJoin

class GridAdapter(val context: Context, val imageRenderer: ImageRenderer, val baseUrl: String) : BaseAdapter() {

    private var items = listOf<Shortcut>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.app_container, null)
        val shortcut = items[position]

        rowView.appName.text = shortcut.name
        rowView.appDescription.text = shortcut.shortDescription
        val iconUrl = urlJoin(baseUrl, shortcut.iconUrl)

        imageRenderer.loadImageFromURL(
                Uri.parse(iconUrl),
                rowView.appIcon
        )

        rowView.appIcon.setOnClickListener { launchApp(shortcut, context, baseUrl)}

        return rowView
    }

    override fun getItem(position: Int): Shortcut = items[position]

    override fun getItemId(position: Int) = items[position].hashCode().toLong()

    override fun getCount() = items.size

    fun setData(shortcuts: List<Shortcut>) {
        items = shortcuts

        notifyDataSetChanged()
    }
}
