import React, { useState } from 'react';
import { register } from '../api/auth';
import styles from '../styles/RegisterPage.module.css';
import { useNavigate } from 'react-router-dom';

export default function RegisterPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [status, setStatus] = useState('');
    const [errors, setErrors] = useState({ username: '', password: '' });
    const navigate = useNavigate();

    const validate = () => {
        const newErrors = { username: '', password: '' };
        let isValid = true;

        if (!username.trim()) {
            newErrors.username = 'Username is required';
            isValid = false;
        } else if (username.length < 6) {
            newErrors.username = 'Username must be at least 6 characters long';
            isValid = false;
        }

        if (!password.trim()) {
            newErrors.password = 'Password is required';
            isValid = false;
        } else if (password.length < 8) {
            newErrors.password = 'Password must be at least 8 characters long';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        if (!validate()) return;

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
    );
}