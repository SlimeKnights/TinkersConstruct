package tconstruct.library.blocks;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.util.IFacingLogic;
import mantle.blocks.abstracts.InventoryLogic;

public abstract class AdaptiveInventoryLogic extends InventoryLogic implements IFacingLogic
{
    Random random = new Random();
    protected int inventorySize;

    public AdaptiveInventoryLogic()
    {
        super(0);
    }

    protected void adjustInventory (int size, boolean forceAdjust)
    {
        if (size != inventorySize || forceAdjust)
        {
            inventorySize = size;

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[size];
            int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (tempInv.length > inventory.length)
            {
                for (int i = inventory.length; i < tempInv.length; i++)
                {
                    ItemStack stack = tempInv[i];
                    if (stack != null)
                    {
                        float jumpX = random.nextFloat() * 0.8F + 0.1F;
                        float jumpY = random.nextFloat() * 0.8F + 0.1F;
                        float jumpZ = random.nextFloat() * 0.8F + 0.1F;

                        int offsetX = 0;
                        int offsetY = 0;
                        int offsetZ = 0;
                        switch (getTossDirection())
                        {
                        case 0: // -y
                            offsetY--;
                            break;
                        case 1: // +y
                            offsetY++;
                            break;
                        case 2: // +z
                            offsetZ--;
                            break;
                        case 3: // -z
                            offsetZ++;
                            break;
                        case 4: // +x
                            offsetX--;
                            break;
                        case 5: // -x
                            offsetX++;
                            break;
                        }

                        while (stack.stackSize > 0)
                        {
                            int itemSize = random.nextInt(21) + 10;

                            if (itemSize > stack.stackSize)
                            {
                                itemSize = stack.stackSize;
                            }

                            stack.stackSize -= itemSize;
                            EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY),
                                    (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.itemID, itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (double) ((float) random.nextGaussian() * offset);
                            entityitem.motionY = (double) ((float) random.nextGaussian() * offset + 0.2F);
                            entityitem.motionZ = (double) ((float) random.nextGaussian() * offset);
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                    }
                }
            }
        }
    }

    public int getTossDirection ()
    {
        return getRenderDirection();
    }
}
