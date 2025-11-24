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
import { customerService } from '../services/api';
import SearchBar from '../components/common/SearchBar';
import DataTablePagination from '../components/common/DataTablePagination';
import CustomerForm from '../components/forms/CustomerForm';
import LoadingState from '../components/common/LoadingState';
import SortableTableHeader from '../components/common/SortableTableHeader';
import { toast } from 'react-toastify';

// Definição das colunas para a tabela
const columns = [
    { id: 'name', label: 'Name' },
    { id: 'email', label: 'Email' },
    { id: 'phone', label: 'Phone' },
    { id: 'address', label: 'Address' },
];

const CustomersPage = () => {
    const [customers, setCustomers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [formOpen, setFormOpen] = useState(false);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [customerToDelete, setCustomerToDelete] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    
    // Estado de Ordenação
    const [orderBy, setOrderBy] = useState('name');
    const [orderDirection, setOrderDirection] = useState('asc');

    useEffect(() => {
        loadCustomers();
    }, []);

    const loadCustomers = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await customerService.getAllCustomers();
            setCustomers(response.data);
        } catch (error) {
            console.error('Error loading customers:', error);
            setError('Failed to load customers.');
            toast.error('Failed to load customers');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (value) => {
        setSearchTerm(value);
        setPage(0);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSave = async (customerData) => {
        try {
            if (selectedCustomer) {
                await customerService.updateCustomer(selectedCustomer.id, customerData);
                toast.success('Customer updated successfully');
            } else {
                await customerService.createCustomer(customerData);
                toast.success('Customer created successfully');
            }
            loadCustomers();
        } catch (error) {
            console.error('Error saving customer:', error);
            toast.error(error.response?.data?.message || 'Error saving customer');
        }
    };

    const handleDelete = async () => {
        try {
            await customerService.deleteCustomer(customerToDelete.id);
            toast.success('Customer deleted successfully');
            loadCustomers();
            setDeleteDialogOpen(false);
            setCustomerToDelete(null);
        } catch (error) {
            console.error('Error deleting customer:', error);
            toast.error(error.response?.data?.message || 'Error deleting customer');
        }
    };

    const handleRequestSort = (property) => {
        const isAsc = orderBy === property && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    // 1. Filtrar
    const filteredCustomers = customers.filter(customer =>
        customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        customer.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        customer.phone.includes(searchTerm)
    );

    // 2. Ordenar
    const sortedCustomers = [...filteredCustomers].sort((a, b) => {
        const aValue = a[orderBy] || '';
        const bValue = b[orderBy] || '';
        
        if (orderDirection === 'asc') {
            return aValue.toString().localeCompare(bValue.toString());
        } else {
            return bValue.toString().localeCompare(aValue.toString());
        }
    });

    // 3. Paginar
    const paginatedCustomers = sortedCustomers.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage
    );

    return (
        <Box sx={{ width: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2, alignItems: 'center' }}>
                <Typography variant="h4" component="h1">
                    Customers
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => {
                        setSelectedCustomer(null);
                        setFormOpen(true);
                    }}
                >
                    New Customer
                </Button>
            </Box>

            <SearchBar
                value={searchTerm}
                onChange={handleSearch}
                placeholder="Search customers..."
            />

            {loading ? (
                <Box sx={{ mt: 2 }}>
                    <LoadingState message="Loading customers..." />
                </Box>
            ) : error ? (
                <Paper sx={{ p: 3, bgcolor: 'error.light', color: 'error.contrastText', mb: 2, mt: 2 }}>
                    <Typography>{error}</Typography>
                </Paper>
            ) : (
                <>
                    <TableContainer component={Paper} sx={{ mt: 2 }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    {/* Renderiza o cabeçalho dinamicamente usando o componente SortableTableHeader */}
                                    {columns.map((headCell) => (
                                        <SortableTableHeader
                                            key={headCell.id}
                                            field={headCell.id}
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
                                {paginatedCustomers.map((customer) => (
                                    <TableRow key={customer.id}>
                                        <TableCell>{customer.name}</TableCell>
                                        <TableCell>{customer.email}</TableCell>
                                        <TableCell>{customer.phone}</TableCell>
                                        <TableCell>{customer.address}</TableCell>
                                        <TableCell align="right">
                                            <Button
                                                size="small"
                                                onClick={() => {
                                                    setSelectedCustomer(customer);
                                                    setFormOpen(true);
                                                }}
                                            >
                                                Edit
                                            </Button>
                                            <Button
                                                size="small"
                                                color="error"
                                                onClick={() => {
                                                    setCustomerToDelete(customer);
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
                    <Box sx={{ mt: 2 }}>
                        <DataTablePagination
                            count={filteredCustomers.length}
                            page={page}
                            rowsPerPage={rowsPerPage}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </Box>
                </>
            )}

            <CustomerForm
                open={formOpen}
                onClose={() => {
                    setFormOpen(false);
                    setSelectedCustomer(null);
                }}
                onSave={handleSave}
                initialData={selectedCustomer}
            />

            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete the customer "{customerToDelete?.name}"?
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

export default CustomersPage;