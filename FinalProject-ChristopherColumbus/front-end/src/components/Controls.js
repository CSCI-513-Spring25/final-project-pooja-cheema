import React, { useEffect } from 'react';
import IconManual from './IconManual';
import axios from 'axios';
import '../styles/Controls.css';

/**
 * Controls component handles CC movement, supports button clicks and key press for navigation
 */
const Controls = ({ onMove, showTempMessage, modalVisible, currentStrategy, setCurrentStrategy }) => {

    /**
     * Sends move command to backend server.
     * Disables movement when modal is active.
     * Displays toast if next cell if island
     */
    const move = async (direction) => {

        if (modalVisible) return; // Prevent movement if modal is visible

        try {
            const response = await axios.post('http://localhost:8080/api/move', null, {
                params: {
                    direction: direction // Pass direction as query param
                }
            });
            if (response.data) {
                onMove(response.data); // If response contains new gamestate, pass updated state via callback
            }
            else {

                // Show temporary toast message is movement blocked due to island
                if (showTempMessage) {
                    showTempMessage("Oops! Island! Change your way.");
                    setTimeout(() => showTempMessage(''), 1500); // Message disappears after 1.5 seconds
                }
            }
        } catch (error) {
            console.error("Error moving:", error);
        }
    };

    /**
     * Handles arrow key inputs for movement on grid
     */
    const handleKeyPress = (event) => {

        if (modalVisible) return; // Prevent movement if modal is visible

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

    /**
     * Registers and cleans up keyboard event listener.
     * Only active when modal is not visible
     */
    useEffect(() => {
        window.addEventListener('keydown', handleKeyPress);
        return () => { // Clean up on unmount
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, [modalVisible]);


    /**
     * Handle Reset button functionality
     */
    const handleReset = async () => {
        try {
            const resetResponse = await axios.post('http://localhost:8080/api/reset');
            if (resetResponse.status === 200 && onMove) {
                // Fetch updated game state and current strategy together
                const [stateRes, strategyRes] = await Promise.all([
                    axios.get('http://localhost:8080/api/state'),
                    axios.get('http://localhost:8080/api/currentStrategy')
                ]);
    
                onMove(stateRes.data); // Update board state
                setCurrentStrategy(strategyRes.data.strategy); // Update label in UI
            }
        } catch (error) {
            console.error("Error resetting game:", error);
        }
    }; 

    return (
        <div className="controls">
            <button className="left-button" onClick={() => move('left')}>Left</button>
            <button className="right-button" onClick={() => move('right')}>Right</button>
            <button className="up-button" onClick={() => move('up')}>Up</button>
            <button className="down-button" onClick={() => move('down')}>Down</button>
            <button className="reset-button" onClick={handleReset}>Reset Game</button>
            <IconManual strategy={currentStrategy} />
            
        </div>
    );

};

export default Controls;