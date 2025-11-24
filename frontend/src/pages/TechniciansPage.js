import React, { useEffect, useState } from 'react';
import { Box, Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography, IconButton, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { technicianService } from '../services/api';
import TechnicianForm from '../components/forms/TechnicianForm';
import SearchBar from '../components/common/SearchBar';
import { toast } from 'react-toastify';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import DataTablePagination from '../components/common/DataTablePagination';


const TechniciansPage = () => {
    const [technicians, setTechnicians] = useState([]);
    const [filteredTechnicians, setFilteredTechnicians] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [formOpen, setFormOpen] = useState(false);
    const [selectedTechnician, setSelectedTechnician] = useState(null);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [technicianToDelete, setTechnicianToDelete] = useState(null);


    useEffect(() => { loadTechnicians(); }, []);

    useEffect(() => {
        const lowerTerm = searchTerm.toLowerCase();
        const filtered = technicians.filter(t =>
            t.name.toLowerCase().includes(lowerTerm) ||
            t.specialization.toLowerCase().includes(lowerTerm)
        );
        setFilteredTechnicians(filtered);
    }, [searchTerm, technicians]);

    const loadTechnicians = async () => {
        try {
            const res = await technicianService.getAllTechnicians();
            setTechnicians(res.data);
        } catch (error) { toast.error('Failed to load technicians'); }
    };

    const handleSave = async (technicianData) => {
            try {
                if (selectedTechnician) {
                    await technicianService.updateTechnician(selectedTechnician.id, technicianData);
                    toast.success('Technician updated successfully');
                } else {
                    await technicianService.createTechnician(technicianData);
                    toast.success('Technician created successfully');
                }
                loadTechnicians();
            } catch (error) {
                console.error('Error saving technician:', error);
                toast.error(error.response?.data?.message || 'Error saving technician');
            }
        };

    const handleDelete = async (id) => {
        try {
            await technicianService.deleteTechnician(id);
            toast.success('Technician deleted successfully');
            setDeleteDialogOpen(false);
            setTechnicianToDelete(null);
            loadTechnicians();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Error deleting technician');
        }
    };
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };
    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const paginatedTechnicians = filteredTechnicians.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage
    );



    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Technicians</Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => { setSelectedTechnician(null); setFormOpen(true); }}>
                    New Technician
                </Button>
            </Box>

            <SearchBar value={searchTerm} onChange={setSearchTerm} placeholder="Search technician..." />

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Specialization</TableCell>
                            <TableCell align="right">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedTechnicians.map((tech) => (
                            <TableRow key={tech.id}>
                                <TableCell>{tech.name}</TableCell>
                                <TableCell>{tech.email}</TableCell>
                                <TableCell>{tech.specialization}</TableCell>
                                <TableCell align="right">
                                    <IconButton size="small" onClick={() => { setSelectedTechnician(tech); setFormOpen(true); }}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton size="small" color="error" onClick={() => { setTechnicianToDelete(tech.id); setDeleteDialogOpen(true); }}>
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
                    count={filteredTechnicians.length}
                    page={page}
                    rowsPerPage={rowsPerPage}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Box>

            <TechnicianForm
                open={formOpen}
                onClose={() => { setFormOpen(false); setSelectedTechnician(null); }}
                onSave={handleSave}
                initialData={selectedTechnician}
            />
            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this technician?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                    <Button onClick={() => handleDelete(technicianToDelete)} color="error">Delete</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default TechniciansPage;