package tconstruct.items.tools;

import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.library.crafting.Detailing.DetailInput;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Chisel extends ToolCore
{
    public Chisel(int id)
    {
        super(id, 0);
        this.setUnlocalizedName("InfiTool.Chisel");
        this.setContainerItem(this);
    }

    @Override
    public ItemStack getContainerItemStack (ItemStack itemStack)
    {
        if (itemStack.hasTagCompound())
        {
            int reinforced = 0;
            NBTTagCompound tags = itemStack.getTagCompound();

            if (tags.getCompoundTag("InfiTool").hasKey("Unbreaking"))
                reinforced = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");

            if (random.nextInt(10) < 10 - reinforced)
            {
                AbilityHelper.damageTool(itemStack, 1, null, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid (ItemStack par1ItemStack)
    {
        return false;
    }

    boolean performDetailing (World world, int x, int y, int z, int blockID, int blockMeta)
    {
        boolean detailed = false;
        return detailed;
    }

    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return false;
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer entityplayer)
    {
        if (entityplayer.capabilities.isCreativeMode)
        {
            onEaten(stack, world, entityplayer);
        }
        else
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.getBoolean("Broken"))
                entityplayer.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public ItemStack onEaten (ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if (!world.isRemote)
        {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);
            if (movingobjectposition == null)
            {
                return itemstack;
            }
            if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
            {
                int x = movingobjectposition.blockX;
                int y = movingobjectposition.blockY;
                int z = movingobjectposition.blockZ;
                int blockID = world.getBlockId(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                DetailInput details = TConstruct.chiselDetailing.getDetailing(blockID, meta);
                if (details != null && details.outputID < 4096)
                {
                    world.setBlock(x, y, z, details.outputID, details.outputMeta, 3);
                    if (!(entityplayer.capabilities.isCreativeMode))
                    {
                        int reinforced = 0;
                        NBTTagCompound tags = itemstack.getTagCompound();

                        if (tags.getCompoundTag("InfiTool").hasKey("Unbreaking"))
                            reinforced = tags.getCompoundTag("InfiTool").getInteger("Unbreaking");

                        if (random.nextInt(10) < 10 - reinforced)
                        {
                            AbilityHelper.damageTool(itemstack, 1, null, false);
                        }
                    }
                    world.playAuxSFX(2001, x, y, z, blockID + (meta << 12));
                    entityplayer.swingItem();
                }
            }
        }

        return itemstack;
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
                player.movementInput.moveForward *= 2.0;
                player.movementInput.moveStrafe *= 2.0;
            }
        }
    }

    @Override
    public int getMaxItemUseDuration (ItemStack itemstack)
    {
        if (!itemstack.hasTagCompound())
            return 20;

        int speed = itemstack.getTagCompound().getCompoundTag("InfiTool").getInteger("MiningSpeed") / 100;
        int truespeed = 20 - speed;
        if (truespeed < 0)
            truespeed = 0;
        return truespeed;
    }

    @Override
    public EnumAction getItemUseAction (ItemStack itemstack)
    {
        return EnumAction.eat;
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
            return "_chisel_head";
        case 1:
            return "_chisel_head_broken";
        case 2:
            return "_chisel_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_chisel_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "chisel";
    }

    @Override
    public Item getHeadItem ()
    {
        return TContent.chiselHead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return null;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "utility" };
    }

    @Override
    public boolean willAllowOffhandWeapon ()
    {
        return false;
    }

    @Override
    public boolean willAllowShield ()
    {
        return false;
    }

    @Override
    public boolean isOffhandHandDualWeapon ()
    {
        return false;
    }
}
