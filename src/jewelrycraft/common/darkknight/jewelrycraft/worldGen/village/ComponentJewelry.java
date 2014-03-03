package common.darkknight.jewelrycraft.worldGen.village;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import common.darkknight.jewelrycraft.block.BlockList;
import common.darkknight.jewelrycraft.item.ItemList;
import common.darkknight.jewelrycraft.item.ItemMolds;
import common.darkknight.jewelrycraft.tileentity.TileEntityDisplayer;
import common.darkknight.jewelrycraft.tileentity.TileEntityMolder;
import common.darkknight.jewelrycraft.tileentity.TileEntitySmelter;
import common.darkknight.jewelrycraft.util.JewelryNBT;
import common.darkknight.jewelrycraft.util.JewelrycraftUtil;

public class ComponentJewelry extends ComponentVillage
{
    private int averageGroundLevel = -1;

    public ComponentJewelry()
    {
    }

    public ComponentJewelry(ComponentVillageStartPiece par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    @SuppressWarnings("rawtypes")
    public static ComponentJewelry buildComponent (ComponentVillageStartPiece villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
    {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 11, 5, 12, p4);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentJewelry(villagePiece, p5, random,
                structureboundingbox, p4) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts (World world, Random random, StructureBoundingBox sbb)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0);
        }

