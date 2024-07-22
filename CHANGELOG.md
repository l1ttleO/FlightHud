This is a bugfix update release to FlightAssistant. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this release or report any bugs.

## Changes

* Reduced the rate of firework activation when CLB thrust is active
* Increased the rate of firework activation when APPR thrust is active

## Fixes

* Fixed an issue that caused autopilot to be unstable when fireworks were the active thrust source
* Fixed an issue that caused more firework activations than needed when using selected altitude
* Fixed an issue that prevented the thrust setting from appearing on the Status Display if fireworks were the active
  thrust source
* Fixed an issue that allowed players to use reverse thrust with sources that do not support them (e.g. fireworks)
* Fixed an issue that caused the stall prevention to activate too late, causing STALL warnings and recovery automations
  to trigger
* Reduced the effect of an issue that causes the artificial horizon to become inaccurate with high angles
