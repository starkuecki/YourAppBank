/**
 * ui.js
 * -----
 * Shared rendering helpers used by every page. No page-specific logic here.
 */

// Minimal inline icon set (so we don't depend on an external icon font/library).
// Add more as needed — keep them as simple stroke-based SVGs to match the Figma style.
const ICONS = {
  dashboard: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg>`,
  accounts: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 10l9-6 9 6"/><path d="M4 10v9h16v-9"/><path d="M9 19v-6h6v6"/></svg>`,
  cards: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg>`,
  send: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M7 16V8l-4 4 4 4z"/><path d="M3 12h12"/><path d="M13 5l8 7-8 7"/></svg>`,
  history: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/></svg>`,
  settings: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.2a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.2a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.2a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.2a1.7 1.7 0 0 0-1.5 1z"/></svg>`,
  chevron: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>`,
  logout: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>`,
  plus: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>`,
  trendUp: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 17l6-6 4 4 8-8"/><path d="M17 7h4v4"/></svg>`,
  bolt: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h7l-1 8 10-12h-7l1-8z"/></svg>`,
  dots: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><circle cx="5" cy="12" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="19" cy="12" r="1.5"/></svg>`,
  arrowDownLeft: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 7L7 17"/><path d="M17 17H7V7"/></svg>`,
  arrowUpRight: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M7 17L17 7"/><path d="M7 7h10v10"/></svg>`,
  shoppingBag: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>`,
  coffee: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/></svg>`,
  zap: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h7l-1 8 10-12h-7l1-8z"/></svg>`,
  car: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 17h14M5 17a2 2 0 1 0 4 0M15 17a2 2 0 1 0 4 0M5 17l1.5-5h11L19 17M6.5 12l1-3h9l1 3"/></svg>`,
  tv: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M8 21h8"/></svg>`,
  search: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>`,
  filter: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16l-6 8v6l-4 2v-8z"/></svg>`,
  help: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.5 9a2.5 2.5 0 0 1 5 0c0 1.5-2.5 2-2.5 3.5"/><circle cx="12" cy="17" r=".5" fill="currentColor"/></svg>`,
};

// Maps category/transaction icon keys (from data.js) to the ICONS set above
const TX_ICON_MAP = {
  "shopping-bag": "shoppingBag",
  "trending-up": "trendUp",
  coffee: "coffee",
  zap: "zap",
  car: "car",
  "arrow-down-left": "arrowDownLeft",
  tv: "tv",
};

function formatCurrency(amount) {
  const sign = amount < 0 ? "−" : amount > 0 ? "+" : "";
  const abs = Math.abs(amount).toLocaleString("en-US", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
  return `${sign}$${abs}`;
}

function formatBalance(amount) {
  return `$${amount.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function formatTxDate(isoString) {
  const date = new Date(isoString);
  const now = new Date("2026-06-18T23:59:59"); // matches the mock "today" in data.js
  const isToday = date.toDateString() === now.toDateString();
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  const isYesterday = date.toDateString() === yesterday.toDateString();

  const time = date.toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit", hour12: false });
  const dateLabel = date.toLocaleDateString("en-US", { month: "short", day: "numeric" });

  if (isToday) return `Today, ${time}`;
  if (isYesterday) return `Yesterday, ${time}`;
  return dateLabel;
}

const NAV_ITEMS = [
  { id: "dashboard", label: "Dashboard", icon: "dashboard", href: "index.html" },
  { id: "accounts", label: "Accounts", icon: "accounts", href: "accounts.html" },
  { id: "cards", label: "Cards", icon: "cards", href: "cards.html" },
  { id: "send", label: "Send Money", icon: "send", href: "send-money.html" },
  { id: "history", label: "History", icon: "history", href: "history.html" },
  { id: "settings", label: "Settings", icon: "settings", href: "settings.html" },
];

/**
 * Renders the sidebar into #sidebar-root.
 * @param {string} activeId - id of NAV_ITEMS entry to mark active
 */
async function renderSidebar(activeId) {
  const root = document.getElementById("sidebar-root");
  if (!root) return;

  const user = await API.getUser();

  const navHtml = NAV_ITEMS.map((item) => {
    const isActive = item.id === activeId;
    return `
      <a class="nav-item ${isActive ? "is-active" : ""}" href="${item.href}">
        <span class="nav-item__icon">${ICONS[item.icon]}</span>
        <span class="nav-item__label">${item.label}</span>
        ${isActive ? `<span class="nav-item__chevron">${ICONS.chevron}</span>` : ""}
      </a>`;
  }).join("");

  root.innerHTML = `
    <div class="sidebar__brand">
      <img class="sidebar__logo" src="assets/logo.png" alt="Booby Bank logo" />
      <div>
        <div class="sidebar__brand-name">Booby Bank</div>
        <div class="sidebar__brand-tag">money, but chill ✦</div>
      </div>
    </div>
    <nav class="sidebar__nav">${navHtml}</nav>
    <div class="sidebar__footer">
      <div class="avatar">${user.initials}</div>
      <div>
        <div class="sidebar__user-name">${user.name}</div>
        <div class="sidebar__user-plan">${user.plan} ✦</div>
      </div>
      <button class="sidebar__logout" title="Log out">${ICONS.logout}</button>
    </div>
  `;
}

/** Renders the floating help bubble present on every page */
function renderHelpBubble() {
  const el = document.createElement("div");
  el.className = "help-bubble";
  el.innerHTML = ICONS.help;
  document.body.appendChild(el);
}

/** Renders a single transaction row, used by both Dashboard "Recent" and History list */
function renderTxRow(tx) {
  const iconKey = TX_ICON_MAP[tx.icon] || "shoppingBag";
  const isIncome = tx.amount > 0;
  return `
    <div class="tx-row">
      <div class="tx-row__icon ${isIncome ? "tx-row__icon--income" : ""}">${ICONS[iconKey]}</div>
      <div class="tx-row__body">
        <div class="tx-row__title">${tx.merchant}</div>
        <div class="tx-row__meta">${tx.category ? tx.category + " · " : ""}${formatTxDate(tx.date)}</div>
      </div>
      <div class="tx-row__amount ${isIncome ? "tx-row__amount--positive" : ""}">${formatCurrency(tx.amount)}</div>
    </div>
  `;
}

document.addEventListener("DOMContentLoaded", renderHelpBubble);
