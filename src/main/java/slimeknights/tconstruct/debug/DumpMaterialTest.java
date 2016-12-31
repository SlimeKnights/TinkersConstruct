package slimeknights.tconstruct.debug;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.melee.item.BroadSword;
import slimeknights.tconstruct.tools.tools.Hatchet;
import slimeknights.tconstruct.tools.tools.Pickaxe;
import slimeknights.tconstruct.tools.tools.Shovel;

public class DumpMaterialTest extends CommandBase {

  public static String path = "./dumps/";
  public Material baseMaterial;

  public DumpMaterialTest() {
    baseMaterial = new Material("Baseline", TextFormatting.WHITE);
    baseMaterial.addStats(new HeadMaterialStats(500, 10, 10, 1));
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }


  @Override
  public String getName() {
    return "dumpMaterialTest";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    printStats();
    printTool(new Pickaxe(), baseMaterial);
    printTool(new Hatchet(), baseMaterial);
    printTool(new BroadSword(), baseMaterial);
    printTool(new Shovel(), baseMaterial);

    for(Material mat1 : TinkerRegistry.getAllMaterials()) {
      if(!mat1.hasStats(MaterialTypes.HEAD)) {
        continue;
      }
      printTool(new Pickaxe(), mat1);
      printTool(new Hatchet(), mat1);
      printTool(new BroadSword(), mat1);
      printTool(new Shovel(), mat1);
    }
  }

  private void printStats() throws CommandException {
    File file = new File("dumps/materials.html");
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(file);
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      throw new CommandException(e.getMessage());
    }


    List<String> header = Lists.newArrayList("Materials", "Durability", "Speed", "Attack", "Handle", "Extra");
    List<List<String>> rows = Lists.newArrayList();

    for(Material mat1 : TinkerRegistry.getAllMaterials()) {
      if(!mat1.hasStats(MaterialTypes.HEAD)) {
        continue;
      }

      HeadMaterialStats stats = mat1.getStats(MaterialTypes.HEAD);
      List<String> row = Lists.newArrayList();
      rows.add(row);
      row.add("<td>" + mat1.getIdentifier() + "</td>");
      /*
      row.add(String.format("<td>%d<br>%d<br>%d</td>", stats.durability, (int)(stats.durability*stats.modifier), (int)(stats.durability*stats.extraQuality)));
      row.add(String.format("<td>%.2f<br>%.2f<br>%.2f</td>", stats.miningspeed, (stats.miningspeed*stats.modifier), (stats.miningspeed*stats.extraQuality)));
      row.add(String.format("<td>%.2f<br>%.2f<br>%.2f</td>", stats.attack, (stats.attack*stats.modifier), (stats.attack*stats.extraQuality)));
      row.add(String.format("<td>%.2f</td>", stats.modifier));
      row.add(String.format("<td>%.2f</td>", stats.extraQuality));*/
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<head>");
    sb.append("Materials");
    sb.append("</head>");
    sb.append("<body>");
    sb.append(array2HTML(header, rows, false));
    sb.append("</body>");
    sb.append("</html>");

    pw.print(sb.toString());
    pw.close();
  }

  private void printTool(ToolCore tool, Material head) throws CommandException {
    File file = new File("dumps/" + tool.getClass().getSimpleName() + "_" + head.getIdentifier() + ".html");
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(file);
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      throw new CommandException(e.getMessage());
    }

    DecimalFormat df = new DecimalFormat("#.00");
    HeadMaterialStats ref = baseMaterial.getStats(MaterialTypes.HEAD);// head.getStats(ToolMaterialStats.TYPE);

    List<String> header = Lists.newArrayList();
    header.add("");
    List<List<String>> tableDur = Lists.newArrayList();
    List<List<String>> tableSpeed = Lists.newArrayList();
    List<List<String>> tableAttack = Lists.newArrayList();

    for(Material mat1 : TinkerRegistry.getAllMaterials()) {
      if(!mat1.hasStats(MaterialTypes.HEAD)) {
        continue;
      }
      header.add(mat1.getIdentifier());

      List<String> dur = Lists.newArrayList();
      tableDur.add(dur);

      List<String> speed = Lists.newArrayList();
      tableSpeed.add(speed);

      List<String> att = Lists.newArrayList();
      tableAttack.add(att);
      for(Material mat2 : TinkerRegistry.getAllMaterials()) {
        if(!mat2.hasStats(MaterialTypes.HEAD)) {
          continue;
        }
        ItemStack stack = tool.buildItem(ImmutableList.of(mat1, head, mat2));
        int d = ToolHelper.getDurabilityStat(stack);
        String s = String.format("<td bgcolor=\"%s\">%s</td>", Integer
            .toHexString(floatToCol((float) d / (float) ref.durability)), String.valueOf(d));
        dur.add(s);
        float sp = ToolHelper.getMiningSpeedStat(stack);
        s = String.format("<td bgcolor=\"%s\">%s</td>", Integer.toHexString(floatToCol(sp / ref.miningspeed)), df.format(sp));
        speed.add(s);
        float at = ToolHelper.getAttackStat(stack) * tool.damagePotential();
        s = String.format("<td bgcolor=\"%s\">%s</td>", Integer.toHexString(floatToCol(at / ref.attack)), df.format(at));
        att.add(s);
      }
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<head>");
    sb.append(tool.getClass().getSimpleName());
    sb.append("</head>");
    sb.append("<body>");
    header.set(0, "Durability");
    sb.append(array2HTML(header, tableDur, true));
    sb.append("<hr>");
    header.set(0, "Speed");
    sb.append(array2HTML(header, tableSpeed, true));
    sb.append("<hr>");
    header.set(0, "Attack");
    sb.append(array2HTML(header, tableAttack, true));
    sb.append("</body>");
    sb.append("</html>");

    pw.print(sb.toString());
    pw.close();
  }

  private int floatToCol(float f) {
    return Color.HSBtoRGB(f / 3f, 0.65f, 0.8f) & 0xffffff;
  }

  public static String array2HTML(List<String> header, List<List<String>> array, boolean headerAsRowCaption) {
    StringBuilder html = new StringBuilder(
        "<table border=\"1\">");
    for(Object elem : header) {
      html.append("<th>").append(elem.toString()).append("</th>");
    }
    for(int i = 0; i < array.size(); i++) {
      List<String> row = array.get(i);
      html.append("<tr>");
      if(headerAsRowCaption) {
        html.append("<td>").append(header.get(i + 1)).append("</td>");
      }
      for(Object elem : row) {
        html.append(elem.toString());
        //html.append("<td>" + elem.toString() + "</td>");
      }
      html.append("</tr>");
    }
    html.append("</table>");
    return html.toString();
  }
}
