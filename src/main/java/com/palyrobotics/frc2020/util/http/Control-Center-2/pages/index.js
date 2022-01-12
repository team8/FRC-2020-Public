import NavBar from "../components/navbar"
import Typing from "react-typing-animation"
import Link from 'next/link'

export default function Home() {
  return (
    <div class = "bg-gray-900 w-screen h-screen font-mono text-white">
      <NavBar/>
      <Typing>
        <p class="text-center text-4xl p-3">Control Center</p>
        <Typing.Delay ms={1000} />
        <Typing.Backspace count={20} />
        <p class="text-center text-4xl">Please Select:</p>
        <p class="text-center text-xl underline text-blue-400">
          <a href="/logtab">LogTab</a>
          <span class="no-underline">|</span>
          <a href="/chart">Chart</a>
          <span class="no-underline">|</span>
          <a href="/config">Config</a>
        </p>
      </Typing>
    </div>
  )
}
