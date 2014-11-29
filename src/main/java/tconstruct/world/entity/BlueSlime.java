package tconstruct.world.entity;

import net.minecraft.item.Item;
import net.minecraft.world.*;
import tconstruct.world.TinkerWorld;

public class BlueSlime extends SlimeBase
{
    public BlueSlime(World world) {
        super(world);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected Item getDropItem ()
    {
        return TinkerWorld.strangeFood;
    }

    @Override
    protected String getSlimeParticle() {
        return "blueslime";
    }

    @Override
    protected SlimeBase createInstance(World world) {
        return new BlueSlime(world);
    }
}
