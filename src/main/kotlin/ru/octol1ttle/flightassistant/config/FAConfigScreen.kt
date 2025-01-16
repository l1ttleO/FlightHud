package ru.octol1ttle.flightassistant.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.dsl.*
import java.awt.Color
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.SafetyOptions

object FAConfigScreen {
    fun generate(parent: Screen): Screen {
        return YetAnotherConfigLib(FlightAssistant.MOD_ID) {
            val global: ConfigCategory by registerGlobalOptions(
                Text.translatable("config.flightassistant.categories.global"),
                FAConfig.global,
                GlobalOptions()
            )

            with(FAConfig.displaysStorage) {
                val notFlyingNoElytra: ConfigCategory by registerDisplayOptions(
                    Text.translatable("config.flightassistant.categories.no_elytra"),
                    notFlyingNoElytra,
                    DisplayOptions().setDisabled()
                )
                val notFlyingHasElytra: ConfigCategory by registerDisplayOptions(
                    Text.translatable("config.flightassistant.categories.has_elytra"),
                    notFlyingHasElytra,
                    DisplayOptions().setMinimal()
                )
                val flying: ConfigCategory by registerDisplayOptions(
                    Text.translatable("config.flightassistant.categories.flying"),
                    flying,
                    DisplayOptions()
                )
            }

            val safety: ConfigCategory by registerSafetyOptions(
                Text.translatable("config.flightassistant.categories.safety"),
                FAConfig.safety,
                SafetyOptions()
            )

            save { FAConfig.save() }
        }.generateScreen(parent)
    }

    private fun RootDsl.registerGlobalOptions(title: Text, current: GlobalOptions, defaults: GlobalOptions) =
        categories.registering {
            name(title)

            rootOptions.register<Boolean>("mod_enabled") {
                setGlobalName()
                binding(current::modEnabled, defaults.modEnabled)
                controller(tickBox())
            }

            rootOptions.register<Boolean>("hud_enabled") {
                setGlobalName()
                binding(current::hudEnabled, defaults.hudEnabled)
                controller(tickBox())
            }

            rootOptions.register<Boolean>("safety_enabled") {
                setGlobalName()
                binding(current::safetyEnabled, defaults.safetyEnabled)
                controller(tickBox())
            }

            rootOptions.register<Boolean>("automations_allowed_in_overlays") {
                setGlobalName()
                binding(current::automationsAllowedInOverlays, defaults.automationsAllowedInOverlays)
                controller(tickBox())
            }
        }

