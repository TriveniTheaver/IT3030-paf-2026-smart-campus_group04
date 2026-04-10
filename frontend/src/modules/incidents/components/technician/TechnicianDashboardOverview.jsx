import React from 'react';
import TechnicianDashboardHeader from './TechnicianDashboardHeader';
import TechnicianStatCard from './TechnicianStatCard';
import TechnicianMyStatsCard from './TechnicianMyStatsCard';
import TechnicianRecentActivityCard from './TechnicianRecentActivityCard';

/**
 * @param {{
 *   welcomeLine: string,
 *   metrics: { assigned: number, inProgress: number, slaBreached: number, resolvedThisWeek: number },
 *   myStatsRows: { label: string, value: string, valueClassName?: string }[],
 *   activityItems: { dotClassName: string, title: string, meta: string }[],
 * }} props
 */
const TechnicianDashboardOverview = ({ welcomeLine, metrics, myStatsRows, activityItems }) => (
  <section className="space-y-8">
    <TechnicianDashboardHeader welcomeLine={welcomeLine} />
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
      <TechnicianStatCard
        label="Assigned to me"
        value={metrics.assigned}
        footer="tickets total"
      />
      <TechnicianStatCard
        label="In progress"
        value={metrics.inProgress}
        footer="active now"
        valueClassName="text-amber-600"
      />
      <TechnicianStatCard
        label="SLA breached"
        value={metrics.slaBreached}
        footer="needs attention"
        valueClassName="text-rose-600"
      />
      <TechnicianStatCard
        label="Resolved this week"
        value={metrics.resolvedThisWeek}
        footer="since Mon"
        valueClassName="text-emerald-600"
      />
    </div>
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <TechnicianMyStatsCard rows={myStatsRows} />
      <TechnicianRecentActivityCard items={activityItems} />
    </div>
  </section>
);

export default TechnicianDashboardOverview;
