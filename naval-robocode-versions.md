## Naval Robocode Version 0.9.5 (2-Jan-2017)

### Changes
* Rebased Naval Robocode on Robocode 1.9.2.6 Alpha
* Ships can launch missiles
    * Missiles have a blast radius and are detectable by radar
* Added some new ships
* Updated ship template
* Added a Projectile and ProjectilePeer from which bullet and missile classes inherit to reduce duplicate code.



## Naval Robocode Version 0.9.2 (02-Mar-2015)

### Changes
* Merged all changes with Naval Robocode with Robocode 1.9.2.4 (newest version).
* Flemming clean-up and Naval Robocode specification
* Now able to use naval-robocode.bat to start Naval Robocode
* Fixed a few UnitTests that fell short due to a few missing classes
* The in-game editor can now create a sample ship for you.

### Bug fixes
* Mines now have a blast Radius. (See NavalRules)
	* Angles have been better documented
	* Custom run config robocode.NavalRobocode

## Naval Robocode Version 0.9.1 (27-Jan-2015)

### First release
* Ships instead of tanks
* Water background
* Still uses robocode.Robocode to launch
* Using the eclipse editor would be for the best
* Ships can place mines
* The version used within the company