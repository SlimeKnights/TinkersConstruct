package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMod;

/* Little mod for actually adding the lapis modifier */

public class ModRepair extends ToolMod
{

    public ModRepair()
    {
        super(new ItemStack[0], 0, "");
    }

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        return canModify(tool, input);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if ((input[0] == null && input[1] == null))
            return false;

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getInteger("Damage") > 0)
        {
            int headID = tags.getInteger("Head");
            if (input[0] != null && input[1] != null)
                return headID == PatternBuilder.instance.getPartID(input[0]) && headID == PatternBuilder.instance.getPartID(input[1]) && calculateIfNecessary(tool, input);
            else if (input[0] != null && input[1] == null)
                return headID == PatternBuilder.instance.getPartID(input[0]);
            else if (input[0] == null && input[1] != null)
                return headID == PatternBuilder.instance.getPartID(input[1]);

        }
        return false;
    }

    private boolean calculateIfNecessary (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int damage = tags.getInteger("Damage");
        int valueSlot1 = 0;
        int valueSlot2 = 0;
        if (input[0] != null)
            valueSlot1 = calculateIncrease(tool, PatternBuilder.instance.getPartValue(input[0]));
        if (input[1] != null)
            valueSlot2 = calculateIncrease(tool, PatternBuilder.instance.getPartValue(input[1]));

        return ((damage - valueSlot1) > 0) && ((damage - valueSlot2) > 0);
    }

    private int calculateIncrease (ItemStack tool, int materialValue)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int damage = tags.getInteger("Damage");
        int dur = tags.getInteger("BaseDurability");
        int increase = (int) (50 + (dur * 0.4f * materialValue));

        int modifiers = tags.getInteger("Modifiers");
        float mods = 1.0f;
        if (modifiers == 2)
            mods = 0.8f;
        else if (modifiers == 1)
            mods = 0.6f;
        else if (modifiers == 0)
            mods = 0.4f;

        increase *= mods;

        int repair = tags.getInteger("RepairCount");
        float repairCount = (100 - repair) / 100f;
        if (repairCount < 0.5f)
            repairCount = 0.5f;
        increase *= repairCount;
        increase /= ((ToolCore) tool.getItem()).getRepairCost();
        return increase;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean("Broken", false);
        int damage = tags.getInteger("Damage");
        int dur = tags.getInteger("BaseDurability");
        int itemsUsed = 0;

        int materialValue = 0;
        if (input[0] != null)
        {
            materialValue += PatternBuilder.instance.getPartValue(input[0]);
            itemsUsed++;
        }
        if (input[1] != null)
        {
            materialValue += PatternBuilder.instance.getPartValue(input[1]);
            itemsUsed++;
        }

        int increase = calculateIncrease(tool, materialValue);
        int repair = tags.getInteger("RepairCount");
        repair += itemsUsed;
        tags.setInteger("RepairCount", repair);

        damage -= increase;
        if (damage < 0)
            damage = 0;
        tags.setInteger("Damage", damage);

        AbilityHelper.damageTool(tool, 0, null, true);
    }

    @Override
    public void addMatchingEffect (ItemStack tool)
    {
    }
}
