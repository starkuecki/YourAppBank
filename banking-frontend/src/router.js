// src/router.js
import Dashboard from './views/Dashboard.js';

const routes = {
    '/': Dashboard
};

export function router() {
    const path = window.location.hash.slice(1) || '/';
    const View = routes[path] || Dashboard;

    const appContainer = document.getElementById('app');
    appContainer.innerHTML = ''; // Vorherige Seite löschen

    const viewInstance = new View();
    
    // 1. Hier wird deine fertige render()-Funktion aufgerufen!
    const domElement = viewInstance.render();
    appContainer.appendChild(domElement); 

    // 2. Hier wird deine init()-Funktion für die API-Daten gestartet!
    if (viewInstance.init) {
        viewInstance.init();
    }
}