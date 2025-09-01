import React, { useEffect } from 'react';

const TawkToChat = () => {
  useEffect(() => {
    // Create a script element
    const script = document.createElement('script');
    
    // This is your correct URL
    script.src = 'https://embed.tawk.to/68b57451cbdd78615202cedb/1j42csnlg';
    script.async = true;
    script.charset = 'UTF-8';
    script.setAttribute('crossorigin', '*');

    // Append the script to the body
    document.body.appendChild(script);

    // Clean up the script when the component unmounts to prevent memory leaks
    return () => {
      document.body.removeChild(script);
    };
  }, []); // The empty dependency array ensures this runs only once when the component mounts

  // This component doesn't render any visible HTML itself, it just adds the script
  return null; 
};

export default TawkToChat;