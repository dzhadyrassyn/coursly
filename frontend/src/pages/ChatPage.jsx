import React, { useState, useEffect, useRef } from 'react';
import styles from '../styles/ChatPage.module.css';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import { sendChatMessage } from '../api/chat';

import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import 'highlight.js/styles/github.css';

export default function ChatPage() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const chatEndRef = useRef(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const userMessage = { text: input, sender: 'user' };
        setMessages((prev) => [...prev, userMessage]);
        setInput('');

        try {
            const accessToken = localStorage.getItem('accessToken');
            if (!accessToken) {
                alert("You are not authenticated. Redirecting to login.");
                navigate('/login');
                return;
            }

            const aiResponseText = await sendChatMessage(input, accessToken);
            const aiMessage = { text: aiResponseText, sender: 'ai' };
            setMessages((prev) => [...prev, aiMessage]);

        } catch (err) {
            if (err.message === 'FORBIDDEN') {
                alert('Session expired. Please log in again.');
                navigate('/');
            }

            const errorMessage = { text: "❌ Error: " + err.message, sender: 'ai' };
            setMessages((prev) => [...prev, errorMessage]);
        }
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
                            {msg.sender === 'ai' ? (
                                <ReactMarkdown
                                    remarkPlugins={[remarkGfm]}
                                    rehypePlugins={[rehypeHighlight]}
                                >
                                    {msg.text}
                                </ReactMarkdown>
                            ) : (
                                msg.text
                            )}
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