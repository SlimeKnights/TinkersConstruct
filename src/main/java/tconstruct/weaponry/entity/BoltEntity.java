package tconstruct.weaponry.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BoltEntity extends ArrowEntity {
    public BoltEntity(World world) {
        super(world);
        this.setSize(0.3f, 0.3f);
    }

    public BoltEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
        this.setSize(0.3f, 0.3f);
    }

    public BoltEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);
        this.setSize(0.3f, 0.3f);
    }

    @Override
    protected void playHitBlockSound(int x, int y, int z) {
        this.playSound("random.bowhit", 1.0F, 1.0F);
    }

    @Override
    protected float armorPenetrationModifier() {
        return 1.5f;
    }
}
