import React from 'react';
import '../styles/welcome.css';

const WelcomePage = ({ onStart }) => {
    return (
        <div className="welcome-page">
            <img src="/images/welcome3.jpg" alt="Welcome" className="welcome-image" />
            <button className="start-button" onClick={onStart}>Let's Start the Game</button>
        </div>
    );
};

export default WelcomePage;