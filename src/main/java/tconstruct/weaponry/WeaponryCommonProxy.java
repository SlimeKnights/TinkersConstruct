package tconstruct.weaponry;

import tconstruct.TConstruct;
import tconstruct.weaponry.entity.*;
import cpw.mods.fml.common.registry.EntityRegistry;

public class WeaponryCommonProxy {
    public void init() {
        EntityRegistry.registerModEntity(ShurikenEntity.class, "Shuriken", 20, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ThrowingKnifeEntity.class, "ThrowingKnife", 21, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(JavelinEntity.class, "Javelin", 22, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(ArrowEntity.class, "Arrow", 23, TConstruct.instance, 32, 5, true);
        EntityRegistry.registerModEntity(BoltEntity.class, "Bolt", 24, TConstruct.instance, 32, 5, true);
    }
}
