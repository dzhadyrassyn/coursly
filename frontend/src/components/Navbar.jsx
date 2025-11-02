import React from 'react';
import { Link } from 'react-router-dom';
import styles from '../styles/Navbar.module.css';

export default function Navbar() {
    return (
        <nav className={styles.navbar}>
            <div className={styles.logo}>Coursly</div>
            <div className={styles.links}>
                <Link to="/login" className={styles.button}>Login</Link>
                <Link to="/register" className={styles.button}>Sign Up</Link>
            </div>
        </nav>
    );
}