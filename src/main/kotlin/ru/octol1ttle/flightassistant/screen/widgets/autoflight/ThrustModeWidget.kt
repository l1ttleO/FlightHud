package ru.octol1ttle.flightassistant.screen.widgets.autoflight

import java.util.ArrayList
import java.util.EnumMap
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.screen.widgets.AbstractParentWidget

class ThrustModeWidget(val computers: ComputerView) : AbstractParentWidget() {
    private val buttons: EnumMap<AutopilotLogicComputer.ThrustMode.Type, ButtonWidget> = EnumMap(AutopilotLogicComputer.ThrustMode.Type::class.java)
    private val textFields: EnumMap<AutopilotLogicComputer.ThrustMode.Type, List<TextFieldWidget>> = EnumMap(AutopilotLogicComputer.ThrustMode.Type::class.java)

    init {
        buttons[AutopilotLogicComputer.ThrustMode.Type.SelectedSpeed] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed")
        ) { computers.autopilot.thrustMode.type = AutopilotLogicComputer.ThrustMode.Type.SelectedSpeed }.build()
        textFields[AutopilotLogicComputer.ThrustMode.Type.SelectedSpeed] = TextFieldWidget(
            mc.textRenderer, TODO()
        )


        buttons[AutopilotLogicComputer.ThrustMode.Type.VerticalTarget] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target")
        ) { computers.autopilot.thrustMode.type = AutopilotLogicComputer.ThrustMode.Type.VerticalTarget }.build()
        buttons[AutopilotLogicComputer.ThrustMode.Type.WaypointThrust] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.waypoint_thrust")
        ) { computers.autopilot.thrustMode.type = AutopilotLogicComputer.ThrustMode.Type.WaypointThrust }.build()
    }

    override fun children(): MutableList<out Element> {
        val list = ArrayList<Element>()
        list.addAll(buttons.values)
        textFields[computers.autopilot.thrustMode.type]?.let {
            list.addAll(it)
        }
        return list
    }
}
