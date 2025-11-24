import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// Add request interceptor
api.interceptors.request.use((config) => {
    console.log(`Making ${config.method.toUpperCase()} request to: ${config.url}`);
    return config;
});

// Add response interceptor
api.interceptors.response.use(
    (response) => {
        console.log('Response received:', response.data);
        return response;
    },
    (error) => {
        console.error('API Error:', {
            message: error.message,
            response: error.response?.data,
            status: error.response?.status
        });
        return Promise.reject(error);
    }
);

export const customerService = {
    getAllCustomers: () => api.get('/customers'),
    getCustomersForSelect: () => api.get('/customers/select'),
    getCustomerById: (id) => api.get(`/customers/${id}`),
    createCustomer: (customer) => api.post('/customers', customer),
    updateCustomer: (id, customer) => api.put(`/customers/${id}`, customer),
    deleteCustomer: (id) => api.delete(`/customers/${id}`)
};

export const vehicleService = {
    getAllVehicles: () => api.get('/vehicles'),
    getVehiclesForSelect: () => api.get('/vehicles/select'),
    getVehicleById: (id) => api.get(`/vehicles/${id}`),
    createVehicle: (vehicle) => api.post('/vehicles', vehicle),
    updateVehicle: (id, vehicle) => api.put(`/vehicles/${id}`, vehicle),
    deleteVehicle: (id) => api.delete(`/vehicles/${id}`)
};

export const technicianService = {
    getAllTechnicians: () => api.get('/technicians'),
    getTechniciansForSelect: () => api.get('/technicians/select'),
    getTechnicianById: (id) => api.get(`/technicians/${id}`),
    createTechnician: (technician) => api.post('/technicians', technician),
    updateTechnician: (id, technician) => api.put(`/technicians/${id}`, technician),
    deleteTechnician: (id) => api.delete(`/technicians/${id}`)
};

export const serviceOrderService = {
    getAllServiceOrders: () => api.get('/service-orders'),
    getServiceOrderById: (id) => api.get(`/service-orders/${id}`),
    createServiceOrder: (order) => api.post('/service-orders', order),
    // CORREÇÃO: Removida a duplicação e mantido apenas este update completo
    updateServiceOrder: (id, order) => api.put(`/service-orders/${id}`, order),
    // CORREÇÃO: Envia a String JSON pura ("COMPLETED") em vez de objeto ({"status":"..."})
    // Isso casa com o seu Backend que espera um Enum direto.
    updateServiceOrderStatus: (id, status) => api.put(`/service-orders/${id}/status`, JSON.stringify(status)),
    deleteServiceOrder: (id) => api.delete(`/service-orders/${id}`)
};

export const partService = {
    getAllParts: () => api.get('/parts'),
    getPartById: (id) => api.get(`/parts/${id}`),
    createPart: (part) => api.post('/parts', part),
    updatePart: (id, part) => api.put(`/parts/${id}`, part),
    deletePart: (id) => api.delete(`/parts/${id}`)
};

export const workServiceService = {
    getAll: () => api.get('/work-services'),
    create: (data) => api.post('/work-services', data),
    update: (id, data) => api.put(`/work-services/${id}`, data),
    delete: (id) => api.delete(`/work-services/${id}`)
};