import React from 'react';
import { Wrench } from 'lucide-react';

/**
 * @param {{ title?: string, welcomeLine: string, statusLabel?: string }} props
 */
const TechnicianDashboardHeader = ({
  title = 'Technician dashboard',
  welcomeLine,
  statusLabel = 'On duty',
}) => (
  <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
    <div>
      <h1 className="sc-page-title flex items-center gap-3 text-slate-900">
        <Wrench className="shrink-0 text-sliit-orange" size={32} aria-hidden />
        {title}
      </h1>
      <p className="mt-2 max-w-2xl text-sm font-medium text-slate-500">{welcomeLine}</p>
    </div>
    <div className="shrink-0">
      <span className="inline-flex items-center gap-2 rounded-full border border-emerald-200 bg-emerald-50 px-3.5 py-1.5 text-xs font-semibold text-emerald-800">
        <span className="h-2 w-2 rounded-full bg-emerald-500" aria-hidden />
        {statusLabel}
      </span>
    </div>
  </div>
);

export default TechnicianDashboardHeader;
