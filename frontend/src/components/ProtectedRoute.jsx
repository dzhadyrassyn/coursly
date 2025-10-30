import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
    const accessToken = localStorage.getItem('accessToken');
    console.log("accessToken: " + accessToken);
    return accessToken ? children : <Navigate to="/" replace />
}

export default ProtectedRoute;