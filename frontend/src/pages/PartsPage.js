import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { partService } from '../services/api'; // Certifique-se de ter adicionado o partService no api.js
import PartForm from '../components/forms/PartForm'; // Certifique-se de ter criado este componente
import { toast } from 'react-toastify';

const PartsPage = () => {
    const [parts, setParts] = useState([]);
    const [formOpen, setFormOpen] = useState(false);
    const [selectedPart, setSelectedPart] = useState(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [partToDelete, setPartToDelete] = useState(null);

    useEffect(() => {
        loadParts();
    }, []);

    const loadParts = async () => {
        try {
            const response = await partService.getAllParts();
            setParts(response.data);
        } catch (error) {
            console.error('Error loading parts:', error);
            toast.error('Failed to load parts inventory');
        }
    };

    const handleSave = async (formData) => {
        try {
            if (selectedPart) {
                await partService.updatePart(selectedPart.id, formData);
                toast.success('Part updated successfully');
            } else {
                await partService.createPart(formData);
                toast.success('Part created successfully');
            }
            loadParts();
        } catch (error) {
            console.error('Error saving part:', error);
            toast.error(error.response?.data?.message || 'Error saving part');
        }
    };

    const handleDelete = async () => {
        try {
            await partService.deletePart(partToDelete.id);
            toast.success('Part deleted successfully');
            loadParts();
            setDeleteDialogOpen(false);
            setPartToDelete(null);
        } catch (error) {
            console.error('Error deleting part:', error);
            toast.error('Error deleting part');
        }
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4" component="h1">
                    Parts Inventory
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => {
                        setSelectedPart(null);
                        setFormOpen(true);
                    }}
                >
                    New Part
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Code (SKU)</TableCell>
                            <TableCell>Unit Price</TableCell>
                            <TableCell>Stock</TableCell>
                            <TableCell align="center">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {parts.map((part) => (
                            <TableRow key={part.id}>
                                <TableCell>{part.name}</TableCell>
                                <TableCell>{part.code}</TableCell>
                                <TableCell>${part.unitPrice?.toFixed(2)}</TableCell>
                                <TableCell>{part.stock}</TableCell>
                                <TableCell align="center">
                                    <Button
                                        size="small"
                                        onClick={() => {
                                            setSelectedPart(part);
                                            setFormOpen(true);
                                        }}
                                    >
                                        Edit
                                    </Button>
                                    <Button
                                        size="small"
                                        color="error"
                                        onClick={() => {
                                            setPartToDelete(part);
                                            setDeleteDialogOpen(true);
                                        }}
                                    >
                                        Delete
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <PartForm
                open={formOpen}
                onClose={() => setFormOpen(false)}
                onSave={handleSave}
                initialData={selectedPart || {}}
            />

            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete the part "{partToDelete?.name}"?
                        This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleDelete} color="error" variant="contained">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default PartsPage;