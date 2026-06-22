/**
 * dashboard.js — logic specific to index.html (Dashboard)
 */

function renderAccountCards(accounts) {
  const root = document.getElementById("account-cards");
  const variants = ["dark", "blue", "gray"];

  root.innerHTML = accounts
    .map((acc, i) => {
      const variant = variants[i % variants.length];
      return `
        <div class="account-card account-card--${variant}">
          <div class="account-card__top">
            <span>${acc.type.toUpperCase()}</span>
            <span>•••• ${acc.last4}</span>
          </div>
          <div class="account-card__balance">${formatBalance(acc.balance)}</div>
          <div class="account-card__change">${ICONS.trendUp} +${acc.changePct}% this month</div>
        </div>
      `;
    })
    .join("");
}

/** Builds a smooth-ish SVG line/area chart from a series of {date, value} points. No chart library needed. */
function renderBalanceChart(series) {
  const root = document.getElementById("chart-root");
  const width = 760;
  const height = 320;
  const padding = { top: 20, right: 20, bottom: 30, left: 50 };
  const innerW = width - padding.left - padding.right;
  const innerH = height - padding.top - padding.bottom;

  const maxVal = Math.ceil(Math.max(...series.map((d) => d.value)) / 7000) * 7000;
  const stepX = innerW / (series.length - 1);

  const points = series.map((d, i) => {
    const x = padding.left + i * stepX;
    const y = padding.top + innerH - (d.value / maxVal) * innerH;
    return { x, y, label: d.date };
  });

  const linePath = points.map((p, i) => `${i === 0 ? "M" : "L"} ${p.x} ${p.y}`).join(" ");
  const areaPath = `${linePath} L ${points[points.length - 1].x} ${padding.top + innerH} L ${points[0].x} ${padding.top + innerH} Z`;

  const yLabels = [0, 1, 2, 3, 4].map((i) => Math.round((maxVal / 4) * i));

  const gridLines = yLabels
    .map((val) => {
      const y = padding.top + innerH - (val / maxVal) * innerH;
      return `<line x1="${padding.left}" y1="${y}" x2="${width - padding.right}" y2="${y}" stroke="#ececec" stroke-dasharray="4 4" />`;
    })
    .join("");

  const yLabelsHtml = yLabels
    .map((val) => {
      const y = padding.top + innerH - (val / maxVal) * innerH;
      return `<text x="${padding.left - 12}" y="${y + 4}" font-size="12" fill="#b5b5b5" text-anchor="end">$${Math.round(val / 1000)}k</text>`;
    })
    .join("");

  const xLabelsHtml = points
    .map((p) => `<text x="${p.x}" y="${height - 6}" font-size="12" fill="#b5b5b5" text-anchor="middle">${p.label}</text>`)
    .join("");

  root.innerHTML = `
    <svg viewBox="0 0 ${width} ${height}" width="100%" height="auto" preserveAspectRatio="xMidYMid meet">
      <defs>
        <linearGradient id="areaFill" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stop-color="#4fa8e0" stop-opacity="0.25" />
          <stop offset="100%" stop-color="#4fa8e0" stop-opacity="0" />
        </linearGradient>
      </defs>
      ${gridLines}
      ${yLabelsHtml}
      ${xLabelsHtml}
      <path d="${areaPath}" fill="url(#areaFill)" />
      <path d="${linePath}" fill="none" stroke="#4fa8e0" stroke-width="2.5" />
    </svg>
    <div style="display:flex;align-items:center;gap:8px;margin-top:12px;font-size:13px;color:#8a8a8a;">
      <span style="width:8px;height:8px;border-radius:50%;background:#4fa8e0;display:inline-block;"></span>
      Account Balance
    </div>
  `;
}

function injectQuickActionIcons() {
  document.querySelectorAll(".btn__icon-circle[data-icon]").forEach((el) => {
    el.innerHTML = ICONS[el.dataset.icon] || "";
  });
  document.querySelectorAll("[data-icon='plus']").forEach((el) => {
    if (!el.classList.contains("btn__icon-circle")) el.innerHTML = ICONS.plus;
  });
}

async function initDashboard() {
  await renderSidebar("dashboard");

  const [user, accounts, transactions, series] = await Promise.all([
    API.getUser(),
    API.getAccounts(),
    API.getTransactions(),
    API.getBalanceSeries(),
  ]);

  document.getElementById("greet-name").textContent = user.firstName;
  renderAccountCards(accounts);
  renderBalanceChart(series);

  document.getElementById("recent-tx-list").innerHTML = transactions.slice(0, 4).map(renderTxRow).join("");

  injectQuickActionIcons();

  document.getElementById("new-transfer-btn").addEventListener("click", () => {
    window.location.href = "send-money.html";
  });
}

document.addEventListener("DOMContentLoaded", initDashboard);
