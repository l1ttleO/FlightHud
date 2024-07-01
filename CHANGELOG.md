This is a Alpha 2 of a minor release to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## Additions

* Added a time-to-waypoint approximation to the Status Display

## Changes

* Removed variable thrust response
    * Reverse activation is now instant
    * Manual thrust changes will result in an immediate response
    * Changes by Autothrust will be interpolated similar to pitch, yaw and roll
    * The "Show Engine Power" setting was replaced with "Show Thrust setting"
    * The thrust setting will no longer appear on the Flight Mode Display
* Added a new condition to activating the GO AROUND phase
    * Now, the player must be no farther than 100 blocks near the destination
    * If the player is farther than 100 blocks and the GO AROUND phase is active, the APPROACH phase will activate
* Reduced the landing thrust to 15% REV, down from 20% REV
* The autothrust will no longer command TOGA thrust on takeoff or go around

## Fixes

* Fixed an issue that would cause approach thrust to be applied during landing
* Fixed an issue that would cause highlighting of empty text on the Flight Mode Display
* Fixed an issue that allowed thrust setting to be changed when automations are paused
