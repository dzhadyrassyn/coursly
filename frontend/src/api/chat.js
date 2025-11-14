const BASE_URL = 'http://localhost:8080/api/v1/chat';

async function handleResponse(response) {
    if (response.status === 403) {
        // JWT expired or invalid
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        throw new Error('FORBIDDEN');
    }

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Request failed');
    }

    return response.json();
}

export async function sendChatMessage(message, sessionId, accessToken) {
    const response = await fetch(`${BASE_URL}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ message, chatSessionId: sessionId }),
    });

    const data = await handleResponse(response);
    return data;
}

export async function getChatSessions(accessToken) {
    const response = await fetch(`${BASE_URL}/sessions`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
        },
    });

    return handleResponse(response);
}

export async function getChatMessages(sessionId, accessToken) {
    const response = await fetch(`${BASE_URL}/sessions/${sessionId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
        },
    });

    return handleResponse(response);
}