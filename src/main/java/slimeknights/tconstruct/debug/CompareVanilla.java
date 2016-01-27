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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;

public class CompareVanilla extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public String getCommandName() {
    return "compareVanilla";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    ImmutableList<Material> woodMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.wood, TinkerMaterials.wood);
    ImmutableList<Material> stoneMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.stone, TinkerMaterials.stone);
    ImmutableList<Material> ironMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.iron, TinkerMaterials.iron);
    ImmutableList<Material> cobaltMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.cobalt, TinkerMaterials.cobalt);
    ImmutableList<Material> manyMaterials = ImmutableList.of(TinkerMaterials.wood, TinkerMaterials.manyullyn, TinkerMaterials.manyullyn);
    // Pickaxe
    ItemStack wood =  TinkerTools.pickaxe.buildItem(woodMaterials);
    ItemStack stone = TinkerTools.pickaxe.buildItem(stoneMaterials);
    ItemStack iron =  TinkerTools.pickaxe.buildItem(ironMaterials);
    ItemStack extra = TinkerTools.pickaxe.buildItem(cobaltMaterials);

    testTools(Blocks.cobblestone,
              wood, stone, iron, extra,
              new ItemStack(Items.wooden_pickaxe), new ItemStack(Items.stone_pickaxe), new ItemStack(Items.iron_pickaxe),
              new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.golden_pickaxe));

    wood =  TinkerTools.shovel.buildItem(woodMaterials);
    stone = TinkerTools.shovel.buildItem(stoneMaterials);
    iron =  TinkerTools.shovel.buildItem(ironMaterials);
    extra = TinkerTools.shovel.buildItem(cobaltMaterials);

    testTools(Blocks.dirt,
              wood, stone, iron, extra,
              new ItemStack(Items.wooden_shovel), new ItemStack(Items.stone_shovel), new ItemStack(Items.iron_shovel),
              new ItemStack(Items.diamond_shovel), new ItemStack(Items.golden_shovel));

    wood =  TinkerTools.hatchet.buildItem(woodMaterials);
    stone = TinkerTools.hatchet.buildItem(stoneMaterials);
    iron =  TinkerTools.hatchet.buildItem(ironMaterials);
    extra = TinkerTools.hatchet.buildItem(cobaltMaterials);

    testTools(Blocks.log,
              wood, stone, iron, extra,
              new ItemStack(Items.wooden_axe), new ItemStack(Items.stone_axe), new ItemStack(Items.iron_axe),
              new ItemStack(Items.diamond_axe), new ItemStack(Items.golden_axe));

    wood =  TinkerTools.broadSword.buildItem(woodMaterials);
    stone = TinkerTools.broadSword.buildItem(stoneMaterials);
    iron =  TinkerTools.broadSword.buildItem(ironMaterials);
    extra = TinkerTools.broadSword.buildItem(manyMaterials);

    testTools(Blocks.melon_block,
              wood, stone, iron, extra,
              new ItemStack(Items.wooden_sword), new ItemStack(Items.stone_sword), new ItemStack(Items.iron_sword),
              new ItemStack(Items.diamond_sword), new ItemStack(Items.golden_sword));
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
    performToolTests(pw, block, wood,   vanillaWood);
    pw.println(genSection("Stone", "#837E7C"));
    performToolTests(pw, block, stone,  vanillaStone);
    pw.println(genSection("Iron", "#CECECE"));
    performToolTests(pw, block, iron,  vanillaIron);
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
    ItemStack tinkerModified = applyModifier(TinkerTools.modHaste, tinker);
    ItemStack vanillaModified = applyEnchantment(Enchantment.efficiency, vanilla);
    pw.println(testToolSpeed(block, tinkerModified, vanillaModified));

    // Quartz/Sharpness
    pw.println(genSection("Sharpness V", ""));
    tinkerModified = applyModifier(TinkerTools.modSharpness, tinker);
    vanillaModified = applyEnchantment(Enchantment.sharpness, vanilla);
    pw.println(testToolAttack(tinkerModified, vanillaModified));
  }

  protected ItemStack applyModifier(IModifier modifier, ItemStack tool) {
    tool = tool.copy();
    try {
      while(modifier.canApply(tool)) {
        modifier.apply(tool);
      }
    } catch(TinkerGuiException e) {
      // gui only
    }

    ToolBuilder.rebuildTool(tool.getTagCompound(), (TinkersItem) tool.getItem());

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

    float speed1 = tinker.getItem().getDigSpeed(tinker, state);
    float speed2 = vanilla.getItem().getDigSpeed(vanilla, state);
    int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, vanilla);
    if(efficiencyLevel > 0) {
      speed2 += efficiencyLevel * efficiencyLevel + 1;
    }

    return genRow("Speed", speed1, speed2);
  }

  protected String testToolAttack(ItemStack tinker, ItemStack vanilla) {
    float attack1 = ToolHelper.getActualDamage(tinker, Minecraft.getMinecraft().thePlayer);
    float attack2 = 1f;
    for(AttributeModifier mod : vanilla.getItem().getAttributeModifiers(vanilla).get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())) {
      attack2 += mod.getAmount();
    }

    // enchantment
    attack2 += EnchantmentHelper.func_152377_a(vanilla, EnumCreatureAttribute.UNDEFINED);

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
