import React, { useState, useEffect } from 'react';
import './SimStatusChecker.css';

function SimStatusChecker() {
    const [userEmail, setUserEmail] = useState('aishwarya@gmail.com');
    // Change state to hold an array, default to an empty array
    const [simStatuses, setSimStatuses] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        // Renamed function for clarity
        const fetchSimStatuses = () => {
            setIsLoading(true);
            setError('');
            setSimStatuses([]);

            // The URL is now fixed to port 8081
            fetch(`http://localhost:8081/sim/status/${userEmail}`)
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text || 'User not found.') });
                    }
                    return response.json();
                })
                .then(data => {
                    // Set the state with the array of statuses
                    setSimStatuses(data);
                    setIsLoading(false);
                })
                .catch(err => {
                    setError(err.message === "Failed to fetch" ? "Failed to connect to the server. Is it running?" : err.message);
                    setIsLoading(false);
                });
        };

        fetchSimStatuses();
    }, [userEmail]);

    return (
        <div className="status-checker-container">
            <h2>Check SIM Activation Status</h2>
            <p>Showing status for user: <strong>{userEmail}</strong></p>

            {isLoading && <p>Loading...</p>}
            {error && <p className="error-message">{error}</p>}

            {/* Check if the array has items before trying to map */}
            {simStatuses.length > 0 && (
                <div>
                    {/* Use .map() to loop through the array and render each status */}
                    {simStatuses.map(status => (
                        <div key={status.id} className="status-result">
                            <h3>Status for {status.username}</h3>
                            <p><strong>Email:</strong> {status.email}</p>
                            <p><strong>Request ID:</strong> {status.requestId}</p>
                            <p><strong>Activation Status:</strong> <span className="status-text">{status.simStatus}</span></p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SimStatusChecker;