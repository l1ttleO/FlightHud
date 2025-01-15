package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.config.v2.api.SerialEntry

class GlobalOptions {
    @SerialEntry
    internal var modEnabled: Boolean = true

    @SerialEntry
    internal var hudEnabled: Boolean = true

    @SerialEntry
    internal var safetyEnabled: Boolean = true

    @SerialEntry
    internal var automationsAllowedInOverlays: Boolean = false
}
