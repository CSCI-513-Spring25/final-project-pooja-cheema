import React, { useEffect } from 'react';
import '../styles/WinCelebration.css';

const WinCelebration = ({ show, items }) => {
    useEffect(() => {
        if (show) {
            const balloonContainer = document.querySelector('.balloon-shower');
            balloonContainer.innerHTML = ''; // Clear previous items
            items.forEach(item => {
                for (let i = 0; i < 50; i++) {
                    const element = document.createElement('div');
                    element.className = item.className;
                    element.style.left = `${Math.random() * 100}%`;
                    element.style.animationDelay = `${Math.random() * 2}s`;
                    balloonContainer.appendChild(element);
                }
            });
        }
    }, [show, items]);

    if (!show) {
        return null;
    }

    return <div className="balloon-shower"></div>;
};

export default WinCelebration;
