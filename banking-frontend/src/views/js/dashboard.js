// src/views/Dashboard.js
import { el } from '../../utils/dom.js';
import store from '../../store.js';
import { getAllAccounts, getTransactionsByAccountIban } from '../../services/accountService.js';

export default class Dashboard {

    constructor() {
        // We save references to nodes we'll update in init()
        this._greetName    = null;
        this._accountCards = null;
        this._recentList   = null;
        this._chartRoot    = null;
    }

    // ─── render() ────────────────────────────────────────────────────────────
    // Builds and returns the full page DOM instantly (no API calls here).
    // Anything that needs real data shows a skeleton placeholder.

    render() {
        // Greeting name — we fill this in init() once we have store data
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

        // Account cards — skeleton until init() loads real accounts
        this._accountCards = el('section', { class: 'account-cards' },
            this._skeleton('account-card account-card--gray'),
            this._skeleton('account-card account-card--gray'),
            this._skeleton('account-card account-card--gray'),
        );

        // Chart panel (left side of bottom grid)
        this._chartRoot = el('div', { id: 'chart-root' });

        const chartPanel = el('div', { class: 'panel' },
            el('div', { class: 'panel__header' },
                el('span', { class: 'panel__title' }, 'Overview'),
            ),
            this._chartRoot,
        );

        // Recent transactions panel (right side of bottom grid)
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

    // ─── init() ──────────────────────────────────────────────────────────────
    // Runs right after render(). Fetches data and updates the DOM nodes
    // that render() already put on screen.

    async init() {
        // Fill in the greeting name from the store (already loaded by app.js)
        const customer = store.currentCustomer;
        if (customer) {
            this._greetName.textContent = customer.name?.split(' ')[0] || 'there';
        }

        try {
            // Use accounts already in the store — no extra request needed
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
        // Load transactions for the primary (selected) account
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

    // ─── Private helpers ─────────────────────────────────────────────────────

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

        // Build a running balance series
        let running = 0;
        const series = sorted.map(tx => {
             if (tx.transactionType === 'deposit') {
                running += tx.amount ?? 0;
            } else {
                running -= tx.amount ?? 0;
            }
            running += tx.amount ?? 0;
            return {
                label: this._shortDate(tx.timestamp),
                value: running,
            };
        });

        const W = 680, H = 280;
        const pad = { top: 20, right: 20, bottom: 30, left: 50 };
        const iW = W - pad.left - pad.right;
        const iH = H - pad.top - pad.bottom;

        const values = series.map(d => d.value);
        const minV   = Math.min(...values);
        const maxV   = Math.max(...values);
        const range  = maxV - minV || 1;

        const pts = series.map((d, i) => ({
            x: pad.left + (i / (series.length - 1 || 1)) * iW,
            y: pad.top + iH - ((d.value - minV) / range) * iH,
            label: d.label,
        }));

        const linePath = pts.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ');
        const areaPath = `${linePath} L ${pts.at(-1).x} ${pad.top + iH} L ${pts[0].x} ${pad.top + iH} Z`;

        const NS  = 'http://www.w3.org/2000/svg';
        const svg = document.createElementNS(NS, 'svg');
        svg.setAttribute('viewBox', `0 0 ${W} ${H}`);
        svg.setAttribute('width',  '100%');
        svg.setAttribute('height', 'auto');

        // Gradient fill under the line
        const defs  = document.createElementNS(NS, 'defs');
        const grad  = document.createElementNS(NS, 'linearGradient');
        grad.id = 'areaFill';
        grad.setAttribute('x1', '0'); grad.setAttribute('y1', '0');
        grad.setAttribute('x2', '0'); grad.setAttribute('y2', '1');
        const s1 = document.createElementNS(NS, 'stop');
        s1.setAttribute('offset', '0%');
        s1.setAttribute('stop-color', 'var(--blue-mid)');
        s1.setAttribute('stop-opacity', '0.3');
        const s2 = document.createElementNS(NS, 'stop');
        s2.setAttribute('offset', '100%');
        s2.setAttribute('stop-color', 'var(--blue-mid)');
        s2.setAttribute('stop-opacity', '0');
        grad.appendChild(s1); grad.appendChild(s2);
        defs.appendChild(grad);
        svg.appendChild(defs);

        const area = document.createElementNS(NS, 'path');
        area.setAttribute('d', areaPath);
        area.setAttribute('fill', 'url(#areaFill)');
        svg.appendChild(area);

        const line = document.createElementNS(NS, 'path');
        line.setAttribute('d', linePath);
        line.setAttribute('fill', 'none');
        line.setAttribute('stroke', 'var(--blue-mid)');
        line.setAttribute('stroke-width', '2.5');
        svg.appendChild(line);

        // X-axis labels
        const step = Math.max(1, Math.floor(pts.length / 6));
        pts.forEach((p, i) => {
            if (i % step !== 0 && i !== pts.length - 1) return;
            const t = document.createElementNS(NS, 'text');
            t.setAttribute('x', p.x);
            t.setAttribute('y', H - 6);
            t.setAttribute('font-size', '11');
            t.setAttribute('fill', 'var(--muted-foreground)');
            t.setAttribute('text-anchor', 'middle');
            t.textContent = p.label;
            svg.appendChild(t);
        });


        this._chartRoot.appendChild(svg);
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
