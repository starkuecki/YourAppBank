// src/views/Dashboard.js
import { el } from '../../utils/dom.js';
import store from '../../store.js';
import { getAllAccounts, getTransactionsByAccountIban } from '../../services/accountService.js';

export default class Dashboard {

    constructor() {
        this._greetName    = null;
        this._accountCards = null;
        this._recentList   = null;
        this._chartRoot    = null;
    }

    // Builds and returns the full page DOM instantly

    render() {
        // Greeting name 
        this._greetName = el('span', {}, '…');

        const subtitle = el('p', { class: 'page-header__subtitle' },
            `${this._todayLabel()} · Here's your money overview`
        );

        const transferBtn = el('button', { class: 'btn btn--primary' }, '+ New Transfer');
        transferBtn.addEventListener('click', () => { window.location.hash = '#/send'; });

        const header = el('div', { class: 'page-header' },
            el('div', {},
                el('h1', {}, 'Hey there 👋, ', this._greetName),
                subtitle
            ),
            transferBtn
        );

        // Account cards
        this._accountCards = el('section', { class: 'account-cards' },
            this._skeleton('account-card account-card--gray'),
            this._skeleton('account-card account-card--gray'),
            this._skeleton('account-card account-card--gray'),
        );

        // Chart panel
        this._chartRoot = el('div', { id: 'chart-root' });

        const chartPanel = el('div', { class: 'panel' },
            el('div', { class: 'panel__header' },
                el('span', { class: 'panel__title' }, 'Overview'),
            ),
            this._chartRoot,
        );

        // Recent transactions panel
        this._recentList = el('div', { class: 'tx-list' },
            this._skeleton('tx-row'),
            this._skeleton('tx-row'),
            this._skeleton('tx-row'),
            this._skeleton('tx-row'),
        );

        const recentLink = el('a', { class: 'panel__link', href: '#/history' }, 'See all →');

        const recentPanel = el('div', { class: 'panel' },
            el('div', { class: 'panel__header' },
                el('span', { class: 'panel__title' }, 'Recent'),
                recentLink,
            ),
            this._recentList,
        );

        const grid = el('section', { class: 'dashboard-grid' },
            chartPanel,
            recentPanel,
        );

        return el('div', {},
            header,
            this._accountCards,
            grid,
        );
    }

    // Fetches data and updates the DOM nodes

    async init() {
        // Fill in the greeting name from the store 
        const customer = store.currentCustomer;
        if (customer) {
            this._greetName.textContent = customer.name?.split(' ')[0] || 'there';
        }

        try {
            // Use accounts already in the store 
            let accounts = store.accounts;
            if (!accounts || accounts.length === 0) {
                accounts = await getAllAccounts(customer.id);
                store.accounts = accounts;
            }

            // Prefer the current account as the initially selected one;
            // fall back to the first account if none is marked 'current'.
            store.selectedAccount =
                accounts.find(acc => acc.accountType === 'current')
                ?? accounts[0];

            this._renderAccountCards(accounts);

            await this.loadTransactions()


        } catch (err) {
            console.error('Dashboard init error:', err);
            this._recentList.innerHTML = '';
            this._recentList.appendChild(
                el('p', { class: 'text-danger' }, 'Could not load data.')
            );
        }
    }

