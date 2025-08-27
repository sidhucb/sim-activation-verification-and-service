import React, { useEffect, useState } from "react";
import {
  getPendingManualDocs,
  getPendingOcrDocs,
  approveManualDoc,
  rejectManualDoc,
  approveOcrDocument,
  rejectOcrDocument,
} from "../services/apiService";

import Button from "../components/Button";
import Card from "../components/Card";

import "./UnifiedKycApproval.css";

export default function UnifiedKycApproval() {
  const [loading, setLoading] = useState(true);
  const [combinedKycList, setCombinedKycList] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const [manualDocs, ocrDocs] = await Promise.all([
          getPendingManualDocs(),
          getPendingOcrDocs(),
        ]);

        // map manual by userId
        const manualMap = {};
        manualDocs.forEach((doc) => {
          manualMap[doc.userId] = doc;
        });

        let combined = ocrDocs.map((ocr) => ({
          userId: ocr.userId,
          ocr,
          manual: manualMap[ocr.userId] || null,
        }));

        // include manual-only users
        manualDocs.forEach((manual) => {
          if (!combined.some((item) => item.userId === manual.userId)) {
            combined.push({ userId: manual.userId, manual, ocr: null });
          }
        });

        setCombinedKycList(combined);
        setError(null);
      } catch (err) {
        setError("Failed to load KYC data");
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  const handleApproveManual = async (id) => {
    try {
      await approveManualDoc(id);
      setCombinedKycList((list) =>
        list.filter((item) => !item.manual || item.manual.id !== id)
      );
    } catch (err) {
      alert("Approve manual document failed");
      console.error(err);
    }
  };

  const handleRejectManual = async (id) => {
    try {
      await rejectManualDoc(id);
      setCombinedKycList((list) =>
        list.filter((item) => !item.manual || item.manual.id !== id)
      );
    } catch (err) {
      alert("Reject manual document failed");
      console.error(err);
    }
  };

  const handleApproveOcr = async (id) => {
    try {
      await approveOcrDocument(id);
      setCombinedKycList((list) =>
        list.filter((item) => !item.ocr || item.ocr.id !== id)
      );
    } catch (err) {
      alert("Approve OCR document failed");
      console.error(err);
    }
  };

  const handleRejectOcr = async (id) => {
    try {
      await rejectOcrDocument(id);
      setCombinedKycList((list) =>
        list.filter((item) => !item.ocr || item.ocr.id !== id)
      );
    } catch (err) {
      alert("Reject OCR document failed");
      console.error(err);
    }
  };

  if (loading) return <div className="loading">Loading pending KYC requests...</div>;
  if (error) return <div className="error">{error}</div>;
  if (combinedKycList.length === 0) return <div>No pending KYC requests.</div>;

  return (
    <div className="page-container">
      <h1 className="page-title">Unified KYC Approval</h1>
      <Card>
        <table className="kyc-table">
          <thead>
            <tr>
              <th rowSpan={2}>User ID</th>
              <th colSpan={6}>Manual Entry</th>
              <th colSpan={8}>OCR Extracted</th>
              <th rowSpan={2}>Actions</th>
            </tr>
            <tr>
              <th>Full Name</th>
              <th>DOB</th>
              <th>Address</th>
              <th>ID Number</th>
              <th>Phone</th>
              <th>Email</th>

              <th>Card Type</th>
              <th>Name</th>
              <th>DOB</th>
              <th>Gender</th>
              <th>Address</th>
              <th>Card Number</th>
              <th>Status</th>
              <th>Eligibility Msg</th>
            </tr>
          </thead>
          <tbody>
            {combinedKycList.map(({ userId, manual, ocr }) => (
              <tr key={userId}>
                <td data-label="User ID">{userId}</td>

                <td data-label="Full Name">{manual?.fullName || "—"}</td>
                <td data-label="DOB">{manual?.dob || "—"}</td>
                <td data-label="Address">{manual?.address || "—"}</td>
                <td data-label="ID Number">{manual?.idNumber || "—"}</td>
                <td data-label="Phone">{manual?.phoneNumber || "—"}</td>
                <td data-label="Email">{manual?.email || "—"}</td>

                <td data-label="Card Type">{ocr?.cardType || "—"}</td>
                <td data-label="Name">{ocr?.name || "—"}</td>
                <td data-label="DOB">{ocr?.dob || "—"}</td>
                <td data-label="Gender">{ocr?.gender || "—"}</td>
                <td data-label="Address">{ocr?.address || "—"}</td>
                <td data-label="Card Number">{ocr?.cardNumber || "—"}</td>
                <td data-label="Status">{ocr?.status || "—"}</td>
                <td data-label="Eligibility Msg">{ocr?.simEligibilityMessage || "—"}</td>

                <td data-label="Actions" className="actions-cell">
                  {manual && (
                    <>
                      <Button onClick={() => handleApproveManual(manual.id)} className="btn-approve">
                        Approve Manual
                      </Button>
                      <Button onClick={() => handleRejectManual(manual.id)} className="btn-reject">
                        Reject Manual
                      </Button>
                    </>
                  )}
                  {ocr && (
                    <>
                      <Button onClick={() => handleApproveOcr(ocr.id)} className="btn-approve">
                        Approve OCR
                      </Button>
                      <Button onClick={() => handleRejectOcr(ocr.id)} className="btn-reject">
                        Reject OCR
                      </Button>
                    </>
                  )}
                  {!manual && !ocr && <span>—</span>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}
