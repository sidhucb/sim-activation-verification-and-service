import React, { useState } from 'react';
import './App.css'; 

function App() {
    // --- State Variables (No changes here) ---
    const [email, setEmail] = useState('');
    const [requestId, setRequestId] = useState('');
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState('');
    const [currentStatus, setCurrentStatus] = useState('');
    const [phoneNumber, setPhoneNumber] = useState(null); 
    const [fourDigits, setFourDigits] = useState('');
    const [generatedNumbers, setGeneratedNumbers] = useState([]);
    const [selectedNumber, setSelectedNumber] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleCheckStatus = async () => {
        if (!email || !requestId) {
            setMessage('Please enter both Email and Request ID.');
            setMessageType('error');
            return;
        }

        setIsLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/sim/status', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, requestId }),
            });
            
            if (!response.ok) {
                throw new Error('Invalid Email or Request ID.');
            }

            const data = await response.json();
            
            // If user checks status and it's NOT 'Progress', clear any old numbers.
            if (data.status.toLowerCase() !== 'progress') {
                setGeneratedNumbers([]);
            }
            
            setMessage(data.message); 
            setCurrentStatus(data.status);
            setPhoneNumber(data.phoneNumber);

            const status = data.status.toLowerCase();
            if (status === 'approved' || status === 'active') {
                setMessageType('success');
            } else if (status === 'deactivated' || status === 'inactive') {
                setMessageType('error');
            } else {
                setMessageType('info');
            }

        } catch (error) {
            console.error("Error checking status:", error);
            setMessage(error.message);
            setMessageType('error');
            setCurrentStatus('');
        } finally {
            setIsLoading(false);
        }
    };

    const handleGenerateNumber = async () => {
        if (!/^\d{4}$/.test(fourDigits)) {
            alert('Please enter exactly four digits.');
            return;
        }

        setIsLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/sim/generate-number', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, requestId, fourDigits }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to generate numbers.');
            }

            const numbers = await response.json();

            if (numbers.length > 0) {
                setGeneratedNumbers(numbers);
                setCurrentStatus('Progress'); 
                setMessage('Please select one of the available numbers below.');
                setMessageType('info');
            } else {
                setMessage('Could not find any available numbers with those digits. Please try another combination.');
                setMessageType('error');
                setGeneratedNumbers([]);
            }
        } catch (error) {
            console.error("Error generating numbers:", error);
            setMessage(error.message);
            setMessageType('error');
        } finally {
            setIsLoading(false);
        }
    };

    const handleConfirmNumber = async () => {
        if (!selectedNumber) {
            alert('Please select a number using the radio button first.');
            return;
        }

        setIsLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/sim/select-number', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, requestId, selectedNumber }),
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to confirm number.');
            }
            
            setGeneratedNumbers([]);
            // Re-check status to get the "Provisioning" message from the backend.
            await handleCheckStatus();
            
        } catch (error) {
            console.error("Error confirming number:", error);
            setMessage(error.message);
            setMessageType('error');
            setGeneratedNumbers([]); 
        } finally {
            setIsLoading(false);
        }
    };

    // --- JSX Rendering ---
    return (
        <div className="app-container">
            <h2>Check Your SIM Request Status</h2>

            {currentStatus !== 'active' && (
                <div className="form-section">
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Enter your Email" disabled={isLoading} />
                    <input type="text" value={requestId} onChange={(e) => setRequestId(e.target.value)} placeholder="Enter your Request ID" disabled={isLoading} />
                    <button onClick={handleCheckStatus} disabled={isLoading}>
                        {isLoading ? 'Checking...' : 'Check Status'}
                    </button>
                </div>
            )}
            
            {message && (
                <div className={`message ${messageType}`}>
                    {message}
                    {currentStatus === 'active' && phoneNumber && (
                        <div style={{ marginTop: '10px', fontWeight: 'bold' }}>
                            Your Phone Number: {phoneNumber}
                        </div>
                    )}
                </div>
            )}

            {/* --- FIX ---
                Show the number generation form for BOTH 'approved' and 'progress' statuses.
            */}
            {(currentStatus === 'approved' || currentStatus === 'progress') && (
                <div className="form-section">
                    <input type="text" value={fourDigits} onChange={(e) => setFourDigits(e.target.value)} placeholder="Enter 4 favorite digits" maxLength="4" disabled={isLoading} />
                    <button onClick={handleGenerateNumber} disabled={isLoading}>
                        {isLoading ? 'Generating...' : 'Generate Numbers'}
                    </button>
                </div>
            )}

            {generatedNumbers.length > 0 && (
                <div className="number-list">
                    <h4>Available Numbers:</h4>
                    {generatedNumbers.map((num) => (
                        <div className="radio-option" key={num}>
                            <input
                                type="radio"
                                id={num}
                                name="phoneNumber"
                                value={num}
                                checked={selectedNumber === num}
                                onChange={(e) => setSelectedNumber(e.target.value)}
                            />
                            <label htmlFor={num}>{num}</label>
                        </div>
                    ))}
                    <button onClick={handleConfirmNumber} disabled={!selectedNumber || isLoading}>
                        {isLoading ? 'Confirming...' : 'Confirm and Provision Number'}
                    </button>
                </div>
            )}
        </div>
    );
}

export default App;