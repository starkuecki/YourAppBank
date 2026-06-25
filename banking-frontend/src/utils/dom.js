// src/utils/dom.js
export function el(type, attributes = {}, ...children) {
    const element = document.createElement(type);
    
    // Attribute und Klassen zuweisen
    for (let key in attributes) {
        if (key === 'class') {
            element.className = attributes[key];
        } else {
            element.setAttribute(key, attributes[key]);
        }
    }
    
    // Kinder (Text oder andere Elemente) hinzufügen
    children.forEach(child => {
        if (typeof child === 'string') {
            element.appendChild(document.createTextNode(child));
        } else if (child instanceof HTMLElement) {
            element.appendChild(child);
        }
    });
    
    return element;
}