import React from 'react';

/**
 * @param {{ subtitle?: string, rows: { label: string, value: string, valueClassName?: string }[] }} props
 */
const TechnicianMyStatsCard = ({ subtitle = 'This month', rows }) => (
  <div className="overflow-hidden rounded-xl border border-slate-100 bg-white shadow-sm shadow-slate-200/40">
    <div className="border-b border-slate-100 bg-slate-50 px-5 py-4">
      <h2 className="text-base font-bold text-sliit-blue">My stats</h2>
      <p className="mt-0.5 text-xs font-medium text-slate-500">{subtitle}</p>
    </div>
    <ul className="divide-y divide-slate-100">
      {rows.map((row) => (
        <li key={row.label} className="flex items-center justify-between gap-4 px-5 py-3.5">
          <span className="text-sm text-slate-600">{row.label}</span>
          <span className={`text-sm font-bold tabular-nums ${row.valueClassName ?? 'text-slate-900'}`}>
            {row.value}
          </span>
        </li>
      ))}
    </ul>
  </div>
);

export default TechnicianMyStatsCard;
