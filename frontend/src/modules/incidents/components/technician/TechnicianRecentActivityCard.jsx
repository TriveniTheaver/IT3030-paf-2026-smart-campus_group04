import React from 'react';

/**
 * @param {{ items: { dotClassName: string, title: string, meta: string }[] }} props
 */
const TechnicianRecentActivityCard = ({ items }) => (
  <div className="overflow-hidden rounded-xl border border-slate-100 bg-white shadow-sm shadow-slate-200/40">
    <div className="border-b border-slate-100 bg-slate-50 px-5 py-4">
      <h2 className="text-base font-bold text-sliit-blue">Recent activity</h2>
    </div>
    <ul className="divide-y divide-slate-100">
      {items.length === 0 ? (
        <li className="px-5 py-8 text-center text-sm text-slate-500">No recent ticket activity yet.</li>
      ) : (
        items.map((item, i) => (
          <li key={`${item.title}-${i}`} className="flex gap-3 px-5 py-3.5">
            <span
              className={`mt-1.5 h-2 w-2 shrink-0 rounded-full ${item.dotClassName}`}
              aria-hidden
            />
            <div className="min-w-0 flex-1">
              <p className="text-sm font-medium leading-snug text-slate-800">{item.title}</p>
              <p className="mt-0.5 text-xs text-slate-500">{item.meta}</p>
            </div>
          </li>
        ))
      )}
    </ul>
  </div>
);

export default TechnicianRecentActivityCard;
