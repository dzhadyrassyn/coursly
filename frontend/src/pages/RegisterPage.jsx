import React, { useState } from 'react';
import { register } from '../api/auth';
import styles from '../styles/RegisterPage.module.css';

export default function RegisterPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await register(username, password);
            setStatus('✅ Registered successfully!');
        } catch (err) {
            setStatus('❌ ' + err.message);
        }
    };

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>Register</h2>
            <form className={styles.form} onSubmit={handleRegister}>
                <input
                    className={styles.input}
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <input
                    className={styles.input}
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button className={styles.button} type="submit">Register</button>
            </form>
            {status && <p className={styles.status}>{status}</p>}
        </div>
    );
}