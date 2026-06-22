/**
 * send-money.js — logic specific to send-money.html
 */

let selectedRecipientId = null;
let accountsCache = [];

function renderFromSelect(accounts) {
  const select = document.getElementById("from-account");
  select.innerHTML = accounts
    .map((acc) => `<option value="${acc.id}">${acc.label} – ${formatBalance(acc.balance)}</option>`)
    .join("");
}

function renderRecipients(recipients) {
  const root = document.getElementById("recipient-list");
  root.innerHTML = recipients
    .map(
      (r) => `
      <button type="button" class="recipient-option" data-id="${r.id}">
        <span class="recipient-option__avatar">${r.emoji}</span>
        <span>
          <div class="recipient-option__name">${r.name}</div>
          <div class="recipient-option__iban">${r.iban}</div>
        </span>
      </button>
    `
    )
    .join("");

  root.querySelectorAll(".recipient-option").forEach((btn) => {
    btn.addEventListener("click", () => {
      selectedRecipientId = btn.dataset.id;
      root.querySelectorAll(".recipient-option").forEach((b) => b.classList.remove("is-selected"));
      btn.classList.add("is-selected");
      updateSubmitState();
    });
  });
}

function renderRecentTransfers(transfers) {
  document.getElementById("recent-transfers").innerHTML = transfers
    .map(
      (t) => `
      <div class="recent-transfer-row">
        <div class="recent-transfer-row__icon">${ICONS.history}</div>
        <div class="recent-transfer-row__body">
          <div class="recent-transfer-row__name">${t.name}</div>
          <div class="recent-transfer-row__date">${t.date}</div>
        </div>
        <div class="recent-transfer-row__amount">
          <div>${formatCurrency(t.amount)}</div>
          ${t.confirmed ? `<div class="recent-transfer-row__check">✓</div>` : ""}
        </div>
      </div>
    `
    )
    .join("");
}

function updateSubmitState() {
  const amount = parseFloat(document.getElementById("amount-input").value);
  const btn = document.getElementById("send-submit-btn");
  const valid = selectedRecipientId && amount > 0;

  btn.disabled = !valid;
  btn.classList.toggle("btn--disabled", !valid);
  btn.classList.toggle("btn--primary", valid);
}

async function handleSubmit(e) {
  e.preventDefault();
  const fromAccountId = document.getElementById("from-account").value;
  const amount = parseFloat(document.getElementById("amount-input").value);
  const note = document.getElementById("note-input").value;

  const btn = document.getElementById("send-submit-btn");
  btn.disabled = true;
  btn.textContent = "Sending...";

  try {
    const result = await API.sendMoney({ fromAccountId, toRecipientId: selectedRecipientId, amount, note });
    if (result.success) {
      alert("Transfer sent! (mock) — wire this up to real navigation/toast once the API is connected.");
      e.target.reset();
      selectedRecipientId = null;
      document.querySelectorAll(".recipient-option").forEach((b) => b.classList.remove("is-selected"));
    }
  } catch (err) {
    alert("Something went wrong sending the transfer.");
    console.error(err);
  } finally {
    btn.textContent = "Send Money →";
    updateSubmitState();
  }
}

async function initSendMoney() {
  await renderSidebar("send");

  const [accounts, recipients, recentTransfers] = await Promise.all([
    API.getAccounts(),
    API.getRecipients(),
    API.getRecentTransfers(),
  ]);

  accountsCache = accounts;
  renderFromSelect(accounts);
  renderRecipients(recipients);
  renderRecentTransfers(recentTransfers);

  document.getElementById("amount-input").addEventListener("input", updateSubmitState);
  document.getElementById("send-form").addEventListener("submit", handleSubmit);
}

document.addEventListener("DOMContentLoaded", initSendMoney);
