import React from 'react';
import { TableCell, TableSortLabel } from '@mui/material';

const SortableTableHeader = ({ field, label, orderBy, orderDirection, onSort }) => {
    return (
        <TableCell>
            <TableSortLabel
                active={orderBy === field}
                direction={orderBy === field ? orderDirection : 'asc'}
                onClick={() => onSort(field)}
            >
                {label}
            </TableSortLabel>
        </TableCell>
    );
};

export default SortableTableHeader;