// src/views/History.js
import { el } from '../../utils/dom.js';
import store from '../../store.js';
import { getTransactionsByAccountIban } from '../../services/accountService.js';

const CATEGORIES = ['All', 'Deposits', 'Withdrawals'];

export default class History {

    constructor() {
        this._listEl         = null;
        this._pillsEl        = null;
        this._searchInput    = null;
        this._allTx          = [];      // full unfiltered list from API
        this._activeCategory = 'All';
    }

    render() {
        const header = el('div', { class: 'page-header' },
            el('h1', {}, 'History 📋'),
        );

        // Search bar
        const searchIcon = el('span', { class: 'search-input-wrap__icon' }, '🔍');

        this._searchInput = el('input', {
            type: 'text',
            placeholder: 'Search transactions…',
        });
        this._searchInput.addEventListener('input', () => this._applyFilters());

        const searchWrap = el('div', { class: 'search-input-wrap' },
            searchIcon,
            this._searchInput,
        );

        const filterBtn = el('button', { class: 'filter-btn' }, '⚡ Filter');

        const searchBar = el('div', { class: 'search-bar' },
            searchWrap,
            filterBtn,
        );

        // Category pills — built by _renderPills()
        this._pillsEl = el('div', { class: 'category-pills' });
        this._renderPills();

        // Transaction list — skeletons until init() loads real data
        this._listEl = el('div', { class: 'history-list' },
            this._skeleton(),
            this._skeleton(),
            this._skeleton(),
            this._skeleton(),
            this._skeleton(),
        );

        return el('div', {},
            header,
            searchBar,
            this._pillsEl,
            this._listEl,
        );
    }

    async init() {
        try {
            const account = store.selectedAccount;

            if (!account?.iban) {
                this._showMessage('No account selected. Go to Accounts first and click Send.');
                return;
            }

            const transactions = await getTransactionsByAccountIban(account.iban);

            // Store full list, newest first
            this._allTx = [...transactions].sort(
                (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
            );

            this._applyFilters();
        } catch (err) {
            console.error('History init error:', err);
            this._showMessage('Could not load transactions.', true);
        }
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    _renderPills() {
        this._pillsEl.innerHTML = '';

        CATEGORIES.forEach(cat => {
            const pill = el('button',
                { class: `pill${cat === this._activeCategory ? ' is-active' : ''}` },
                cat
            );
            pill.addEventListener('click', () => {
                this._activeCategory = cat;
                this._renderPills();     // re-render pills with new active state
                this._applyFilters();    // re-filter the list
            });
            this._pillsEl.appendChild(pill);
        });
    }

    _applyFilters() {
        const query = (this._searchInput?.value || '').toLowerCase();
        const cat   = this._activeCategory;

        const filtered = this._allTx.filter(tx => {
            const matchesCat =
                cat === 'All' ||
                (cat === 'Deposits'    && tx.transactionType === 'deposit') ||
                (cat === 'Withdrawals' && tx.transactionType === 'withdrawal');
            const description   = (tx.purpose || tx.description || '').toLowerCase();
            const matchesSearch = !query || description.includes(query);
            return matchesCat && matchesSearch;
        });

        this._renderList(filtered);
    }

    _renderList(transactions) {
        this._listEl.innerHTML = '';

        if (transactions.length === 0) {
            this._showMessage('No transactions found.');
            return;
        }

        transactions.forEach(tx => {
            this._listEl.appendChild(this._txRow(tx));
        });
    }

    _txRow(tx) {
        const isIncome = tx.transactionType === 'deposit';

        const icon = el('div',
            { class: `tx-row__icon${isIncome ? ' tx-row__icon--income' : ''}` },
            isIncome ? '↑' : '↓'
        );

        const dateStr = tx.timestamp
            ? new Date(tx.timestamp).toLocaleDateString('en-GB', {
                day: '2-digit', month: 'short', year: 'numeric',
            })
            : '';

        const body = el('div', { class: 'tx-row__body' },
            el('div', { class: 'tx-row__title' },
                tx.purpose || tx.description || 'Transaction'
            ),
            el('div', { class: 'tx-row__meta' },
                [tx.transactionType, dateStr].filter(Boolean).join(' · ')
            ),
        );

        const sign   = isIncome ? '+' : '−';
        const amount = el('div',
            { class: `tx-row__amount ${isIncome ? 'text-success' : 'text-danger'}` },
            `${sign}$${Math.abs(tx.amount ?? 0).toFixed(2)}`
        );

        return el('div', { class: 'tx-row' }, icon, body, amount);
    }

    _showMessage(text, isError = false) {
        this._listEl.innerHTML = '';
        const p = el('p', { class: isError ? 'text-danger' : '' }, text);
        p.style.cssText = 'padding:40px;text-align:center;color:var(--muted-foreground);';
        this._listEl.appendChild(p);
    }

    _skeleton() {
        const s = el('div', {});
        s.style.cssText = 'height:64px;background:var(--secondary);border-radius:var(--radius);margin:4px 0;animation:pulse 1.5s ease-in-out infinite;';
        return s;
    }
}
