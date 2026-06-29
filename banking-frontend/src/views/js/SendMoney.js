// src/views/SendMoney.js
import { el } from '../../utils/dom.js';
import store from '../../store.js';
import { getAllAccounts, getAccountByIban, postWithdrawal, postDeposit } from '../../services/accountService.js';

export default class SendMoney {

    constructor() {
        this._fromSelect   = null;
        this._ibanInput    = null;
        this._amountInput  = null;
        this._purposeInput = null;
        this._submitBtn    = null;
        this._statusMsg    = null;
    }

    render() {
        // FROM select
        this._fromSelect = el('select', { class: 'select-box' },
            el('option', {}, 'Loading accounts…')
        );
        this._fromSelect.options[0].disabled = true;
        this._fromSelect.options[0].selected = true;

        const fromBlock = el('div', { class: 'field-block' },
            el('label', { class: 'field-label' }, 'From'),
            this._fromSelect,
        );

        // TO — recipient IBAN typed by user
        this._ibanInput = el('input', {
            class: 'text-input',
            type: 'text',
            placeholder: 'e.g. DE89 3704 0044 0532 0130 00',
        });
        this._ibanInput.addEventListener('input', () => this._updateSubmitState());

        const toBlock = el('div', { class: 'field-block' },
            el('label', { class: 'field-label' }, 'To (IBAN)'),
            this._ibanInput,
        );

        // AMOUNT
        this._amountInput = el('input', {
            type: 'number',
            placeholder: '0.00',
            min: '0.01',
            step: '0.01',
        });
        this._amountInput.addEventListener('input', () => this._updateSubmitState());

        const amountWrap = el('div', { class: 'amount-input-wrap' },
            el('span', { class: 'currency-prefix' }, '$'),
            this._amountInput,
        );

        const amountBlock = el('div', { class: 'field-block' },
            el('label', { class: 'field-label' }, 'Amount'),
            amountWrap,
        );

        // PURPOSE / NOTE
        this._purposeInput = el('input', {
            class: 'text-input',
            type: 'text',
            placeholder: "What's this for? 🍕",
        });

        const purposeBlock = el('div', { class: 'field-block' },
            el('label', { class: 'field-label' }, 'Note (optional)'),
            this._purposeInput,
        );

        // SUBMIT button — disabled until form is valid
        this._submitBtn = el('button',
            { class: 'btn btn--disabled send-submit' },
            'Send Money →'
        );
        this._submitBtn.disabled = true;
        this._submitBtn.addEventListener('click', () => this._handleSubmit());

        // Status message — shown after submit attempt
        this._statusMsg = el('p', {});

        const form = el('div', {},
            fromBlock,
            toBlock,
            amountBlock,
            purposeBlock,
            this._submitBtn,
            this._statusMsg,
        );

        const grid = el('section', { class: 'send-grid' },
            form,
            el('aside', {}),
        );

        return el('div', {},
            el('div', { class: 'page-header' },
                el('h1', {}, 'Send Money 🚀'),
            ),
            grid,
        );
    }

    async init() {
        try {
            let accounts = store.accounts;
            if (!accounts || accounts.length === 0) {
                accounts = await getAllAccounts(store.currentCustomer.id);
                store.accounts = accounts;
            }

            // Populate the FROM dropdown with real accounts
            this._fromSelect.innerHTML = '';
            accounts.forEach(acc => {
                const displayType = acc.accountType === 'current' ? 'Current Account' : 'Savings Account';
                const opt = el('option', { value: acc.iban },
                    `${displayType} – ${this._formatCurrency(acc.balance ?? 0)}`
                );
                // Pre-select whichever account the user clicked Send from
                if (store.selectedAccount?.iban === acc.iban) {
                    opt.selected = true;
                }
                this._fromSelect.appendChild(opt);
            });
        } catch (err) {
            console.error('SendMoney init error:', err);
        }
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    async _handleSubmit() {
        const fromIban = this._fromSelect.value;
        const toIban   = this._ibanInput.value.trim();
        const amount   = parseFloat(this._amountInput.value);
        const purpose  = this._purposeInput.value.trim() || 'Transfer';

        this._submitBtn.disabled = true;
        this._submitBtn.textContent = 'Sending…';
        this._statusMsg.textContent = '';
        this._statusMsg.className = '';

        try {
            // 0. Zielkonto vorher validieren — bevor irgendwas abgebucht wird
            try {
                await getAccountByIban(toIban);
            } catch {
                throw new Error(`Recipient account ${toIban} not found.`);
            }

            // 1. Geld beim Sender abbuchen
            await postWithdrawal(fromIban, amount, purpose);

            // 2. Geld beim Empfänger gutschreiben
            try {
                await postDeposit(toIban, amount, purpose);
            } catch (depositErr) {
                // Withdrawal war schon erfolgreich, Deposit ist fehlgeschlagen.
                // Das Geld ist jetzt "verschwunden" — klare Fehlermeldung statt Stille.
                console.error('Kritisch: Withdrawal erfolgreich, aber Deposit fehlgeschlagen.', depositErr);
                throw new Error(
                    `Withdrawal succeeded but deposit failed. Please contact support — your money may be stuck. (${depositErr.message})`
                );
            }
            this._statusMsg.className = 'text-success';
            this._statusMsg.textContent = `✓ $${amount.toFixed(2)} sent to ${toIban} successfully!`;

            // Reset form fields
            this._ibanInput.value    = '';
            this._amountInput.value  = '';
            this._purposeInput.value = '';

            // Refresh account balances in the store
            const accounts = await getAllAccounts(store.currentCustomer?.id);
            store.accounts = accounts;

        } catch (err) {
            this._statusMsg.className = 'text-danger';
            this._statusMsg.textContent = `✗ ${err.message}`;
        } finally {
            this._updateSubmitState();
        }
    }

    _updateSubmitState() {
        const hasIban  = this._ibanInput.value.trim().length > 0;
        const amount   = parseFloat(this._amountInput.value);
        const valid    = hasIban && amount > 0;

        this._submitBtn.disabled  = !valid;
        this._submitBtn.className = valid
            ? 'btn btn--primary send-submit'
            : 'btn btn--disabled send-submit';
    }

    _formatCurrency(amount) {
        return Math.abs(amount).toLocaleString('en-US', {
            style: 'currency', currency: 'USD',
        });
    }
}
