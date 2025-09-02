import React, { useState, useEffect } from "react";
import {
  submitManualDetails,
  uploadDocumentWithImages,
  getEligibilityStatus,
} from "../services/apiService";
import Card from "../components/Card";
import Button from "../components/Button";
import "./UserKycDashboard.css";

export default function UserKycDashboard() {
  const [manualDetails, setManualDetails] = useState({
    fullName: "",
    dob: "",
    address: "",
    idNumber: "",
    phoneNumber: "",
    email: "",
  });
  const [imageFront, setImageFront] = useState(null);
  const [imageBack, setImageBack] = useState(null);

  const [loadingStatus, setLoadingStatus] = useState(true);
  const [kycStatus, setKycStatus] = useState(null);
  const [simStatus, setSimStatus] = useState(null);
  const [eligibilityMessage, setEligibilityMessage] = useState("");

  const [submittingManual, setSubmittingManual] = useState(false);
  const [uploadingDocs, setUploadingDocs] = useState(false);
  const [manualSubmitMsg, setManualSubmitMsg] = useState("");
  const [docUploadMsg, setDocUploadMsg] = useState("");

  useEffect(() => {
    async function fetchStatus() {
      setLoadingStatus(true);
      try {
        const status = await getEligibilityStatus();
        setKycStatus(status.kycStatus || "Unknown");
        setSimStatus(status.simStatus || "Unknown");
        setEligibilityMessage(status.eligibilityMsg || "");
      } catch (error) {
        console.error("Failed to fetch eligibility status", error);
      } finally {
        setLoadingStatus(false);
      }
    }
    fetchStatus();
  }, []);

  const handleInputChange = (e) => {
    setManualDetails({
      ...manualDetails,
      [e.target.name]: e.target.value,
    });
  };

  const handleManualSubmit = async (e) => {
    e.preventDefault();
    setSubmittingManual(true);
    setManualSubmitMsg("");
    try {
      await submitManualDetails(manualDetails);
      setManualSubmitMsg("Manual details submitted successfully.");
    } catch (error) {
      setManualSubmitMsg("Failed to submit manual details.");
      console.error(error);
    } finally {
      setSubmittingManual(false);
    }
  };

  const handleDocUpload = async (e) => {
    e.preventDefault();
    if (!imageFront) {
      setDocUploadMsg("Please select front image of your ID.");
      return;
    }
    setUploadingDocs(true);
    setDocUploadMsg("");
    try {
      await uploadDocumentWithImages("idcard", imageFront, imageBack);
      setDocUploadMsg("Documents uploaded successfully.");
    } catch (error) {
      setDocUploadMsg("Failed to upload documents.");
      console.error(error);
    } finally {
      setUploadingDocs(false);
    }
  };

  return (
    <div className="user-kyc-page">
      <h1>User KYC Dashboard</h1>

      {loadingStatus ? (
        <p>Loading status...</p>
      ) : (
        <Card className="status-card">
          <p><strong>KYC Status:</strong> {kycStatus}</p>
          <p><strong>SIM Status:</strong> {simStatus}</p>
          {eligibilityMessage && (
            <p><strong>Message:</strong> {eligibilityMessage}</p>
          )}
        </Card>
      )}

      <Card className="manual-details-card">
        <h2>Submit Manual KYC Details</h2>
        <form onSubmit={handleManualSubmit}>
          <input
            type="text"
            name="fullName"
            placeholder="Full Name"
            value={manualDetails.fullName}
            onChange={handleInputChange}
            required
          />
          <input
            type="date"
            name="dob"
            placeholder="Date of Birth"
            value={manualDetails.dob}
            onChange={handleInputChange}
            required
          />
          <input
            type="text"
            name="address"
            placeholder="Address"
            value={manualDetails.address}
            onChange={handleInputChange}
            required
          />
          <input
            type="text"
            name="idNumber"
            placeholder="ID Number"
            value={manualDetails.idNumber}
            onChange={handleInputChange}
            required
          />
          <input
            type="tel"
            name="phoneNumber"
            placeholder="Phone Number"
            value={manualDetails.phoneNumber}
            onChange={handleInputChange}
            required
          />
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={manualDetails.email}
            onChange={handleInputChange}
            required
          />
          <Button type="submit" disabled={submittingManual}>
            {submittingManual ? "Submitting..." : "Submit Manual Details"}
          </Button>
        </form>
        {manualSubmitMsg && <p className="message">{manualSubmitMsg}</p>}
      </Card>

      <Card className="doc-upload-card">
        <h2>Upload ID Documents</h2>
        <form onSubmit={handleDocUpload}>
          <label>
            Front Image:
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setImageFront(e.target.files[0])}
              required
            />
          </label>
          <label>
            Back Image (optional):
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setImageBack(e.target.files[0])}
            />
          </label>
          <Button type="submit" disabled={uploadingDocs}>
            {uploadingDocs ? "Uploading..." : "Upload Documents"}
          </Button>
        </form>
        {docUploadMsg && <p className="message">{docUploadMsg}</p>}
      </Card>
    </div>
  );
}
