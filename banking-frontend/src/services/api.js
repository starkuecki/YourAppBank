// src/services/api.js
import store from '../store.js';

class ApiService {
    #baseUrl = 'http://localhost:8080/v1';

    // Hilfsmethode, um die Standard-Header inklusive JWT-Token zu generieren
    #getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
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

            if (response.status === 204) return null;

            // Wenn der Server einen Fehler wirft (400, 404, 422, 409)
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                
                // Wir extrahieren 'detail' oder 'title' aus dem RFC-9457 ProblemDetail
                const message = errorData.detail || errorData.title || `Server-Fehler: ${response.status}`;
                const error = new Error(message);
                
                // Falls Validierungsfehler mitkommen (z.B. Feld-Verletzungen), hängen wir sie an
                error.violations = errorData.violations || [];
                error.status = response.status;
                
                throw error;
            }

            return await response.json();
        } catch (error) {
            console.error(`API-Fehler bei ${endpoint}:`, error.message);
            throw error; 
        }
    }

    // --- ÖFFENTLICHE API METHODEN ---

    async get(endpoint) { return this.#request(endpoint, { method: 'GET' }); }
    async post(endpoint, body) { return this.#request(endpoint, { method: 'POST', body: JSON.stringify(body) }); }
    async delete(endpoint) { return this.#request(endpoint, { method: 'DELETE' }); }
}

const api = new ApiService();
export default api;