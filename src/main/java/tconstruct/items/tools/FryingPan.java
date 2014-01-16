package tconstruct.items.tools;

import java.util.List;

import tconstruct.blocks.logic.EquipLogic;
import tconstruct.common.TRepo;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.Weapon;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        mob.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 0)); //5 seconds of stun
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

    public String getToolName ()
    {
        return "Frying Pan";
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
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

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        if (side == 0 || !player.isSneaking())
        {
            return false;
        }
        else if (!world.func_147439_a(x, y, z).func_149688_o().isSolid())
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
            else if (!TRepo.heldItemBlock.func_149742_c(world, x, y, z))
            {
                return false;
            }
            else
            {
                world.func_147465_d(x, y, z, TRepo.heldItemBlock, 0, 3);
                TRepo.heldItemBlock.func_149689_a(world, x, y, z, player, stack);
                world.playSoundEffect(x, y, z, "tinker:frypan_hit", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 0.65F);

                EquipLogic logic = (EquipLogic) world.func_147438_o(x, y, z);
                logic.setEquipmentItem(stack);
                --stack.stackSize;

                return true;
            }
        }
    }

    @Override
    public Item getHeadItem ()
    {
        return TRepo.frypanHead;
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
