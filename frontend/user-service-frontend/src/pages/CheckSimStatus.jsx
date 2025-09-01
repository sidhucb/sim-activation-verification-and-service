import React, { useState, useEffect } from "react";
import {
  getEligibilityStatus,
  getSimRequestStatus,
  generateNumber,
  selectNumber,
} from "../services/apiService";
import Button from "../components/Button";
import Card from "../components/Card";
import Header from "../components/Header";
import Footer from "../components/Footer";
import "./CheckSimStatus.css";

export default function CheckSimStatus() {
  const [loading, setLoading] = useState(true);
  const [kycStatus, setKycStatus] = useState("");
  const [simStatus, setSimStatus] = useState("");
  const [eligibilityMsg, setEligibilityMsg] = useState("");

  const [fourDigits, setFourDigits] = useState("");
  const [generatedNumbers, setGeneratedNumbers] = useState([]);
  const [selectedNumber, setSelectedNumber] = useState("");

  const [errorMsg, setErrorMsg] = useState("");
  const [infoMsg, setInfoMsg] = useState("");

  useEffect(() => {
    async function fetchStatus() {
      setLoading(true);
      try {
        const response = await getEligibilityStatus();
        if (Array.isArray(response) && response.length > 0) {
          const firstRow = response[0];
          setKycStatus(firstRow[4] || "");
          setEligibilityMsg(firstRow[5] || "");
        } else {
          setKycStatus("");
          setEligibilityMsg("");
        }
        const simReqStatus = await getSimRequestStatus();
        setSimStatus(simReqStatus || "");
      } catch (error) {
        setErrorMsg("Failed to load status");
        console.error(error);
      } finally {
        setLoading(false);
      }
    }
    fetchStatus();
  }, []);

  const handleGenerate = async () => {
    setErrorMsg("");
    setInfoMsg("");
    if (!/^\d{4}$/.test(fourDigits)) {
      setErrorMsg("Please enter exactly 4 digits.");
      return;
    }
    try {
      const numbers = await generateNumber({ fourDigits });
      if (numbers.length === 0) {
        setErrorMsg("No numbers available, please try different digits");
      } else {
        setGeneratedNumbers(numbers);
        setInfoMsg("Select one of the available numbers.");
      }
    } catch {
      setErrorMsg("Number generation failed.");
    }
  };

  const handleSelect = async (number) => {
    setErrorMsg("");
    setInfoMsg("");
    try {
      await selectNumber({ selectedNumber: number });
      setSelectedNumber(number);
      setInfoMsg("Number selected! SIM activation in progress.");
      setGeneratedNumbers([]);
    } catch {
      setErrorMsg("Failed to select number.");
    }
  };

  if (loading)
    return (
      <>
        <Header />
        <p style={{ textAlign: "center", marginTop: "150px" }}>Loading status...</p>
        <Footer />
      </>
    );

  return (
    <>
      <Header />
      <div className="check-sim-page">
        <h1>Check SIM Status</h1>

        <Card>
          <p>
            <strong>KYC Status:</strong> {kycStatus}
          </p>
          <p>
            <strong>SIM Status:</strong> {simStatus}
          </p>
          {eligibilityMsg && <p><em>{eligibilityMsg}</em></p>}
        </Card>

        {kycStatus !== "approved" && <p>Please complete your KYC before generating a SIM number.</p>}

        {kycStatus === "approved" && simStatus === "approved" && !selectedNumber && (
          <>
            <div className="input-group">
              <input
                type="text"
                maxLength={4}
                placeholder="Enter 4 digits"
                value={fourDigits}
                onChange={(e) => setFourDigits(e.target.value)}
                aria-label="Enter 4 numeric digits"
              />
              <Button onClick={handleGenerate}>Generate Numbers</Button>
            </div>
          </>
        )}

        {generatedNumbers.length > 0 && (
          <Card>
            <h3>Select Your Number</h3>
            <ul className="generated-numbers">
              {generatedNumbers.map((num) => (
                <li key={num}>
                  {num} <Button onClick={() => handleSelect(num)}>Select</Button>
                </li>
              ))}
            </ul>
          </Card>
        )}

        {selectedNumber && (
          <Card>
            <h3>Your Selected Number</h3>
            <p>{selectedNumber}</p>
            <p>SIM provisioning is in progress and activation will happen within 24 hours.</p>
          </Card>
        )}

        {errorMsg && <p className="error-message">{errorMsg}</p>}
        {infoMsg && <p className="info-message">{infoMsg}</p>}
      </div>
      <Footer />
    </>
  );
}
