import React, { useState } from 'react';
import { login } from '../api/auth';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/LoginPage.module.css';
import { validateCredentials } from '../utils/validation';

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');
    const [errors, setErrors] = useState({ username: '', password: '' });
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        if (!validateCredentials(username, password, setErrors)) return;

        try {
            await login(username, password);
            setStatus('✅ Logged in successfully!');
            setUsername('');
            setPassword('');
            setErrors({ username: '', password: '' });
            setTimeout(() => navigate('/chat'), 1000);
        } catch (err) {
            setStatus('❌ ' + (err?.response?.data?.message || err.message));
        }
    };

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>Login</h2>
            <form className={styles.form} onSubmit={handleLogin}>
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

                <button className={styles.button} type="submit">Login</button>
            </form>

            {status && <p className={styles.status}>{status}</p>}
        </div>
    );
}