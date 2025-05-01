import React, { useState, useEffect } from 'react';
import GameGrid from './components/GameGrid';
import Controls from './components/Controls';
import WinCelebration from './components/WinCelebration';
import WelcomePage from './components/WelcomePage';
import Modal from './components/Modal';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './styles/styles.css';

// Main app component for game
const App = () => {
    const [notification, setNotification] = useState(''); // Message shown in modal dialogs
    const [gameState, setGameState] = useState(null); // Holds latest state from backend API
    const [gameStarted, setGameStarted] = useState(false); // Tracks if game is in progress
    const [modalVisible, setModalVisible] = useState(false); // Controls modal dialog visibility
    const [collisionType, setCollisionType] = useState(''); // Stores collision happens with whom (pirate, monster)
    const navigate = useNavigate();
    const [showBalloonShower, setShowBalloonShower] = useState(false); // Show balloons on win
    const [tempMessage, setTempMessage] = useState(''); // Temporary toast message


    // Poll game state from back end every second when game is running
    useEffect(() => {
        if (gameStarted) {
            const intervalId = setInterval(() => {
                axios.get('http://localhost:8080/api/state')
                    .then(response => setGameState(response.data))
                    .catch(error => console.error("Polling error:", error));
            }, 1000); // Poll every second

            // Stop polling when game stops
            return () => clearInterval(intervalId);
        }
    }, [gameStarted]);


    // Pause/resume monster movement on server, depending on modal (visible / not visible)
    useEffect(() => {

        // Only pause/resume if game has started
        if (!gameStarted) return;

        if (modalVisible) {
            axios.post('http://localhost:8080/api/pause');
        }
        else {
            axios.post('http://localhost:8080/api/resume');
        }
    }, [modalVisible, gameStarted]);


    // Detect collision sent by backend (even if not from movement)
    useEffect(() => {
        if (gameState && gameState.collision && !modalVisible) {
            setCollisionType(gameState.collision);

            switch (gameState.collision) {
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
                    setTempMessage("Island ahead! Change your way.");
                    setTimeout(() => setTempMessage(''), 1500);
                    return; // Don't show modal
                default:
                    setNotification("");
            }

            setModalVisible(true);


            // Clear collision from backend
            axios.post('http://localhost:8080/api/clear-collision');


        }
    }, [gameState, modalVisible]);


    // Handle result of player movement (called by controls component)
    const handleMove = (updatedState) => {
        console.log('Updated state:', updatedState);

        // Check if ccPosition matches any pirate position (for collision)
        const ccPosition = updatedState.ccPosition;
        const pirates = updatedState.pirates;
        let collisionDetected = false;

        for (let pirate of pirates) {
            if (ccPosition[0] === pirate.position[0] && ccPosition[1] === pirate.position[1]) {
                collisionDetected = true;
                break;
            }
        }

        // If collision happens, show hijack notification
        if (collisionDetected) {
            setCollisionType("pirate");
            setNotification("Hijacked! Your voyage ends in pirate chains.. Begin again!");
            setModalVisible(true);
        }

        // Handle collision responses sent by back end (treasure, monster, island)
        else if (updatedState.collision) {
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
                    if (setTempMessage) { // Show a short toast message for island collision, not a modal
                        setTempMessage("Island ahead! Change your way.");
                        setTimeout(() => setTempMessage(''), 1500);
                    }
                    return; // Do not open modal for island collision
                default:
                    setNotification("");
            }
            setModalVisible(true);
        }

        setGameState(updatedState); // Update UI with new game state
    };



    // Start or restart the game, and reset state
    const handleStart = () => {
        setGameStarted(true);
        setGameState(null);
        axios.post('http://localhost:8080/api/start');
    };

    // Handle closing the modal - reset game position based on collision type
    const closeModal = () => {
        setModalVisible(false);
        if (collisionType === "pirate") {
            setGameStarted(false); // End game on pirate collision
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

    // Go back button handler - goes to welcome page
    const handleGoBack = () => {
        setGameStarted(false);
    };

    // Render game UI
    return (
        <div className="App">

            {tempMessage && (
                <div className="toast-message">
                    <img src="/images/island.jpg" alt="Island" style={{ width: 40, height: 40, marginRight: 4 }} />
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