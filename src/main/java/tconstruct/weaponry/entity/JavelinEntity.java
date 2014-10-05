package tconstruct.weaponry.entity;

import tconstruct.weaponry.TinkerWeaponry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class JavelinEntity extends ProjectileBase {
    public int roll = 0;

    public JavelinEntity(World world) {
        super(world);
    }

    public JavelinEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
    }

    public JavelinEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);
    }

    @Override
    public void onUpdate() {
        // you turn me right round baby
        if(this.ticksInGround == 0)
            roll = (roll + 13) % 360;

        super.onUpdate();
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);

        // this is only relevant clientside only, so we don't actually have it on the server
        roll = TinkerWeaponry.random.nextInt(360);
    }

    protected double getGravity() { return 0.07; }

    @Override
    protected double getStuckDepth() {
        return 0.5f;
    }
}
