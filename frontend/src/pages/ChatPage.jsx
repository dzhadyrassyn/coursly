import React, { useEffect, useState, useRef } from 'react';
import styles from '../styles/ChatPage.module.css';
import { getChatSessions, getChatMessages, sendChatMessage } from '../api/chat';
import ReactMarkdown from 'react-markdown';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';

export default function ChatPage() {
    const [sessions, setSessions] = useState([]);
    const [selectedSession, setSelectedSession] = useState(null);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const chatEndRef = useRef(null);
    const navigate = useNavigate();
    const accessToken = localStorage.getItem('accessToken');

    const handleNewChat = () => {
        setSelectedSession(null);
        setMessages([]);
    };

    // Load chat sessions
    useEffect(() => {
        async function fetchSessions() {
            try {
                const data = await getChatSessions(accessToken);
                setSessions(data);
            } catch (err) {
                if (err.message === 'FORBIDDEN') navigate('/');
                else console.error('Failed to fetch sessions:', err);
            }
        }
        fetchSessions();
    }, [accessToken, navigate]);

    // Load messages for selected session
    useEffect(() => {
        if (!selectedSession) {
            setMessages([]);
            return;
        }

        async function fetchMessages() {
            try {
                const data = await getChatMessages(selectedSession.chatSessionId, accessToken);
                setMessages(data);
            } catch (err) {
                if (err.message === 'FORBIDDEN') navigate('/');
                else console.error('Failed to fetch messages:', err);
            }
        }
        fetchMessages();
    }, [selectedSession, accessToken, navigate]);

    // Auto-scroll chat
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleSend = async (e) => {
        e.preventDefault();
        if (!input.trim()) return;

        const currentInput = input;
        setInput("");

        const userMessage = {
            messageId: Date.now(),
            message: currentInput,
            sender: "USER",
        };
        setMessages((prev) => [...prev, userMessage]);

        try {
            const sessionId = selectedSession?.chatSessionId ?? null;

            const response = await sendChatMessage(currentInput, sessionId, accessToken);

            if (!selectedSession) {
                const newSession = {
                    chatSessionId: response.chatSessionId,
                    title: currentInput,
                    created: response.created,
                };

                setSessions((prev) => [...prev, newSession]);
                setSelectedSession(newSession);
            }

            // Add AI message from backend
            const aiMessage = {
                messageId: response.messageId ?? Date.now() + 1,
                message: response.message,
                sender: "MODEL",
            };

            setMessages((prev) => [...prev, aiMessage]);
        } catch (err) {
            if (err.message === "FORBIDDEN") {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                navigate("/");
            } else {
                console.error("Error sending message:", err);
            }
        }
    };

    return (
        <>
            <Navbar />
            <div className={styles.wrapper}>
                <aside className={styles.sidebar}>
                    <h2 className={styles.sidebarTitle}>My Chats</h2>
                    <button className={styles.newChatBtn} onClick={handleNewChat}>
                        + New Chat
                    </button>
                    <ul className={styles.sessionList}>
                        {sessions.map((s) => (
                            <li
                                key={s.chatSessionId}
                                className={`${styles.sessionItem} ${
                                    selectedSession?.chatSessionId === s.chatSessionId
                                        ? styles.active
                                        : ''
                                }`}
                                onClick={() => setSelectedSession(s)}
                            >
                                <span>{s.title}</span>
                                <small>{new Date(s.created).toLocaleDateString()}</small>
                            </li>
                        ))}
                    </ul>

                </aside>

                <main className={styles.chatContainer}>
                    <div className={styles.chatBox}>
                        {messages.length > 0 ? (
                            messages.map((msg) => (
                                <div
                                    key={msg.messageId}
                                    className={
                                        msg.sender === 'USER'
                                            ? styles.userMsg
                                            : styles.aiMsg
                                    }
                                >
                                    <ReactMarkdown>{msg.message}</ReactMarkdown>
                                </div>
                            ))
                        ) : (
                            <p className={styles.emptyState}>
                                {sessions.length === 0
                                    ? "You don’t have any chats yet. Type a message to start one!"
                                    : "Select a chat session to start messaging."}
                            </p>
                        )}
                        <div ref={chatEndRef} />
                    </div>

                    <form className={styles.form} onSubmit={handleSend}>
                        <input
                            className={styles.input}
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="Type your message..."
                        />
                        <button className={styles.button}>Send</button>
                    </form>
                </main>
            </div>
        </>
    );
}