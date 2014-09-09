package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

/* This event fires after all of the other construction. The resulting nbttag is added to the tool 
 * Note: The tag is the base tag. toolTag.getCompoundTag("InfiTool") will have all of the tool's data.
 */

public class PartBuilderEvent extends Event
{
    public final ItemStack material;
    public final ItemStack pattern;
    public final ItemStack otherPattern;
    protected ItemStack[] resultStacks;

    public PartBuilderEvent(ItemStack material, ItemStack pattern, ItemStack otherPattern)
    {
        this.material = material;
        this.pattern = pattern;
        this.otherPattern = otherPattern;
    }

    @HasResult
    public static class NormalPart extends PartBuilderEvent
    {
        public NormalPart(ItemStack material, ItemStack pattern, ItemStack otherPattern)
        {
            super(material, pattern, otherPattern);
        }

        /**
         * Fires before other processing is done
         * 
         * Result is significant: DEFAULT: Allows part to be crafted normally
         * ALLOW: Uses resultStack instead DENY: Stops part crafting altogether
         */

        public void overrideResult (ItemStack[] result)
        {
            resultStacks = result;
            this.setResult(Result.ALLOW);
        }

        public ItemStack[] getResultStacks ()
        {
            return resultStacks;
        }
    }
}