        /**
         * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
         * maxZ, int placeBlockId, int replaceBlockId, boolean alwaysreplace)
         */
        this.fillWithBlocks(world, sbb, 0, 0, 6, 10, 5, 11, 0, 0, false);
        this.fillWithBlocks(world, sbb, 2, 0, 0, 8, 5, 5, 0, 0, false);
        //Pillars
        this.fillWithBlocks(world, sbb, 2, 0, 0, 2, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(world, sbb, 2, 0, 3, 2, 3, 3, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(world, sbb, 8, 0, 0, 8, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(world, sbb, 8, 0, 3, 8, 3, 3, Block.wood.blockID, Block.wood.blockID, false);

        //Walls
        this.fillWithBlocks(world, sbb, 2, 0, 1, 2, 3, 2, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(world, sbb, 2, 0, 4, 2, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(world, sbb, 8, 0, 1, 8, 3, 2, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(world, sbb, 8, 0, 4, 8, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(world, sbb, 3, 0, 0, 7, 3, 0, Block.planks.blockID, Block.planks.blockID, false);

        this.fillWithBlocks(world, sbb, 0, 0, 6, 10, 3, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(world, sbb, 0, 0, 11, 10, 3, 11, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(world, sbb, 0, 0, 6, 0, 3, 11, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(world, sbb, 10, 0, 6, 10, 3, 11, Block.cobblestone.blockID, Block.cobblestone.blockID, false);

        //Roof
        for(int i = 3; i <= 7; i++)
            for(int j = 1; j <= 5; j++)
                this.placeBlockAtCurrentPosition(world, Block.woodSingleSlab.blockID, 2, i, 4, j, sbb);

        for(int i = 3; i <= 7; i++)
            for(int j = 6; j <= 6; j++)
                this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID, 0, i, 4, j, sbb);

        for(int i = 1; i <= 9; i++)
            for(int j = 7; j <= 10; j++)
                this.placeBlockAtCurrentPosition(world, Block.stoneSingleSlab.blockID, 3, i, 4, j, sbb);

        for(int i = 2; i <= 8; i++)
            this.placeBlockAtCurrentPosition(world, Block.woodDoubleSlab.blockID, 2, i, 4, 0, sbb);

        for(int i = 1; i <= 5; i++){
            this.placeBlockAtCurrentPosition(world, Block.woodDoubleSlab.blockID, 2, 2, 4, i, sbb);
            this.placeBlockAtCurrentPosition(world, Block.woodDoubleSlab.blockID, 2, 8, 4, i, sbb);
        }

        for(int i = 0; i <= 2; i++){
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, i, 4, 6, sbb);
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, i + 8, 4, 6, sbb);
        }

        for(int i = 7; i <= 11; i++){
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, 0, 4, i, sbb);
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, 10, 4, i, sbb);
        }

        for(int i = 0; i <= 10; i++)
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, i, 4, 11, sbb);

        //Base
        for(int i = 2; i <= 8; i++)
            for(int j = 0; j <= 5; j++)
                this.placeBlockAtCurrentPosition(world, Block.planks.blockID, 1, i, 0, j, sbb);
        this.fillWithBlocks(world, sbb, 0, 0, 6, 10, 0, 11, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false);

        for(int i = 6; i <= 10; i++)
            this.placeBlockAtCurrentPosition(world, Block.stoneDoubleSlab.blockID, 0, 5, 0, i, sbb);

        for(int i = 7; i <= 10; i++){
            this.placeBlockAtCurrentPosition(world, Block.stoneBrick.blockID, 3, 1, 0, i, sbb);
            this.placeBlockAtCurrentPosition(world, Block.stoneBrick.blockID, 3, 9, 0, i, sbb);
        }

        //Decorations        
        this.placeDoorAtCurrentPosition(world, sbb, random, 6, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));
        this.placeDoorAtCurrentPosition(world, sbb, random, 5, 1, 6, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 3, 2, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 4, 2, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 2, 2, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 2, 2, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 2, 2, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 2, 2, 5, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 5, sbb);

        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 6, 3, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 3, 3, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 7, 3, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 5, 3, 5, sbb);

        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 5, 3, 7, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 5, 3, 10, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 1, 3, 8, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 1, 3, 9, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 9, 3, 8, sbb);
        this.placeBlockAtCurrentPosition(world, Block.torchWood.blockID, 0, 9, 3, 9, sbb);

        int bgCarpetColor = random.nextInt(16);

        for(int i = 4; i <= 7; i++)
            for(int j = 1; j <= 5; j++)
                this.placeBlockAtCurrentPosition(world, Block.carpet.blockID, bgCarpetColor, i, 1, j, sbb);

        generateChest(world, 3, 1, 1, 0, random, sbb, 2, 6);
        generateDisplayer(world, 3, 1, 2, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb);
        placeBlockAtCurrentPosition(world, BlockList.jewelCraftingTable.blockID, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, 3, 1, 3, sbb);
        generateDisplayer(world, 3, 1, 4, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb);
        generateChest(world, 3, 1, 5, 0, random, sbb, 2, 6);

        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 1, 7, sbb);
        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 2, 7, sbb);
        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 3, 7, sbb);
        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 1, 10, sbb);
        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 2, 10, sbb);
        this.placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, 0, 1, 3, 10, sbb);

        generateSmelter(world, 1, 1, 8, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb, random.nextBoolean());
        generateSmelter(world, 1, 1, 9, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb, random.nextBoolean());

        generateMolder(world, 2, 1, 8, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb, random.nextBoolean(), random.nextBoolean());
        generateMolder(world, 2, 1, 9, (coordBaseMode == 0 || coordBaseMode == 2)?1:2, random, sbb, random.nextBoolean(), random.nextBoolean());
        
        this.placeBlockAtCurrentPosition(world, Block.chest.blockID, 0, 9, 1, 7, sbb);
        this.placeBlockAtCurrentPosition(world, Block.chest.blockID, 0, 9, 1, 8, sbb);
        this.placeBlockAtCurrentPosition(world, Block.chestTrapped.blockID, 0, 9, 1, 9, sbb);
        this.placeBlockAtCurrentPosition(world, Block.chestTrapped.blockID, 0, 9, 1, 10, sbb);
        

        for (int l = 0; l < 6; ++l)
        {
            for (int i1 = 2; i1 < 9; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.fillCurrentPositionBlocksDownwards(world, Block.cobblestone.blockID, 0, i1, -1, l, sbb);
            }
        }

        for (int l = 6; l < 12; ++l)
        {
            for (int i1 = 0; i1 < 11; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.fillCurrentPositionBlocksDownwards(world, Block.cobblestone.blockID, 0, i1, -1, l, sbb);
            }
        }

        this.spawnVillagers(world, sbb, 3, 1, 3, 1);

        return true;
    }

    public void generateChest(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb, int min, int max)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        int t = random.nextInt(max - min + 1) + min;
        this.placeBlockAtCurrentPosition(world, Block.chest.blockID, metadata, i, j, k, sbb);
        TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(i1, j1, k1);
        while(chest != null && t > 0)
        {
            chest.setChestGuiName("Jeweler's Chest");
            if(random.nextBoolean()) chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), JewelrycraftUtil.modifiers.get(random.nextInt(JewelrycraftUtil.modifiers.size())));
            else chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), JewelrycraftUtil.jewel.get(random.nextInt(JewelrycraftUtil.jewel.size())));
            t--;
        }
    }

    public void generateTrappedChest(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb, int min, int max)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        int t = random.nextInt(max - min + 1) + min;
        this.placeBlockAtCurrentPosition(world, Block.chestTrapped.blockID, metadata, i, j, k, sbb);
        TileEntityChest chest = (TileEntityChest)world.getBlockTileEntity(i1, j1, k1);
        while(chest != null && t > 0)
        {
            chest.setChestGuiName("Jeweler's Chest");
            if(random.nextBoolean()) chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), JewelrycraftUtil.modifiers.get(random.nextInt(JewelrycraftUtil.modifiers.size())));
            else chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), JewelrycraftUtil.jewel.get(random.nextInt(JewelrycraftUtil.jewel.size())));
            t--;
        }
    }

    public void generateDisplayer(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        placeBlockAtCurrentPosition(world, BlockList.displayer.blockID, metadata, i, j, k, sbb);
        TileEntityDisplayer displayer = (TileEntityDisplayer)world.getBlockTileEntity(i1, j1, k1);
        if(displayer != null)
        {
            ItemStack ring = new ItemStack(ItemList.ring);
            JewelryNBT.addMetal(ring, JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size())));
            JewelryNBT.addModifier(ring, JewelrycraftUtil.modifiers.get(random.nextInt(JewelrycraftUtil.modifiers.size())));
            JewelryNBT.addJewel(ring, JewelrycraftUtil.jewel.get(random.nextInt(JewelrycraftUtil.jewel.size())));
            if(JewelryNBT.isModifierEffectType(ring)) JewelryNBT.addMode(ring, "Activated");
            if(JewelryNBT.isJewelX(ring, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(ring, new ItemStack(Item.book))) 
                JewelryNBT.addMode(ring, "Disenchant");
            displayer.object = ring;
            displayer.quantity = 1;
            displayer.hasObject = true;
        }
    }

    public void generateSmelter(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb, boolean isEmpty)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        placeBlockAtCurrentPosition(world, BlockList.smelter.blockID, metadata, i, j, k, sbb);
        TileEntitySmelter smelter = (TileEntitySmelter)world.getBlockTileEntity(i1, j1, k1);
        if(smelter != null && !isEmpty)
        {
            smelter.moltenMetal = JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size()));
            smelter.hasMoltenMetal = true;
        }
    }

    public void generateMolder(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb, boolean hasMold, boolean hasStuff)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        placeBlockAtCurrentPosition(world, BlockList.molder.blockID, metadata, i, j, k, sbb);
        TileEntityMolder molder = (TileEntityMolder)world.getBlockTileEntity(i1, j1, k1);
        if(molder != null)
        {
            if(hasMold){
                int meta = random.nextInt(ItemMolds.moldsItemNames.length + 1);
                molder.mold = new ItemStack(ItemList.molds, 1, meta);
                molder.hasMold = true;
                if(hasStuff){
                    ItemStack ring = new ItemStack(ItemList.ring);
                    JewelryNBT.addMetal(ring, JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size())));
                    if(meta == 0) molder.jewelBase = JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size())); 
                    else molder.jewelBase = ring;
                    molder.hasJewelBase = true;
                }
            }
        }
    }

    public void generateFurnace(World world, int i, int j, int k, int metadata, Random random, StructureBoundingBox sbb, int min, int max, boolean hasMetal)
    {
        int i1 = this.getXWithOffset(i, k);
        int j1 = this.getYWithOffset(j);
        int k1 = this.getZWithOffset(i, k);
        placeBlockAtCurrentPosition(world, Block.furnaceIdle.blockID, metadata, i, j, k, sbb);
        TileEntityFurnace furnace = (TileEntityFurnace)world.getBlockTileEntity(i1, j1, k1);
        if(furnace != null)
        {
//            if(random.nextBoolean()) furnace.setInventorySlotContents(1, new ItemStack(Item.coal, random.nextInt(16)));
//            if(hasMetal){
//                ItemStack metal = JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size()));
//                metal.stackSize = random.nextInt(max - min + 1) + min;
//                furnace.setInventorySlotContents(2, metal);
//            }
        }
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType (int par1)
    {
        return 3000;
    }
}