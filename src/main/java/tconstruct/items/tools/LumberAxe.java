package tconstruct.items.tools;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.*;
import gnu.trove.set.hash.THashSet;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import mantle.player.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import tconstruct.library.*;
import tconstruct.library.tools.*;
import tconstruct.tools.TinkerTools;

public class LumberAxe extends AOEHarvestTool {
    public LumberAxe() {
        super(0, 1, 1);
        this.setUnlocalizedName("InfiTool.LumberAxe");
    }

    @Override
    protected Material[] getEffectiveMaterials() {
        return materials;
    }

    @Override
    protected String getHarvestType() {
        return "axe";
    }

    @Override
    public float getRepairCost() {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier() {
        return 2.5f;
    }

    @Override
    public boolean onBlockDestroyed(
            ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player) {
        if (block != null && block.getMaterial() == Material.leaves) return false;

        return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
    }

    static Material[] materials = {Material.wood, Material.vine, Material.circuits, Material.cactus, Material.gourd};

    /* Lumber axe specific */

    /*
     * @Override public void onUpdate (ItemStack stack, World world, Entity
     * entity, int par4, boolean par5) { super.onUpdate(stack, world, entity,
     * par4, par5); if (entity instanceof EntityPlayer) { EntityPlayer player =
     * (EntityPlayer) entity; ItemStack equipped =
     * player.getCurrentEquippedItem(); if (equipped == stack) {
     * player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1, 1)); }
     * } }
     */

    @Override
    public float breakSpeedModifier() {
        return 0.4f;
    }

    @Override
    public float stoneboundModifier() {
        return 216f;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        if (!stack.hasTagCompound() || player.isSneaking()) return super.onBlockStartBreak(stack, x, y, z, player);

        World world = player.worldObj;
        final Block wood = world.getBlock(x, y, z);

        if (wood == null) return super.onBlockStartBreak(stack, x, y, z, player);

        if (wood.isWood(world, x, y, z) || wood.getMaterial() == Material.sponge)
            if (detectTree(world, x, y, z)) {
                TreeChopTask chopper =
                        new TreeChopTask((AOEHarvestTool) this, stack, new ChunkPosition(x, y, z), player, 128);
                try {
                    FMLCommonHandler.instance().bus().register(chopper);
                } catch (LinkageError l) {
                    PlayerUtils.sendChatMessage(player, "Well, that's embarrassing, you missed!");
                    FMLLog.warning("Unable to spawn TreeChopTask due to LinkageError");
                }
                // custom block breaking code, don't call vanilla code
                return true;
            }

        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    public static boolean detectTree(World world, int pX, int pY, int pZ) {
        ChunkPosition pos = null;
        Stack<ChunkPosition> candidates = new Stack<>();
        candidates.add(new ChunkPosition(pX, pY, pZ));

        while (!candidates.isEmpty()) {
            ChunkPosition candidate = candidates.pop();
            int curX = candidate.chunkPosX, curY = candidate.chunkPosY, curZ = candidate.chunkPosZ;

            Block block = world.getBlock(curX, curY, curZ);
            if ((pos == null || candidate.chunkPosY > pos.chunkPosY) && block.isWood(world, curX, curY, curZ)) {
                pos = new ChunkPosition(curX, candidate.chunkPosY + 1, curZ);
                // go up
                while (world.getBlock(curX, pos.chunkPosY, curZ).isWood(world, curX, pos.chunkPosY, curZ)) {
                    pos = new ChunkPosition(curX, pos.chunkPosY + 1, curZ);
                }
                // check if we still have a way diagonally up
                candidates.add(new ChunkPosition(curX + 1, pos.chunkPosY + 1, curZ));
                candidates.add(new ChunkPosition(curX, pos.chunkPosY + 1, curZ + 1));
                candidates.add(new ChunkPosition(curX - 1, pos.chunkPosY + 1, curZ));
                candidates.add(new ChunkPosition(curX, pos.chunkPosY + 1, curZ - 1));
            }
        }

        // not even one match, so there were no logs.
        if (pos == null) {
            return false;
        }

        // check if there were enough leaves around the last position
        // pos now contains the block above the topmost log
        // we want at least 5 leaves in the surrounding 26 blocks
        int d = 3;
        int leaves = 0;
        for (int offX = 0; offX < d; offX++) {
            for (int offY = 0; offY < d; offY++) {
                for (int offZ = 0; offZ < d; offZ++) {
                    int xPos = pos.chunkPosX - 1 + offX,
                            yPos = pos.chunkPosY - 1 + offY,
                            zPos = pos.chunkPosZ - 1 + offZ;
                    Block leaf = world.getBlock(xPos, yPos, zPos);
                    if (leaf != null && leaf.isLeaves(world, xPos, yPos, zPos)) {
                        if (++leaves >= 5) {
                            return true;
                        }
                    }
                }
            }
        }

        // not enough leaves. sorreh
        return false;
    }

    public static class TreeChopTask {

        public final World world;
        public final EntityPlayer player;
        public final AOEHarvestTool tool;
        public final ItemStack stack;
        public final int blocksPerTick;

        public Queue<ChunkPosition> blocks = Lists.newLinkedList();
        public Set<ChunkPosition> visited = new THashSet<>();

        public TreeChopTask(
                AOEHarvestTool tool, ItemStack stack, ChunkPosition start, EntityPlayer player, int blocksPerTick) {
            this.world = player.getEntityWorld();
            this.player = player;
            this.tool = tool;
            this.stack = stack;
            this.blocksPerTick = blocksPerTick;

            this.blocks.add(start);
        }

        private void queueCoordinate(int x, int y, int z) {
            ChunkPosition pos = new ChunkPosition(x, y, z);
            if (!visited.contains(pos)) {
                blocks.add(pos);
            }
        }

        @SubscribeEvent
        public void onWorldTick(TickEvent.WorldTickEvent event) {
            if (event.side.isClient()) {
                finish();
                return;
            }
            // only if same dimension
            if (event.world.provider.dimensionId != world.provider.dimensionId) {
                return;
            }

            // setup
            int left = blocksPerTick;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

            // continue running
            ChunkPosition pos;
            while (left > 0) {
                // completely done or can't do our job anymore?!
                if (blocks.isEmpty() || tags.getBoolean("Broken")) {
                    finish();
                    return;
                }

                pos = blocks.remove();
                if (!visited.add(pos)) {
                    continue;
                }
                int x = pos.chunkPosX, y = pos.chunkPosY, z = pos.chunkPosZ;

                Block block = world.getBlock(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                // can we harvest the block and is effective?
                if (!block.isWood(world, x, y, z) || !tool.isEffective(block, meta)) {
                    continue;
                }

                // save its neighbors
                queueCoordinate(x + 1, y, z);
                queueCoordinate(x, y, z + 1);
                queueCoordinate(x - 1, y, z);
                queueCoordinate(x, y, z - 1);

                // also add the layer above.. stupid acacia trees
                for (int offX = 0; offX < 3; offX++) {
                    for (int offZ = 0; offZ < 3; offZ++) {
                        queueCoordinate(x - 1 + offX, y + 1, z - 1 + offZ);
                    }
                }

                // break it, wooo!
                boolean cancelHarvest = false;
                for (ActiveToolMod mod : TConstructRegistry.activeModifiers) {
                    if (mod.beforeBlockBreak(tool, stack, x, y, z, player)) {
                        cancelHarvest = true;
                    }
                }
                if (!cancelHarvest) tool.breakExtraBlock(player.worldObj, x, y, z, 0, player, x, y, z);

                left--;
            }
        }

        private void finish() {
            // goodbye cruel world
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }

    @Override
    public Item getHeadItem() {
        return TinkerTools.broadAxeHead;
    }

    @Override
    public Item getHandleItem() {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem() {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem() {
        return TinkerTools.toughBinding;
    }

    @Override
    public int getPartAmount() {
        return 4;
    }

    @Override
    public String getIconSuffix(int partType) {
        switch (partType) {
            case 0:
                return "_lumberaxe_head";
            case 1:
                return "_lumberaxe_head_broken";
            case 2:
                return "_lumberaxe_handle";
            case 3:
                return "_lumberaxe_shield";
            case 4:
                return "_lumberaxe_binding";
            default:
                return "";
        }
    }

    @Override
    public String getEffectSuffix() {
        return "_lumberaxe_effect";
    }

    @Override
    public String getDefaultFolder() {
        return "lumberaxe";
    }

    @Override
    public int durabilityTypeAccessory() {
        return 2;
    }

    @Override
    public int durabilityTypeExtra() {
        return 1;
    }
}
