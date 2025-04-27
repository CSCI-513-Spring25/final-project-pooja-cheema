import React, { useState, useEffect } from 'react';
import GameGrid from './components/GameGrid';
import Controls from './components/Controls';
import WinCelebration from './components/WinCelebration';
import WelcomePage from './components/WelcomePage';
import Modal from './components/Modal';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './styles/styles.css';

const App = () => {
    const [notification, setNotification] = useState('');
    const [gameState, setGameState] = useState(null);
    const [gameStarted, setGameStarted] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [collisionType, setCollisionType] = useState('');
    const navigate = useNavigate();
    const [showBalloonShower, setShowBalloonShower] = useState(false);
    const [tempMessage, setTempMessage] = useState('');

    useEffect(() => {
        if (gameStarted) {
            const intervalId = setInterval(() => {
                axios.get('http://localhost:8080/api/state')
                    .then(response => setGameState(response.data))
                    .catch(error => console.error("Polling error:", error));
            }, 1000); // Poll every second

            return () => clearInterval(intervalId);
        }
    }, [gameStarted]);



    useEffect(() => {

        // Only pause/resume if game has started
        if (!gameStarted) return;

        if (modalVisible) {
            axios.post('http://localhost:8080/api/pause');
        } else {
            axios.post('http://localhost:8080/api/resume');
        }
    }, [modalVisible, gameStarted]);
    

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
                    setNotification("Yay!! Reached the treasure!");
                    setShowBalloonShower(true);
                    break;
                case "monster":
                    setNotification("Chomp! The sea monster got you. Restart from Cell 1.");
                    break;
                case "pirate":
                    setNotification("Hijacked! Your voyage ends in pirate chains.. Begin again!");
                    break;
                case "island":
                    // setNotification("Island ahead! Change your way.");
                    if (setTempMessage) {
                        setTempMessage("Island ahead! Change your way.");
                        setTimeout(() => setTempMessage(''), 1500);
                    }
                    return;
                // break;
                default:
                    setNotification("");
            }
            setModalVisible(true);
        }

        setGameState(updatedState);
    };


    const handleStart = () => {
        setGameStarted(true);
        setGameState(null);
        axios.post('http://localhost:8080/api/start');
    };

    const closeModal = () => {
        setModalVisible(false);
        if (collisionType === "pirate") {
            setGameStarted(false);
        }
        else if (collisionType === "monster") {
            setGameState(prevState => ({
                ...prevState,
                ccPosition: [0, 0]
            }));
        }
        else if (collisionType === "treasure") {
            setGameStarted(false);
            setShowBalloonShower(false);
        }
    };

    const handleGoBack = () => {
        setGameStarted(false);
    };

    return (
        <div className="App">

            {tempMessage && (
                <div className="toast-message">
                    {tempMessage}
                </div>
            )}

            {!gameStarted ? (
                <WelcomePage onStart={handleStart} />
            ) : (
                <>
                    <WinCelebration show={showBalloonShower} items={[
                        { className: 'balloon' },
                        { className: 'hurray-thread' }
                    ]} />

                    <Modal show={modalVisible} message={notification} onClose={closeModal} />
                    <div className="game-container">

                        <button className="go-back-button"
                            onClick={handleGoBack}>
                            ⬅ Go Back
                        </button>

                        <Controls onMove={handleMove} modalVisible={modalVisible} showTempMessage={setTempMessage} />

                        <GameGrid gameState={gameState} />
                    </div>
                </>
            )}
        </div>
    );
};

export default App;