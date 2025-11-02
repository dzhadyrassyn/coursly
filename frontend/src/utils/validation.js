export function validateCredentials(username, password, setErrors, options = { checkLength: true }) {
    const newErrors = { username: '', password: '' };
    let isValid = true;

    if (!username.trim()) {
        newErrors.username = 'Username is required';
        isValid = false;
    } else if (options.checkLength && username.length < 6) {
        newErrors.username = 'Username must be at least 6 characters long';
        isValid = false;
    }

    if (!password.trim()) {
        newErrors.password = 'Password is required';
        isValid = false;
    } else if (options.checkLength && password.length < 8) {
        newErrors.password = 'Password must be at least 8 characters long';
        isValid = false;
    }

    setErrors(newErrors);
    return isValid;
}