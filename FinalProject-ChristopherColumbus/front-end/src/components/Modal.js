import React, { useEffect } from 'react';
import '../styles/Modal.css';

/**
 * This component renders a modal dialog when 'show' is true displaying a messasge and OK button
 * Close modal when clicked on OK button, or enter key is pressed
 */
const Modal = ({ show, message, onClose }) => {

    // Handling pressing enter key to close modal
    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            onClose();
        }
    };

    // Handle adding/removing keydown listener only when modal is shown
    useEffect(() => {
        if (show) {
            window.addEventListener('keydown', handleKeyPress);
        }
        // Clean up - Remove listener when modal closes or unmount
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, [show]);

    // If show is false, render nothing
    if (!show) {
        return null;
    }

    // Modal UI
    return (
        <div className="modal-overlay">
            <div className="modal">
                <div className="modal-content">
                    <p>{message}</p>
                    <button onClick={onClose}>Ok</button>
                </div>
            </div>
        </div>
    );
};

export default Modal;