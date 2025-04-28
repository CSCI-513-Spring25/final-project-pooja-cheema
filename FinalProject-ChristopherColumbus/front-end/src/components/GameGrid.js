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
            setCcPosition(Array.isArray(gameState.ccPosition) ? gameState.ccPosition : [0, 0]);
            setTreasurePosition(Array.isArray(gameState.treasurePosition) ? gameState.treasurePosition : [9, 9]);
            setPirates(Array.isArray(gameState.pirates) ? gameState.pirates.map(p => ({ position: p.position, type: p.type })) : []);
            setMonsters(Array.isArray(gameState.seaMonsters) ? gameState.seaMonsters.map(m => m.position) : []);
            setIslands(Array.isArray(gameState.islands) ? gameState.islands : []);
        }
    }, [gameState]);

    const renderCell = (rowIndex, cellIndex) => {
        if (ccPosition[0] === rowIndex && ccPosition[1] === cellIndex) {
            return <img src="/images/CCship.png" alt="CC" className='cc' />;
        }
        if (treasurePosition[0] === rowIndex && treasurePosition[1] === cellIndex) {
            return <img src="/images/treasure1.png" alt="Treasure" className='treasure' />;
        }
        const pirate = pirates.find(p => p.position[0] === rowIndex && p.position[1] === cellIndex);
        if (pirate) {
            let pirateImg = '/images/pirateShip.png'; // default
            // if (pirate.type === 'slow') pirateImg = '/images/pirateShip.png';
            if (pirate.type === 'fast') pirateImg = '/images/pirateship2.png';
            else if (pirate.type === 'patrol') pirateImg = '/images/fast_pirate.png';

            return <img src={pirateImg} alt={pirate.type + " Pirate"} className='pirate-ship' />;

        }
        if (monsters.some(m => m[0] === rowIndex && m[1] === cellIndex)) {
            return <img src="/images/monster.png" alt="Monster" className='monster' />;
        }
        if (islands.some(i => i[0] === rowIndex && i[1] === cellIndex)) {
            return <img src="/images/island.jpg" alt="Island" className='island' />;
        }
        return null;
    };

    return (
        <div className="grid-container">
            <div className="grid">
                {Array.from({ length: 10 }).map((_, rowIndex) => (
                    <div key={rowIndex} className="row">
                        {Array.from({ length: 10 }).map((_, cellIndex) => {
                            const absRow = viewPort.y + rowIndex;
                            const absCol = viewPort.x + cellIndex;
                            const isIsland = islands.some(i => i[0] === absRow && i[1] === absCol);

                            return (
                                <div
                                    key={cellIndex}
                                    className={`cell${isIsland ? ' island' : ''}`}
                                >
                                    {renderCell(absRow, absCol)}
                                </div>
                            );
                        })}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default GameGrid;