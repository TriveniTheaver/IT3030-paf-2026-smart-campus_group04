import React from 'react';

/**
 * @param {{ label: string, value: string | number, footer: string, valueClassName?: string }} props
 */
const TechnicianStatCard = ({ label, value, footer, valueClassName = 'text-slate-900' }) => (
  <div className="rounded-xl border border-slate-100 bg-white px-5 py-4 shadow-sm shadow-slate-200/40">
    <p className="text-[10px] font-semibold uppercase tracking-wider text-slate-500">{label}</p>
    <p className={`mt-2 text-3xl font-bold tabular-nums tracking-tight ${valueClassName}`}>{value}</p>
    <p className="mt-1 text-xs text-slate-500">{footer}</p>
  </div>
);

export default TechnicianStatCard;
