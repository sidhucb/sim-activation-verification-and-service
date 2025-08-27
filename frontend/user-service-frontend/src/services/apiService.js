import axios from "axios";

// Define base URLs for each microservice
// Make sure these ports match your running backend services
const USER_SERVICE_BASE_URL = "http://localhost:8081";
const DOCUMENT_SERVICE_BASE_URL = "http://localhost:8082"; 

// Create reusable axios instances for each service
const userClient = axios.create({
  baseURL: USER_SERVICE_BASE_URL,
});

const documentClient = axios.create({
  baseURL: DOCUMENT_SERVICE_BASE_URL,
});

// Helper function to get the JWT token and authorization headers
const getAuthHeaders = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    // Or throw an error, depending on how you handle unauthenticated requests
    return {}; 
  }
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

// --- User Service APIs (for both users and admins) ---

export const loginUser = async (email, password) => {
  try {
    const response = await userClient.post("/users/login", { email, password });
    return response.data; // { token: "..." }
  } catch (err) {
    console.error("Login error:", err);
    throw new Error(err.response?.data?.message || "Login failed. Check credentials or server.");
  }
};

export const registerUser = async (email, password) => {
  try {
    const response = await userClient.post("/users/register", { email, password, role: "USER" });
    return response.data;
  } catch (err) {
    console.error("Registration error:", err);
    throw new Error(err.response?.data || "Registration failed. Try again.");
  }
};

export const getAllUsers = async () => {
  try {
    const response = await userClient.get("/users/admin", getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Get all users error:", err);
    throw new Error(err.response?.data?.message || "Failed to fetch users.");
  }
};

// --- Document Verification Service APIs ---

// User-Facing Endpoints
export const submitManualDetails = async (details) => {
  try {
    const response = await documentClient.post("/api/manual-docs/submit", details, getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Manual details submission error:", err);
    throw new Error(err.response?.data?.message || "Failed to submit manual details.");
  }
};

export const uploadDocumentWithImages = async (cardType, image1, image2) => {
  const formData = new FormData();
  formData.append("cardType", cardType);
  formData.append("image1", image1);
  if (image2) {
    formData.append("image2", image2);
  }

  try {
    const response = await documentClient.post("/api/documents/upload", formData, {
      ...getAuthHeaders(),
      headers: { ...getAuthHeaders().headers, "Content-Type": "multipart/form-data" },
    });
    return response.data;
  } catch (err) {
    console.error("Document upload error:", err);
    throw new Error(err.response?.data?.message || "Failed to upload document.");
  }
};

export const getEligibilityStatus = async () => {
  try {
    const response = await documentClient.get("/api/documents/eligibility", getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Eligibility status error:", err);
    throw new Error(err.response?.data?.message || "Failed to get eligibility status.");
  }
};

// Admin-Facing Endpoints
export const getPendingManualDocs = async () => {
  try {
    const response = await documentClient.get("/api/manual-docs/pending", getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Get pending manual docs error:", err);
    throw new Error(err.response?.data?.message || "Failed to fetch pending manual documents.");
  }
};

export const getPendingOcrDocs = async () => {
  try {
    // Note: The backend endpoint is '/api/documents/pending'
    const response = await documentClient.get("/api/documents/pending", getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Get pending OCR docs error:", err);
    throw new Error(err.response?.data?.message || "Failed to fetch pending OCR documents.");
  }
};

export const approveManualDoc = async (id) => {
  try {
    const response = await documentClient.put(`/api/manual-docs/${id}/approve`, {}, getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Approve manual doc error:", err);
    throw new Error(err.response?.data?.message || "Failed to approve manual document.");
  }
};

export const rejectManualDoc = async (id) => {
  try {
    const response = await documentClient.put(`/api/manual-docs/${id}/reject`, {}, getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Reject manual doc error:", err);
    throw new Error(err.response?.data?.message || "Failed to reject manual document.");
  }
};

export const approveOcrDocument = async (id) => {
  try {
    const response = await documentClient.put(`/api/documents/approve/${id}`, {}, getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Approve OCR doc error:", err);
    throw new Error(err.response?.data?.message || "Failed to approve OCR document.");
  }
};

export const rejectOcrDocument = async (id) => {
  try {
    const response = await documentClient.put(`/api/documents/reject/${id}`, {}, getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Reject OCR doc error:", err);
    throw new Error(err.response?.data?.message || "Failed to reject OCR document.");
  }
};

export const getAllOcrDocuments = async () => {
  try {
    const response = await documentClient.get("/api/documents/all", getAuthHeaders());
    return response.data;
  } catch (err) {
    console.error("Get all OCR docs error:", err);
    throw new Error(err.response?.data?.message || "Failed to fetch all OCR documents.");
  }
};