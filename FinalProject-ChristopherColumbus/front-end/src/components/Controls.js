import React, { useEffect } from 'react';
import axios from 'axios';
import '../styles/Controls.css';

const Controls = ({ onMove }) => {
    const move = async (direction) => {
        try {
            const response = await axios.post('http://localhost:8080/api/move', null, {
                params: {
                    direction: direction
                }
            });
            if (response.data) {
                onMove(response.data); // Pass the updated state to the parent component
            } else {
                alert("Oops! Island ahead! Change your way.");
            }
        } catch (error) {
            console.error("Error moving:", error);
        }
    };

    const handleKeyPress = (event) => {
        switch (event.key) {
            case 'ArrowUp':
                move('up');
                break;
            case 'ArrowDown':
                move('down');
                break;
            case 'ArrowLeft':
                move('left');
                break;
            case 'ArrowRight':
                move('right');
                break;
            default:
                break;
        }
    };

    useEffect(() => {
        window.addEventListener('keydown', handleKeyPress);
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, []);

    return (
        <div className="controls">
            <button className="left-button" onClick={() => move('left')}>Left</button>
            <button className="right-button" onClick={() => move('right')}>Right</button>
            <button className="up-button" onClick={() => move('up')}>Up</button>
            <button className="down-button" onClick={() => move('down')}>Down</button>
        </div>
    );
    
};

export default Controls;