import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import "./Home.css";

export default function Home() {
  return (
    <>
      <Header />
      <main className="home-main">
        <section className="hero-section">
          <h1>Welcome to Nexus Networks</h1>
          <p>Your trusted partner for fast SIM verification, secure management, and seamless connectivity.</p>
          <button className="cta-btn" onClick={() => alert('Navigate to Signup!')}>
            Get Started
          </button>
        </section>

        <section className="features-section">
          <h2>Key Features</h2>
          <ul>
            <li>Fast SIM verification</li>
            <li>Secure and intuitive account management</li>
            <li>Real-time notifications on your account</li>
            <li>Modern Aurora-inspired UI</li>
          </ul>
        </section>

        {/* New detailed section about SIM card activation */}
        <section className="activation-details-section">
          <h2>About Nexus Networks SIM Card Activation</h2>
          <p>
            Activating your Nexus Networks SIM card is quick and effortless. Simply insert your SIM card into your device,
            and follow our easy activation steps online or via the app. Our system verifies your SIM in real-time, ensuring
            immediate activation without delays.
          </p>
          <p>
            Benefits of our SIM activation process:
          </p>
          <ul>
            <li><strong>Instant Activation:</strong> Your SIM is verified and activated within seconds.</li>
            <li><strong>Secure Verification:</strong> Multi-factor authentication keeps your account safe.</li>
            <li><strong>User-Friendly Process:</strong> Intuitive app and web interface guide you through each step.</li>
            <li><strong>24/7 Customer Support:</strong> Assistance is available anytime to resolve activation issues.</li>
          </ul>
          <p>
            Start your connected journey with Nexus Networks and enjoy reliable coverage and cutting-edge technology built
            to keep you seamlessly linked to what matters most.
          </p>
        </section>
      </main>
      <Footer />
    </>
  );
}
