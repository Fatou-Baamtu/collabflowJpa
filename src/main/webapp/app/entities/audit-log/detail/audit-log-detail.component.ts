import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IAuditLog } from '../audit-log.model';

@Component({
  standalone: true,
  selector: 'jhi-audit-log-detail',
  templateUrl: './audit-log-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class AuditLogDetailComponent {
  auditLog = input<IAuditLog | null>(null);

  previousState(): void {
    window.history.back();
  }
}
