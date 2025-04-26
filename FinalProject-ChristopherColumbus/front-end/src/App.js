import React, { useState, useEffect } from 'react';
import GameGrid from './components/GameGrid';
import Controls from './components/Controls';
import WelcomePage from './components/WelcomePage';
import Modal from './components/Modal';
import axios from 'axios';
import './styles/styles.css';

const App = () => {
    const [notification, setNotification] = useState('');
    const [gameState, setGameState] = useState(null);
    const [gameStarted, setGameStarted] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [collisionType, setCollisionType] = useState('');

    useEffect(() => {
        if (gameStarted) {
            const fetchInitialState = async () => {
                try {
                    const response = await axios.get('http://localhost:8080/api/state');
                    setGameState(response.data);
                } catch (error) {
                    console.error("Error fetching initial state:", error);
                }
            };
            fetchInitialState();
        }
    }, [gameStarted]);

    const handleMove = (updatedState) => {
        console.log('Updated state:', updatedState);
    
        // Check if ccPosition matches any pirate position
        const ccPosition = updatedState.ccPosition;
        const pirates = updatedState.pirates;
        let collisionDetected = false;
    
        for (let pirate of pirates) {
            if (ccPosition[0] === pirate.position[0] && ccPosition[1] === pirate.position[1]) {
                collisionDetected = true;
                break;
            }
        }
    
        if (collisionDetected) {
            setCollisionType("pirate");
            setNotification("Hijacked! Your voyage ends in pirate chains.. Begin again!");
            setModalVisible(true);
        } else if (updatedState.collision) {
            setCollisionType(updatedState.collision);
            switch (updatedState.collision) {
                case "treasure":
                    setNotification("Yay!! You have arrived.");
                    break;
                case "monster":
                    setNotification("Chomp! The sea monster got you. Restart your voyage.");
                    break;
                case "pirate":
                    setNotification("Hijacked! Your voyage ends in pirate chains.. Begin again!");
                    break;
                case "island":
                    setNotification("Oops! Island ahead! Change your way.");
                    break;
                default:
                    setNotification("");
            }
            setModalVisible(true);
        }
    
        setGameState(updatedState);
    };
    

    const handleStart = () => {
        setGameStarted(true);
        axios.post('http://localhost:8080/api/start');
    };

    const closeModal = () => {
        setModalVisible(false);
        if (collisionType === "pirate") {
            setGameStarted(false);
        } else if (collisionType === "monster") {
            setGameState(prevState => ({
                ...prevState,
                ccPosition: [0, 0]
            }));
        }
    };

    return (
        <div className="App">
            {!gameStarted ? (
                <WelcomePage onStart={handleStart} />
            ) : (
                <>
                    <Modal show={modalVisible} message={notification} onClose={closeModal} />
                    <div className="game-container">
                        <Controls onMove={handleMove} />
                        <GameGrid gameState={gameState} />
                    </div>
                </>
            )}
        </div>
    );
};

export default App;