package mods.tinker.tconstruct.items;

import java.util.List;

import mods.tinker.tconstruct.blocks.logic.LiquidTextureLogic;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FilledBucket extends ItemBucket
{

    public FilledBucket(int id)
    {
        super(id, 0);
        //setTextureFile(TContent.craftingTexture);
        //setIconIndex(224);
        setUnlocalizedName("tconstruct.bucket");
        setContainerItem(Item.bucketEmpty);
        this.setHasSubtypes(true);
    }

    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        float var4 = 1.0F;
        double trueX = player.prevPosX + (player.posX - player.prevPosX) * (double) var4;
        double trueY = player.prevPosY + (player.posY - player.prevPosY) * (double) var4 + 1.62D - (double) player.yOffset;
        double trueZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) var4;
        boolean wannabeFull = false;
        MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, wannabeFull);

        if (position == null)
        {
            return stack;
        }
        else
        {
            /*FillBucketEvent event = new FillBucketEvent(player, stack, world, position);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
            	return stack;
            }

            if (event.getResult() == Event.Result.ALLOW)
            {
            	if (player.capabilities.isCreativeMode)
            	{
            		return stack;
            	}

            	if (--stack.stackSize <= 0)
            	{
            		return event.result;
            	}

            	if (!player.inventory.addItemStackToInventory(event.result))
            	{
            		player.dropPlayerItem(event.result);
            	}

            	return stack;
            }*/

            if (position.typeOfHit == EnumMovingObjectType.TILE)
            {
                int clickX = position.blockX;
                int clickY = position.blockY;
                int clickZ = position.blockZ;

                if (!world.canMineBlock(player, clickX, clickY, clickZ))
                {
                    return stack;
                }

                if (position.sideHit == 0)
                {
                    --clickY;
                }

                if (position.sideHit == 1)
                {
                    ++clickY;
                }

                if (position.sideHit == 2)
                {
                    --clickZ;
                }

                if (position.sideHit == 3)
                {
                    ++clickZ;
                }

                if (position.sideHit == 4)
                {
                    --clickX;
                }

                if (position.sideHit == 5)
                {
                    ++clickX;
                }

                if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack))
                {
                    return stack;
                }

                if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.getItemDamage()) && !player.capabilities.isCreativeMode)
                {
                    return new ItemStack(Item.bucketEmpty);
                }
            }

            return stack;
        }
    }

    public boolean tryPlaceContainedLiquid (World world, int clickX, int clickY, int clickZ, int meta)
    {
        if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlockMaterial(clickX, clickY, clickZ).isSolid())
        {
            return false;
        }
        else
        {
            world.setBlock(clickX, clickY, clickZ, TContent.liquidMetalStill.blockID);
            LiquidTextureLogic logic = (LiquidTextureLogic) world.getBlockTileEntity(clickX, clickY, clickZ);
            logic.setLiquidType(meta);

            return true;
        }
    }

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < icons.length; i++)
            list.add(new ItemStack(id, 1, i));
    }

    public Icon[] icons;

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:materials/bucket_" + textureNames[i]);
        }
    }

    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, materialNames.length);
        return getUnlocalizedName() + "." + materialNames[arr];
    }

    public static final String[] materialNames = new String[] { "Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "AluBrass", "Manyullyn", "Alumite", "Obsidian", "Steel",
            "Glass", "Stone", "Villager", "Cow", "Nickel", "Lead", "Silver", "Shiny", "Invar", "Electrum", "Ender" };

    public static final String[] textureNames = new String[] { "iron", "gold", "copper", "tin", "aluminum", "cobalt", "ardite", "bronze", "alubrass", "manyullyn", "alumite", "obsidian", "steel",
            "glass", "stone", "emerald", "blood", "nickel", "lead", "silver", "shiny", "invar", "electrum", "ender" };
}
