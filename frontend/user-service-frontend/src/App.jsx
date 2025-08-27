import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import AdminDashboard from "./pages/AdminDashboard";
import UserManagement from "./pages/UserManagement";
import UnifiedKycApproval from "./pages/UnifiedKycApproval"; // import new page
import Navbar from "./components/Navbar";

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
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } 
        />
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
        {/* New route for unified KYC approval */}
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
