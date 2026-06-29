/**
 * data.js
 * -------
 * This is the ONLY file that should change when you connect a real backend.
 *
 * Every function here returns a Promise, exactly like a fetch() call would.
 * Right now they resolve instantly with hardcoded mock data. When your API
 * is ready, replace the *body* of each function with a real fetch() call
 * that returns data in the same shape — nothing in ui.js or the pages
 * needs to change as long as the shape stays the same.
 *
 * Example of what these will look like later:
 *
 *   async function getAccounts() {
 *     const res = await fetch('/api/accounts');
 *     if (!res.ok) throw new Error('Failed to load accounts');
 *     return res.json();
 *   }
 */

const MOCK_USER = {
  name: "Martin Ross",
  firstName: "Martin",
  initials: "MR",
  plan: "Premium",
};

const MOCK_ACCOUNTS = [
  {
    id: "checking",
    type: "Checking",
    label: "Primary Checking",
    last4: "4821",
    iban: "DE89 3704 0044 0532 0130 00",
    currency: "USD",
    balance: 24571.38,
    changePct: 2.4,
    status: "Active",
    apy: null,
  },
  {
    id: "savings",
    type: "Savings",
    label: "High-Yield Savings",
    last4: "7293",
    iban: "DE57 2004 1029 0230 4025 89",
    currency: "USD",
    balance: 48200.0,
    changePct: 2.4,
    status: "Active",
    apy: 4.85,
  },
  {
    id: "investments",
    type: "Investments",
    label: "Investment Portfolio",
    last4: "3047",
    iban: "DE27 6708 0030 1234 1234 00",
    currency: "USD",
    balance: 92450.75,
    changePct: 2.4,
    status: "Active",
    apy: null,
  },
];

const MOCK_TRANSACTIONS = [
  { id: "t1", merchant: "Apple Store", category: "Shopping", icon: "shopping-bag", date: "2026-06-18T14:32:00", amount: -1299.0 },
  { id: "t2", merchant: "Salary – Meridian Corp", category: "Income", icon: "trending-up", date: "2026-06-18T09:00:00", amount: 7200.0 },
  { id: "t3", merchant: "Starbucks", category: "Food & Drink", icon: "coffee", date: "2026-06-17T08:15:00", amount: -6.8 },
  { id: "t4", merchant: "Electric Bill", category: "Utilities", icon: "zap", date: "2026-06-15T00:00:00", amount: -142.5 },
  { id: "t5", merchant: "Uber", category: "Transport", icon: "car", date: "2026-06-14T00:00:00", amount: -18.4 },
  { id: "t6", merchant: "Transfer from Sarah", category: "Transfer", icon: "arrow-down-left", date: "2026-06-13T00:00:00", amount: 320.0 },
  { id: "t7", merchant: "Netflix", category: "Entertainment", icon: "tv", date: "2026-06-12T00:00:00", amount: -15.99 },
];

const MOCK_RECIPIENTS = [
  { id: "r1", name: "Sarah Chen", iban: "GB29 NWBK 6016 133…", emoji: "👩‍💻" },
  { id: "r2", name: "James Liu", iban: "DE89 3704 0044 053…", emoji: "👨‍🎨" },
  { id: "r3", name: "Priya Patel", iban: "FR76 3000 6000 011…", emoji: "👩‍🔬" },
];

const MOCK_RECENT_TRANSFERS = [
  { id: "rt1", name: "Sarah Chen", date: "Jun 13", amount: -320, confirmed: true },
  { id: "rt2", name: "James Liu", date: "May 18", amount: -1500, confirmed: true },
  { id: "rt3", name: "Rent – Meridian RE", date: "Jun 1", amount: -1800, confirmed: true },
];

// Chart series for the dashboard "Overview" balance graph
const MOCK_BALANCE_SERIES = [
  { date: "1 Jul", value: 21200 },
  { date: "5 Jul", value: 21800 },
  { date: "9 Jul", value: 19400 },
  { date: "13 Jul", value: 23100 },
  { date: "17 Jul", value: 23400 },
  { date: "21 Jul", value: 22600 },
  { date: "25 Jul", value: 27800 },
  { date: "29 Jul", value: 26200 },
];

const API = {
  async getUser() {
    return Promise.resolve(structuredClone(MOCK_USER));
  },

  async getAccounts() {
    return Promise.resolve(structuredClone(MOCK_ACCOUNTS));
  },

  async getTransactions({ category = "All", search = "" } = {}) {
    let results = structuredClone(MOCK_TRANSACTIONS);
    if (category && category !== "All") {
      results = results.filter((t) => t.category === category);
    }
    if (search) {
      const q = search.toLowerCase();
      results = results.filter((t) => t.merchant.toLowerCase().includes(q));
    }
    return Promise.resolve(results);
  },

  async getRecipients() {
    return Promise.resolve(structuredClone(MOCK_RECIPIENTS));
  },

  async getRecentTransfers() {
    return Promise.resolve(structuredClone(MOCK_RECENT_TRANSFERS));
  },

  async getBalanceSeries() {
    return Promise.resolve(structuredClone(MOCK_BALANCE_SERIES));
  },

  async sendMoney({ fromAccountId, toRecipientId, amount, note }) {
    // Mock "success" response shape an API would return
    console.log("[mock] sendMoney called with:", { fromAccountId, toRecipientId, amount, note });
    return Promise.resolve({ success: true, transferId: "mock-" + Date.now() });
  },
};
