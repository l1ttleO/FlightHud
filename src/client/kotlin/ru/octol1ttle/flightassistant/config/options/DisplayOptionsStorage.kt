package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.config.v2.api.SerialEntry

class DisplayOptionsStorage {
    @SerialEntry
    val flying = DisplayOptions()

    @SerialEntry
    val notFlyingHasElytra = DisplayOptions().setMinimal()

    @SerialEntry
    val notFlyingNoElytra = DisplayOptions().setDisabled()
}
