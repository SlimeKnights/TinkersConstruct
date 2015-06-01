package tconstruct.weaponry.entity;

import net.minecraft.util.MovingObjectPosition;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.weaponry.TinkerWeaponry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ThrowingKnifeEntity extends ProjectileBase {
    public ThrowingKnifeEntity(World world) {
        super(world);
    }

    public ThrowingKnifeEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
    }

    public ThrowingKnifeEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);
    }

    @Override
    public void onHitBlock(MovingObjectPosition movingobjectposition)
    {
        super.onHitBlock(movingobjectposition);
        this.defused = true;
    }

    @Override
    protected double getStuckDepth() {
        return 0.3d + TinkerWeaponry.random.nextDouble();
    }
}
