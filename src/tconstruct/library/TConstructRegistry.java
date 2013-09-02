package tconstruct.library;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.ArrowMaterial;
import tconstruct.library.tools.BowMaterial;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.library.tools.CustomMaterial;
import tconstruct.library.tools.FletchingMaterial;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.TabTools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** A registry to store any relevant API work
 * 
 * @author mDiyo
 */

public class TConstructRegistry
{
    public static TConstructRegistry instance = new TConstructRegistry();

    /* Creative tabs */
    public static TabTools toolTab;
    public static TabTools materialTab;
    public static TabTools blockTab;

    /* Items */

    /** A directory of crafting items and tools used by the mod.
     * 
     * Tools:
     * pickaxe, shovel, hatchet, broadsword, longsword, rapier, dagger, cutlass
     * frypan, battlesign, mattock, chisel
     * lumberaxe, cleaver, scythe, excavator, hammer, battleaxe
     * 
     * Patterns:
     * blankPattern, woodPattern, metalPattern
     * 
     * Tool crafting parts:
     * toolRod, toolShard, binding, toughBinding, toughRod, heavyPlate
     * pickaxeHead, shovelhead, hatchetHead, swordBlade, wideguard, handGuard, crossbar, knifeBlade,
     * fullGuard, frypanHead, signHead, chiselHead
     * scytheBlade, broadAxeHead, excavatorHead, largeSwordBlade, hammerHead
     * bowstring, fletching, arrowhead
     */
    public static HashMap<String, Item> itemDirectory = new HashMap<String, Item>();

    /** Adds an item to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemToDirectory (String name, Item itemstack)
    {
        Item add = itemDirectory.get(name);
        if (add != null)
            System.out.println("[TCon API] " + name + " is already present in the Item directory");

        itemDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static Item getItem (String name)
    {
        Item ret = itemDirectory.get(name);
        if (ret == null)
            System.out.println("[TCon API] Could not find " + name + " in the Item directory");

        return ret;
    }

    /** A directory of ItemStacks. Contains mostly crafting items
     * 
     * Materials:
     * paperStack, greenSlimeCrystal, blueSlimeCrystal, searedBrick, mossBall, lavaCrystal, necroticBone, silkyCloth, silkyJewel
     * ingotCobalt, ingotArdite, ingotManyullyn, ingotCopper, ingotTin, ingotAluminum, rawAluminum,
     * ingotBronze, ingotAluminumBrass, ingotAlumite, ingotSteel, ingotObsidian
     * nuggetIron, nuggetCopper, nuggetTin, nuggetAluminum, nuggetSilver, nuggetAluminumBrass
     * oreberryIron, oreberryGold, oreberryCopper, oreberryTin, oreberryTin, oreberrySilver, 
     * diamondApple, blueSlimeFood, canisterEmpty, miniRedHeart, canisterRedHeart
     * 
     * Patterns - These have a suffix of Pattern or Cast. ex: hatchetHeadPattern
     * ingot, toolRod, pickaxeHead, shovelHead, hatchetHead, swordBlade, wideGuard, handGuard, crossbar, binding, frypanHead, signHead, 
     * knifeBlade, chiselHead, toughRod, toughBinding, largePlate, broadAxeHead, scytheHead, excavatorHead, largeBlade, hammerHead, fullGuard, bowstring
     */
    static HashMap<String, ItemStack> itemstackDirectory = new HashMap<String, ItemStack>();

    /** Adds an itemstack to the directory
     * 
     * @param name Associates the name with the stack
     * @param itemstack The stack to add to the directory
     */

    public static void addItemStackToDirectory (String name, ItemStack itemstack)
    {
        ItemStack add = itemstackDirectory.get(name);
        if (add != null)
            System.out.println("[TCon API] " + name + " is already present in the ItemStack directory");

        itemstackDirectory.put(name, itemstack);
    }

