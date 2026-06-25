// src/router.js
import Dashboard  from './views/js/Dashboard.js';
import Accounts   from './views/js/Accounts.js';
import SendMoney  from './views/js/SendMoney.js';
import History    from './views/js/History.js';

const routes = {
    '/':          Dashboard,
    '/accounts':  Accounts,
    '/send':      SendMoney,
    '/history':   History,
};

export function router() {
    // Strip the leading '#' — e.g. '#/accounts' → '/accounts'
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

    // Keep sidebar nav links visually in sync with the active route
    document.querySelectorAll('.nav-item[data-route]').forEach(link => {
        const isActive = link.dataset.route === path;
        link.classList.toggle('is-active', isActive);
    });
}
