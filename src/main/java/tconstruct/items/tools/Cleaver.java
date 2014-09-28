package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.world.World;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class Cleaver extends Weapon
{
    public Cleaver()
    {
        super(5);
        this.setUnlocalizedName("InfiTool.Cleaver");
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.largeSwordBlade;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 2.5f;
    }

    @Override
    public float getDamageModifier ()
    {
        return 1.4f;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_cleaver_head";
        case 1:
            return "_cleaver_head_broken";
        case 2:
            return "_cleaver_handle";
        case 3:
            return "_cleaver_shield";
        case 4:
            return "_cleaver_guard";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_cleaver_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "cleaver";
    }

    /* Cleaver specific */
    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        if (AbilityHelper.onLeftClickEntity(stack, player, entity, this))
        {
            entity.hurtResistantTime += 7;

            /*
             * if (entity instanceof EntityLiving) { EntityLiving living =
             * (EntityLiving) entity; if (living.getHealth() <= 0) {
             * 
             * } }
             */
            // if (entity.getHealth() <= 0)
        }
        return true;
    }

    @Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped == stack)
            {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 2, 2, true));
            }
        }
    }

    /*
     * @Override public boolean onEntitySwing (EntityLiving entity, ItemStack
     * stack) { entity.swingProgressInt /= 2; return false; }
     */
}
