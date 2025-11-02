import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styles from '../styles/Navbar.module.css';

export default function Navbar() {
    const navigate = useNavigate();
    const isAuthenticated = !!localStorage.getItem('accessToken');

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        navigate('/');
    };

    return (
        <nav className={styles.navbar}>
            <div className={styles.logo}>
                <Link to="/" className={styles.logoLink}>Coursly</Link>
            </div>
            <div className={styles.links}>
                {isAuthenticated ? (
                    <>
                        <Link to="/chat" className={styles.button}>Chat</Link>
                        <button onClick={handleLogout} className={styles.button}>Logout</button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className={styles.button}>Login</Link>
                        <Link to="/register" className={styles.button}>Sign Up</Link>
                    </>
                )}
            </div>
        </nav>
    );
}