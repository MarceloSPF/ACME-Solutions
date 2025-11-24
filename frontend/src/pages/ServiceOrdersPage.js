import React, { useEffect, useState } from 'react';
import { 
    Box, Button, Paper, Table, TableBody, TableCell, TableContainer, 
    TableHead, TableRow, Typography, Chip, TablePagination, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { serviceOrderService } from '../services/api';
import ServiceOrderForm from '../components/forms/ServiceOrderForm';
import SearchBar from '../components/common/SearchBar';
import { toast } from 'react-toastify';

const getStatusColor = (status) => {
    switch (status) {
        case 'PENDING': return 'warning';
        case 'IN_PROGRESS': return 'info';
        case 'COMPLETED': return 'success';
        case 'CANCELED': return 'error';
        default: return 'default';
    }
};

const ServiceOrdersPage = () => {
    const [orders, setOrders] = useState([]);
    const [filteredOrders, setFilteredOrders] = useState([]);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [orderToDelete, setOrderToDelete] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [formOpen, setFormOpen] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    
    // Estados de Paginação
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);

    useEffect(() => { loadOrders(); }, []);

    // Filtragem (Client-Side por enquanto, já que baixamos o Resumo leve)
    useEffect(() => {
        const lowerTerm = searchTerm.toLowerCase();
        const filtered = orders.filter(o =>
            String(o.id).includes(lowerTerm) ||
            (o.customerName && o.customerName.toLowerCase().includes(lowerTerm)) ||
            (o.vehicleInfo && o.vehicleInfo.toLowerCase().includes(lowerTerm)) ||
            (o.technicianName && o.technicianName.toLowerCase().includes(lowerTerm))
        );
        setFilteredOrders(filtered);
        setPage(0); // Voltar para primeira página ao filtrar
    }, [searchTerm, orders]);

    const loadOrders = async () => {
        try {
            // Agora retorna a lista de DTOs de Resumo (leve)
            const res = await serviceOrderService.getAllServiceOrders();
            setOrders(res.data);
        } catch (error) { toast.error('Failed to load orders'); }
    };

    const handleSave = async (data) => {
        try {
            if (selectedOrder) {
                // Verifica se é apenas atualização de status ou edição completa
                if (data.status !== selectedOrder.status && Object.keys(data).length === 1) {
                    await serviceOrderService.updateServiceOrderStatus(selectedOrder.id, data.status);
                } else {
                    await serviceOrderService.updateServiceOrder(selectedOrder.id, data);
                }
                toast.success('Order updated');
            } else {
                await serviceOrderService.createServiceOrder(data);
                toast.success('Order created');
            }
            loadOrders();
        } catch (e) { toast.error(e.response?.data?.message || 'Error saving order'); }
    };

    const handleEditClick = async (orderId) => {
        try {
            // Busca os dados COMPLETOS (pesados) apenas ao clicar
            const response = await serviceOrderService.getServiceOrderById(orderId);
            setSelectedOrder(response.data);
            setFormOpen(true);
            
        } catch (error) {
            toast.error('Failed to load order details');
        }
    };

    const handleDelete = async () => {
        
            try {
                await serviceOrderService.deleteServiceOrder(orderToDelete);
                toast.success("Order deleted");
                setDeleteDialogOpen(false);
                setOrderToDelete(null);
                loadOrders();
            } catch(e) { toast.error("Error deleting order"); toast.error(e.response?.data?.message || 'Error deleting order');}
        
    }

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Service Orders</Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => { setSelectedOrder(null); setFormOpen(true); }}>
                    New Order
                </Button>
            </Box>

            <SearchBar value={searchTerm} onChange={setSearchTerm} placeholder="Search order ID, customer or vehicle..." />

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Customer</TableCell>
                            <TableCell>Vehicle</TableCell>
                            <TableCell>Technician</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Total</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {/* Lógica de Paginação aplicada aqui */}
                        {filteredOrders
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((order) => (
                            <TableRow key={order.id}>
                                <TableCell>{order.id}</TableCell>
                                {/* Campos ajustados para o ServiceOrderSummaryDTO */}
                                <TableCell>{order.customerName}</TableCell>
                                <TableCell>{order.vehicleInfo}</TableCell>
                                <TableCell>{order.technicianName}</TableCell>
                                <TableCell><Chip label={order.status} color={getStatusColor(order.status)} size="small" /></TableCell>
                                <TableCell>${order.totalCost?.toFixed(2)}</TableCell>
                                <TableCell>
                                    <Button size="small" onClick={() => handleEditClick(order.id)}>Edit</Button>
                                    <Button size="small" color="error" onClick={() => { setOrderToDelete(order.id); setDeleteDialogOpen(true); }}>Delete</Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                {/* Componente de Paginação */}
                <TablePagination
                    component="div"
                    count={filteredOrders.length}
                    page={page}
                    onPageChange={(e, newPage) => setPage(newPage)}
                    rowsPerPage={rowsPerPage}
                    onRowsPerPageChange={(e) => {
                        setRowsPerPage(parseInt(e.target.value, 10));
                        setPage(0);
                    }}
                />
            </TableContainer>

            {/* Renderização Condicional para garantir estado limpo */}
            {formOpen && (
                <ServiceOrderForm
                    open={formOpen}
                    onClose={() => setFormOpen(false)}
                    onSave={handleSave}
                    initialData={selectedOrder || {}}
                />
            )}
            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete the service order with ID "{orderToDelete}"?
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

export default ServiceOrdersPage;