package slimeknights.tconstruct.debug;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.List;

import scala.Int;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.item.BroadSword;
import slimeknights.tconstruct.tools.item.Hatchet;
import slimeknights.tconstruct.tools.item.Pickaxe;

public class DumpMaterialTest extends CommandBase {
  public static String path = "./dumps/";
  public Material baseMaterial;

  public DumpMaterialTest() {
    baseMaterial = new Material("Baseline", EnumChatFormatting.WHITE);
    baseMaterial.addStats(new ToolMaterialStats(1000, 100, 100, 1, 1, 1));
  }

  @Override
  public String getCommandName() {
    return "dumpMaterialTest";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    printTool(new Pickaxe());
    printTool(new Hatchet());
    printTool(new BroadSword());
  }

  private void printTool(ToolCore tool) throws CommandException {
    File file = new File("dump_" + tool.getClass().getSimpleName() + ".html");
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(file);
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      throw new CommandException(e.getMessage());
    }

    DecimalFormat df = new DecimalFormat("#.00");

    List<String> header = Lists.newArrayList();
    header.add("");
    List<List<String>> tableDur = Lists.newArrayList();
    List<List<String>> tableSpeed = Lists.newArrayList();
    List<List<String>> tableAttack = Lists.newArrayList();

    for(Material mat1 : TinkerRegistry.getAllMaterials()) {
      if(!mat1.hasStats(ToolMaterialStats.TYPE))
        continue;
      header.add(mat1.getIdentifier());

      List<String> dur = Lists.newArrayList();
      tableDur.add(dur);

      List<String> speed = Lists.newArrayList();
      tableSpeed.add(speed);

      List<String> att = Lists.newArrayList();
      tableAttack.add(att);
      for(Material mat2 : TinkerRegistry.getAllMaterials()) {
        if(!mat2.hasStats(ToolMaterialStats.TYPE))
          continue;
        ItemStack stack = tool.buildItem(ImmutableList.of(mat1, baseMaterial, mat2));
        int d = ToolHelper.getDurability(stack);
        String s = String.format("<td bgcolor=\"%s\">%s</td>", Integer
            .toHexString(floatToCol((float) d / 1000f)), String.valueOf(d));
        dur.add(s);
        float sp = ToolHelper.getMiningSpeed(stack);
        s = String.format("<td bgcolor=\"%s\">%s</td>", Integer.toHexString(floatToCol(sp/100f)), df.format(sp));
        speed.add(s);
        float at = ToolHelper.getAttack(stack) * tool.damagePotential();
        s = String.format("<td bgcolor=\"%s\">%s</td>", Integer.toHexString(floatToCol(at/100f)), df.format(at));
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
    sb.append(array2HTML(header, tableDur));
    sb.append("<hr>");
    header.set(0, "Speed");
    sb.append(array2HTML(header, tableSpeed));
    sb.append("<hr>");
    header.set(0, "Attack");
    sb.append(array2HTML(header, tableAttack));
    sb.append("</body>");
    sb.append("</html>");

    pw.print(sb.toString());
    pw.close();
  }

  private int floatToCol(float f) {
    return Color.HSBtoRGB(f/3f, 0.65f, 0.8f) & 0xffffff;
  }

  public static String array2HTML(List header, List array){
    StringBuilder html = new StringBuilder(
        "<table border=\"1\">");
    for(Object elem : header){
      html.append("<th>" + elem.toString() + "</th>");
    }
    for(int i = 0; i < array.size(); i++) {
      List<Object> row = (List<Object>)array.get(i);
      html.append("<tr>");
      html.append("<td>" + header.get(i+1) + "</td>");
      for(Object elem : row){
        html.append(elem.toString());
        //html.append("<td>" + elem.toString() + "</td>");
      }
      html.append("</tr>");
    }
    html.append("</table>");
    return html.toString();
  }
}
