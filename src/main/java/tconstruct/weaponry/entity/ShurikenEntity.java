package tconstruct.weaponry.entity;

import tconstruct.library.entity.ProjectileBase;
import tconstruct.weaponry.TinkerWeaponry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShurikenEntity extends ProjectileBase {
    public int spin = 0;
    public int rollAngle = 0;

    public ShurikenEntity(World world) {
        super(world);
        setSize(0.3f, 0.1f);
        this.bounceOnNoDamage = false;
    }

    public ShurikenEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
        setSize(0.3f, 0.1f);
        this.bounceOnNoDamage = false;
    }

    public ShurikenEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);
        setSize(0.3f, 0.1f);
        this.bounceOnNoDamage = false;
    }

    @Override
    public void onUpdate() {
        // you turn me right round baby
        if(this.ticksInGround == 0)
            spin = (spin + 33) % 360;

        super.onUpdate();
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);

        // this is only relevant clientside only, so we don't actually have it on the server
        //rollAngle = (TinkerWeaponry.random.nextInt(3)-1)*45 + TinkerWeaponry.random.nextInt(30)-15;
        spin = TinkerWeaponry.random.nextInt(360);
    }

    @Override
    protected double getGravity() {
        return 0; // todo: remove debug code ;)
        //return (this.ticksExisted/8) * 0.018d; // integer division. so the first 20 ticks it will have no gravity at all.
    }

    @Override
    protected double getSlowdown() {
        return 0.15f;
    }

    @Override
    protected double getStuckDepth() {
        return 0.8d;
    }
}
