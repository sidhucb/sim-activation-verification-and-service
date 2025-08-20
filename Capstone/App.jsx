
import { useEffect, useMemo, useState } from 'react';

const BASE = 'http://localhost:8082/api/documents';

export default function App() {
  const [cardType, setCardType] = useState('Aadhar');
  const [image1, setImage1] = useState(null);
  const [image2, setImage2] = useState(null);
  const [uploadResult, setUploadResult] = useState(null);
  const [pending, setPending] = useState([]);
  const [eligibility, setEligibility] = useState([]);
  const [loading, setLoading] = useState({ upload:false, pending:false, elig:false });

  const requiresBack = useMemo(() => cardType.toLowerCase() === 'aadhar', [cardType]);

  async function handleUpload(e) {
    e.preventDefault();
    const fd = new FormData();
    fd.append('cardType', cardType);
    if (image1) fd.append('image1', image1);
    if (requiresBack && image2) fd.append('image2', image2);
    setLoading(s => ({...s, upload:true}));
    setUploadResult('Uploading...');
    try {
      const res = await fetch(`${BASE}/upload`, { method: 'POST', body: fd });
      const text = await res.text();
      try { setUploadResult(JSON.parse(text)); }
      catch { setUploadResult(text); }
      await loadPending();
    } catch (e) {
      setUploadResult({ error: e.message });
    } finally { setLoading(s => ({...s, upload:false})); }
  }

  async function loadPending() {
    setLoading(s => ({...s, pending:true}));
    try {
      const res = await fetch(`${BASE}/pending`);
      const data = await res.json();
      setPending(data);
    } catch (e) {
      console.error(e);
    } finally { setLoading(s => ({...s, pending:false})); }
  }

  async function updateStatus(id, status) {
    try {
      await fetch(`${BASE}/${id}/status?status=${encodeURIComponent(status)}`, { method: 'PUT' });
      await loadPending();
    } catch (e) {
      alert('Update failed: ' + e.message);
    }
  }

  async function loadEligibility() {
    setLoading(s => ({...s, elig:true}));
    try {
      const res = await fetch(`${BASE}/eligibility`);
      const data = await res.json();
      setEligibility(data);
    } catch (e) { console.error(e); }
    finally { setLoading(s => ({...s, elig:false})); }
  }

  useEffect(() => { loadPending(); }, []);

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 p-6">
      <div className="max-w-5xl mx-auto space-y-6">
        <h1 className="text-2xl font-semibold">Document Verification</h1>

        {/* Upload */}
        <div className="bg-slate-800/60 rounded-2xl p-5 shadow-xl">
          <h2 className="text-lg font-medium mb-4">Upload Document</h2>
          <form onSubmit={handleUpload} className="grid gap-3">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
              <label className="text-slate-300">Card Type</label>
              <select value={cardType} onChange={e=>setCardType(e.target.value)} className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-3 py-2">
                <option>Aadhar</option>
                <option>Pan</option>
                <option>Other</option>
              </select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
              <label className="text-slate-300">Image 1 (Front)</label>
              <input type="file" accept="image/*,.pdf" onChange={e=>setImage1(e.target.files?.[0] ?? null)} className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-3 py-2" required />
            </div>

            {requiresBack && (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
                <label className="text-slate-300">Image 2 (Back)</label>
                <input type="file" accept="image/*,.pdf" onChange={e=>setImage2(e.target.files?.[0] ?? null)} className="md:col-span-2 bg-slate-900 border border-slate-700 rounded-xl px-3 py-2" />
              </div>
            )}

            <div>
              <button disabled={loading.upload} className="bg-blue-500 hover:brightness-110 disabled:opacity-50 text-slate-900 font-semibold rounded-xl px-4 py-2">{loading.upload ? 'Uploading...' : 'Upload'}</button>
            </div>
          </form>

          <div className="mt-4 text-sm bg-slate-900 border border-slate-700 rounded-xl p-3 overflow-auto">
            <pre className="whitespace-pre-wrap">{typeof uploadResult === 'string' ? uploadResult : JSON.stringify(uploadResult, null, 2)}</pre>
          </div>
        </div>

        {/* Pending */}
        <div className="bg-slate-800/60 rounded-2xl p-5 shadow-xl">
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-lg font-medium">Pending Documents</h2>
            <button onClick={loadPending} className="bg-slate-200 text-slate-900 rounded-xl px-3 py-1">Refresh</button>
          </div>
          <div className="overflow-auto">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="text-left border-b border-slate-700">
                  <th className="py-2 pr-3">ID</th>
                  <th className="py-2 pr-3">Card Type</th>
                  <th className="py-2 pr-3">Name</th>
                  <th className="py-2 pr-3">Age</th>
                  <th className="py-2 pr-3">Status</th>
                  <th className="py-2 pr-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading.pending ? (
                  <tr><td className="py-3" colSpan={6}>Loading...</td></tr>
                ) : pending.length ? pending.map(p => (
                  <tr key={p.id} className="border-b border-slate-800">
                    <td className="py-2 pr-3">{p.id}</td>
                    <td className="py-2 pr-3">{p.cardType}</td>
                    <td className="py-2 pr-3">{p.name}</td>
                    <td className="py-2 pr-3">{p.age ?? '-'}</td>
                    <td className="py-2 pr-3"><span className="bg-slate-700 rounded-full px-2 py-1 text-xs">{p.status}</span></td>
                    <td className="py-2 pr-3 space-x-2">
                      <button onClick={()=>updateStatus(p.id, 'approved')} className="bg-green-400 text-slate-900 rounded-lg px-3 py-1">Approve</button>
                      <button onClick={()=>updateStatus(p.id, 'disapproved')} className="bg-rose-400 text-slate-900 rounded-lg px-3 py-1">Disapprove</button>
                    </td>
                  </tr>
                )) : (
                  <tr><td className="py-3" colSpan={6}>No pending documents</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Eligibility */}
        <div className="bg-slate-800/60 rounded-2xl p-5 shadow-xl">
          <div className="flex items-center justify-between mb-3">
            <h2 className="text-lg font-medium">Eligibility</h2>
            <button onClick={loadEligibility} className="bg-slate-200 text-slate-900 rounded-xl px-3 py-1">Load</button>
          </div>
          <div className="overflow-auto">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="text-left border-b border-slate-700">
                  <th className="py-2 pr-3">ID</th>
                  <th className="py-2 pr-3">Name</th>
                  <th className="py-2 pr-3">Age</th>
                  <th className="py-2 pr-3">Address</th>
                  <th className="py-2 pr-3">Eligibility</th>
                </tr>
              </thead>
              <tbody>
                {loading.elig ? (
                  <tr><td className="py-3" colSpan={5}>Loading...</td></tr>
                ) : eligibility.length ? eligibility.map(el => (
                  <tr key={el.id} className="border-b border-slate-800">
                    <td className="py-2 pr-3">{el.id}</td>
                    <td className="py-2 pr-3">{el.name}</td>
                    <td className="py-2 pr-3">{el.age ?? '-'}</td>
                    <td className="py-2 pr-3">{el.address ?? '-'}</td>
                    <td className="py-2 pr-3">{el.eligibilityStatus}</td>
                  </tr>
                )) : (
                  <tr><td className="py-3" colSpan={5}>No data</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
