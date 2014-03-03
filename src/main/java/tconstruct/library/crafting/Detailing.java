package tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.List;

import tconstruct.library.tools.ToolCore;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.registry.GameRegistry;

public class Detailing
{
    public List<DetailInput> detailing = new ArrayList<DetailInput>();

    public void addDetailing (Object input, int inputMeta, Object output, int outputMeta, ToolCore tool)
    {
        int iID, iMeta = inputMeta, oID, oMeta = outputMeta;

        if (input instanceof Block)
            iID = ((Block) input).blockID;

        else if (input instanceof Item)
            iID = ((Item) input).itemID;

        else if (input instanceof Integer)
            iID = (Integer) input;

        else
            throw new RuntimeException("Invalid detail input!");

        if (output instanceof Block)
            oID = ((Block) output).blockID;

        else if (output instanceof Item)
            oID = ((Item) output).itemID;

        else if (output instanceof Integer)
            oID = (Integer) output;

        else
            throw new RuntimeException("Invalid detail output!");

        this.addDetailing(new DetailInput(iID, iMeta, oID, oMeta), tool);
    }

    public void addDetailing (DetailInput details, ToolCore tool)
    {
        detailing.add(details);
        ItemStack toolstack = new ItemStack(tool, 1, Short.MAX_VALUE);

        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound toolTag = new NBTTagCompound();
        toolTag.setInteger("RenderHandle", 0);
        toolTag.setInteger("RenderHead", 2);
        toolTag.setInteger("RenderAccessory", 2);
        toolTag.setInteger("Damage", 0);
        toolTag.setInteger("TotalDurability", 100);
        compound.setCompoundTag("InfiTool", toolTag);
        toolstack.setTagCompound(compound);
        addShapelessToolRecipe(new ItemStack(details.outputID, 1, details.outputMeta), toolstack, new ItemStack(details.inputID, 1, details.inputMeta));
    }

    public void addShapelessToolRecipe (ItemStack par1ItemStack, Object... par2ArrayOfObj)
    {
        ArrayList arraylist = new ArrayList();
        Object[] aobject = par2ArrayOfObj;
        int i = par2ArrayOfObj.length;

        for (int j = 0; j < i; ++j)
        {
            Object object1 = aobject[j];

            if (object1 instanceof ItemStack)
            {
                arraylist.add(((ItemStack) object1).copy());
            }
            else if (object1 instanceof Item)
            {
                arraylist.add(new ItemStack((Item) object1));
            }
            else
            {
                if (!(object1 instanceof Block))
                {
                    throw new RuntimeException("Invalid shapeless tool recipe!");
                }

                arraylist.add(new ItemStack((Block) object1));
            }
        }

        GameRegistry.addRecipe(new ShapelessToolRecipe(par1ItemStack, arraylist));
    }

    public DetailInput getDetailing (int inputID, int inputMeta)
    {
        for (int i = 0; i < detailing.size(); i++)
        {
            DetailInput detail = (DetailInput) detailing.get(i);
            if (inputID == detail.inputID && inputMeta == detail.inputMeta)
            {
                return detail;
            }
        }
        return null;
    }

    public class DetailInput
    {
        public int inputID;
        public int inputMeta;
        public int outputID;
        public int outputMeta;

        public DetailInput(int inputID, int inputMeta, int outputID, int outputMeta)
        {
            this.inputID = inputID;
            this.inputMeta = inputMeta;
            this.outputID = outputID;
            this.outputMeta = outputMeta;
        }
    }
}
