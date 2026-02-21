import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import GameListPage from './pages/GameListPage'
import GamePage from './pages/GamePage'
import AppShell from './components/AppShell'
import { GameProvider } from './context/GameContext'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<AppShell />}>
          <Route path="/games" element={<GameListPage />} />
          <Route
            path="/game/:gameId"
            element={
              <GameProvider>
                <GamePage />
              </GameProvider>
            }
          />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
