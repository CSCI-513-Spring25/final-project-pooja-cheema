// import React from 'react';
import React, { useEffect } from 'react';
import '../styles/welcome.css';

/**
 * This component renders welcome page, with an image and play button
 */
const WelcomePage = ({ onStart }) => {
    useEffect(() => {
        // Key press handler : start game if enter key is pressed
        const handleKeyPress = (event) => {
            if (event.key === 'Enter') {
                onStart();
            }
        };

        // Attach event listener on mount
        window.addEventListener('keydown', handleKeyPress);

        // Clean up event listener on unmount
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, [onStart]);


    return (
        <div className="welcome-page">
            <img src="/images/welcome3.jpg" alt="Welcome" className="welcome-image" />
            <button className="start-button" onClick={onStart}>Let's Start the Game</button>
        </div>
    );
};

export default WelcomePage;