    async loadTransactions() {
        // Load transactions for the selected account
        const primary = store.selectedAccount;
        if (primary.iban) {
            const transactions = await getTransactionsByAccountIban(primary.iban);
            const sorted = [...transactions].sort(
                (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
            );
            this._renderRecentList(sorted.slice(0, 4));
            this._renderChart(sorted);
        }
    }

    // Private helpers 

    _renderAccountCards(accounts) {
        this._accountCards.innerHTML = '';
        const variants = ['dark', 'blue'];

        accounts.forEach((acc, i) => {
            const variant = variants[i % variants.length];

            const isSelected = store.selectedAccount.iban === acc.iban;

            const card = el('div', { class: `account-card account-card--${variant}${isSelected ? ` account-card--active` : ''}` },
                el('div', { class: 'account-card__top' },
                    el('span', {}, (acc.accountType || 'Account').toUpperCase()),
                    el('span', {}, `•••• ${String(acc.iban || '').slice(-4)}`),
                ),
                el('div', { class: 'account-card__balance' },
                    this._formatCurrency(acc.balance ?? 0)
                ),
            );

            card.style.cursor = 'pointer';
            card.addEventListener('click', () => {
                store.selectedAccount = acc;
                this.loadTransactions();
                this._renderAccountCards(accounts);
            });

            this._accountCards.appendChild(card);
        });
    }

    _renderRecentList(transactions) {
        this._recentList.innerHTML = '';

        if (transactions.length === 0) {
            this._recentList.appendChild(
                el('p', {}, 'No recent transactions.')
            );
            return;
        }

        transactions.forEach(tx => {
            this._recentList.appendChild(this._txRow(tx));
        });
    }

    _renderChart(transactions) {
        this._chartRoot.innerHTML = '';
        if (!transactions.length) return;

        const sorted = [...transactions].sort(
            (a, b) => new Date(a.timestamp) - new Date(b.timestamp)
        );

        // Build running balance series — same logic as before
        let running = 0;
        const labels = [];
        const values = [];

        sorted.forEach(tx => {
            if (tx.transactionType === 'deposit') {
                running += tx.amount;
            } else {
                running -= tx.amount;
            }
            labels.push(this._shortDate(tx.timestamp));
            values.push(running);
        });

        // Create a canvas element 
        const canvas = document.createElement('canvas');
        //this._chartRoot.innerHTML = '';
        this._chartRoot.appendChild(canvas);

        // One call — Chart.js does all the rest
        new Chart(canvas, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    data: values,
                    borderColor: 'var(--blue-mid)',
                    backgroundColor: 'rgba(126, 200, 227, 0.15)',
                    fill: true,
                    tension: 0.4,        
                    pointRadius: 2.5,
                    borderWidth: 1.5,
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                },
                scales: {
                    y: {
                        ticks: {
                            callback: value => `$${value.toFixed(0)}`
                        }
                    }
                }
            }
        });
    }
    // Builds a single transaction row 
    _txRow(tx) {
        const isIncome = tx.transactionType === 'deposit';

        const icon = el('div',
            { class: `tx-row__icon${isIncome ? ' tx-row__icon--income' : ''}` },
            isIncome ? '↑' : '↓'
        );

        const dateStr = tx.timestamp
            ? new Date(tx.timestamp).toLocaleString('en-GB', {
                day: '2-digit', month: 'short',
                hour: '2-digit', minute: '2-digit',
            })
            : '';

        const body = el('div', { class: 'tx-row__body' },
            el('div', { class: 'tx-row__title' }, tx.purpose || tx.description || 'Transaction'),
            el('div', { class: 'tx-row__meta'  }, dateStr),
        );

        const sign   = isIncome ? '+' : '−';
        const amount = el('div',
            { class: `tx-row__amount ${isIncome ? 'text-success' : 'text-danger'}` },
            `${sign}$${Math.abs(tx.amount ?? 0).toFixed(2)}`
        );

        return el('div', { class: 'tx-row' }, icon, body, amount);
    }

    // Builds a grey animated skeleton placeholder block
    _skeleton(extraClasses = '') {
        const s = el('div', { class: extraClasses });
        s.style.cssText = 'min-height:80px;background:var(--secondary);border-radius:var(--radius);animation:pulse 1.5s ease-in-out infinite;';
        return s;
    }

    _formatCurrency(amount) {
        return Math.abs(amount).toLocaleString('en-US', {
            style: 'currency', currency: 'USD',
        });
    }

    _todayLabel() {
        return new Date().toLocaleDateString('en-US', {
            weekday: 'long', month: 'long', day: 'numeric',
        });
    }

    _shortDate(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        return `${d.getDate()} ${d.toLocaleString('en-US', { month: 'short' })}`;
    }
}
