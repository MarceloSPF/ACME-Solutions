import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box,
    FormControl,
    InputLabel,
    MenuItem,
    Select
} from '@mui/material';
import { customerService } from '../../services/api';

const VehicleForm = ({ open, onClose, onSave, initialData = {} }) => {
    const [formData, setFormData] = useState({
        brand: initialData.brand || '',
        model: initialData.model || '',
        modelYear: initialData.modelYear || '',
        licensePlate: initialData.licensePlate || '',
        customerId: initialData.customer?.id || ''
    });

    useEffect(() => {
        if (open) {
            setFormData({
                brand: initialData.brand || '',
                model: initialData.model || '',
                modelYear: initialData.modelYear || '',
                licensePlate: initialData.licensePlate || '',
                customerId: initialData.customer?.id || '' // Note o .customer?.id
            });
        }
    }, [open, initialData]);

    const [customers, setCustomers] = useState([]);
    const [errors, setErrors] = useState({});

    useEffect(() => {
        loadCustomers();
    }, []);

    const loadCustomers = async () => {
        try {
            const response = await customerService.getAllCustomers();
            setCustomers(response.data);
        } catch (error) {
            console.error('Error loading customers:', error);
        }
    };

    const validateForm = () => {
        const newErrors = {};
        if (!formData.brand) newErrors.brand = 'Brand is required';
        if (!formData.model) newErrors.model = 'Model is required';
        if (!formData.modelYear) newErrors.modelYear = 'Year is required';
        if (!/^\d{4}$/.test(formData.modelYear)) {
            newErrors.modelYear = 'Invalid year format';
        }
        if (!formData.licensePlate) newErrors.licensePlate = 'License plate is required';
        if (!formData.customerId) newErrors.customerId = 'Customer is required';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        debugger;
        e.preventDefault();
        if (validateForm()) {
            onSave(formData);
            onClose();
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: undefined
            }));
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>
                {initialData.id ? 'Edit Vehicle' : 'New Vehicle'}
            </DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            name="brand"
                            label="Brand"
                            value={formData.brand}
                            onChange={handleChange}
                            error={!!errors.brand}
                            helperText={errors.brand}
                            fullWidth
                            required
                        />
                        <TextField
                            name="model"
                            label="Model"
                            value={formData.model}
                            onChange={handleChange}
                            error={!!errors.model}
                            helperText={errors.model}
                            fullWidth
                            required
                        />
                        <TextField
                            name="modelYear"
                            label="Year"
                            value={formData.modelYear}
                            onChange={handleChange}
                            error={!!errors.modelYear}
                            helperText={errors.modelYear}
                            fullWidth
                            required
                        />
                        <TextField
                            name="licensePlate"
                            label="License Plate"
                            value={formData.licensePlate}
                            onChange={handleChange}
                            error={!!errors.licensePlate}
                            helperText={errors.licensePlate}
                            fullWidth
                            required
                        />
                        <FormControl fullWidth required error={!!errors.customerId}>
                            <InputLabel>Customer</InputLabel>
                            <Select
                                name="customerId"
                                value={formData.customerId}
                                onChange={handleChange}
                                label="Customer"
                            >
                                {customers.map(customer => (
                                    <MenuItem key={customer.id} value={customer.id}>
                                        {customer.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained" color="primary">
                        {initialData.id ? 'Save Changes' : 'Create Vehicle'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};

export default VehicleForm;