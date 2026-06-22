/**
 * history.js — logic specific to history.html
 */

const CATEGORIES = ["All", "Income", "Shopping", "Food & Drink", "Utilities", "Transport", "Entertainment", "Housing", "Transfer", "Groceries"];

let activeCategory = "All";
let activeSearch = "";

function renderCategoryPills() {
  const root = document.getElementById("category-pills");
  root.innerHTML = CATEGORIES.map(
    (cat) => `<button class="pill ${cat === activeCategory ? "is-active" : ""}" data-cat="${cat}">${cat}</button>`
  ).join("");

  root.querySelectorAll(".pill").forEach((btn) => {
    btn.addEventListener("click", async () => {
      activeCategory = btn.dataset.cat;
      renderCategoryPills();
      await refreshList();
    });
  });
}

async function refreshList() {
  const transactions = await API.getTransactions({ category: activeCategory, search: activeSearch });
  const root = document.getElementById("history-list");
  root.innerHTML = transactions.length
    ? transactions.map(renderTxRow).join("")
    : `<div style="padding:40px;text-align:center;color:var(--color-text-muted);">No transactions found.</div>`;
}

let searchDebounce;
function handleSearchInput(e) {
  clearTimeout(searchDebounce);
  searchDebounce = setTimeout(async () => {
    activeSearch = e.target.value;
    await refreshList();
  }, 200);
}

async function initHistory() {
  await renderSidebar("history");

  document.getElementById("search-icon").innerHTML = ICONS.search;
  document.getElementById("filter-icon").innerHTML = ICONS.filter;

  renderCategoryPills();
  await refreshList();

  document.getElementById("search-input").addEventListener("input", handleSearchInput);
}

document.addEventListener("DOMContentLoaded", initHistory);
