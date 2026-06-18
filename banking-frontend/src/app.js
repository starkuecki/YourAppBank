// src/app.js
import { router } from './router.js';
import store from './store.js';
import { getCustomerById } from './services/customerService.js';

// Diese Funktion läuft DIREKT beim Laden der Seite los
async function bootstrap() {
    // Gibt es ein Token oder eine gespeicherte Customer-ID im Browser?
    const savedCustomerId = "c4f8d1c2-7b6e-4a91-9f3d-2e8b5a7c1d44"; // Test-ID

    if (savedCustomerId) {
        try {
            // HIER ist die Verbindung zur API! 
            // Wir holen EINMALIG beim App-Start den aktuellen Kunden vom Server.
            const customer = await getCustomerById(savedCustomerId);
            
            // Wir sichern die Daten zentral im Store
            store.currentCustomer = customer;
            
        } catch (error) {
            console.error("Kunde konnte beim Start nicht geladen werden:", error);
        }
    }

    // --- AB HIER ÜBERNIMMT DER ROUTER ---
    
    // 1. Lausche auf URL-Änderungen (z.B. wenn der User auf einen Link klickt)
    window.addEventListener('hashchange', router);
    
    // 2. Rufe den Router das erste Mal manuell auf, um die Startseite anzuzeigen
    router();
}

// App starten
bootstrap();