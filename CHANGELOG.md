This is a Alpha 1 of a minor release to FlightAssistant. Please note that there may be critical issues and features may
not work as intended. Please use [Discord](https://discord.gg/5kcBCvnbTp)
or [GitHub](https://github.com/Octol1ttle/FlightAssistant) to discuss this alpha or report any bugs.

## Additions

### Thrust Management

This update includes a new thrust management system which tries to simulate how thrust is handled on real aircraft. You
can control your virtual thrust levers by using your keyboard arrows (check keybinds settings)

#### External Thrust Support

This update includes a thrust management system, which is capable of integrating with mods that provide external thrust,
like Do a Barrel Roll (thrusting must be enabled in DaBR settings),
however they are not a requirement.

### Autopilot v4

The autopilot has been completely rewritten... for the fourth time!

#### Automatic Roll Control ft. Do a Barrel Roll

Autopilot v4 is capable of maintaining level roll when using DaBR, meaning it is now capable of maintaining its course
without player intervention

#### Improved Autothrust

Autopilot v4 features an improved autothrust system that makes use of the precise control offered by the new Thrust
Management.

#### Smoother vertical guidance

Do I need to explain much?

### Configurable Elytra health display units

You can now configure how Elytra health is displayed in the config screen. In addition to the percentage, you can switch
to using raw durability units instead.

## Changes

* The Elytra durability will now also display while holding, but not wearing an Elytra in either hands. In case the
  player is both holding and wearing an Elytra, the shown health will be for the worn Elytra.
* If the Elytra is unbreakable, its health display will be replaced with "INF"
* Flight directors will now be hidden at extreme bank angles as they become inaccurate with roll
* The flight path vector icon will now be hidden at extreme bank angles as it becomes inaccurate with roll
* Warnings and protections will now be suppressed if the player is invulnerable
* Elytra will no longer open automatically when the player is in Creative Mode
* The "Not flying (has elytra)" will no longer activate when the player is using Creative flight

## Fixes

* Fixed an issue that would cause the Ground Proximity Warning System to block manual pitch UP inputs when the player is
  stalling, making manual recovery impossible
* Fixed an issue that allowed faulted computers to issue pitch and yaw inputs
