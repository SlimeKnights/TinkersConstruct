package tconstruct.weaponry;

import tconstruct.TConstruct;
import tconstruct.weaponry.entity.*;
import cpw.mods.fml.common.registry.EntityRegistry;

public class WeaponryCommonProxy {
    public void init() {
        EntityRegistry.registerModEntity(ShurikenEntity.class, "Shuriken", 0, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ThrowingKnifeEntity.class, "ThrowingKnife", 1, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(JavelinEntity.class, "Javelin", 2, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 3, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(BoltEntity.class, "Bolt", 4, TConstruct.instance, 32, 5, true);
    }
}
