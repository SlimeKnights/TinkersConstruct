package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;
import net.minecraft.world.World;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.EquipLogic;

public class FryingPan extends Weapon
{
    public FryingPan()
    {
        super(2);
        this.setUnlocalizedName("InfiTool.FryPan");
    }

    @Override
    public boolean hitEntity (ItemStack stack, EntityLivingBase mob, EntityLivingBase player)
    {
        AbilityHelper.knockbackEntity(mob, 1.7f);
        mob.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 0)); // 5
                                                                               // seconds
                                                                               // of
                                                                               // stun
        return true;
    }

    @Override
    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity mob)
    {
        if (mob.canAttackWithItem() && !mob.hitByEntity(player) && mob.hurtResistantTime < 14)
            AbilityHelper.onLeftClickEntity(stack, player, mob, this);
        return false;
    }

    @Override
    public void onEntityDamaged (World world, EntityLivingBase player, Entity entity)
    {
        world.playSoundEffect(entity.posX, entity.posY, entity.posZ, "tinker:frypan_hit", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public void getSubItems (Item id, CreativeTabs tab, List list)
    {
        super.getSubItems(id, tab, list);

        Item accessory = getAccessoryItem();
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, 2), new ItemStack(getHandleItem(), 1, 16), null, "Bane of Pigs");

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setInteger("Modifiers", 0);
        tags.setInteger("Attack", Integer.MAX_VALUE / 100);
        tags.setInteger("TotalDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("BaseDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("MiningSpeed", Integer.MAX_VALUE / 100);

        int[] keyPair = new int[] { Integer.MAX_VALUE / 100, 0, 0 };
        tags.setIntArray("Blaze", keyPair);
        tags.setInteger("Necrotic", Integer.MAX_VALUE / 100);
        tags.setInteger("Effect1", 7);

        tags.setBoolean("Built", true);
        list.add(tool);
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        if (side == 0 || !player.isSneaking())
        {
            return false;
        }
        else if (!world.getBlock(x, y, z).getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }

            if (!player.canPlayerEdit(x, y, z, side, stack))
            {
                return false;
            }
            else if (!TinkerTools.heldItemBlock.canPlaceBlockAt(world, x, y, z))
            {
                return false;
            }
            else
            {
                world.setBlock(x, y, z, TinkerTools.heldItemBlock, 0, 3);
                TinkerTools.heldItemBlock.onBlockPlacedBy(world, x, y, z, player, stack);
                world.playSoundEffect(x, y, z, "tinker:frypan_hit", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 0.65F);

                EquipLogic logic = (EquipLogic) world.getTileEntity(x, y, z);
                logic.setEquipmentItem(stack);
                --stack.stackSize;

                return true;
            }
        }
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.frypanHead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 8;
    }

    @Override
    public int getPartAmount ()
    {
        return 2;
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenPartStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_frypan_head";
        case 1:
            return "_frypan_head_broken";
        case 2:
            return "_frypan_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_frypan_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "frypan";
    }
}
