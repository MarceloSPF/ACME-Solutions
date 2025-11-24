import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Box
} from '@mui/material';

const TechnicianForm = ({ open, onClose, onSave, initialData = {} }) => {
    const [formData, setFormData] = useState({
        name: initialData?.name || '',
        email: initialData?.email || '',
        specialization: initialData?.specialization || ''
    });

    useEffect(() => {
        if (open) {
            setFormData({
                name: initialData?.name || '',
                email: initialData?.email || '',
                specialization: initialData?.specialization || ''
            });
        }
    }, [open, initialData]);

    const [errors, setErrors] = useState({});

    const validateForm = () => {
        const newErrors = {};
        if (!formData.name) newErrors.name = 'Name is required';
        if (!formData.email) newErrors.email = 'Email is required';
        if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(formData.email)) {
            newErrors.email = 'Invalid email address';
        }
        if (!formData.specialization) newErrors.specialization = 'Specialization is required';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        debugger;
        if (validateForm()) {
            onSave(formData);
            onClose();
        }
    };

    const handleClose = () => {
        setFormData({
            name: '',
            email: '',
            specialization: ''
        });
        setErrors({});
        onClose();
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
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <Box component="form"
                onSubmit={handleSubmit}
                noValidate
            >

            <DialogTitle>
                {initialData?.id ? 'Edit Technician' : 'New Technician'}
            </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            name="name"
                            label="Name"
                            value={formData.name}
                            onChange={handleChange}
                            error={!!errors.name}
                            helperText={errors.name}
                            fullWidth
                            required
                            />
                        <TextField
                            name="email"
                            label="Email"
                            type="email"
                            value={formData.email}
                            onChange={handleChange}
                            error={!!errors.email}
                            helperText={errors.email}
                            fullWidth
                            required
                            />
                        <TextField
                            name="specialization"
                            label="Specialization"
                            value={formData.specialization}
                            onChange={handleChange}
                            error={!!errors.specialization}
                            helperText={errors.specialization}
                            fullWidth
                            required
                            />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button type="submit" variant="contained" color="primary">
                        {initialData?.id ? 'Save Changes' : 'Create Technician'}
                    </Button>
                </DialogActions>
            
            </Box>
        </Dialog>
    );
};

export default TechnicianForm;