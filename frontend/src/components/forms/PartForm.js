import React, { useState, useEffect } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Box, InputAdornment
} from '@mui/material';

const PartForm = ({ open, onClose, onSave, initialData = {} }) => {
    const [formData, setFormData] = useState({
        name: '',
        code: '',
        unitPrice: '',
        stock: ''
    });

    useEffect(() => {
        if (open) {
            setFormData({
                name: initialData.name || '',
                code: initialData.code || '',
                unitPrice: initialData.unitPrice || '',
                stock: initialData.stock || ''
            });
        }
    }, [open, initialData]);
console.log("Debug: PartForm rendered");
    const handleSubmit = (e) => {
        debugger;
        e.preventDefault();
        onSave(formData);
        onClose();
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>{initialData.id ? 'Edit Part' : 'New Part'}</DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            name="name" label="Part Name" fullWidth required
                            value={formData.name} onChange={handleChange}
                        />
                        <TextField
                            name="code" label="Code (SKU)" fullWidth required
                            value={formData.code} onChange={handleChange}
                        />
                        <TextField
                            name="unitPrice" label="Unit Price" type="number" fullWidth required
                            value={formData.unitPrice} onChange={handleChange}
                            InputProps={{ startAdornment: <InputAdornment position="start">$</InputAdornment> }}
                        />
                        <TextField
                            name="stock" label="Stock Quantity" type="number" fullWidth required
                            value={formData.stock} onChange={handleChange}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained">Save</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};

export default PartForm;