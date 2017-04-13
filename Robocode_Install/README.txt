First install Naval Robocode by running the robocode-0.9.5-setup.jar(MUST BE INSTALLED IN THE DEFAULT FOLDER, WHICH IS home/naval-robocode on Linux or C:\Users\<USER> on Windows).
You can then proceed to run the start.bat (on Windows) or the start.sh (on Linux) to launch the server. Once the server is running you can open your browser and navigate to the page.
The page will either be on "localhost:8081" or the corresponding IP address of the machine on which the server runs( "192.168.1.1:8081" for example).
You can then start to build a ship in blockly, if you are building the ship on the same machine as the server runs on, you have to publish the ship locally.
If the server runs on another machine and you connected to Blockly with the IP of that machine, you have to publish the ship remotely. 
Publishing locally or remotely is done by selecting "Local" or "Remote" in the combo box next to the Publish button.
The server will then proceed to compile and import the generated ship to the right directory, preparing it to be used in the Battle Arena of Naval Robocode.
The server will also save the ship's block configuration as a XML file in the file-uploads directory, in the server's directory. 
That enables the ship to be loaded afterwards from another pc, or the same one, it doesn't really matter. 
If the ship is saved on the server's machine (which isn't the one you're building your ship on) you have to set the combo-box next to the drop-down with ships to "Remote".
If the ship is saved on the server's machine (which is the one you're building your ship on) you have to set the combo-box next to the drop-down with ships to "Local". 
You can then proceed to click "Load Ship" to load the blocks into the workspace.

So, in short:
1.	Install robocode-0.9.5-setup.jar(in default folder)
2. 	Make sure JDK is configured correctly, so you are able to use the javac command
3.	Run start.sh or start.bat(.bat for Windows, .sh for Linux)
4.	Connect to localhost:8081 or corresponding <IP>:8081
5.	Publish locally if server is running on the same machine you're building your ship on
6.	Publish remotely if server is running on another machine.
7.	Use the ship in Naval Robocode.
