/*
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.testutils

import java.util

import com.waz.api.{Permission, PermissionProvider}
import com.waz.api.Permission.Status.GRANTED
import com.waz.threading.Threading
import org.robolectric.Robolectric

import scala.collection.breakOut
import scala.collection.JavaConverters._

class RoboPermissionProvider extends PermissionProvider {
  override def requestPermissions(ps: util.Set[Permission], callback: ResponseHandler): Unit = {
    Threading.assertUiThread()
    Robolectric.getShadowApplication.grantPermissions(ps.asScala.map(_.id)(breakOut):_*)
    callback.handleResponse(ps.asScala.map(_ -> GRANTED).toMap.asJava)
  }
}