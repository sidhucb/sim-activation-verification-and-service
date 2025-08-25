import axios from "axios";

const BASE_URL = "http://localhost:8081";

// Login API
export const loginUser = async (email, password) => {
  try {
    const response = await axios.post(`${BASE_URL}/users/login`, { email, password });
    return response.data; // { token: "..." }
  } catch (err) {
    console.error("Login error:", err);
    if (err.response && err.response.data && err.response.data.message) {
      throw new Error(err.response.data.message);
    }
    throw new Error("Login failed. Check credentials or server.");
  }
};

// Registration API
export const registerUser = async (email, password) => {
  try {
    const response = await axios.post(`${BASE_URL}/users/register`, { email, password, role: "USER" });
    return response.data;
  } catch (err) {
    console.error("Registration error:", err);
    if (err.response && err.response.data && err.response.data.message) {
      throw new Error(err.response.data.message);
    }
    throw new Error("Registration failed. Try again.");
  }
};
