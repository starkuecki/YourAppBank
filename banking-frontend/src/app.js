import { router } from './router.js';

// Lausche auf Vor-/Zurück-Buttons im Browser und Klicks
window.addEventListener('hashchange', router);
window.addEventListener('DOMContentLoaded', router);