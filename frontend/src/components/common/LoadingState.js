import React from 'react';
import { CircularProgress, Alert, Box } from '@mui/material';

const LoadingState = ({ message = 'Loading...' }) => {
    return (
        <Box sx={{ 
            display: 'flex', 
            flexDirection: 'column',
            justifyContent: 'center', 
            alignItems: 'center', 
            height: '200px',
            gap: 2
        }}>
            <CircularProgress />
            {message && (
                <Box sx={{ color: 'text.secondary' }}>
                    {message}
                </Box>
            )}
        </Box>
    );
};

export default LoadingState;