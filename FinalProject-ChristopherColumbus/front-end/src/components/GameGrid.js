import React, { useEffect, useState } from 'react';
import '../styles/styles.css';

const GameGrid = ({ gameState }) => {
    const [ccPosition, setCcPosition] = useState([0, 0]);
    const [treasurePosition, setTreasurePosition] = useState([9, 9]);
    const [pirates, setPirates] = useState([]);
    const [monsters, setMonsters] = useState([]);
    const [islands, setIslands] = useState([]);
    const [viewPort, setViewPort] = useState({ x: 0, y: 0 });

    useEffect(() => {
        if (gameState) {
            setCcPosition(gameState.ccPosition);
            setTreasurePosition(gameState.treasurePosition);
            setPirates(gameState.pirates.map(p => ({ position: p.position, type: p.type })));
            setMonsters(gameState.seaMonsters.map(m => m.position));
            setIslands(gameState.islands);
        }
    }, [gameState]);

    const renderCell = (rowIndex, cellIndex) => {
        if (ccPosition[0] === rowIndex && ccPosition[1] === cellIndex) {
            return <img src="/images/CC.png" alt="CC" className='cc' />;
        }
        if (treasurePosition[0] === rowIndex && treasurePosition[1] === cellIndex) {
            return <img src="/images/treasure1.png" alt="Treasure" className='treasure' />;
        }
        const pirate = pirates.find(p => p.position[0] === rowIndex && p.position[1] === cellIndex);
        if (pirate) {
            return <img src={pirate.type === 'fast' ? '/images/fast_pirate.png' : '/images/pirateShip.png'} alt="Pirate" className='pirate-ship' />;
        }
        if (monsters.some(m => m[0] === rowIndex && m[1] === cellIndex)) {
            return <img src="/images/monster.png" alt="Monster" className='monster' />;
        }
        if (islands.some(i => i[0] === rowIndex && i[1] === cellIndex)) {
            return <img src="/images/island.png" alt="Island" className='island' />;
        }
        return null;
    };

    return (
        <div className="grid-container">
            <div className="grid">
                {Array.from({ length: 10 }).map((_, rowIndex) => (
                    <div key={rowIndex} className="row">
                        {Array.from({ length: 10 }).map((_, cellIndex) => (
                            <div key={cellIndex} className="cell">
                                {renderCell(viewPort.y + rowIndex, viewPort.x + cellIndex)}
                            </div>
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default GameGrid;