package mods.tinker.tconstruct.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.BowBase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Shortbow extends BowBase
{
    public Shortbow(int itemID)
    {
        super(itemID);
        this.setUnlocalizedName("InfiTool.Shortbow");
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
    public Item getHeadItem ()
    {
        return TContent.toolRod;
    }
    
    @Override
    public Item getHandleItem ()
    {
        return TContent.bowstring;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.toolRod;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "ranged", "bow" };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP) entity;
            ItemStack usingItem = player.getItemInUse();
            if (usingItem != null && usingItem.getItem() == this)
            {
                player.movementInput.moveForward *= 1.3F;
                player.movementInput.moveStrafe *= 1.3F;
            }
        }
    }
}
