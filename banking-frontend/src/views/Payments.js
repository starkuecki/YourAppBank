// src/views/Payments.js
import { el } from '../utils/dom.js';

export default class Payments {
    constructor() {
        this.amountInput = null;
    }

    // HIER kommt die render-Funktion hin!
    render() {
        this.amountInput = el('input', { type: 'number', placeholder: 'Betrag' });
        const sendButton = el('button', {}, 'Geld senden');

        // Das Haupt-Element dieser Seite zusammenbauen
        const pageContainer = el('div', { class: 'payments-page' },
            el('h1', {}, 'Zahlung tätigen'),
            this.amountInput,
            sendButton
        );

        // WICHTIG: Am Ende immer das fertige Element zurückgeben
        return pageContainer;
    }

    init() {
        // Hier kommt die Logik hin, z.B. Event-Listener für den Button
    }
}