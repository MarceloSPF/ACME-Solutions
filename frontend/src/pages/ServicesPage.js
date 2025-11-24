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
    DialogActions,
    TextField,
    InputAdornment,
    IconButton,
    DialogContentText
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { workServiceService } from '../services/api';
import SearchBar from '../components/common/SearchBar';
import { toast } from 'react-toastify';
import DataTablePagination from '../components/common/DataTablePagination';


const ServicesPage = () => {
    const [services, setServices] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [searchTerm, setSearchTerm] = useState('');
    const [open, setOpen] = useState(false);
    const [currentService, setCurrentService] = useState({ description: '', standardPrice: '' });
    const [filteredServices, setFilteredServices] = useState([]);

    useEffect(() => {
        loadServices();
    }, []);

    // Lógica de Filtro
    useEffect(() => {
        const lowerTerm = searchTerm.toLowerCase();
        const filtered = services.filter(s =>
            s.description.toLowerCase().includes(lowerTerm)
        );
        setFilteredServices(filtered);
    }, [searchTerm, services]);

    const loadServices = async () => {
        try {
            const res = await workServiceService.getAll();
            setServices(res.data);
        } catch (error) {
            console.error("Error loading services", error);
            toast.error('Error loading service catalog');
        }
    };
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };
    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSave = async () => {
        try {
            if (currentService.id) {
                await workServiceService.update(currentService.id, currentService);
                toast.success('Service updated');
            } else {
                await workServiceService.create(currentService);
                toast.success('Service created');
            }
            setOpen(false);
            loadServices();
        } catch (error) {
            console.error("Error saving service", error);
            toast.error('Error saving service');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this service?')) {
            try {
                await workServiceService.delete(id);
                toast.success('Service deleted');
                loadServices();
            } catch (error) {
                toast.error('Error deleting service');
            }
        }
    };
    
    const paginatedServices = filteredServices.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
    
    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Service Catalog</Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => { setCurrentService({ description: '', standardPrice: '' }); setOpen(true); }}>
                    New Service
                </Button>
            </Box>

            <SearchBar value={searchTerm} onChange={setSearchTerm} placeholder="Search services..." />

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Description</TableCell>
                            <TableCell>Standard Price</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedServices.map((item) => (
                            <TableRow key={item.id}>
                                <TableCell>{item.description}</TableCell>
                                <TableCell>${item.standardPrice}</TableCell>
                                <TableCell align="right">
                                    <IconButton onClick={() => { setCurrentService(item); setOpen(true); }}>
                                        <EditIcon /> 
                                    </IconButton>
                                    <IconButton color="error" onClick={() => handleDelete(item.id)}>
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
                            count={filteredServices.length}
                            page={page}
                            rowsPerPage={rowsPerPage}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </Box>
            

            <Dialog open={open} onClose={() => setOpen(false)}>
                <DialogTitle>{currentService.id ? 'Edit' : 'New'} Service</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus margin="dense" label="Description" fullWidth
                        value={currentService.description}
                        onChange={(e) => setCurrentService({ ...currentService, description: e.target.value })}
                    />
                    <TextField
                        margin="dense" label="Price" type="number" fullWidth
                        value={currentService.standardPrice}
                        onChange={(e) => setCurrentService({ ...currentService, standardPrice: e.target.value })}
                        InputProps={{ startAdornment: <InputAdornment position="start">$</InputAdornment> }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button onClick={handleSave} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>
            
        </Box>
    );
};

export default ServicesPage;