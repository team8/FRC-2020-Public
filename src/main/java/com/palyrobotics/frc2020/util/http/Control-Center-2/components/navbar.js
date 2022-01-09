import React from 'react';

class NavBar extends React.Component {
  render() {
    return (
      <nav class="bg-gray-800">
          <div>
            <div class="relative flex items-center justify-between h-16">
              <div class="flex-1 flex items-center justify-center sm:items-stretch sm:justify-start">
                  <div class="flex-shrink-0 flex items-center">
                    <img class="h-10 w-auto lg:auto mx-auto rounded-l max-w-md flex-grow md:flex-1 w-full transition duration-500 ease-in-out transform hover:-translate-y-1 shadow-2xl" src="palyrobotics.png" alt="Icon"></img>
                    <a href="http://palyrobotics.com/" class="text-white p-1 font-bold"> Paly Robotics</a>
                  </div>
                <div class="hidden sm:block p-auto">
                  <div class="flex space-x-4">
                    <a href="/" class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Home</a>
    
                    <a href="/logtab" class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Logs</a>
    
                    <a href="/chart" class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Graph</a>
    
                    <a href="/config" class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Config</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
       </nav>
    )
  }
}
export default NavBar