    private fun RootDsl.registerDisplayOptions(
        title: Text,
        current: DisplayOptions,
        defaults: DisplayOptions
    ): RegisterableActionDelegateProvider<CategoryDsl, ConfigCategory> {
        return categories.registering {
            name(title)

            val percentageFormatter: (Float) -> Text = { value: Float -> Text.of("${(value * 100).toInt()}%") }
            val degreeFormatter: (Int) -> Text = { value: Int -> Text.of("$value°") }

            rootOptions.registerLabel("frame", Text.translatable("config.flightassistant.options.display.frame"))
            rootOptions.register<Float>("frame.width") {
                setDisplayName()
                binding(current::frameWidth, defaults.frameWidth)
                controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
            }
            rootOptions.register<Float>("frame.height") {
                setDisplayName()
                binding(current::frameHeight, defaults.frameHeight)
                controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
            }

            rootOptions.registerLabel("colors", Text.translatable("config.flightassistant.options.display.colors"))
            rootOptions.register<Color>("colors.primary") {
                setDisplayName()
                binding(current::primaryColor, defaults.primaryColor)
                controller(colorPicker())
            }
            rootOptions.register<Color>("colors.advisory") {
                setDisplayName()
                binding(current::advisoryColor, defaults.advisoryColor)
                controller(colorPicker())
            }
            rootOptions.register<Color>("colors.caution") {
                setDisplayName()
                binding(current::cautionColor, defaults.cautionColor)
                controller(colorPicker())
            }
            rootOptions.register<Color>("colors.warning") {
                setDisplayName()
                binding(current::warningColor, defaults.warningColor)
                controller(colorPicker())
            }

            rootOptions.registerLabel("attitude", Text.translatable("config.flightassistant.options.display.attitude"))
            rootOptions.register<DisplayOptions.AttitudeDisplayMode>("attitude.show") {
                setDisplayName()
                binding(current::showAttitude, defaults.showAttitude)
                controller(enumSwitch(DisplayOptions.AttitudeDisplayMode::class.java))
            }
            rootOptions.register<Int>("attitude.degree_step") {
                setDisplayName()
                binding(current::attitudeDegreeStep, defaults.attitudeDegreeStep)
                controller(slider(5..45, 5, degreeFormatter))
            }
            rootOptions.register<Boolean>("attitude.horizon_outside_frame") {
                setDisplayName()
                binding(current::drawHorizonOutsideFrame, defaults.drawHorizonOutsideFrame)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("attitude.pitch_outside_frame") {
                setDisplayName()
                binding(current::drawPitchOutsideFrame, defaults.drawPitchOutsideFrame)
                controller(tickBox())
            }

            rootOptions.registerLabel("heading", Text.translatable("config.flightassistant.options.display.heading"))
            rootOptions.register<Boolean>("heading.show_reading") {
                setDisplayName()
                binding(current::showHeadingReading, defaults.showHeadingReading)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("heading.show_scale") {
                setDisplayName()
                binding(current::showHeadingScale, defaults.showHeadingScale)
                controller(tickBox())
            }
            rootOptions.register<Int>("heading.scale_step") {
                setDisplayName()
                binding(current::headingDegreeStep, defaults.headingDegreeStep)
                controller(slider(5..90, 5, degreeFormatter))
            }

            rootOptions.registerLabel("speed", Text.translatable("config.flightassistant.options.display.speed"))
            rootOptions.register<Boolean>("speed.show_reading") {
                setDisplayName()
                binding(current::showSpeedReading, defaults.showSpeedReading)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("speed.show_scale") {
                setDisplayName()
                binding(current::showSpeedScale, defaults.showSpeedScale)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("speed.show_ground") {
                setDisplayName()
                binding(current::showGroundSpeed, defaults.showGroundSpeed)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("speed.show_vertical") {
                setDisplayName()
                binding(current::showVerticalSpeed, defaults.showVerticalSpeed)
                controller(tickBox())
            }

            rootOptions.registerLabel("altitude", Text.translatable("config.flightassistant.options.display.altitude"))
            rootOptions.register<Boolean>("altitude.show_reading") {
                setDisplayName()
                binding(current::showAltitudeReading, defaults.showAltitudeReading)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("altitude.show_scale") {
                setDisplayName()
                binding(current::showAltitudeScale, defaults.showAltitudeScale)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "flight_path_vector",
                Text.translatable("config.flightassistant.options.display.flight_path_vector")
            )
            rootOptions.register<Boolean>("flight_path_vector.show") {
                setDisplayName()
                binding(current::showFlightPathVector, defaults.showFlightPathVector)
                controller(tickBox())
            }
            rootOptions.register<Float>("flight_path_vector.size") {
                setDisplayName()
                binding(current::flightPathVectorSize, defaults.flightPathVectorSize)
                controller(slider(0.5f..2.0f, 0.05f, percentageFormatter))
            }

            rootOptions.registerLabel(
                "elytra_durability",
                Text.translatable("config.flightassistant.options.display.elytra_durability")
            )
            rootOptions.register<Boolean>("elytra_durability.show") {
                setDisplayName()
                binding(current::showElytraDurability, defaults.showElytraDurability)
                controller(tickBox())
            }
            rootOptions.register<DisplayOptions.DurabilityUnits>("elytra_durability.units") {
                setDisplayName()
                binding(current::elytraDurabilityUnits, defaults.elytraDurabilityUnits)
                controller(enumSwitch(DisplayOptions.DurabilityUnits::class.java))
            }

            rootOptions.registerLabel("misc", Text.translatable("config.flightassistant.options.display.misc"))
            rootOptions.register<Boolean>("misc.coordinates") {
                setDisplayName()
                binding(current::showCoordinates, defaults.showCoordinates)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("misc.alerts") {
                setDisplayName()
                binding(current::showAlerts, defaults.showAlerts)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("misc.automation_modes") {
                setDisplayName()
                binding(current::showAutomationModes, defaults.showAutomationModes)
                controller(tickBox())
            }
        }
    }

    private fun RootDsl.registerSafetyOptions(
        title: Text,
        current: SafetyOptions,
        defaults: SafetyOptions
    ): RegisterableActionDelegateProvider<CategoryDsl, ConfigCategory> {
        return categories.registering {
            name(title)

            val percentageFormatter: (Float) -> Text = { value: Float -> Text.of("${(value * 100).toInt()}%") }

            rootOptions.register<Float>("alert_volume") {
                setSafetyName()
                binding(current::alertVolume, defaults.alertVolume)
                controller(slider(0.0f..1.0f, 0.01f, percentageFormatter))
            }

            rootOptions.register<Boolean>("consider_invulnerability") {
                setSafetyName()
                binding(current::considerInvulnerability, defaults.considerInvulnerability)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "elytra",
                Text.translatable("config.flightassistant.options.safety.elytra")
            )
            rootOptions.register<SafetyOptions.AlertMode>("elytra.durability_alert_mode") {
                setSafetyName()
                binding(current::elytraDurabilityAlertMode, defaults.elytraDurabilityAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register<Boolean>("elytra.auto_open") {
                setSafetyName()
                binding(current::elytraAutoOpen, defaults.elytraAutoOpen)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("elytra.close_underwater") {
                setSafetyName()
                binding(current::elytraCloseUnderwater, defaults.elytraCloseUnderwater)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "stall",
                Text.translatable("config.flightassistant.options.safety.stall")
            )
            rootOptions.register<SafetyOptions.AlertMode>("stall.alert_mode") {
                setSafetyName()
                binding(current::stallAlertMode, defaults.stallAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register<Boolean>("stall.limit_pitch") {
                setSafetyName()
                binding(current::stallLimitPitch, defaults.stallLimitPitch)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("stall.auto_thrust") {
                setSafetyName()
                binding(current::stallAutoThrust, defaults.stallAutoThrust)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "void",
                Text.translatable("config.flightassistant.options.safety.void")
            )
            rootOptions.register<SafetyOptions.AlertMode>("void.alert_mode") {
                setSafetyName()
                binding(current::voidAlertMode, defaults.voidAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register<Boolean>("void.limit_pitch") {
                setSafetyName()
                binding(current::voidLimitPitch, defaults.voidLimitPitch)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("void.auto_thrust") {
                setSafetyName()
                binding(current::voidAutoThrust, defaults.voidAutoThrust)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("void.auto_pitch") {
                setSafetyName()
                binding(current::voidAutoPitch, defaults.voidAutoPitch)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "gpws",
                Text.translatable("config.flightassistant.options.safety.gpws")
            )
            rootOptions.register<SafetyOptions.AlertMode>("gpws.sink_rate.alert_mode") {
                setSafetyName()
                binding(current::sinkRateAlertMode, defaults.sinkRateAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register<Boolean>("gpws.sink_rate.limit_pitch") {
                setSafetyName()
                binding(current::sinkRateLimitPitch, defaults.sinkRateLimitPitch)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("gpws.sink_rate.auto_pitch") {
                setSafetyName()
                binding(current::sinkRateAutoPitch, defaults.sinkRateAutoPitch)
                controller(tickBox())
            }
            rootOptions.register<SafetyOptions.AlertMode>("gpws.obstacle.alert_mode") {
                setSafetyName()
                binding(current::obstacleAlertMode, defaults.obstacleAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register<Boolean>("gpws.obstacle.limit_pitch") {
                setSafetyName()
                binding(current::obstacleLimitPitch, defaults.obstacleLimitPitch)
                controller(tickBox())
            }
            rootOptions.register<Boolean>("gpws.obstacle.auto_pitch") {
                setSafetyName()
                binding(current::obstacleAutoPitch, defaults.obstacleAutoPitch)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "firework",
                Text.translatable("config.flightassistant.options.safety.firework")
            )
            rootOptions.register<Boolean>("firework.lock_explosive") {
                setSafetyName()
                binding(current::fireworkLockExplosive, defaults.fireworkLockExplosive)
                controller(tickBox())
            }
        }
    }

    private fun OptionDsl<*>.setGlobalName() {
        name(Text.translatable("config.flightassistant.options.global.${this.optionId}"))
    }

    private fun OptionDsl<*>.setDisplayName() {
        name(Text.translatable("config.flightassistant.options.display.${this.optionId}"))
    }

    private fun OptionDsl<*>.setSafetyName() {
        name(Text.translatable("config.flightassistant.options.safety.${this.optionId}"))
    }
}
