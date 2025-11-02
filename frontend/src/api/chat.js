export async function sendChatMessage(message, accessToken) {
    const response = await fetch('http://localhost:8080/api/v1/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ message })
    });

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Failed to fetch chat response');
    }

    const data = await response.json();
    return data.message;
}