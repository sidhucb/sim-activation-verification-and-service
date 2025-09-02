import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import AdminDashboard from "./pages/AdminDashboard";
import UserManagement from "./pages/UserManagement";
import UnifiedKycApproval from "./pages/UnifiedKycApproval";
import Navbar from "./components/Navbar";
import UserKycDashboard from "./pages/UserKycDashboard";
import CheckSimStatus from "./pages/CheckSimStatus";
import FaqPage from "./pages/FaqPages";
import TawkToChat from './components/TawkToChat'; // <-- 1. IMPORT THE CHAT COMPONENT

// MOCK AUTH HOOK FOR DEMONSTRATION
// In your real app, this would get state from a context
const useAuth = () => {
  const user = { 
    isLoggedIn: true, // Replace with your actual auth state
    role: 'ADMIN' // Replace with your actual user role
  };
  return user;
};

// PROTECTED ROUTE COMPONENT
const ProtectedRoute = ({ children, requiredRole }) => {
  const { isLoggedIn, role } = useAuth(); // Use your actual auth hook

  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && role !== requiredRole) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

function App() {
  return (
    <Router>
      <Navbar />
      <TawkToChat /> {/* <-- 2. ADD THE CHAT COMPONENT HERE */}
      <Routes>
        {/* --- Public Routes --- */}
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/faq" element={<FaqPage />} />

        {/* --- Protected User Routes --- */}
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } 
        />
        <Route
          path="/dashboard/kyc"
          element={
            <ProtectedRoute>
                <UserKycDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard/check-status"
          element={
            <ProtectedRoute>
              <CheckSimStatus />
            </ProtectedRoute>
          }
        />

        {/* --- Protected Admin Routes --- */}
        <Route 
          path="/admin-dashboard" 
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/admin-dashboard/users" 
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <UserManagement />
            </ProtectedRoute>
          } 
        />
        <Route
          path="/admin-dashboard/kyc-approval"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <UnifiedKycApproval />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;