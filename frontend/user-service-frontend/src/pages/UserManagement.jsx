import React, { useState, useEffect } from 'react';
import Card from '../components/Card';
import { getAllUsers } from '../services/apiService';
import './UserManagement.css'; 

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const usersData = await getAllUsers();
        setUsers(usersData);
        setLoading(false);
      } catch (err) {
        setError("Failed to fetch users.");
        setLoading(false);
      }
    };
    fetchUsers();
  }, []);

  if (loading) return <div className="user-management-page">Loading users...</div>;
  if (error) return <div className="user-management-page error-message">{error}</div>;

  return (
    <div className="user-management-page">
      <h1 className="page-title">User Management</h1>
      <Card className="aurora-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Email</th>
              <th>Role</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
    </div>
  );
}