// src/store.js

class BankStore {
    #currentCustomer = null;
    #accounts = [];
    #selectedAccount = null;

    // --- Getter ---
    get currentCustomer() { 
        return this.#currentCustomer; 
    }

    get accounts() {
        return this.#accounts;
    }

    get selectedAccount() {
        return this.#selectedAccount;
    }

    // --- Setter ---
    set currentCustomer(customer) {
        this.#currentCustomer = customer;
        this.#accounts = []; // Wenn wir einen neuen Kunden setzen, leeren wir die Accounts
        this.#selectedAccount = null; // Und auch die ausgewählte Account-Info
    }

    set accounts(accountsList) {
        this.#accounts = accountsList;
        if (accountsList.length > 0 && !this.#selectedAccount) {
            this.#selectedAccount = accountsList[0]; // Standardmäßig die erste Account auswählen
        }
    }

    set selectedAccount(account) {
        this.#selectedAccount = account;
    }

    clear() {
        this.#currentCustomer = null;
        this.#accounts = [];
        this.#selectedAccount = null;
    }
}
// Wir exportieren wieder eine EINZIGE Instanz der Klasse (Singleton)
const store = new BankStore();
export default store;