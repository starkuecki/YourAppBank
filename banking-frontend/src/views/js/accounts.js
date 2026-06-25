// src/views/Accounts.js
import { el } from '../../utils/dom.js';
import store from '../../store.js';
import { getAllAccounts } from '../../services/accountService.js';

export default class Accounts {

    constructor() {
        this._totalValueEl = null;
        this._listEl       = null;
    }

    render() {
        // Header
        const openBtn = el('button', { class: 'btn btn--primary' }, '+ Open Account');

        const header = el('div', { class: 'page-header' },
            el('h1', {}, 'Your Accounts'),
            openBtn,
        );

        // Total assets banner — value filled in init()
        this._totalValueEl = el('div', { class: 'total-assets__value' }, '—');

        const totalBanner = el('section', { class: 'total-assets' },
            el('div', { class: 'total-assets__label' }, 'Total Assets'),
            this._totalValueEl,
        );

        // Account list — skeletons until init() loads real data
        this._listEl = el('section', { class: 'account-list' },
            this._skeleton(),
            this._skeleton(),
            this._skeleton(),
        );

        return el('div', {},
            header,
            totalBanner,
            this._listEl,
        );
    }

    async init() {
        try {
            let accounts = store.accounts;
            if (!accounts || accounts.length === 0) {
                accounts = await getAllAccounts(store.currentCustomer?.id);
                store.accounts = accounts;
            }

            // Total across all accounts
            const total = accounts.reduce((sum, a) => sum + (a.balance ?? 0), 0);
            this._totalValueEl.textContent = this._formatCurrency(total);

            this._renderList(accounts);
        } catch (err) {
            console.error('Accounts init error:', err);
            this._listEl.innerHTML = '';
            this._listEl.appendChild(
                el('p', { class: 'text-danger' }, 'Could not load accounts.')
            );
        }
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    _renderList(accounts) {
        this._listEl.innerHTML = '';

        accounts.forEach(acc => {
            const sendBtn = el('button', { class: 'btn btn--outline btn--sm' }, '↗ Send');
            sendBtn.addEventListener('click', () => {
                store.selectedAccount = acc;
                window.location.hash = '#/send';
            });

            const receiveBtn = el('button', { class: 'btn btn--outline btn--sm' }, '↙ Receive');

            const row = el('div', { class: 'account-row' },
                el('div', {},
                    el('div', { class: 'account-row__name-line' },
                        el('span', { class: 'account-row__name' },
                            acc.accountName || acc.accountType || 'Account'
                        ),
                        el('span', { class: 'badge badge--active' }, '✓ Active'),
                        acc.apy
                            ? el('span', { class: 'badge badge--apy' }, `APY ${acc.apy}%`)
                            : null,
                    ),
                    el('div', { class: 'account-row__iban' }, acc.iban || '—'),
                    el('div', { class: 'account-row__type' },
                        `${acc.accountType || 'Account'} · ${acc.currency || 'USD'}`
                    ),
                    el('div', { class: 'account-row__actions' }, sendBtn, receiveBtn),
                ),
                el('div', { class: 'account-row__balance' },
                    this._formatCurrency(acc.balance ?? 0)
                ),
            );

            // el() ignores null children — safe to pass the optional APY badge
            // but we need to remove any null entries manually here
            row.querySelectorAll('null').forEach(n => n.remove());

            this._listEl.appendChild(row);
        });
    }

    _skeleton() {
        const s = el('div', {});
        s.style.cssText = 'height:140px;background:var(--secondary);border-radius:var(--radius);animation:pulse 1.5s ease-in-out infinite;';
        return s;
    }

    _formatCurrency(amount) {
        return Math.abs(amount).toLocaleString('en-US', {
            style: 'currency', currency: 'USD',
        });
    }
}
