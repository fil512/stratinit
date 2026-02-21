import { useState } from 'react'
import SectorTab from './tabs/SectorTab'
import UnitsTab from './tabs/UnitsTab'
import CitiesTab from './tabs/CitiesTab'
import BattleLogTab from './tabs/BattleLogTab'
import PlayersTab from './tabs/PlayersTab'
import MessagesTab from './tabs/MessagesTab'
import NewsTab from './tabs/NewsTab'

const TABS = ['Sector', 'Units', 'Cities', 'Battle', 'Players', 'Mail', 'News'] as const
type TabName = typeof TABS[number]

export default function SidePanel() {
  const [activeTab, setActiveTab] = useState<TabName>('Sector')

  return (
    <div className="w-80 flex flex-col border-l border-gray-600 bg-gray-900 text-gray-100 text-sm">
      <div className="flex border-b border-gray-700">
        {TABS.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`flex-1 px-1 py-2 text-xs font-medium ${
              activeTab === tab
                ? 'bg-gray-800 text-white border-b-2 border-blue-400'
                : 'text-gray-400 hover:text-gray-200'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>
      <div className="flex-1 overflow-y-auto p-2">
        {activeTab === 'Sector' && <SectorTab />}
        {activeTab === 'Units' && <UnitsTab />}
        {activeTab === 'Cities' && <CitiesTab />}
        {activeTab === 'Battle' && <BattleLogTab />}
        {activeTab === 'Players' && <PlayersTab />}
        {activeTab === 'Mail' && <MessagesTab />}
        {activeTab === 'News' && <NewsTab />}
      </div>
    </div>
  )
}
