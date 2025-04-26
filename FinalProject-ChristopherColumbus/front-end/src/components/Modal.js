import React, { useEffect } from 'react';
import '../styles/Modal.css';

const Modal = ({ show, message, onClose }) => {
    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            onClose();
        }
    };

    useEffect(() => {
        if (show) {
            window.addEventListener('keydown', handleKeyPress);
        }
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, [show]);

    if (!show) {
        return null;
    }

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