package mods.tinker.tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.List;

import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class Detailing
{
    public List<DetailInput> detailing = new ArrayList<DetailInput>();
    
    public void addDetailing(Object input, int inputMeta, Object output, int outputMeta, ToolCore tool)
    {
        int iID, iMeta = inputMeta, oID, oMeta = outputMeta;
        
        if (input instanceof Block)
            iID = ((Block)input).blockID;
        
        else if (input instanceof Item)
            iID = ((Item)input).itemID;
        
        else if (input instanceof Integer)
            iID = (Integer) input;
        
        else
            throw new RuntimeException("Invalid detail input!");
        
        if (output instanceof Block)
            oID = ((Block)output).blockID;
        
        else if (output instanceof Item)
            oID = ((Item)output).itemID;
        
        else if (output instanceof Integer)
            oID = (Integer) output;
        
        else
            throw new RuntimeException("Invalid detail output!");
        
        this.addDetailing(new DetailInput(iID, iMeta, oID, oMeta), tool);
    }
    
    public void addDetailing(DetailInput details, ToolCore tool)
    {
        detailing.add(details);
        GameRegistry.addShapelessRecipe(new ItemStack(details.outputID, 1, details.outputMeta), new ItemStack(tool, 1, Short.MAX_VALUE), new ItemStack(details.inputID, 1, details.inputMeta));
    }
    
    public DetailInput getDetailing(int inputID, int inputMeta)
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

        public DetailInput (int inputID, int inputMeta, int outputID, int outputMeta)
        {
            this.inputID = inputID;
            this.inputMeta = inputMeta;
            this.outputID = outputID;
            this.outputMeta = outputMeta;
        }
    }
}
