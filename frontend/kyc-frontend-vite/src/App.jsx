import { useEffect, useMemo, useState } from 'react';

// CORRECTED BASE URL to match backend port 8082
const BASE = 'http://localhost:8082/api/documents'; 

export default function App() {
  const [cardType, setCardType] = useState('Aadhar');
  const [image1, setImage1] = useState(null);
  const [image2, setImage2] = useState(null);
  const [uploadResult, setUploadResult] = useState(null);
  const [loading, setLoading] = useState({ upload: false });

  // Determines if the back image is required based on cardType
  const requiresBack = useMemo(() => cardType.toLowerCase() === 'aadhar', [cardType]);

  // Log the BASE URL for verification
  useEffect(() => {
    console.log("Frontend attempting to reach backend at BASE URL:", BASE);
  }, []);

  // Helper function to mask card numbers
  const maskCardNumber = (cardNumber) => {
    if (!cardNumber || typeof cardNumber !== 'string' || cardNumber.length < 4) {
      return cardNumber;
    }
    const lastFour = cardNumber.slice(-4);
    return 'X'.repeat(cardNumber.length - 4) + lastFour;
  };

  // Helper function to format DOB string from dd/MM/yyyy to YYYY-MM-DD
  const formatDobDisplay = (dobString) => {
    console.log("DEBUG-DOB: formatDobDisplay called with:", dobString, " (Type:", typeof dobString + ")");

    if (!dobString || typeof dobString !== 'string') {
      console.log("DEBUG-DOB: Invalid DOB string (null, undefined, or not string). Returning original:", dobString);
      return dobString;
    }

    const parts = dobString.split('/');
    console.log("DEBUG-DOB: Split parts by '/':", parts);

    if (parts.length === 3) {
      const [day, month, year] = parts;
      console.log("DEBUG-DOB: Extracted: Day =", day, "Month =", month, "Year =", year);
      
      const formattedDate = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`; // Example: 1986-07-16
      console.log("DEBUG-DOB: Successfully formatted to:", formattedDate);
      return formattedDate;
    }
    
    console.log("DEBUG-DOB: DOB string format unexpected after split. Returning original:", dobString);
    return dobString;
  };

  // NEW: Robust Helper function to format LocalDateTime string from dd/MM/yyyy HH:mm:ss
  const formatDateTimeDisplay = (dateTimeString) => {
    console.log("DEBUG-DATETIME: formatDateTimeDisplay called with:", dateTimeString, " (Type:", typeof dateTimeString + ")");
    if (!dateTimeString || typeof dateTimeString !== 'string') {
      console.log("DEBUG-DATETIME: Invalid DateTime string. Returning original.");
      return dateTimeString;
    }

    // Expected format from backend: "dd/MM/yyyy HH:mm:ss"
    // Manually parse to construct a Date object, as Date.parse() is unreliable for dd/MM/yyyy.
    const parts = dateTimeString.match(/(\d{2})\/(\d{2})\/(\d{4}) (\d{2}):(\d{2}):(\d{2})/);
    if (parts && parts.length === 7) {
        // Note: JavaScript Date object month is 0-indexed (0=Jan, 11=Dec)
        const [, day, month, year, hour, minute, second] = parts.map(Number); // Convert all matched parts to numbers
        const dateObject = new Date(year, month - 1, day, hour, minute, second);
        
        // Check if the constructed date is valid
        if (isNaN(dateObject.getTime())) {
            console.log("DEBUG-DATETIME: Constructed Date object is invalid. Returning original.");
            return dateTimeString;
        }

        const formatted = dateObject.toLocaleString(navigator.language, {
            year: 'numeric',
            month: 'short', // e.g., "Aug"
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: true // Use 12-hour format with AM/PM
        });
        console.log("DEBUG-DATETIME: Successfully formatted to:", formatted);
        return formatted;
    }
    console.log("DEBUG-DATETIME: DateTime string format unexpected after regex match. Returning original.");
    return dateTimeString;
  };


  // Handles document upload
  async function handleUpload(e) {
    e.preventDefault();
    const fd = new FormData();
    fd.append('cardType', cardType);
    if (image1) fd.append('image1', image1);
    if (requiresBack && image2) fd.append('image2', image2);

    setLoading(s => ({ ...s, upload: true }));
    setUploadResult('Uploading...');

    try {
      const res = await fetch(`${BASE}/upload`, { method: 'POST', body: fd });
      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`HTTP error! status: ${res.status}, body: ${errorText}`);
      }
      const text = await res.text();
      try {
        const parsedResult = JSON.parse(text);
        
        console.log("DEBUG-UPLOAD: Raw parsedResult from backend:", parsedResult); // Full backend response
        console.log("DEBUG-UPLOAD: Raw dob from backend:", parsedResult.dob); 
        console.log("DEBUG-UPLOAD: Raw address from backend:", parsedResult.address); // Log address
        console.log("DEBUG-UPLOAD: Raw preparedAt from backend:", parsedResult.preparedAt); 

        if (parsedResult && parsedResult.cardNumber) { 
          parsedResult.cardNumber = maskCardNumber(parsedResult.cardNumber); 
        }

        // Store backend's preparedAt for display formatting
        parsedResult.displayPreparedAt = parsedResult.preparedAt; 

        if (parsedResult.simEligibilityMessage) {
          if (parsedResult.simEligibilityMessage.includes("Not eligible")) {
            parsedResult.simEligibilityDisplayStatus = "Not Eligible";
          } else if (parsedResult.simEligibilityMessage.includes("Eligible for SIM card.")) {
            parsedResult.simEligibilityDisplayStatus = "Eligible";
          } else {
            parsedResult.simEligibilityDisplayStatus = "Undetermined";
          }
        } else {
           parsedResult.simEligibilityDisplayStatus = "Undetermined";
        }

        setUploadResult(parsedResult);
      } catch (jsonError) {
        console.error("DEBUG-UPLOAD: Failed to parse backend response as JSON. Raw text:", text, "Error:", jsonError);
        setUploadResult({ error: "Failed to parse backend response as JSON: " + text.substring(0, 200) + "..." });
      }
    } catch (e) {
      console.error("DEBUG-UPLOAD: Upload failed:", e);
      setUploadResult({ error: e.message });
    } finally {
      setLoading(s => ({ ...s, upload: false }));
    }
  }

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 p-6 font-sans">
      <div className="max-w-5xl mx-auto space-y-6">
        <h1 className="text-4xl font-extrabold text-center text-blue-400 mb-8 rounded-lg p-3 shadow-lg">KYC Document Processer</h1>

        {/* Upload Section */}
        <div className="bg-slate-800/60 rounded-3xl p-6 shadow-2xl border border-slate-700">
          <h2 className="text-2xl font-bold mb-5 text-blue-300">Upload Document</h2>
          <form onSubmit={handleUpload} className="grid gap-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
              <label htmlFor="cardType" className="text-slate-300 font-medium">Government Proof Type</label>
              <select
                id="cardType"
                value={cardType}
                onChange={e => setCardType(e.target.value)}
                className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
              >
                <option value="Aadhar">Aadhar</option>
                <option value="Pan">Pan</option>
              </select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
              <label htmlFor="image1" className="text-slate-300 font-medium">Front image of the Government Id</label>
              <input
                type="file"
                id="image1"
                accept="image/*,.pdf"
                onChange={e => setImage1(e.target.files?.[0] ?? null)}
                className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-4 py-2 file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-sm file:font-semibold file:bg-blue-500 file:text-slate-900 hover:file:bg-blue-600 transition duration-200"
                required
              />
            </div>

            {requiresBack && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
                <label htmlFor="image2" className="text-slate-300 font-medium">Back image of the Government Id</label>
                <input
                  type="file"
                  id="image2"
                  accept="image/*,.pdf"
                  onChange={e => setImage2(e.target.files?.[0] ?? null)}
                  className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-4 py-2 file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-sm file:font-semibold file:bg-blue-500 file:text-slate-900 hover:file:bg-blue-600 transition duration-200"
                />
              </div>
            )}

            <div>
              <button
                type="submit"
                disabled={loading.upload}
                className="bg-blue-500 hover:brightness-110 disabled:opacity-50 text-slate-900 font-bold rounded-xl px-6 py-2 shadow-md transition duration-200"
              >
                {loading.upload ? 'Uploading...' : 'Upload Document'}
              </button>
            </div>
          </form>

          <div className="mt-6 text-sm bg-slate-900 border border-slate-700 rounded-xl p-4 overflow-auto max-h-60">
            <h3 className="text-slate-400 font-semibold mb-2">Upload Result:</h3>
            {loading.upload && <p className="text-slate-200">Uploading...</p>}
            {uploadResult && typeof uploadResult === 'string' && !loading.upload && (
              <p className="text-slate-200">{uploadResult}</p>
            )}
            {uploadResult && typeof uploadResult === 'object' && !loading.upload && (
              <div className="overflow-x-auto rounded-xl border border-slate-700">
                <table className="min-w-full text-sm divide-y divide-slate-700">
                  <thead className="bg-slate-700">
                    <tr className="text-left text-slate-200">
                      <th className="py-3 px-4">Field</th>
                      <th className="py-3 px-4">Value</th>
                    </tr>
                  </thead>
                  <tbody className="bg-slate-800 divide-y divide-slate-700">
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">ID</td>
                      <td className="py-3 px-4">{uploadResult.id ?? '-'}</td>
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Card Type</td>
                      <td className="py-3 px-4">{uploadResult.cardType ?? '-'}</td>
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Name</td> 
                      <td className="py-3 px-4">{uploadResult.name ?? '-'}</td> 
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Gender</td> 
                      <td className="py-3 px-4">{uploadResult.gender ?? '-'}</td> 
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Address</td> 
                      <td className="py-3 px-4">{uploadResult.address ?? '-'}</td> 
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Age</td> 
                      <td className="py-3 px-4">{uploadResult.age ?? '-'}</td> 
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Card Number</td> 
                      <td className="py-3 px-4">{uploadResult.cardNumber ?? '-'}</td> 
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">Status</td>
                      <td className="py-3 px-4"><span className="bg-blue-700 text-blue-100 rounded-full px-3 py-1 text-xs font-semibold">{uploadResult.status ?? '-'}</span></td>
                    </tr>
                    <tr className="hover:bg-slate-700 transition duration-150">
                      <td className="py-3 px-4">SIM Eligibility</td>
                      <td className="py-3 px-4">
                        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${
                          uploadResult.simEligibilityDisplayStatus === 'Eligible' ? 'bg-green-700 text-green-100' :
                          uploadResult.simEligibilityDisplayStatus === 'Not Eligible' ? 'bg-red-700 text-red-100' :
                          'bg-gray-700 text-gray-100'
                        }`}>
                          {uploadResult.simEligibilityDisplayStatus ?? '-'}
                        </span>
                        {uploadResult.simEligibilityMessage && (
                          <p className="mt-1 text-xs text-slate-400">{uploadResult.simEligibilityMessage}</p>
                        )}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            )}
             {uploadResult && uploadResult.error && (
              <p className="text-red-400">Error: {uploadResult.error}</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
