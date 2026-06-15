// src/services/api.js
import store from '../store.js';

class ApiService {
    #baseUrl = 'https://api.deine-bank.de/v1'; // Die URL deines Backends

    // Hilfsmethode, um die Standard-Header inklusive JWT-Token zu generieren
    #getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        // Wenn im Store ein Token existiert, hängen wir es automatisch an jeden Request an
        if (store.token) {
            headers['Authorization'] = `Bearer ${store.token}`;
        }

        return headers;
    }

    // Zentrale Methode, die die eigentliche Fetch-Arbeit und Fehlerbehandlung macht
    async #request(endpoint, options = {}) {
        const url = `${this.#baseUrl}${endpoint}`;
        
        // Header automatisch dazumischen
        options.headers = {
            ...this.#getHeaders(),
            ...options.headers
        };

        try {
            const response = await fetch(url, options);

            // Spezialfall fürs Banking: Wenn das Token abgelaufen ist (401 Unauthorized)
            if (response.status === 401) {
                store.logout(); // Store leeren
                window.location.hash = '#/login'; // Nutzer kicken
                throw new Error('Sitzung abgelaufen. Bitte neu anmelden.');
            }

            // Wenn der HTTP-Status nicht im 2xx Bereich liegt
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Server-Fehler: ${response.status}`);
            }

            // Bei erfolgreichen Requests (z.B. 204 No Content) gibt es keinen Body
            if (response.status === 204) return null;

            return await response.json();

        } catch (error) {
            console.error(`API-Fehler bei ${endpoint}:`, error.message);
            throw error; // Fehler weiterwerfen, damit die View darauf reagieren kann
        }
    }

    // --- ÖFFENTLICHE API METHODEN ---

    // Für Daten-Abfragen (GET)
    async get(endpoint) {
        return this.#request(endpoint, { method: 'GET' });
    }

    // Für das Erstellen von Daten (POST, z.B. Überweisungen oder Login)
    async post(endpoint, body) {
        return this.#request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }

    // Für Aktualisierungen (PUT, z.B. Profileinstellungen)
    async put(endpoint, body) {
        return this.#request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    }

    // Für das Löschen von Daten (DELETE, z.B. Konto löschen)
    async delete(endpoint) {
        return this.#request(endpoint, { method: 'DELETE' });
    }
}

const api = new ApiService();
export default api;