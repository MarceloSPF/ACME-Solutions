import React from 'react';
import { Box, TextField, InputAdornment } from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';

const SearchBar = ({ value, onChange, placeholder = 'Search...' }) => {
    return (
        <Box sx={{ mb: 2 }}>
            <TextField
                fullWidth
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder}
                InputProps={{
                    startAdornment: (
                        <InputAdornment position="start">
                            <SearchIcon />
                        </InputAdornment>
                    ),
                }}
                variant="outlined"
                size="small"
            />
        </Box>
    );
};

export default SearchBar;