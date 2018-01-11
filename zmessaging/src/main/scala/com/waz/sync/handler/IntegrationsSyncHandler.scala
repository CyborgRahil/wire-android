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
package com.waz.sync.handler

import com.waz.ZLog.debug
import com.waz.ZLog.ImplicitTag._
import com.waz.model.{IntegrationId, ProviderId}
import com.waz.service.IntegrationsService
import com.waz.sync.SyncResult
import com.waz.sync.client.IntegrationsClient
import com.waz.threading.Threading

import scala.concurrent.Future

trait IntegrationsSyncHandler {
  def syncIntegrations(name: String): Future[SyncResult]
  def syncIntegration(pId: ProviderId, iId: IntegrationId): Future[SyncResult]

  def syncProvider(pId: ProviderId): Future[SyncResult]
}

class IntegrationsSyncHandlerImpl(client: IntegrationsClient, service: IntegrationsService) extends IntegrationsSyncHandler {
  import Threading.Implicits.Background

  override def syncProvider(pId: ProviderId) = client.getProvider(pId).future.flatMap {
    case Right(data) =>
      debug(s"quering for provider with id $pId returned $data")
      service.onProviderSynced(pId, data).map(_ => SyncResult.Success)
    case Left(error) =>
      debug(s"quering for provider with id $pId returned $error")
      Future.successful(SyncResult(error))
  }

  override def syncIntegration(pId: ProviderId, iId: IntegrationId) = client.getIntegration(pId, iId).future.flatMap {
    case Right(data) =>
      debug(s"quering for integration with pId $pId and iId $iId returned $data")
      service.onIntegrationSynced(pId, iId, data).map(_ => SyncResult.Success)
    case Left(error) =>
      debug(s"quering for provider with pId $pId and iId $iId returned $error")
      Future.successful(SyncResult(error))
  }

  override def syncIntegrations(name: String) = client.searchIntegrations(name).future.flatMap {
    case Right(data) =>
      debug(s"quering for integrations with name $name returned $data")
      service.onIntegrationsSynced(name, data).map(_ => SyncResult.Success)
    case Left(error) =>
      debug(s"quering for integrations with name $name returned $error")
      Future.successful(SyncResult(error))
  }
}
