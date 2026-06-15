// src/services/accountService.js

export function getAccountBalance() {
    // Simuliert eine Netzwerkverzögerung von 1 Sekunde
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({ amount: 2450.75 }); // Das schickt später dein echtes Backend
        }, 1000);
    });
}