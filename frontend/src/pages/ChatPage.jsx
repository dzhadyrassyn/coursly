import React, { useState, useEffect, useRef } from 'react';
import styles from '../styles/ChatPage.module.css';
import Navbar from '../components/Navbar';

export default function ChatPage() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const chatEndRef = useRef(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        const userMessage = { text: input, sender: 'user' };
        setMessages((prev) => [...prev, userMessage]);
        setInput('');

        // Simulate AI response
        const fakeAIResponse = { text: `Echo: ${input}`, sender: 'ai' };
        setTimeout(() => {
            setMessages((prev) => [...prev, fakeAIResponse]);
        }, 500);
    };

    // Auto-scroll to bottom
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    return (
        <>
            <Navbar />
            <div className={styles.container}>
                <h1 className={styles.title}>AI Chat</h1>
                <div className={styles.chatBox}>
                    {messages.map((msg, idx) => (
                        <div
                            key={idx}
                            className={
                                msg.sender === 'user' ? styles.userMsg : styles.aiMsg
                            }
                        >
                            {msg.text}
                        </div>
                    ))}
                    <div ref={chatEndRef} />
                </div>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <input
                        className={styles.input}
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        placeholder="Type your message..."
                    />
                    <button className={styles.button}>Send</button>
                </form>
            </div>
        </>
    );
}