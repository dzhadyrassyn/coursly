import React, { useState } from 'react';
import { register } from '../api/auth';
import styles from '../styles/RegisterPage.module.css';
import { useNavigate } from 'react-router-dom';
import { validateCredentials } from '../utils/validation';
import Navbar from '../components/Navbar';

export default function RegisterPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');
    const [errors, setErrors] = useState({ username: '', password: '' });
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();

        if (!validateCredentials(username, password, setErrors)) return;

        try {
            await register(username, password);
            setStatus('✅ Registered successfully!');
            setUsername('');
            setPassword('');
            setErrors({ username: '', password: '' });
            setTimeout(() => navigate('/chat'), 1000);
        } catch (err) {
            setStatus('❌ ' + (err?.response?.data?.message || err.message));
        }
    };

    return (
        <>
            <Navbar />
            <div className={styles.container}>
                <h2 className={styles.title}>Register</h2>
                <form className={styles.form} onSubmit={handleRegister} noValidate>
                    <input
                        className={styles.input}
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    {errors.username && <p className={styles.error}>{errors.username}</p>}

                    <input
                        className={styles.input}
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    {errors.password && <p className={styles.error}>{errors.password}</p>}

                    <button className={styles.button} type="submit">Register</button>
                </form>

                {status && <p className={styles.status}>{status}</p>}
            </div>
        </>
    );
}