    /** Retrieves an itemstack from the directory
     * 
     * @param name The name of the item to get
     * @return Item associated with the name, or null if not present.
     */

    public static ItemStack getItemStack (String name)
    {
        ItemStack ret = itemstackDirectory.get(name);
        if (ret == null)
            System.out.println("[TCon API] Could not find " + name + " in the ItemStack directory");

        return ret;
    }

    public static ArrayList<ToolCore> tools = new ArrayList<ToolCore>(20);

    //Parts

    /** List: Item ID, metadata, material ID
     *  ItemStack: Output. Ex: Cactus Binding
     */
    public static HashMap<List, ItemStack> patternPartMapping = new HashMap<List, ItemStack>();

    /** Maps an item and a material ID to an output part
     * 
     * @param patternID ID to check against
     * @param patternMeta Metadata to check against
     * @param materialID Material that goes with the item
     * @param output The resulting part
     */
    public static void addPartMapping (int patternID, int patternMeta, int materialID, ItemStack output)
    {
        patternPartMapping.put(Arrays.asList(patternID, patternMeta, materialID), output);
    }

    public static ItemStack getPartMapping (int itemID, int metadata, int materialID)
    {
        ItemStack stack = patternPartMapping.get(Arrays.asList(itemID, metadata, materialID));
        if (stack != null)
            return stack.copy();
        return null;
    }

    //Tools

    /** Internal tool mapping, used for adding textures
     * 
     * @param tool
     */

    public static void addToolMapping (ToolCore tool)
    {
        tools.add(tool);
    }

    /** Internal tool mapping, used for adding textures
     * 
     * @return List of tools
     */

    public static ArrayList<ToolCore> getToolMapping ()
    {
        return tools;
    }

    /** Registers a tool to its crafting parts.
     * If an output is registered multiple times the parts are added to the recipe's input list
     * Valid part amounts are 1, 2, and 4. Part counts of 1 and 2 assume a Tool Rod as a handle.
     * Null items are valid as placeholders. ex: Hatchet
     * 
     * @see ToolBuidler
     * @param output The ToolCore to craft
     * @param parts Pieces to make the tool with
     */
    public static void addToolRecipe (ToolCore output, Item... parts)
    {
        ToolBuilder tb = ToolBuilder.instance;
        if (parts.length < 1 || parts.length > 4 || parts.length == 3)
            System.out.println("[TCon API] Wrong amount of items to craft into a tool");

        tb.addToolRecipe(output, parts);
    }

    //Materials
    public static HashMap<Integer, ToolMaterial> toolMaterials = new HashMap<Integer, ToolMaterial>(40);
    public static HashMap<String, ToolMaterial> toolMaterialStrings = new HashMap<String, ToolMaterial>(40);

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param materialName Unique name for data lookup purposes
     * @param harvestLevel The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2: Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability Base durability of the tool, affects tool heads.
     * @param miningspeed Base mining speed, divided by 100 in use
     * @param attack Base attack
     * @param handleModifier Durability multiplier on the tool
     * @param reinforced Reinforced level
     * @param stonebound Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */

