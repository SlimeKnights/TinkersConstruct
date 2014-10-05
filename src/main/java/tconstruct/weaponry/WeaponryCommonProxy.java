package boni.tinkersweaponry;

import boni.tinkersweaponry.entity.*;
import cpw.mods.fml.common.registry.EntityRegistry;

public class WeaponryCommonProxy {
    public void init() {
        EntityRegistry.registerModEntity(ShurikenEntity.class, "Shuriken", 0, TinkerWeaponry.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ThrowingKnifeEntity.class, "ThrowingKnife", 1, TinkerWeaponry.instance, 32, 5, true);
        EntityRegistry.registerModEntity(JavelinEntity.class, "Javelin", 2, TinkerWeaponry.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 3, TinkerWeaponry.instance, 32, 5, true);
        EntityRegistry.registerModEntity(BoltEntity.class, "Bolt", 4, TinkerWeaponry.instance, 32, 5, true);
    }
}
