This is a Alpha 3 of a major update to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

Versions 1.20.2, 1.20.3 and 1.20.4 are no longer supported.

## New features in Alpha 3
- An error in the Air Data Computer or Pitch Computer will now automatically disable protections and produce an alert
- **Added autothrust**. Currently, the only available mode is Selected Speed
- **Added autopilot**. Currently, available modes are Selected Pitch and Selected Heading

## Changes in Alpha 3
- Computers are now automatically reset and disabled when faulted
- Alerts are now sorted better
- Pitch is no longer limited by the Stall Computer when it is disabled

## Fixed issues
- The HUD no longer draws itself over the hotbar
- Pitch Computer no longer faults when falling at a high speed below the void
- More reliable alert sound playback
- Fixed an issue where pitch movement was very aggressive if the target pitch was lower than current pitch
