// src/app.js
import { router } from './router.js';
import store from './store.js';
import { getCustomerById } from './services/customerService.js';
import { getAllAccounts }  from './services/accountService.js';

// ─── Sidebar ────────────────────────────────────────────────────────────────
// The sidebar is permanent shell UI — it lives outside #app so the router
// never wipes it. We build it once here with createElement (same rule as views).

function buildSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    // Brand
    const brand = document.createElement('div');
    brand.className = 'sidebar__brand';

    const logo = document.createElement('img');
    logo.src = 'assets/logo.png';
    logo.alt = 'Booby Bank';
    logo.className = 'sidebar__logo';

    const brandText = document.createElement('div');

    const brandName = document.createElement('div');
    brandName.className = 'sidebar__brand-name';
    brandName.textContent = 'Booby Bank';

    const brandTag = document.createElement('div');
    brandTag.className = 'sidebar__brand-tag';
    brandTag.textContent = 'money, but chill ✦';

    brandText.appendChild(brandName);
    brandText.appendChild(brandTag);
    brand.appendChild(logo);
    brand.appendChild(brandText);

    // Nav
    const nav = document.createElement('nav');
    nav.className = 'sidebar__nav';

    const navItems = [
        { route: '/',         label: 'Dashboard',   icon: navIcon('dashboard') },
        { route: '/accounts', label: 'Accounts',    icon: navIcon('accounts')  },
        { route: '/send',     label: 'Send Money',  icon: navIcon('send')      },
        { route: '/history',  label: 'History',     icon: navIcon('history')   },
    ];

    navItems.forEach(({ route, label, icon }) => {
        const a = document.createElement('a');
        a.href = `#${route}`;
        a.className = 'nav-item';
        a.dataset.route = route;

        a.appendChild(icon);

        const labelEl = document.createElement('span');
        labelEl.className = 'nav-item__label';
        labelEl.textContent = label;
        a.appendChild(labelEl);

        nav.appendChild(a);
    });

    // Footer (user info — populated after bootstrap)
    const footer = document.createElement('div');
    footer.className = 'sidebar__footer';
    footer.id = 'sidebar-footer';

    sidebar.appendChild(brand);
    sidebar.appendChild(nav);
    sidebar.appendChild(footer);
}

function updateSidebarUser() {
    const footer = document.getElementById('sidebar-footer');
    if (!footer) return;
    footer.innerHTML = '';

    const customer = store.currentCustomer;
    if (!customer) return;

    const nameParts = (customer.name || '').split(' ');
    const initials = nameParts.map(p => p[0]).join('').slice(0, 2).toUpperCase();

    const avatar = document.createElement('div');
    avatar.className = 'avatar';
    avatar.textContent = initials;

    const info = document.createElement('div');

    const name = document.createElement('div');
    name.className = 'sidebar__user-name';
    name.textContent = customer.name;

    const plan = document.createElement('div');
    plan.className = 'sidebar__user-plan';
    plan.textContent = 'Premium ✦';

    info.appendChild(name);
    info.appendChild(plan);

    footer.appendChild(avatar);
    footer.appendChild(info);
}

// ─── SVG icons (same set as before, now as helpers) ──────────────────────────

const SVG_ICONS = {
    dashboard: '<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>',
    accounts:  '<path d="M3 10l9-6 9 6"/><path d="M4 10v9h16v-9"/><path d="M9 19v-6h6v6"/>',
    send:      '<path d="M7 16V8l-4 4 4 4z"/><path d="M3 12h12"/><path d="M13 5l8 7-8 7"/>',
    history:   '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/>',
    settings:  '<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.2a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.9.3l-.1-.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.2a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.2a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.2a1.7 1.7 0 0 0-1.5 1z"/>',
};

export function navIcon(name) {
    const wrap = document.createElement('span');
    wrap.className = 'nav-item__icon';
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('viewBox', '0 0 24 24');
    svg.setAttribute('width', '20');
    svg.setAttribute('height', '20');
    svg.setAttribute('fill', 'none');
    svg.setAttribute('stroke', 'currentColor');
    svg.setAttribute('stroke-width', '2');
    svg.innerHTML = SVG_ICONS[name] || '';
    wrap.appendChild(svg);
    return wrap;
}


// Diese Funktion läuft DIREKT beim Laden der Seite los
async function bootstrap() {
    buildSidebar();
    // Gibt es ein Token oder eine gespeicherte Customer-ID im Browser?
    const savedCustomerId = "11111111-1111-1111-1111-111111111111"; // Test-ID

    if (savedCustomerId) {
        try {
            // HIER ist die Verbindung zur API! 
            // Wir holen EINMALIG beim App-Start den aktuellen Kunden vom Server.
            const customer = await getCustomerById(savedCustomerId);
            
            // Wir sichern die Daten zentral im Store
            store.currentCustomer = customer;

            // Also pre-load accounts into the store so every view can use them
            const accounts = await getAllAccounts(savedCustomerId);
            store.accounts = accounts;

            updateSidebarUser();
        } catch (error) {
            console.error("Kunde konnte beim Start nicht geladen werden:", error);
        }
    }

    // --- AB HIER ÜBERNIMMT DER ROUTER ---
    
    // 1. Lausche auf URL-Änderungen (z.B. wenn der User auf einen Link klickt)
    window.addEventListener('hashchange', router);
    
    // 2. Rufe den Router das erste Mal manuell auf, um die Startseite anzuzeigen
    router();

    initSupportModal();
}

function initSupportModal() {
    const helpBtn = document.getElementById('helpBtn');
    const supportModal = document.getElementById('supportModal');
    const closeHelpBtn = document.getElementById('closeHelpBtn');

    // Falls die Elemente auf der aktuellen Seite nicht existieren, breche ab
    if (!helpBtn || !supportModal || !closeHelpBtn) return;

    // Pop-up öffnen
    helpBtn.addEventListener('click', () => {
        supportModal.classList.add('is-open');
    });

    // Pop-up über das 'X' schließen
    closeHelpBtn.addEventListener('click', () => {
        supportModal.classList.remove('is-open');
    });

    // Pop-up schließen, wenn man außerhalb des Fensters klickt
    supportModal.addEventListener('click', (event) => {
        if (event.target === supportModal) {
            supportModal.classList.remove('is-open');
        }
    });
}

// App starten
bootstrap();

