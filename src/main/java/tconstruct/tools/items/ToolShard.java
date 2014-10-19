package tconstruct.tools.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.DynamicToolPart;

import java.util.List;

public class ToolShard extends DynamicToolPart
{

    public ToolShard(String tex, String name)
    {
        super(tex, name);
    }

    @Override
    public void getSubItems (Item item, CreativeTabs tab, List list)
    {
        // material id == metadata
        for(Integer matID : TConstructRegistry.defaultShardMaterials) {
            ItemStack stack = new ItemStack(item, 1, matID);
            if (this.getMaterialID(stack) != -1)
                list.add(stack);
        }
    }
}
