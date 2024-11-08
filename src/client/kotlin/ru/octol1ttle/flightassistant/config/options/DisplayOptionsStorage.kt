package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.config.v2.api.SerialEntry

class DisplayOptionsStorage {
    @SerialEntry
    var flying = DisplayOptions()

    @SerialEntry
    var notFlyingHasElytra = DisplayOptions().setMinimal()

    @SerialEntry
    var notFlyingNoElytra = DisplayOptions().setDisabled()
}
