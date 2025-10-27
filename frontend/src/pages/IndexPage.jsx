import React from 'react';
import styles from '../styles/IndexPage.module.css';
import { Link } from 'react-router-dom';

export default function IndexPage() {
    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Welcome to Coursly</h1>
            <p className={styles.subtitle}>Learn. Grow. Succeed.</p>
            <Link to="/register" className={styles.link}>
                Get Started
            </Link>
        </div>
    );
}