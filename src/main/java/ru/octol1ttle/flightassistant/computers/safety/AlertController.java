package ru.octol1ttle.flightassistant.computers.safety;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import ru.octol1ttle.flightassistant.AlertSoundInstance;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.alerts.autoflight.AutoFireworkOffAlert;
import ru.octol1ttle.flightassistant.alerts.autoflight.AutopilotOffAlert;
import ru.octol1ttle.flightassistant.alerts.fault.ComputerFaultAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkNoResponseAlert;
import ru.octol1ttle.flightassistant.alerts.firework.FireworkUnsafeAlert;
import ru.octol1ttle.flightassistant.alerts.nav.ApproachingVoidDamageLevelAlert;
import ru.octol1ttle.flightassistant.alerts.nav.MinimumsAlert;
import ru.octol1ttle.flightassistant.alerts.nav.UnloadedChunkAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveDescentAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.ExcessiveTerrainClosureAlert;
import ru.octol1ttle.flightassistant.alerts.nav.gpws.UnsafeTerrainClearanceAlert;
import ru.octol1ttle.flightassistant.alerts.other.ElytraHealthLowAlert;
import ru.octol1ttle.flightassistant.alerts.other.StallAlert;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class AlertController implements ITickableComputer {
    public final List<BaseAlert> activeAlerts;
    private final ComputerHost host;
    private final SoundManager manager;
    private final List<BaseAlert> allAlerts;

    public AlertController(ComputerHost host, SoundManager manager) {
        this.host = host;
        this.manager = manager;
        // TODO: ECAM actions
        allAlerts = List.of(
                new StallAlert(host.stall),
                new UnloadedChunkAlert(host.chunkStatus),
                new ExcessiveDescentAlert(host.data, host.gpws), new ExcessiveTerrainClosureAlert(host.gpws, host.time),
                new UnsafeTerrainClearanceAlert(host.gpws, host.plan),
                new AutopilotOffAlert(host.autoflight), new AutoFireworkOffAlert(host.autoflight),
                new MinimumsAlert(host.plan),
                //new ComputerFaultAlert(host), new IndicatorFaultAlert(renderer),
                new ApproachingVoidDamageLevelAlert(host.voidLevel),
                new ElytraHealthLowAlert(host.data),
                new FireworkUnsafeAlert(host.data, host.firework), new FireworkNoResponseAlert(host.firework)
        );
        activeAlerts = new ArrayList<>(allAlerts.size());
    }

    @Override
    public void tick() {
        for (BaseAlert alert : allAlerts) {
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

            if (!host.data.isFlying()
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
    public String getId() {
        return "alert_mgr";
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
