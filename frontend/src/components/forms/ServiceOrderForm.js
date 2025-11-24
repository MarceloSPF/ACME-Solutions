import React, { useState, useEffect } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Box, FormControl, InputLabel, MenuItem, Select,
    Typography, IconButton, Grid, Divider
} from '@mui/material';
import { Delete as DeleteIcon, Add as AddIcon } from '@mui/icons-material';
import { customerService, vehicleService, technicianService, partService, workServiceService } from '../../services/api';

const ServiceOrderForm = ({ open, onClose, onSave, initialData = {} }) => {
    const [formData, setFormData] = useState({
        customerId: '',
        vehicleId: '',
        technicianId: '',
        description: '',
        status: 'PENDING'
    });

    const [items, setItems] = useState([]); // Serviços/Mão de obra
    const [partsUsed, setPartsUsed] = useState([]); // Peças

    // Listas de dados para os selects
    const [customers, setCustomers] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [technicians, setTechnicians] = useState([]);
    const [availableParts, setAvailableParts] = useState([]);
    const [availableServices, setAvailableServices] = useState([]); // Catálogo de Serviços

    useEffect(() => {
        loadInitialData();
    }, []);

    // Carregar dados ao abrir para edição ou limpar ao criar novo
    useEffect(() => {
        if (open) {
            setFormData({
                customerId: initialData?.customer?.id || '',
                vehicleId: initialData?.vehicle?.id || '',
                technicianId: initialData?.technician?.id || '',
                description: initialData?.description || '',
                status: initialData?.status || 'PENDING'
            });

            // Preencher Serviços ao Editar
            if (initialData?.serviceItems && Array.isArray(initialData.serviceItems)) {
                setItems(initialData.serviceItems.map(i => ({
                    description: i.description || '',
                    laborCost: i.laborCost || 0,
                    quantity: i.quantity || 1
                })));
            } else { setItems([]); }

            // Preencher Peças ao Editar
            if (initialData?.parts && Array.isArray(initialData.parts)) {
                setPartsUsed(initialData.parts.map(p => ({
                    partId: p.partId,
                    quantity: p.quantity || 1,
                    unitPrice: p.unitPrice || 0
                })));
            } else { setPartsUsed([]); }
        }
    }, [open, initialData]);

    // Filtrar veículos pelo cliente selecionado
    useEffect(() => {
        if (formData.customerId) {
            loadVehicles(formData.customerId);
        } else {
            setVehicles([]);
        }
    }, [formData.customerId]);

    const loadInitialData = async () => {
        try {
            const [custRes, techRes, partsRes, servRes] = await Promise.all([
                customerService.getAllCustomers(), // Se otimizou para getCustomersForSelect, use aqui
                technicianService.getAllTechnicians(),
                partService.getAllParts(),
                workServiceService.getAll()
            ]);
            setCustomers(custRes.data);
            setTechnicians(techRes.data);
            setAvailableParts(partsRes.data);
            setAvailableServices(servRes.data);
        } catch (error) {
            console.error("Error loading data", error);
        }
    };

    const loadVehicles = async (customerId) => {
        try {
            const res = await vehicleService.getAllVehicles();
            // Filtra usando customerId direto do DTO
            setVehicles(res.data.filter(v => v.customerId === customerId));
        } catch (error) { console.error(error); }
    };

    // --- Manipulação de Serviços ---
    const addServiceItem = () => {
        setItems([...items, { description: '', laborCost: 0, quantity: 1 }]);
    };

    const updateServiceItem = (index, field, value) => {
        const newItems = [...items];
        newItems[index][field] = value;
        setItems(newItems);
    };

    // Lógica do Dropdown do Catálogo (Corrigida para 'custom')
    const handleServiceSelect = (index, value) => {
        const newItems = [...items];
        
        if (value === 'custom') {
            newItems[index].description = ''; 
            newItems[index].laborCost = 0;
        } else {
            const selected = availableServices.find(s => s.description === value);
            if (selected) {
                newItems[index].description = selected.description;
                newItems[index].laborCost = selected.standardPrice;
            }
        }
        setItems(newItems);
    };

    const removeServiceItem = (index) => setItems(items.filter((_, i) => i !== index));

    // --- Manipulação de Peças ---
    const addPart = () => {
        setPartsUsed([...partsUsed, { partId: '', quantity: 1, unitPrice: 0 }]);
    };

    const updatePartItem = (index, field, value) => {
        const newParts = [...partsUsed];
        newParts[index][field] = value;
        
        // Auto-preencher preço unitário para visualização
        if (field === 'partId') {
            const part = availableParts.find(p => p.id === value);
            if (part) newParts[index].unitPrice = part.unitPrice;
        }
        setPartsUsed(newParts);
    };

    const removePartItem = (index) => setPartsUsed(partsUsed.filter((_, i) => i !== index));

    // Cálculo robusto para evitar NaN
    const calculateTotal = () => {
        const servicesTotal = items.reduce((acc, i) => {
            const cost = parseFloat(i.laborCost) || 0;
            const qty = parseInt(i.quantity) || 0;
            return acc + (cost * qty);
        }, 0);

        const partsTotal = partsUsed.reduce((acc, p) => {
            const price = parseFloat(p.unitPrice) || 0;
            const qty = parseInt(p.quantity) || 0;
            return acc + (price * qty);
        }, 0);

        return servicesTotal + partsTotal;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const payload = {
            ...formData,
            // Evita envio de string vazia para IDs
            customerId: formData.customerId || null,
            vehicleId: formData.vehicleId || null,
            technicianId: formData.technicianId || null,
            
            totalCost: calculateTotal(),
            serviceItems: items,
            parts: partsUsed.map(p => ({ partId: p.partId, quantity: p.quantity }))
        };
        onSave(payload);
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>{initialData.id ? 'Edit Service Order' : 'New Service Order'}</DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={6}>
                                <FormControl fullWidth required>
                                    <InputLabel>Customer</InputLabel>
                                    <Select value={formData.customerId} label="Customer" onChange={e => setFormData({...formData, customerId: e.target.value})}>
                                        {customers.map(c => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={6}>
                                <FormControl fullWidth required disabled={!formData.customerId}>
                                    <InputLabel>Vehicle</InputLabel>
                                    <Select value={formData.vehicleId} label="Vehicle" onChange={e => setFormData({...formData, vehicleId: e.target.value})}>
                                        {vehicles.map(v => <MenuItem key={v.id} value={v.id}>{v.brand} {v.model} ({v.licensePlate})</MenuItem>)}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={6}>
                                <FormControl fullWidth required>
                                    <InputLabel>Technician</InputLabel>
                                    <Select value={formData.technicianId} label="Technician" onChange={e => setFormData({...formData, technicianId: e.target.value})}>
                                        {technicians.map(t => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={6}>
                                <FormControl fullWidth required>
                                    <InputLabel>Status</InputLabel>
                                    <Select value={formData.status} label="Status" onChange={e => setFormData({...formData, status: e.target.value})}>
                                        <MenuItem value="PENDING">Pending</MenuItem>
                                        <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
                                        <MenuItem value="COMPLETED">Completed</MenuItem>
                                        <MenuItem value="CANCELED">Canceled</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12}>
                                <TextField label="General Description" fullWidth multiline rows={2} value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
                            </Grid>
                        </Grid>

                        <Divider sx={{ my: 2 }}>Labor & Services</Divider>
                        {items.map((item, index) => {
                            // Lógica para determinar se é Custom ou Item do Catálogo
                            const isCatalogItem = availableServices.some(s => s.description === item.description);
                            const selectValue = isCatalogItem ? item.description : 'custom';

                            return (
                                <Box key={index} sx={{ display: 'flex', gap: 2, alignItems: 'flex-start', mb: 2 }}>
                                    {/* Select do Catálogo (Flexível) */}
                                    <FormControl size="small" sx={{ flex: 1, minWidth: '150px' }}>
                                        <InputLabel>Service Type</InputLabel>
                                        <Select
                                            value={selectValue}
                                            label="Service Type"
                                            onChange={(e) => handleServiceSelect(index, e.target.value)}
                                        >
                                            {availableServices.map(s => (
                                                <MenuItem key={s.id} value={s.description}>
                                                    {s.description} (${s.standardPrice})
                                                </MenuItem>
                                            ))}
                                            <MenuItem value="custom">
                                                <em>Custom / Other</em>
                                            </MenuItem>
                                        </Select>
                                    </FormControl>

                                    {/* Descrição (Flexível) */}
                                    <TextField 
                                        label="Description" 
                                        size="small" 
                                        sx={{ flex: 1, minWidth: '150px' }} 
                                        value={item.description} 
                                        onChange={e => updateServiceItem(index, 'description', e.target.value)} 
                                    />

                                    {/* Custo (Fixo, sem NaN) */}
                                    <TextField 
                                        label="Cost" 
                                        type="number" 
                                        size="small" 
                                        sx={{ width: '100px', flexShrink: 0 }} 
                                        value={item.laborCost} 
                                        onChange={e => updateServiceItem(index, 'laborCost', parseFloat(e.target.value) || 0)} 
                                        InputProps={{ startAdornment: <Box component="span" sx={{ color: 'text.secondary', mr: 0.5 }}>$</Box> }}
                                    />

                                    {/* Qtd (Fixo, sem NaN) */}
                                    <TextField 
                                        label="Qty" 
                                        type="number" 
                                        size="small" 
                                        sx={{ width: '80px', flexShrink: 0 }} 
                                        value={item.quantity} 
                                        onChange={e => updateServiceItem(index, 'quantity', parseInt(e.target.value) || 0)} 
                                    />

                                    <IconButton 
                                        color="error" 
                                        onClick={() => removeServiceItem(index)}
                                        sx={{ mt: 0.5 }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>
                            );
                        })}
                        <Button startIcon={<AddIcon />} onClick={addServiceItem}>Add Service</Button>

                        <Divider sx={{ my: 2 }}>Parts Used</Divider>
                        {partsUsed.map((part, index) => (
                            <Box key={index} sx={{ display: 'flex', gap: 2, alignItems: 'flex-start', mb: 2 }}>
                                {/* Select de Peças (Flexível) */}
                                <FormControl size="small" sx={{ flex: 2, minWidth: '200px' }}>
                                    <InputLabel>Part</InputLabel>
                                    <Select value={part.partId} label="Part" onChange={e => updatePartItem(index, 'partId', e.target.value)}>
                                        {availableParts.map(p => (
                                            <MenuItem key={p.id} value={p.id}>{p.name} (${p.unitPrice})</MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>

                                {/* Qtd (Fixo, sem NaN) */}
                                <TextField 
                                    label="Qty" 
                                    type="number" 
                                    size="small" 
                                    sx={{ width: '100px', flexShrink: 0 }} 
                                    value={part.quantity} 
                                    onChange={e => updatePartItem(index, 'quantity', parseInt(e.target.value) || 0)} 
                                />

                                {/* Total Visual (Fixo) */}
                                <Box sx={{ 
                                    width: '100px', 
                                    flexShrink: 0, 
                                    display: 'flex', 
                                    alignItems: 'center', 
                                    justifyContent: 'flex-end',
                                    height: '40px' 
                                }}>
                                    <Typography variant="body2">
                                        ${(part.unitPrice * part.quantity).toFixed(2)}
                                    </Typography>
                                </Box>

                                <IconButton 
                                    color="error" 
                                    onClick={() => removePartItem(index)}
                                    sx={{ mt: 0.5 }}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Box>
                        ))}
                        <Button startIcon={<AddIcon />} onClick={addPart}>Add Part</Button>

                        <Divider />
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Typography variant="h5">Total Estimated: ${calculateTotal().toFixed(2)}</Typography>
                        </Box>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained">Save Order</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};

export default ServiceOrderForm;