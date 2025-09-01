import React, { useState } from 'react';
import './FaqPages.css'; // We will create this CSS file in the next step

// --- FAQ Data ---
// You can easily add, remove, or edit questions and answers here.
const faqData = [
  {
    question: "What is the KYC process for?",
    answer: "The Know Your Customer (KYC) process is a mandatory verification of a customer's identity. It helps us prevent fraud and ensure that all users are legitimate, which is required by regulations for providing services like SIM cards."
  },
  {
    question: "What documents can I upload for verification?",
    answer: "Currently, our automated system is optimized for Aadhar cards. You will need to upload clear images of both the front and back of your card for the OCR and AI verification to work correctly."
  },
  {
    question: "How long does the verification process take?",
    answer: "After you submit your documents, they are sent for admin review. This typically takes 24-48 business hours. You will receive an email notification once your KYC status is updated."
  },
  {
    question: "My KYC was approved. What's the next step?",
    answer: "Congratulations! Once your KYC is approved, you can proceed to the 'Check SIM Status' page from your dashboard. There, you will be able to generate and select your new mobile number."
  },
  {
    question: "How do I generate and choose my phone number?",
    answer: "On the 'Check SIM Status' page, you will find an option to generate numbers. You can enter your four favorite digits, and our system will provide a list of available numbers. Simply click on the one you want to select it."
  },
  {
    question: "How long until my new SIM is activated?",
    answer: "After you select your number, it enters the 'Provisioning' stage. Activation is typically completed within 24 hours. You will be notified once it's active."
  }
];

// --- Accordion Item Component ---
const FaqItem = ({ item, index, openIndex, handleToggle }) => {
  const isOpen = index === openIndex;

  return (
    <div className="faq-item">
      <div className="faq-question" onClick={() => handleToggle(index)}>
        {item.question}
        <span className="faq-icon">{isOpen ? '−' : '+'}</span>
      </div>
      <div className={`faq-answer ${isOpen ? 'open' : ''}`}>
        <p>{item.answer}</p>
      </div>
    </div>
  );
};

// --- Main FAQ Page Component ---
const FaqPage = () => {
  const [openIndex, setOpenIndex] = useState(null);

  const handleToggle = (index) => {
    // If the clicked item is already open, close it. Otherwise, open it.
    setOpenIndex(openIndex === index ? null : index);
  };

  return (
    <div className="faq-page-container">
      <div className="faq-content card">
        <h1 className="faq-title">Frequently Asked Questions</h1>
        <div className="faq-list">
          {faqData.map((item, index) => (
            <FaqItem
              key={index}
              item={item}
              index={index}
              openIndex={openIndex}
              handleToggle={handleToggle}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default FaqPage;