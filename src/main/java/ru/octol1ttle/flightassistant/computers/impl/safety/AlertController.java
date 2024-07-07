package ru.octol1ttle.flightassistant.computers.impl.safety;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import ru.octol1ttle.flightassistant.AlertSoundInstance;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.impl.autoflight.AutoThrustOffAlert;
import ru.octol1ttle.flightassistant.alerts.impl.autoflight.AutopilotOffAlert;
import ru.octol1ttle.flightassistant.alerts.impl.fault.ComputerFaultAlert;
import ru.octol1ttle.flightassistant.alerts.impl.fault.IndicatorFaultAlert;
import ru.octol1ttle.flightassistant.alerts.impl.firework.FireworkNoResponseAlert;
import ru.octol1ttle.flightassistant.alerts.impl.firework.FireworkUnsafeAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.ApproachingVoidDamageLevelAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.MinimumsAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.UnloadedChunkAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.gpws.ExcessiveDescentAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.gpws.ExcessiveTerrainClosureAlert;
import ru.octol1ttle.flightassistant.alerts.impl.nav.gpws.UnsafeTerrainClearanceAlert;
import ru.octol1ttle.flightassistant.alerts.impl.other.ElytraHealthLowAlert;
import ru.octol1ttle.flightassistant.alerts.impl.other.StallAlert;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.registries.AlertRegistry;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.events.CustomAlertRegistrationCallback;

public class AlertController implements ITickableComputer {
    public final List<BaseAlert> activeAlerts = new ArrayList<>();
    private final AirDataComputer airData = ComputerRegistry.resolve(AirDataComputer.class);
    private final SoundManager manager;

    public AlertController(SoundManager manager) {
        this.manager = manager;
        // TODO: ECAM actions
        AlertRegistry.register(new StallAlert());
        AlertRegistry.register(new ExcessiveDescentAlert());
        AlertRegistry.register(new ExcessiveTerrainClosureAlert());
        AlertRegistry.register(new UnsafeTerrainClearanceAlert());
        AlertRegistry.register(new AutopilotOffAlert());
        AlertRegistry.register(new UnloadedChunkAlert());
        AlertRegistry.register(new ComputerFaultAlert());
        AlertRegistry.register(new AutoThrustOffAlert());
        AlertRegistry.register(new MinimumsAlert());
        AlertRegistry.register(new ApproachingVoidDamageLevelAlert());
        AlertRegistry.register(new ElytraHealthLowAlert());
        AlertRegistry.register(new FireworkUnsafeAlert());
        AlertRegistry.register(new FireworkNoResponseAlert());
        AlertRegistry.register(new IndicatorFaultAlert());

        CustomAlertRegistrationCallback.EVENT.invoker().registerCustomAlerts();
    }

    @Override
    public void tick() {
        for (BaseAlert alert : AlertRegistry.getAlerts()) {
            if (alert.isTriggered()) {
                if (!activeAlerts.contains(alert)) {
                    activeAlerts.add(alert);
                }
                continue;
            }

            if (!activeAlerts.contains(alert)) {
                continue;
            }

            alert.played = false;
            alert.hidden = false;

            if (alert.soundInstance != null) {
                manager.stop(alert.soundInstance);
                alert.soundInstance = null;
            }

            activeAlerts.remove(alert);
        }

        boolean interrupt = false;
        activeAlerts.sort(Comparator.comparingDouble(alert -> alert.getSoundData().priority()));

        for (BaseAlert alert : activeAlerts) {
            AlertSoundData data = alert.getSoundData();

            boolean soundChanged = false;
            if (alert.soundInstance != null) {
                soundChanged = data.sound() == null || !data.sound().getId().equals(alert.soundInstance.getId());
                if (soundChanged || interrupt || alert.hidden) {
                    manager.stop(alert.soundInstance);
                    alert.soundInstance = null;
                    if (soundChanged) {
                        alert.played = false; // Schedule for new sound instance creation
                    } else {
                        continue;
                    }
                } else if (manager.isPlaying(alert.soundInstance)) {
                    interrupt = true;
                }
            }

            if (!airData.isFlying()
                    || data.sound() == null
                    || alert.hidden || alert.played
                    || interrupt && !soundChanged) {
                continue;
            }

            alert.soundInstance = new AlertSoundInstance(data.sound());
            manager.play(alert.soundInstance);
            alert.played = true;

            interrupt = true;
        }
    }

    public void hide() {
        for (BaseAlert alert : activeAlerts) {
            if (!alert.hidden && alert instanceof IECAMAlert) {
                alert.hidden = true;
                return;
            }
        }
    }

    public void recall() {
        for (int i = activeAlerts.size() - 1; i >= 0; i--) {
            BaseAlert alert = activeAlerts.get(i);
            if (alert.hidden && alert instanceof IECAMAlert) {
                alert.hidden = false;
                return;
            }
        }
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.alert_mgr";
    }

    @Override
    public void reset() {
        for (BaseAlert alert : activeAlerts) {
            alert.played = false;
            alert.hidden = false;

            if (alert.soundInstance != null) {
                manager.stop(alert.soundInstance);
                alert.soundInstance = null;
            }
        }
        activeAlerts.clear();
    }
}