    public static void addToolMaterial (int materialID, String materialName, int harvestLevel, int durability, int miningspeed, int attack, float handleModifier, int reinforced, float stonebound,
            String style, String ability)
    {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            mat = new ToolMaterial(materialName, harvestLevel, durability, miningspeed, attack, handleModifier, reinforced, stonebound, style, ability);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param materialName Unique name for data lookup purposes
     * @param displayName Prefix for creative mode tools
     * @param harvestLevel The materials which the tool can harvest. Pickaxe levels - 0: Wood, 1: Stone, 2: Redstone/Diamond, 3: Obsidian, 4: Cobalt/Ardite, 5: Manyullyn
     * @param durability Base durability of the tool, affects tool heads.
     * @param miningspeed Base mining speed, divided by 100 in use
     * @param attack Base attack
     * @param handleModifier Durability multiplier on the tool
     * @param reinforced Reinforced level
     * @param stonebound Amount of Stonebound to put on the tool. Negative numbers are Spiny.
     */

    public static void addToolMaterial (int materialID, String materialName, String displayName, int harvestLevel, int durability, int miningspeed, int attack, float handleModifier, int reinforced,
            float stonebound, String style, String ability)
    {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            mat = new ToolMaterial(materialName, displayName, harvestLevel, durability, miningspeed, attack, handleModifier, reinforced, stonebound, style, ability);
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(materialName, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Adds a tool material to the registry
     * 
     * @param materialID Unique ID, stored for each part
     * @exception materialID must be unique
     * @param material Complete tool material to add. Uses the name in the material for lookup purposes.
     */

    public static void addtoolMaterial (int materialID, ToolMaterial material)
    {
        ToolMaterial mat = toolMaterials.get(materialID);
        if (mat == null)
        {
            toolMaterials.put(materialID, mat);
            toolMaterialStrings.put(material.name(), mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Material ID " + materialID + " is already occupied by " + mat.materialName);
    }

    /** Looks up a tool material by ID
     * 
     * @param key The ID to look up
     * @return Tool Material
     */

    public static ToolMaterial getMaterial (int key)
    {
        return (toolMaterials.get(key));
    }

    /** Looks up a tool material by name
     * 
     * @param key the name to look up
     * @return Tool Material
     */

    public static ToolMaterial getMaterial (String key)
    {
        return (toolMaterialStrings.get(key));
    }

    //Bow materials
    public static HashMap<Integer, BowMaterial> bowMaterials = new HashMap<Integer, BowMaterial>(40);

    public static void addBowMaterial (int materialID, int durability, int drawSpeed, float speedMax)
    {
        BowMaterial mat = bowMaterials.get(materialID);
        if (mat == null)
        {
            mat = new BowMaterial(durability, drawSpeed, speedMax);
            bowMaterials.put(materialID, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Bow Material ID " + materialID + " is already occupied");
    }

    public static boolean validBowMaterial (int materialID)
    {
        return bowMaterials.containsKey(materialID);
    }

    public static BowMaterial getBowMaterial (int materialID)
    {
        return bowMaterials.get(materialID);
    }

    public static HashMap<Integer, ArrowMaterial> arrowMaterials = new HashMap<Integer, ArrowMaterial>(40);

    public static void addArrowMaterial (int materialID, float mass, float breakChance, float accuracy)
    {
        ArrowMaterial mat = arrowMaterials.get(materialID);
        if (mat == null)
        {
            mat = new ArrowMaterial(mass, breakChance, accuracy);
            arrowMaterials.put(materialID, mat);
        }
        else
            throw new IllegalArgumentException("[TCon API] Arrow Material ID " + materialID + " is already occupied");
    }

    public static boolean validArrowMaterial (int materialID)
    {
        return arrowMaterials.containsKey(materialID);
    }

    public static ArrowMaterial getArrowMaterial (int materialID)
    {
        return arrowMaterials.get(materialID);
    }

    //Custom materials - bowstrings, fletching, etc
    public static ArrayList<CustomMaterial> customMaterials = new ArrayList<CustomMaterial>();

    public static void addCustomMaterial (CustomMaterial mat)
    {
        if (mat != null)
            customMaterials.add(mat);
    }

    public static void addBowstringMaterial (int materialID, int value, ItemStack input, ItemStack craftingMaterial, float durability, float drawSpeed, float flightSpeed)
    {
        BowstringMaterial mat = new BowstringMaterial(materialID, value, input, craftingMaterial, durability, drawSpeed, flightSpeed);
        customMaterials.add(mat);
    }

    public static void addFletchingMaterial (int materialID, int value, ItemStack input, ItemStack craftingMaterial, float accuracy, float breakChance, float mass)
    {
        FletchingMaterial mat = new FletchingMaterial(materialID, value, input, craftingMaterial, accuracy, breakChance, mass);
        customMaterials.add(mat);
    }

    public static CustomMaterial getCustomMaterial (int materialID, Class<? extends CustomMaterial> clazz)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.getClass().equals(clazz) && mat.materialID == materialID)
                return mat;
        }
        return null;
    }

    public static CustomMaterial getCustomMaterial (ItemStack input, Class<? extends CustomMaterial> clazz)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.getClass().equals(clazz) && input.isItemEqual(mat.input))
                return mat;
        }
        return null;
    }

    /*public static CustomMaterial getCustomMaterial(ItemStack input, ItemStack pattern)
    {
        for (CustomMaterial mat : customMaterials)
        {
            if (mat.matches(input, pattern))
                return mat;
        }
        return null;
    }*/

    /*public static ItemStack craftBowString(ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;
        
        for (BowstringMaterial mat : bowstringMaterials)
        {
            if (stack.isItemEqual(mat.input))
                return mat.craftingItem.copy();
        }
        return null;
    }
    
    public static BowstringMaterial getBowstringMaterial(ItemStack stack)
    {
        if (stack.stackSize < 3)
            return null;
        
        for (BowstringMaterial mat : bowstringMaterials)
        {
            if (stack.isItemEqual(mat.input))
                return mat;
        }
        return null;
    }*/

    public static LiquidCasting getTableCasting ()
    {
        return instance.tableCasting();
    }

    LiquidCasting tableCasting ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getTableCasting");
            LiquidCasting lc = (LiquidCasting) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            System.out.println("[TCon API] Could not find casting table recipes.");
            return null;
        }
    }

    public static LiquidCasting getBasinCasting ()
    {
        return instance.basinCasting();
    }

    LiquidCasting basinCasting ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getBasinCasting");
            LiquidCasting lc = (LiquidCasting) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            System.out.println("[TCon API] Could not find casting basin recipes.");
            return null;
        }
    }

    public static Detailing getChiselDetailing ()
    {
        return instance.chiselDetailing();
    }

    Detailing chiselDetailing ()
    {
        try
        {
            Class clazz = Class.forName("tconstruct.TConstruct");
            Method method = clazz.getMethod("getChiselDetailing");
            Detailing lc = (Detailing) method.invoke(this);
            return lc;
        }
        catch (Exception e)
        {
            System.out.println("[TCon API] Could not find chisel detailing recipes.");
            return null;
        }
    }

    public static ArrayList<ActiveToolMod> activeModifiers = new ArrayList<ActiveToolMod>();

    public static void registerActiveToolMod (ActiveToolMod mod)
    {
        activeModifiers.add(mod);
    }

    /* Used to determine how blocks are laid out in the drawbridge
     * 0: Metadata has to match
     * 1: Metadata has no meaning
     * 2: Should not be placed
     * 3: Has rotational metadata
     * 4: Rails
     * 5: Has rotational TileEntity data
     * 6: Custom placement logic
     */
    public static int[] drawbridgeState = new int[Block.blocksList.length];
    /** Blocks that are interchangable with each other. Ex: Still and flowing water */
    public static int[] interchangableBlockMapping = new int[Block.blocksList.length];
    /** Blocks that place items, and vice versa */
    public static int[] blockToItemMapping = new int[Item.itemsList.length];

    static void initializeDrawbridgeState ()
    {
        drawbridgeState[Block.stone.blockID] = 1;
        drawbridgeState[Block.grass.blockID] = 1;
        drawbridgeState[Block.dirt.blockID] = 1;
        drawbridgeState[Block.cobblestone.blockID] = 1;
        drawbridgeState[Block.bedrock.blockID] = 2;
        drawbridgeState[Block.waterMoving.blockID] = 1;
        drawbridgeState[Block.waterStill.blockID] = 1;
        interchangableBlockMapping[Block.waterStill.blockID] = Block.waterMoving.blockID;
        interchangableBlockMapping[Block.waterMoving.blockID] = Block.waterStill.blockID;
        drawbridgeState[Block.lavaMoving.blockID] = 1;
        drawbridgeState[Block.lavaStill.blockID] = 1;
        interchangableBlockMapping[Block.lavaStill.blockID] = Block.lavaMoving.blockID;
        interchangableBlockMapping[Block.lavaMoving.blockID] = Block.lavaStill.blockID;
        drawbridgeState[Block.sand.blockID] = 1;
        drawbridgeState[Block.gravel.blockID] = 1;
        drawbridgeState[Block.oreGold.blockID] = 1;
        drawbridgeState[Block.oreIron.blockID] = 1;
        drawbridgeState[Block.oreCoal.blockID] = 1;
        drawbridgeState[Block.sponge.blockID] = 1;
        drawbridgeState[Block.oreLapis.blockID] = 1;
        drawbridgeState[Block.blockLapis.blockID] = 1;
        drawbridgeState[Block.dispenser.blockID] = 3;
        drawbridgeState[Block.music.blockID] = 1;
        drawbridgeState[Block.bed.blockID] = 2;
        drawbridgeState[Block.railPowered.blockID] = 4;
        drawbridgeState[Block.railDetector.blockID] = 4;
        drawbridgeState[Block.pistonStickyBase.blockID] = 3;
        drawbridgeState[Block.web.blockID] = 1;
        drawbridgeState[Block.pistonBase.blockID] = 3;
        drawbridgeState[Block.pistonExtension.blockID] = 2;
        drawbridgeState[Block.plantYellow.blockID] = 1;
        drawbridgeState[Block.plantRed.blockID] = 1;
        drawbridgeState[Block.mushroomBrown.blockID] = 1;
        drawbridgeState[Block.mushroomRed.blockID] = 1;
        drawbridgeState[Block.blockGold.blockID] = 1;
        drawbridgeState[Block.blockIron.blockID] = 1;
        drawbridgeState[Block.brick.blockID] = 1;
        drawbridgeState[Block.tnt.blockID] = 1;
        drawbridgeState[Block.bookShelf.blockID] = 1;
        drawbridgeState[Block.cobblestoneMossy.blockID] = 1;
        drawbridgeState[Block.obsidian.blockID] = 1;
        drawbridgeState[Block.torchWood.blockID] = 1;
        drawbridgeState[Block.fire.blockID] = 1;
        drawbridgeState[Block.mobSpawner.blockID] = 2;
        drawbridgeState[Block.stairsWoodOak.blockID] = 3;
        drawbridgeState[Block.chest.blockID] = 5;
        drawbridgeState[Block.redstoneWire.blockID] = 1;
        blockToItemMapping[Block.redstoneWire.blockID] = Item.redstone.itemID;
        blockToItemMapping[Item.redstone.itemID] = Block.redstoneWire.blockID;
        drawbridgeState[Block.oreDiamond.blockID] = 1;
        drawbridgeState[Block.blockDiamond.blockID] = 1;
        drawbridgeState[Block.workbench.blockID] = 1;
        drawbridgeState[Block.crops.blockID] = 2;
        drawbridgeState[Block.tilledField.blockID] = 1;
        drawbridgeState[Block.furnaceIdle.blockID] = 3;
        drawbridgeState[Block.furnaceBurning.blockID] = 3;
        interchangableBlockMapping[Block.furnaceIdle.blockID] = Block.furnaceBurning.blockID;
        interchangableBlockMapping[Block.furnaceBurning.blockID] = Block.furnaceIdle.blockID;
        drawbridgeState[Block.tilledField.blockID] = 1;
        drawbridgeState[Block.signPost.blockID] = 3;
        drawbridgeState[Block.doorWood.blockID] = 2;
        drawbridgeState[Block.ladder.blockID] = 1;
        drawbridgeState[Block.rail.blockID] = 4;
        drawbridgeState[Block.stairsCobblestone.blockID] = 3;
        drawbridgeState[Block.signWall.blockID] = 3;
        drawbridgeState[Block.lever.blockID] = 3;
        drawbridgeState[Block.pressurePlateStone.blockID] = 1;
        drawbridgeState[Block.doorIron.blockID] = 2;
        drawbridgeState[Block.pressurePlatePlanks.blockID] = 1;
        drawbridgeState[Block.oreRedstone.blockID] = 1;
        drawbridgeState[Block.oreRedstoneGlowing.blockID] = 1;
        drawbridgeState[Block.torchRedstoneIdle.blockID] = 1;
        drawbridgeState[Block.torchRedstoneActive.blockID] = 1;
        drawbridgeState[Block.stoneButton.blockID] = 3;
        drawbridgeState[Block.snow.blockID] = 1;
        drawbridgeState[Block.ice.blockID] = 1;
        drawbridgeState[Block.blockSnow.blockID] = 1;
        drawbridgeState[Block.cactus.blockID] = 2;
        drawbridgeState[Block.blockClay.blockID] = 1;
        drawbridgeState[Block.reed.blockID] = 1;
        drawbridgeState[Block.jukebox.blockID] = 1;
        drawbridgeState[Block.fence.blockID] = 1;
        drawbridgeState[Block.pumpkin.blockID] = 1;
        drawbridgeState[Block.netherrack.blockID] = 1;
        drawbridgeState[Block.slowSand.blockID] = 1;
        drawbridgeState[Block.glowStone.blockID] = 1;
        drawbridgeState[Block.portal.blockID] = 2;
        drawbridgeState[Block.pumpkinLantern.blockID] = 1;
        drawbridgeState[Block.cake.blockID] = 2;
        drawbridgeState[Block.redstoneRepeaterIdle.blockID] = 3;
        drawbridgeState[Block.redstoneRepeaterActive.blockID] = 3;
        interchangableBlockMapping[Block.redstoneRepeaterIdle.blockID] = Block.redstoneRepeaterActive.blockID;
        interchangableBlockMapping[Block.redstoneRepeaterActive.blockID] = Block.redstoneRepeaterIdle.blockID;
        blockToItemMapping[Block.redstoneRepeaterIdle.blockID] = Item.redstoneRepeater.itemID;
        blockToItemMapping[Block.redstoneRepeaterActive.blockID] = Item.redstoneRepeater.itemID;
        blockToItemMapping[Item.redstoneRepeater.itemID] = Block.redstoneRepeaterIdle.blockID;
        drawbridgeState[Block.lockedChest.blockID] = 5;
        drawbridgeState[Block.trapdoor.blockID] = 3;
        drawbridgeState[Block.mushroomCapBrown.blockID] = 1;
        drawbridgeState[Block.mushroomCapRed.blockID] = 1;
        drawbridgeState[Block.fenceIron.blockID] = 1;
        drawbridgeState[Block.thinGlass.blockID] = 1;
        drawbridgeState[Block.melon.blockID] = 1;
        drawbridgeState[Block.pumpkinStem.blockID] = 2;
        drawbridgeState[Block.melonStem.blockID] = 2;
        drawbridgeState[Block.vine.blockID] = 3;
        drawbridgeState[Block.fenceGate.blockID] = 3;
        drawbridgeState[Block.stairsBrick.blockID] = 3;
        drawbridgeState[Block.stairsStoneBrick.blockID] = 3;
        drawbridgeState[Block.mycelium.blockID] = 1;
        drawbridgeState[Block.waterlily.blockID] = 1;
        drawbridgeState[Block.netherBrick.blockID] = 1;
        drawbridgeState[Block.netherFence.blockID] = 1;
        drawbridgeState[Block.netherFence.blockID] = 3;
        drawbridgeState[Block.netherStalk.blockID] = 2;
        drawbridgeState[Block.enchantmentTable.blockID] = 1;
        drawbridgeState[Block.brewingStand.blockID] = 1;
        drawbridgeState[Block.cauldron.blockID] = 1;
        drawbridgeState[Block.endPortal.blockID] = 2;
        drawbridgeState[Block.dragonEgg.blockID] = 1;
        drawbridgeState[Block.redstoneLampIdle.blockID] = 1;
        drawbridgeState[Block.redstoneLampActive.blockID] = 1;
        drawbridgeState[Block.cocoaPlant.blockID] = 2;
        drawbridgeState[Block.stairsSandStone.blockID] = 3;
        drawbridgeState[Block.oreEmerald.blockID] = 1;
        drawbridgeState[Block.enderChest.blockID] = 5;
        drawbridgeState[Block.tripWireSource.blockID] = 1;
        drawbridgeState[Block.tripWire.blockID] = 1;
        drawbridgeState[Block.blockEmerald.blockID] = 1;
        drawbridgeState[Block.stairsWoodSpruce.blockID] = 3;
        drawbridgeState[Block.stairsWoodBirch.blockID] = 3;
        drawbridgeState[Block.stairsWoodJungle.blockID] = 3;
        drawbridgeState[Block.commandBlock.blockID] = 1;
        drawbridgeState[Block.beacon.blockID] = 1;
        drawbridgeState[Block.cobblestoneWall.blockID] = 1;
        drawbridgeState[Block.flowerPot.blockID] = 1;
        drawbridgeState[Block.carrot.blockID] = 2;
        drawbridgeState[Block.potato.blockID] = 1;
        drawbridgeState[Block.woodenButton.blockID] = 3;
        drawbridgeState[Block.skull.blockID] = 2;
        drawbridgeState[Block.chestTrapped.blockID] = 5;
        drawbridgeState[Block.pressurePlateGold.blockID] = 1;
        drawbridgeState[Block.pressurePlateIron.blockID] = 1;
        drawbridgeState[Block.redstoneComparatorIdle.blockID] = 1;
        drawbridgeState[Block.redstoneComparatorActive.blockID] = 1;
        interchangableBlockMapping[Block.redstoneComparatorIdle.blockID] = Block.redstoneComparatorActive.blockID;
        interchangableBlockMapping[Block.redstoneComparatorActive.blockID] = Block.redstoneComparatorIdle.blockID;
        blockToItemMapping[Block.redstoneComparatorIdle.blockID] = Item.comparator.itemID;
        blockToItemMapping[Block.redstoneComparatorActive.blockID] = Item.comparator.itemID;
        blockToItemMapping[Item.comparator.itemID] = Block.redstoneComparatorIdle.blockID;
        drawbridgeState[Block.daylightSensor.blockID] = 1;
        drawbridgeState[Block.blockRedstone.blockID] = 1;
        drawbridgeState[Block.oreNetherQuartz.blockID] = 1;
        drawbridgeState[Block.hopperBlock.blockID] = 3;
        drawbridgeState[Block.blockNetherQuartz.blockID] = 1;
        drawbridgeState[Block.stairsNetherQuartz.blockID] = 3;
        drawbridgeState[Block.railActivator.blockID] = 4;
        drawbridgeState[Block.dropper.blockID] = 3;
        interchangableBlockMapping[Block.dirt.blockID] = Block.grass.blockID;
        interchangableBlockMapping[Block.grass.blockID] = Block.dirt.blockID;
    }

    static
    {
        initializeDrawbridgeState();
    }

    /** Default Material Index
     * 0:  Wood
     * 1:  Stone
     * 2:  Iron
     * 3:  Flint
     * 4:  Cactus
     * 5:  Bone
     * 6:  Obsidian
     * 7:  Netherrack
     * 8:  Green Slime
     * 9:  Paper
     * 10: Cobalt
     * 11: Ardite
     * 12: Manyullyn
     * 13: Copper
     * 14: Bronze
     * 15: Alumite
     * 16: Steel
     * 17: Blue Slime
     */
}
