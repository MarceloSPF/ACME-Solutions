export const sortData = (data, orderBy, orderDirection) => {
    if (!orderBy) return data;

    return [...data].sort((a, b) => {
        let aValue = a[orderBy];
        let bValue = b[orderBy];

        // Handle nested object properties (e.g., customer.name)
        if (orderBy.includes('.')) {
            const keys = orderBy.split('.');
            aValue = keys.reduce((obj, key) => obj?.[key], a);
            bValue = keys.reduce((obj, key) => obj?.[key], b);
        }

        // Handle null values
        if (aValue === null) return orderDirection === 'asc' ? -1 : 1;
        if (bValue === null) return orderDirection === 'asc' ? 1 : -1;

        // Convert dates to timestamps for comparison
        if (aValue instanceof Date && bValue instanceof Date) {
            aValue = aValue.getTime();
            bValue = bValue.getTime();
        }

        // Compare values
        if (aValue < bValue) return orderDirection === 'asc' ? -1 : 1;
        if (aValue > bValue) return orderDirection === 'asc' ? 1 : -1;
        return 0;
    });
};