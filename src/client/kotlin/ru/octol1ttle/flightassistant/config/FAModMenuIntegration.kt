package ru.octol1ttle.flightassistant.config

import com.terraformersmc.modmenu.api.*
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.dsl.*
import java.awt.Color
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.options.*
import ru.octol1ttle.flightassistant.impl.computer.ElytraStatusComputer

object FAModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { parent: Screen ->
        YetAnotherConfigLib(FlightAssistant.MOD_ID) {
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
        }

    private fun RootDsl.registerDisplayOptions(
        title: Text,
        current: DisplayOptions,
        defaults: DisplayOptions
    ): RegisterableActionDelegateProvider<CategoryDsl, ConfigCategory> {
        return categories.registering {
            name(title)

            val percentageFormatter: (Float) -> Text = { value: Float -> Text.of("${(value * 100).toInt()}%") }

            rootOptions.registerLabel("frame", Text.translatable("config.flightassistant.options.display.frame"))
            rootOptions.register<Float>("frame.width") {
                setDisplayName()
                binding(current::frameWidth, defaults.frameWidth)
                controller(slider(0f..1f, 0.05f, percentageFormatter))
            }
            rootOptions.register<Float>("frame.height") {
                setDisplayName()
                binding(current::frameHeight, defaults.frameHeight)
                controller(slider(0f..1f, 0.05f, percentageFormatter))
            }

            rootOptions.registerLabel("colors", Text.translatable("config.flightassistant.options.display.colors"))
            rootOptions.register<Color>("colors.primary") {
                setDisplayName()
                binding(current::primaryColor, defaults.primaryColor)
                controller(colorPicker())
            }
            rootOptions.register<Color>("colors.warning") {
                setDisplayName()
                binding(current::warningColor, defaults.warningColor)
                controller(colorPicker())
            }

            rootOptions.registerLabel("attitude", Text.translatable("config.flightassistant.options.display.attitude"))
            rootOptions.register<Boolean>("attitude.enabled") {
                setDisplayName()
                binding(current::showAttitude, defaults.showAttitude)
                controller(tickBox())
            }
            rootOptions.register<Int>("attitude.degree_step") {
                setDisplayName()
                binding(current::attitudeDegreeStep, defaults.attitudeDegreeStep)
                controller(slider(5..45, 5) { value: Int -> Text.of("$value*") })
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
            rootOptions.register<Boolean>("attitude.show_heading") {
                setDisplayName()
                binding(current::showHorizonHeading, defaults.showHorizonHeading)
                controller(tickBox())
            }
            rootOptions.register<Int>("attitude.heading_step") {
                setDisplayName()
                binding(current::headingDegreeStep, defaults.headingDegreeStep)
                controller(slider(5..180, 5) { value: Int -> Text.of("$value*") })
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
                binding(current::showFlightPathVector, defaults.showFlightPathVector)
                controller(tickBox())
            }
            rootOptions.register<ElytraStatusComputer.DurabilityUnits>("elytra_durability.units") {
                setDisplayName()
                binding(current::elytraDurabilityUnits, defaults.elytraDurabilityUnits)
                controller(enumSwitch(ElytraStatusComputer.DurabilityUnits::class.java))
            }

            rootOptions.registerLabel("alerts", Text.translatable("config.flightassistant.options.display.alerts"))
            rootOptions.register<Boolean>("alerts.show") {
                setDisplayName()
                binding(current::showAlerts, defaults.showAlerts)
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
}
