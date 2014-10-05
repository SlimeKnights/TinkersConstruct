package tconstruct.weaponry.entity;

import tconstruct.weaponry.library.util.PiercingArrowDamage;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;

public class ArrowEntity extends ProjectileBase {
    public ArrowEntity(World world) {
        super(world);
    }

    public ArrowEntity(World world, double d, double d1, double d2) {
        super(world, d, d1, d2);
    }

    public ArrowEntity(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        super(world, player, speed, accuracy, stack);
    }

    @Override
    protected double getStuckDepth() {
        return 0.25d;
    }

    @Override
    protected double getSlowdown() {
        // todo: fletchling material of arrows impact
        return super.getSlowdown();
    }

    @Override
    protected double getGravity() {
        float mass = returnStack.getTagCompound().getCompoundTag("InfiTool").getFloat("Mass");
        mass /= 36f; // why 36? simple because it's roughly 0.05 with flint head and wooden arrow shaft! Yes, that's the only reason.
        return mass;
    }

    @Override
    public void onHitBlock(MovingObjectPosition movingobjectposition) {
        super.onHitBlock(movingobjectposition);

        // we might break! oh noez!
        float chance = returnStack.getTagCompound().getCompoundTag("InfiTool").getFloat("BreakChance");
        if(chance > TConstruct.random.nextFloat())
            this.setDead();
    }

    @Override
    protected void playHitBlockSound(int x, int y, int z) {
        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    protected void playHitEntitySound() {
        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
    }

    // returns true if it was successful
    @Override
    public boolean dealDamage(float damage, ToolCore ammo, NBTTagCompound tags, Entity entityHit)
    {
        boolean dealtDamage;

        //Minecraft.getMinecraft().thePlayer.sendChatMessage("Damage/Weight: " + damage + "  -  " + tags.getFloat("Mass"));

        // we take the weight, and shift the damage done towards armor piercing, the more weight the arrow/bolt has!
        float shift = (tags.getFloat("Mass") - 0.7f);

        if(shift < 0)
            shift = 0;
        if(shift > damage)
            shift = damage;

        // deal regular damage
        dealtDamage = super.dealDamage(damage-shift, ammo, tags, entityHit);

        // deal armor piercing damage
        if(shift > 0) {
            DamageSource damagesource;
            if (this.shootingEntity == null)
                damagesource = new PiercingArrowDamage("arrow", this, this);
            else
                damagesource = new PiercingArrowDamage("arrow", this, this.shootingEntity);

            //Minecraft.getMinecraft().thePlayer.sendChatMessage("Piercing Damage: " + shift);

            dealtDamage |= entityHit.attackEntityFrom(damagesource, shift);
        }

        return dealtDamage;
    }
}
