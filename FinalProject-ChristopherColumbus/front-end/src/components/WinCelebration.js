import React, { useEffect } from 'react';
import '../styles/WinCelebration.css';

/**
 * This component displays a celebratory animation when CC wins
 */
const WinCelebration = ({ show, items }) => {
    useEffect(() => {
        if (show) {

            // Select container for animation items (eg. balloons)
            const balloonContainer = document.querySelector('.win-celebration');
            balloonContainer.innerHTML = ''; // Clear previous items

            // For each item type in items array, create multiple elements
            items.forEach(item => {
                for (let i = 0; i < 100; i++) { // 100 per type for denser effect
                    const element = document.createElement('div');
                    element.className = item.className; // Assign provided class for styling/animation

                    // Randomize horizontal position and animation delay for more randomness
                    element.style.left = `${Math.random() * 100}%`;
                    element.style.animationDelay = `${Math.random() * 10}s`;
                    balloonContainer.appendChild(element);
                }
            });
        }
    }, [show, items]); // Re-run effect if 'show' or 'items' change

    // If not showing, don't render anything
    if (!show) {
        return null;
    }

    // Render the overlay container for shower effect
    return <div className="win-celebration"></div>;
};

export default WinCelebration;