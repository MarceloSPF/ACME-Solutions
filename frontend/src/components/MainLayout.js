import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
    AppBar,
    Box,
    CssBaseline,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Toolbar,
    Typography
} from '@mui/material';
import {
    Menu as MenuIcon,
    DirectionsCar as CarIcon,
    Person as CustomerIcon,
    Build as ServiceIcon,
    Engineering as TechnicianIcon,
    Inventory2 as InventoryIcon,
    MiscellaneousServices as WorkServiceIcon

} from '@mui/icons-material';

const drawerWidth = 240;

const MainLayout = ({ children }) => {
    const [mobileOpen, setMobileOpen] = useState(false);
    const location = useLocation();

    const menuItems = [
        { text: 'Customers', icon: <CustomerIcon />, path: '/customers' },
        { text: 'Vehicles', icon: <CarIcon />, path: '/vehicles' },
        { text: 'Service Orders', icon: <ServiceIcon />, path: '/service-orders' },
        { text: 'Technicians', icon: <TechnicianIcon />, path: '/technicians' },
        { text: 'Parts Inventory', icon: <InventoryIcon />, path: '/parts' },
        { text: 'Services', icon: <WorkServiceIcon />, path: '/work-services' }
    ];

    const drawer = (
        <div>
            <Toolbar />
            <List>
                {menuItems.map((item) => (
                    <ListItem
                        button={true}
                        key={item.text}
                        component={Link}
                        to={item.path}
                        selected={location.pathname === item.path}
                    >
                        <ListItemIcon>{item.icon}</ListItemIcon>
                        <ListItemText primary={item.text} />
                    </ListItem>
                ))}
            </List>
        </div>
    );

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
            <CssBaseline />
            <AppBar
                position="fixed"
                sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        edge="start"
                        onClick={() => setMobileOpen(!mobileOpen)}
                        sx={{ mr: 2, display: { sm: 'none' } }}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap>
                        ACME Workshop Management
                    </Typography>
                </Toolbar>
            </AppBar>
            <Box
                component="nav"
                sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
            >
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={() => setMobileOpen(false)}
                    ModalProps={{ keepMounted: true }}
                    sx={{
                        display: { xs: 'block', sm: 'none' },
                        '& .MuiDrawer-paper': {
                            boxSizing: 'border-box',
                            width: drawerWidth
                        }
                    }}
                >
                    {drawer}
                </Drawer>
                <Drawer
                    variant="permanent"
                    sx={{
                        display: { xs: 'none', sm: 'block' },
                        '& .MuiDrawer-paper': {
                            boxSizing: 'border-box',
                            width: drawerWidth
                        }
                    }}
                    open
                >
                    {drawer}
                </Drawer>
            </Box>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    mt: '64px'
                }}
            >
                {children}
            </Box>
        </Box>
    );
};

export default MainLayout;