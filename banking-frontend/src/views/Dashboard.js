// src/views/Dashboard.js
import { getAccountBalance } from '../services/accountService.js';

export default class Dashboard {
    constructor() {
        this.balanceAmountElement = null; // Referenz merken für spätere Updates
    }

    render() {
        const container = document.createElement('div');
        // Nutzt die seiten-spezifische Klasse aus dem CSS
        container.classList.add('dashboard'); 

        const title = document.createElement('h1');
        title.textContent = 'Willkommen auf deinem Konto';

        const card = document.createElement('div');
        // Nutzt die wiederverwendbare Standard-Klasse aus dem CSS
        card.classList.add('bank-card'); 

        const cardTitle = document.createElement('h3');
        cardTitle.textContent = 'Aktueller Kontostand';

        this.balanceAmountElement = document.createElement('p');
        this.balanceAmountElement.textContent = 'Lädt...';

        card.appendChild(cardTitle);
        card.appendChild(this.balanceAmountElement);
        container.appendChild(title);
        container.appendChild(card);

        return container; 
    }

    async init() {
        try {
            const data = await getAccountBalance();
            // Kein schwerfälliges document.getElementById mehr nötig, 
            // da wir die Referenz direkt halten!
            this.balanceAmountElement.textContent = `${data.amount} €`;
        } catch (error) {
            this.balanceAmountElement.textContent = "Fehler beim Laden";
        }
    }
}