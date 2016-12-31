package slimeknights.tconstruct.debug;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;

public class CompareVanilla extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public String getName() {
    return "compareVanilla";
  }


  @Override
  public String getUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    ImmutableList<Material> woodMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.wood, TinkerMaterials.wood);
    ImmutableList<Material> stoneMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.stone, TinkerMaterials.stone);
    ImmutableList<Material> ironMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.iron, TinkerMaterials.iron);
    ImmutableList<Material> cobaltMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.cobalt);
    ImmutableList<Material> manyMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.manyullyn, TinkerMaterials.manyullyn);
    // Pickaxe
    ItemStack wood = TinkerHarvestTools.pickaxe.buildItem(woodMaterials);
    ItemStack stone = TinkerHarvestTools.pickaxe.buildItem(stoneMaterials);
    ItemStack iron = TinkerHarvestTools.pickaxe.buildItem(ironMaterials);
    ItemStack extra = TinkerHarvestTools.pickaxe.buildItem(cobaltMaterials);

    testTools(Blocks.COBBLESTONE,
              wood, stone, iron, extra,
              new ItemStack(Items.WOODEN_PICKAXE), new ItemStack(Items.STONE_PICKAXE), new ItemStack(Items.IRON_PICKAXE),
              new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE));

    wood = TinkerHarvestTools.shovel.buildItem(woodMaterials);
    stone = TinkerHarvestTools.shovel.buildItem(stoneMaterials);
    iron = TinkerHarvestTools.shovel.buildItem(ironMaterials);
    extra = TinkerHarvestTools.shovel.buildItem(cobaltMaterials);

    testTools(Blocks.DIRT,
              wood, stone, iron, extra,
              new ItemStack(Items.WOODEN_SHOVEL), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.IRON_SHOVEL),
              new ItemStack(Items.DIAMOND_SHOVEL), new ItemStack(Items.GOLDEN_SHOVEL));

    wood = TinkerHarvestTools.hatchet.buildItem(woodMaterials);
    stone = TinkerHarvestTools.hatchet.buildItem(stoneMaterials);
    iron = TinkerHarvestTools.hatchet.buildItem(ironMaterials);
    extra = TinkerHarvestTools.hatchet.buildItem(cobaltMaterials);

    testTools(Blocks.LOG,
              wood, stone, iron, extra,
              new ItemStack(Items.WOODEN_AXE), new ItemStack(Items.STONE_AXE), new ItemStack(Items.IRON_AXE),
              new ItemStack(Items.DIAMOND_AXE), new ItemStack(Items.GOLDEN_AXE));

    wood = TinkerMeleeWeapons.broadSword.buildItem(woodMaterials);
    stone = TinkerMeleeWeapons.broadSword.buildItem(stoneMaterials);
    iron = TinkerMeleeWeapons.broadSword.buildItem(ironMaterials);
    extra = TinkerMeleeWeapons.broadSword.buildItem(manyMaterials);

    testTools(Blocks.MELON_BLOCK,
              wood, stone, iron, extra,
              new ItemStack(Items.WOODEN_SWORD), new ItemStack(Items.STONE_SWORD), new ItemStack(Items.IRON_SWORD),
              new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.GOLDEN_SWORD));
  }

  protected void testTools(Block block, ItemStack wood, ItemStack stone, ItemStack iron, ItemStack extra1, ItemStack vanillaWood, ItemStack vanillaStone, ItemStack vanillaIron, ItemStack vanillaDiamond, ItemStack vanillaGold) {
    // setup output
    File file = new File("test/" + wood.getItem().getClass().getSimpleName() + ".html");
    PrintWriter pw;
    try {
      if(!file.exists()) {
        file.createNewFile();
      }
      pw = new PrintWriter(file);
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      return;
    } catch(IOException e) {
      e.printStackTrace();
      return;
    }

    pw.println("<html>");
    pw.println("<head>");
    pw.println("</head>");
    pw.println("<body>");
    pw.println("<table border=\"1\">");

    // perform the tests for each material
    pw.println(genSection("Wood", "#806517"));
    performToolTests(pw, block, wood, vanillaWood);
    pw.println(genSection("Stone", "#837E7C"));
    performToolTests(pw, block, stone, vanillaStone);
    pw.println(genSection("Iron", "#CECECE"));
    performToolTests(pw, block, iron, vanillaIron);
    pw.println(genSection("Diamond", "#5CB3FF"));
    performToolTests(pw, block, extra1, vanillaDiamond);
    pw.println(genSection("Gold", "#EAC117"));
    performToolTests(pw, block, extra1, vanillaGold);


    // finish output
    pw.println("</table>");
    pw.println("</body>");
    pw.println("<html>");
    pw.close();
  }

  protected void performToolTests(PrintWriter pw, Block block, ItemStack tinker, ItemStack vanilla) {
    //PrintWriter pw = new PrintWriter(out);
    // first header
    pw.println(genHeader("", tinker.getDisplayName(), vanilla.getDisplayName()));

    // Unmodified
    pw.println(genSection("Unmodified", ""));
    pw.println(testTool(block, tinker, vanilla));

    // Redstone/Efficiency
    pw.println(genSection("Haste/Efficiency V", ""));
    ItemStack tinkerModified = applyModifier(TinkerModifiers.modHaste, tinker);
    ItemStack vanillaModified = applyEnchantment(Enchantments.EFFICIENCY, vanilla);
    pw.println(testToolSpeed(block, tinkerModified, vanillaModified));

    // Quartz/Sharpness
    pw.println(genSection("Sharpness V", ""));
    tinkerModified = applyModifier(TinkerModifiers.modSharpness, tinker);
    vanillaModified = applyEnchantment(Enchantments.SHARPNESS, vanilla);
    pw.println(testToolAttack(tinkerModified, vanillaModified));
  }

  protected ItemStack applyModifier(IModifier modifier, ItemStack tool) {
    tool = tool.copy();
    try {
      while(modifier.canApply(tool, tool)) {
        modifier.apply(tool);
      }
    } catch(TinkerGuiException e) {
      // gui only
    }

    try {
      ToolBuilder.rebuildTool(tool.getTagCompound(), (TinkersItem) tool.getItem());
    } catch(TinkerGuiException e) {
      // no need to do anything, NBT is still correct
    }

    return tool;
  }

  protected ItemStack applyEnchantment(Enchantment enchantment, ItemStack tool) {
    tool = tool.copy();
    NBTTagCompound tag = new NBTTagCompound();
    for(int i = 0; i < enchantment.getMaxLevel(); i++) {
      ToolBuilder.addEnchantment(tag, enchantment);
    }
    tool.setTagCompound(tag);
    return tool;
  }

  protected String testTool(Block block, ItemStack tinker, ItemStack vanilla) {
    return testToolDurability(tinker, vanilla) +
           testToolSpeed(block, tinker, vanilla) +
           testToolAttack(tinker, vanilla);
  }

  protected String testToolDurability(ItemStack tinker, ItemStack vanilla) {
    int durability1 = tinker.getMaxDamage();
    int durability2 = vanilla.getMaxDamage();

    return genRow("Durability", durability1, durability2);
  }

  protected String testToolSpeed(Block block, ItemStack tinker, ItemStack vanilla) {
    IBlockState state = block.getDefaultState();

    float speed1 = tinker.getItem().getStrVsBlock(tinker, state);
    float speed2 = vanilla.getItem().getStrVsBlock(vanilla, state);
    int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, vanilla);
    if(efficiencyLevel > 0) {
      speed2 += efficiencyLevel * efficiencyLevel + 1;
    }

    return genRow("Speed", speed1, speed2);
  }

  protected String testToolAttack(ItemStack tinker, ItemStack vanilla) {
    float attack1 = ToolHelper.getActualDamage(tinker, Minecraft.getMinecraft().player);
    float attack2 = 1f;
    for(AttributeModifier mod : vanilla.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, vanilla).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
      attack2 += mod.getAmount();
    }

    // enchantment
    attack2 += EnchantmentHelper.getModifierForCreature(vanilla, EnumCreatureAttribute.UNDEFINED);

    return genRow("Attack", attack1, attack2);
  }

  private String genHeader(String desc, Object v1, Object v2) {
    return "<tr><th>" +
           desc +
           "</th><th>" +
           v1 +
           "</th><th>" +
           v2 +
           "</th></tr>";
  }

  private String genSection(String desc, String color) {
    return "<tr><td colspan=\"3\" align=\"center\" bgcolor=\"" + color + "\">" +
           desc +
           "</td></tr>";
  }

  private String genRow(String desc, Number v1, Number v2) {
    Number max = v1.floatValue() > v2.floatValue() ? v1 : v2;
    String c1 = Integer.toHexString(floatToCol(v1.floatValue() / max.floatValue()));
    String c2 = Integer.toHexString(floatToCol(v2.floatValue() / max.floatValue()));

    return "<tr><td bgcolor=\"lightgray\">" +
           desc +
           "</td><td bgcolor=\"" + c1 + "\">" +
           v1 +
           "</td><td bgcolor=\"" + c2 + "\">" +
           v2 +
           "</td></tr>";
  }

  private int floatToCol(float f) {
    return Color.HSBtoRGB(f / 3f, 0.65f, 0.8f) & 0xffffff;
  }
}
