import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegistrationPage from './pages/RegistrationPage'
import GameListPage from './pages/GameListPage'
import LeaderboardPage from './pages/LeaderboardPage'
import RankingsPage from './pages/RankingsPage'
import UnitStatsPage from './pages/UnitStatsPage'
import SettingsPage from './pages/SettingsPage'
import GamePage from './pages/GamePage'
import AppShell from './components/AppShell'
import { GameProvider } from './context/GameContext'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegistrationPage />} />
        <Route element={<AppShell />}>
          <Route path="/games" element={<GameListPage />} />
          <Route path="/leaderboard" element={<LeaderboardPage />} />
          <Route path="/rankings" element={<RankingsPage />} />
          <Route path="/stats/:gameId" element={<UnitStatsPage />} />
          <Route path="/stats" element={<UnitStatsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
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
