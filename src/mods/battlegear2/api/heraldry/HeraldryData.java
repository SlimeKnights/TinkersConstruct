package mods.battlegear2.api.heraldry;

import mods.battlegear2.utils.BattlegearUtils;

import javax.management.Query;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HeraldryData {



    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
    public static final int MAX_CRESTS = 6;

    private byte pattern;
    private int[] patternColours;
    private byte[] extraData;

    private static final int extraDataSize = 6;
    public static final HeraldryData defaultData =
            new HeraldryData((byte)0, Color.YELLOW.getRGB(), Color.BLUE.getRGB(), Color.BLACK.getRGB(), new ArrayList<Crest>(), new byte[extraDataSize]);


    private byte[] byteArray = null;
    private List<Crest> crests;

    public HeraldryData(byte pattern, int pattern_col_1, int pattern_col_2, int pattern_col_3, List<Crest> crests, byte[] extraData) {
        this.pattern = pattern;
        this.patternColours = new int[]{pattern_col_1, pattern_col_2, pattern_col_3};
        this.crests = crests;
        this.extraData = extraData;
    }

    public HeraldryData(byte[] crestData){
        DataInputStream input = null;

        try{
            input = new DataInputStream(new ByteArrayInputStream(crestData));

            pattern = input.readByte();
            patternColours = new int[]{input.readInt(), input.readInt(), input.readInt()};
            byte crestCount = input.readByte();
            crests = new ArrayList<Crest>(crestCount);
            for(int i = 0; i < crestCount; i++){
                byte[] bytes = new byte[Crest.dataSize];
                input.read(bytes);
                crests.add(new Crest(bytes));
            }
            extraData = new byte[extraDataSize];
            for(int i = 0; i < extraDataSize; i++){
                extraData[i] = input.readByte();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(input != null){
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HeraldryData getDefault() {
        return defaultData;
    }


    public byte[] getByteArray(){
        if(byteArray != null){
            return byteArray;
        }else{
            DataOutputStream output = null;

            ByteArrayOutputStream bos = null;

            try{
                bos = new ByteArrayOutputStream();
                output = new DataOutputStream(bos);

                output.writeByte(pattern);
                output.writeInt(patternColours[0]);
                output.writeInt(patternColours[1]);
                output.writeInt(patternColours[2]);
                output.writeByte(crests.size());
                for(Crest c: crests){
                    output.write(c.getByteArray());
                }
                output.write(extraData);

                byteArray = bos.toByteArray();

                return byteArray;
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                try{
                    if(output != null){
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public List<Crest> getCrests() {
        return crests;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    public byte getPattern() {
        return pattern;
    }

    public int getColour(int index) {
        return patternColours[index];
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

	public void setColour(int i, int rgb) {
		patternColours[i] = rgb;
		byteArray = null;
		
	}

	public void setPattern(int pattern) {
		this.pattern = (byte)pattern;
		byteArray = null;
	}
	
	
	public HeraldryData clone(){
		return new HeraldryData(pattern, patternColours[0], patternColours[1], patternColours[2], crests, extraData);
	}
}
