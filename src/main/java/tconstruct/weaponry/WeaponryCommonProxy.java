package tconstruct.weaponry;

import tconstruct.TConstruct;
import tconstruct.weaponry.entity.*;
import cpw.mods.fml.common.registry.EntityRegistry;

public class WeaponryCommonProxy {
    public void init() {
        EntityRegistry.registerModEntity(ShurikenEntity.class, "Shuriken", 10, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ThrowingKnifeEntity.class, "ThrowingKnife", 11, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(JavelinEntity.class, "Javelin", 12, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 13, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(BoltEntity.class, "Bolt", 14, TConstruct.instance, 32, 5, true);
    }
}
