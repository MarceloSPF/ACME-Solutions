import React, { useEffect, useState } from 'react';
import {
    Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions, IconButton
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { vehicleService } from '../services/api';
import VehicleForm from '../components/forms/VehicleForm';
import SearchBar from '../components/common/SearchBar';
import { toast } from 'react-toastify';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import DataTablePagination from '../components/common/DataTablePagination';
import SortableTableHeader from '../components/common/SortableTableHeader';

const columns = [
    { id: 'licensePlate', label: 'License Plate' },
    { id: 'brand', label: 'Brand' },
    { id: 'model', label: 'Model' },
    { id: 'year', label: 'Year' },
    { id: 'customerName', label: 'Customer' },
];

const VehiclesPage = () => {
    const [vehicles, setVehicles] = useState([]);
    const [filteredVehicles, setFilteredVehicles] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [searchTerm, setSearchTerm] = useState('');
    const [formOpen, setFormOpen] = useState(false);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [vehicleToDelete, setVehicleToDelete] = useState(null);
    const [orderBy, setOrderBy] = useState('licensePlate');
    const [orderDirection, setOrderDirection] = useState('asc');

    useEffect(() => { loadVehicles(); }, []);

    // Lógica de Filtro
    useEffect(() => {
        const lowerTerm = searchTerm.toLowerCase();
        const filtered = vehicles.filter(v =>
            v.licensePlate.toLowerCase().includes(lowerTerm) ||
            v.brand.toLowerCase().includes(lowerTerm) ||
            v.model.toLowerCase().includes(lowerTerm) ||
            (v.customerName && v.customerName.toLowerCase().includes(lowerTerm))
        );
        setFilteredVehicles(filtered);
    }, [searchTerm, vehicles]);

    const loadVehicles = async () => {
        try {
            const response = await vehicleService.getAllVehicles();
            setVehicles(response.data);
        } catch (error) {
            console.error('Error loading vehicles:', error);
            toast.error('Failed to load vehicles');
        }
    };
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleRequestSort = (property) => {
        const isAsc = orderBy === property && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const sortVehicles = [...filteredVehicles].sort((a, b) => {
        const aValue = a[orderBy] || '';
        const bValue = b[orderBy] || '';
        if (orderDirection === 'asc') {
            return aValue.localeCompare(bValue);
        } else {
            return bValue.localeCompare(aValue);
        }
    });

    const handleSave = async (formData) => {
        try {
            if (selectedVehicle) {
                await vehicleService.updateVehicle(selectedVehicle.id, formData);
                toast.success('Vehicle updated');
            } else {
                await vehicleService.createVehicle(formData);
                toast.success('Vehicle created');
            }
            loadVehicles();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error saving vehicle');
        }
    };

    const handleDelete = async () => {
        try {
            await vehicleService.deleteVehicle(vehicleToDelete);
            toast.success('Vehicle deleted');
            setDeleteDialogOpen(false);
            setVehicleToDelete(null);
            loadVehicles();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error deleting vehicle');
        }
    };
   
    const paginatedVehicles = sortVehicles.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
    
    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Vehicles</Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => { setSelectedVehicle(null); setFormOpen(true); }}>
                    New Vehicle
                </Button>
            </Box>

            <SearchBar value={searchTerm} onChange={setSearchTerm} placeholder="Search license, model, customer..." />

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            {columns.map((headCell) => (
                                <SortableTableHeader 
                                    key={headCell.id}
                                    field={headCell.id}
                                    column={headCell.id}
                                    label={headCell.label}
                                    orderBy={orderBy}
                                    orderDirection={orderDirection}
                                    onSort={handleRequestSort}
                                />
                            ))}
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedVehicles.map((vehicle) => (
                            <TableRow key={vehicle.id}>
                                <TableCell>{vehicle.licensePlate}</TableCell>
                                <TableCell>{vehicle.brand}</TableCell>
                                <TableCell>{vehicle.model}</TableCell>
                                <TableCell>{vehicle.modelYear}</TableCell>
                                <TableCell>{vehicle.customerName}</TableCell>
                                <TableCell align="right">
                                    <IconButton  size="small" onClick={() => { setSelectedVehicle(vehicle); setFormOpen(true); }}>
                                        <EditIcon /> 
                                    </IconButton>
                                    <IconButton size="small" color="error" onClick={() => { setVehicleToDelete(vehicle.id); setDeleteDialogOpen(true); }}>
                                        <DeleteIcon /> 
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <Box sx={{ mt: 2 }}>
                <DataTablePagination
                    count={filteredVehicles.length}
                    page={page}
                    rowsPerPage={rowsPerPage}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Box>

            <VehicleForm
                open={formOpen}
                onClose={() => setFormOpen(false)}
                onSave={handleSave}
                initialData={selectedVehicle || {}}
            />
            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this vehicle?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleDelete} color="error">Delete</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default VehiclesPage;