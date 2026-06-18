// src/services/accountService.js

import api from './api.js';

export async function getAllAccounts(ownerId = null){
    const endpoint = ownerId ? `/accounts?ownerId=${ownerId}` : '/accounts';
    return await api.get(endpoint);
}

export async function getAccountByIban(iban){
    return await api.get(`/accounts/${iban}`);
}

export async function getTransactionsByAccountIban(iban){
    return await api.get(`/accounts/${iban}/transactions`);
}

export async function postDeposit(iban, amount, purpose){
    return await api.post(`/accounts/${iban}/deposit`, { 
        amount: parseFloat(amount),
        purpose: purpose,
        timestamp: new Date().toISOString()
    });
}

export async function postWithdrawal(iban, amount, purpose){
    return await api.post(`/accounts/${iban}/withdrawal`, { 
        amount: parseFloat(amount),
        purpose: purpose,
        timestamp: new Date().toISOString()
    });
}