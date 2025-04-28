import React from 'react';

/**
 * Notification component , displays simple notification message
 */
const Notification = ({ message }) => {
    return (
        // Render notification message in div with a CSS class for styling
        <div className="notification">
            {message}
        </div>
    );
};

export default Notification;