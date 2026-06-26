// src/views/js/login.js
import store from '../../store.js';
import { getAllCustomers } from '../../services/customerService.js';

export default class Login {
    constructor() { this.container = null; }

    render() {
        this.container = document.createElement('div');
        this.container.classList.add('login-wrapper'); 
        this.container.innerHTML = `
            <div class="panel" style="max-width: 400px; margin: 100px auto; padding: 40px; background: var(--color-white);">
                <h2 style="margin-bottom: 8px; text-align: center;">Booby Bank ✦</h2>
                <p style="color: var(--color-text-muted); text-align: center; font-size: var(--fs-sm); margin-bottom: 24px;">money, but chill</p>
                <form id="login-form">
                    <div class="field-block">
                        <label class="field-label">Benutzername / Name</label>
                        <input type="text" id="login-username" class="text-input" value="Max Musterfrau" required>
                    </div>
                    <div class="field-block">
                        <label class="field-label">Passwort</label>
                        <input type="password" id="login-password" class="text-input" value="chillig123" required>
                    </div>
                    <button type="submit" class="btn btn--primary" style="width: 100%; justify-content: center; margin-top: 16px;">Einloggen →</button>
                </form>
                <div id="login-error" style="color: var(--destructive); margin-top: 16px; text-align: center; font-size: var(--fs-sm); font-weight: 600;"></div>
            </div>
        `;
        return this.container;
    }

    async init() {
        const form = this.container.querySelector('#login-form');
        const errorDiv = this.container.querySelector('#login-error');

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorDiv.textContent = "";

            const usernameInput = this.container.querySelector('#login-username').value.trim();

            try {
                //const customers = await getAllCustomers();
                //const customer = customers.find(c => c.name.toLowerCase() === usernameInput.toLowerCase());

                if (true) {
                    // OHNE TOKEN: Nur das Kundenobjekt wird zentral abgelegt
                    //store.currentCustomer = customer; 
                    window.location.hash = '/dashboard';
                } else {
                    errorDiv.textContent = "Benutzername existiert nicht.";
                }
            } catch (err) {
                errorDiv.textContent = "Fehler bei der Authentifizierung.";
                console.error(err);
            }
        });
    }
}