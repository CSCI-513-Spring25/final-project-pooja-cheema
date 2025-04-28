import React from 'react';
import '../styles/IconManual.css';

const IconManual = () => {
    return (
        <div className="icon-manual">
            <h2>Icon Manual</h2>
            <ul>
                <li><img src="/images/CCship.png" alt="CC Ship" /> CC Ship</li>
                <li><img src="/images/pirateShip.png" alt="Slow Pirate" /> Slow Pirate</li>
                <li><img src="/images/pirateship2.png" alt="Fast Pirate" /> Fast Pirate</li>
                <li><img src="/images/fast_pirate.png" alt="Patrol Ship" /> Patrol Ship</li>
                <li><img src="/images/island.jpg" alt="Island" /> Island</li>
                <li><img src="/images/treasure1.png" alt="Treasure" /> Treasure</li>
                <li><img src="/images/monster.png" alt="Monster" /> Monster</li>
            </ul>
        </div>
    );
};

export default IconManual;