// src/services/apiService.js
import axios from "axios";

const BASE_URL = "http://localhost:8081";

export const loginUser = async (email, password) => {
  try {
    const response = await axios.post(`${BASE_URL}/users/login`, {
      email,
      password,
    });
    return response.data;   // => { token: "..." }
  } catch (error) {
    console.error("Login error:", error);
    throw error;
  }
};
