import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { log, getCategory } from '../logging';

import { Connection, Connections, Integration, Integrations } from '../model';
import { ConnectionStore } from '../store/connection/connection.store';
import { IntegrationStore } from '../store/integration/integration.store';

const category = getCategory('Dashboard');

@Component({
  selector: 'ipaas-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  connections: Observable<Connections>;
  integrations: Observable<Integrations>;
  loading: Observable<boolean>;
  selectedId = undefined;
  truncateLimit = 80;
  truncateTrail = '…';

  constructor(private connectionStore: ConnectionStore, private integrationStore: IntegrationStore) {
    this.connections = this.connectionStore.list;
    this.integrations = this.integrationStore.list;
    this.loading = this.connectionStore.loading;
  }

  ngOnInit() {
    this.connectionStore.loadAll();
    this.integrationStore.loadAll();
  }
}