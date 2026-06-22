/**
 * accounts.js — logic specific to accounts.html
 */

function renderTotalAssets(accounts) {
  const total = accounts.reduce((sum, a) => sum + a.balance, 0);
  // In a real API this monthly-change figure would come from the backend.
  // Hardcoded here to match the design mock.
  const monthlyChange = 3412.5;

  document.getElementById("total-assets").innerHTML = `
    <div class="total-assets__label">Total Assets</div>
    <div class="total-assets__value">${formatBalance(total)}</div>
    <div class="total-assets__change">${ICONS.trendUp} +${formatBalance(monthlyChange)} this month 🎉</div>
  `;
}

function renderAccountList(accounts) {
  document.getElementById("account-list").innerHTML = accounts
    .map(
      (acc) => `
      <div class="account-row">
        <div>
          <div class="account-row__name-line">
            <span class="account-row__name">${acc.label}</span>
            <span class="badge badge--active">✓ ${acc.status}</span>
            ${acc.apy ? `<span class="badge badge--apy">APY ${acc.apy}%</span>` : ""}
          </div>
          <div class="account-row__iban">${acc.iban}</div>
          <div class="account-row__type">${acc.type} · ${acc.currency}</div>
          <div class="account-row__actions">
            <button class="btn btn--outline btn--sm">${ICONS.arrowUpRight} Send</button>
            <button class="btn btn--outline btn--sm">${ICONS.arrowDownLeft} Receive</button>
          </div>
        </div>
        <div class="account-row__balance">${formatBalance(acc.balance)}</div>
      </div>
    `
    )
    .join("");
}

async function initAccounts() {
  await renderSidebar("accounts");
  const accounts = await API.getAccounts();
  renderTotalAssets(accounts);
  renderAccountList(accounts);

  document.querySelectorAll("[data-icon='plus']").forEach((el) => (el.innerHTML = ICONS.plus));
}

document.addEventListener("DOMContentLoaded", initAccounts);
