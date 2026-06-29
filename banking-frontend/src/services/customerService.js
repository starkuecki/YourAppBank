import api from './api.js';

export async function getAllCustomers(){
    return await api.get('/customers');
}

export async function getCustomerById(id){
    return await api.get(`/customers/${id}`);
}

export async function createCustomer(customerData){
    return await api.post('/customers', customerData);
}