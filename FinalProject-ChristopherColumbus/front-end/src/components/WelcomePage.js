// import React from 'react';
import React, { useEffect } from 'react';
import '../styles/welcome.css';

const WelcomePage = ({ onStart }) => {


    useEffect(() => {
        const handleKeyPress = (event) => {
            if (event.key === 'Enter') {
                onStart();
            }
        };

        window.addEventListener('keydown', handleKeyPress);
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