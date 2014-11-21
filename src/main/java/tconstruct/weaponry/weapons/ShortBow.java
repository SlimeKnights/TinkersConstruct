package tconstruct.weaponry.weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityPlayerSP;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.library.weaponry.BowBaseAmmo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShortBow extends BowBaseAmmo {
    public ShortBow() {
        super(0, "Shortbow");
    }

    @Override
    public float minAccuracy(ItemStack itemStack) {
        return 1;
    }

    @Override
    public float maxAccuracy(ItemStack itemStack) {
        return 1;
    }

    @Override
    protected Entity createProjectile(ItemStack arrows, World world, EntityPlayer player, float speed, float accuracy, float windup) {
        if(arrows.getItem() instanceof ArrowAmmo)
        {
            // modify accuraccy of the arrow depending on its accuraccy and weight
            NBTTagCompound tags = arrows.getTagCompound().getCompoundTag("InfiTool");
            float matAccuracy = tags.getFloat("Accuracy");
            float weight = tags.getFloat("Mass");

            // weight influences the accuracy too, but only a little bit.
            accuracy += ((100f-matAccuracy)/10f)/(weight);
            if(accuracy < 0)
                accuracy = 0;
        }

        return super.createProjectile(arrows, world, player, speed, accuracy, windup);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_bow_top";
            case 1:
                return "_bowstring_broken";
            case 2:
                return "_bowstring";
            case 3:
                return "_bow_bottom";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_bow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "shortbow";
    }

    @Override
    public int getPartAmount() {
        return 3;
    }

    @Override
    protected boolean animateLayer(int renderPass) {
        return renderPass < 3;
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerWeaponry.partBowLimb;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerWeaponry.bowstring;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerWeaponry.partBowLimb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        // shortbows are smaller and more mobile than longbows
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 1.5F;
                player.movementInput.moveStrafe *= 1.5F;
            }
        }
    }
}
