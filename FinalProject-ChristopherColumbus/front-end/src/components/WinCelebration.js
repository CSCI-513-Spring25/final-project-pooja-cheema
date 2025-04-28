import React, { useEffect } from 'react';
import '../styles/WinCelebration.css';

/**
 * This component displays a celebratory animation when CC wins
 */
const WinCelebration = ({ show, items }) => {
    useEffect(() => {
        if (show) {

            // Select container for celebration balloons
            const balloonContainer = document.querySelector('.balloon-shower');
            balloonContainer.innerHTML = ''; // Clear previous items

            // For each item type in items array, create multiple elements
            items.forEach(item => {
                for (let i = 0; i < 50; i++) {
                    const element = document.createElement('div');
                    element.className = item.className; // Assign provided class for styling/animation
                    // Randomize left position and animation delay
                    element.style.left = `${Math.random() * 100}%`;
                    element.style.animationDelay = `${Math.random() * 2}s`;
                    // Add animated element to container
                    balloonContainer.appendChild(element);
                }
            });
        }
    }, [show, items]); // Re-run effect if 'show' or 'items' change

    // If not showing, don't render anything
    if (!show) {
        return null;
    }

    // Render the container for shower effect
    return <div className="balloon-shower"></div>;
};

export default WinCelebration;
