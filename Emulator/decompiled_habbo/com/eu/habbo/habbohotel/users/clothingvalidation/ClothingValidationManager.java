/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.clothingvalidation;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.clothingvalidation.Figuredata;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataPalette;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataPaletteColor;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataSettype;
import com.eu.habbo.habbohotel.users.clothingvalidation.FiguredataSettypeSet;
import gnu.trove.TIntCollection;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClothingValidationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClothingValidationManager.class);
    public static String FIGUREDATA_URL = "";
    public static boolean VALIDATE_ON_HC_EXPIRE = false;
    public static boolean VALIDATE_ON_LOGIN = false;
    public static boolean VALIDATE_ON_CHANGE_LOOKS = false;
    public static boolean VALIDATE_ON_MIMIC = false;
    public static boolean VALIDATE_ON_MANNEQUIN = false;
    public static boolean VALIDATE_ON_FBALLGATE = false;
    private static final Figuredata FIGUREDATA = new Figuredata();

    public static void reloadFiguredata(String newUrl) {
        try {
            FIGUREDATA.parseXML(newUrl);
        }
        catch (Exception e) {
            VALIDATE_ON_HC_EXPIRE = false;
            VALIDATE_ON_LOGIN = false;
            VALIDATE_ON_CHANGE_LOOKS = false;
            VALIDATE_ON_MIMIC = false;
            VALIDATE_ON_MANNEQUIN = false;
            VALIDATE_ON_FBALLGATE = false;
            LOGGER.error("Caught exception", e);
        }
    }

    public static String validateLook(Habbo habbo) {
        return ClothingValidationManager.validateLook(habbo.getHabboInfo().getLook(), habbo.getHabboInfo().getGender().name(), habbo.getHabboStats().hasActiveClub(), habbo.getInventory().getWardrobeComponent().getClothingSets());
    }

    public static String validateLook(Habbo habbo, String look, String gender) {
        return ClothingValidationManager.validateLook(look, gender, habbo.getHabboStats().hasActiveClub(), habbo.getInventory().getWardrobeComponent().getClothingSets());
    }

    public static String validateLook(String look, String gender) {
        return ClothingValidationManager.validateLook(look, gender, false, new TIntHashSet());
    }

    public static String validateLook(String look, String gender, boolean isHC) {
        return ClothingValidationManager.validateLook(look, gender, isHC, new TIntHashSet());
    }

    public static String validateLook(String look, String gender, boolean isHC, TIntCollection ownedClothing) {
        if (ClothingValidationManager.FIGUREDATA.palettes.size() == 0 || ClothingValidationManager.FIGUREDATA.settypes.size() == 0) {
            return look;
        }
        String[] newLookParts = look.split(Pattern.quote("."));
        ArrayList lookParts = new ArrayList();
        THashMap<String, String[]> parts = new THashMap<String, String[]>();
        for (String lookpart : newLookParts) {
            String[] data2;
            FiguredataSettype settype;
            if (!lookpart.contains("-") || (settype = ClothingValidationManager.FIGUREDATA.settypes.get((data2 = lookpart.split(Pattern.quote("-")))[0])) == null) continue;
            parts.put(data2[0], data2);
        }
        ClothingValidationManager.FIGUREDATA.settypes.entrySet().stream().filter(x -> !parts.containsKey(x.getKey())).forEach(x -> {
            FiguredataSettype settype = (FiguredataSettype)x.getValue();
            if (gender.equalsIgnoreCase("M") && !isHC && !settype.mandatoryMale0) {
                return;
            }
            if (gender.equalsIgnoreCase("F") && !isHC && !settype.mandatoryFemale0) {
                return;
            }
            if (gender.equalsIgnoreCase("M") && isHC && !settype.mandatoryMale1) {
                return;
            }
            if (gender.equalsIgnoreCase("F") && isHC && !settype.mandatoryFemale1) {
                return;
            }
            parts.put((String)x.getKey(), new String[]{(String)x.getKey()});
        });
        parts.forEach((key, data) -> {
            try {
                if (((String[])data).length >= 1) {
                    FiguredataPaletteColor color;
                    FiguredataSettype settype = ClothingValidationManager.FIGUREDATA.settypes.get(data[0]);
                    if (settype == null) {
                        return;
                    }
                    FiguredataPalette palette = ClothingValidationManager.FIGUREDATA.palettes.get(settype.paletteId);
                    if (palette == null) {
                        throw new Exception("Palette " + settype.paletteId + " does not exist");
                    }
                    int setId = Integer.parseInt(((String[])data).length >= 2 ? data[1] : "-1");
                    FiguredataSettypeSet set = settype.getSet(setId);
                    if (set == null || set.club && !isHC || !set.selectable || set.sellable && !ownedClothing.contains(set.id) || !set.gender.equalsIgnoreCase("U") && !set.gender.equalsIgnoreCase(gender)) {
                        if (gender.equalsIgnoreCase("M") && !isHC && !settype.mandatoryMale0) {
                            return;
                        }
                        if (gender.equalsIgnoreCase("F") && !isHC && !settype.mandatoryFemale0) {
                            return;
                        }
                        if (gender.equalsIgnoreCase("M") && isHC && !settype.mandatoryMale1) {
                            return;
                        }
                        if (gender.equalsIgnoreCase("F") && isHC && !settype.mandatoryFemale1) {
                            return;
                        }
                        set = settype.getFirstNonHCSetForGender(gender);
                        setId = set.id;
                    }
                    ArrayList<Object> dataParts = new ArrayList<Object>();
                    int color1 = -1;
                    int color2 = -1;
                    if (set.colorable && ((color = palette.getColor(color1 = ((String[])data).length >= 3 ? Integer.parseInt(data[2]) : -1)) == null || color.club && !isHC)) {
                        color1 = palette.getFirstNonHCColor().id;
                    }
                    if (((String[])data).length >= 4 && set.colorable && ((color = palette.getColor(color2 = Integer.parseInt(data[3]))) == null || color.club && !isHC)) {
                        color2 = palette.getFirstNonHCColor().id;
                    }
                    dataParts.add(settype.type);
                    dataParts.add("" + setId);
                    if (color1 > -1) {
                        dataParts.add("" + color1);
                    }
                    if (color2 > -1) {
                        dataParts.add("" + color2);
                    }
                    lookParts.add(String.join((CharSequence)"-", dataParts));
                }
            }
            catch (Exception e) {
                LOGGER.error("Error in clothing validation", e);
            }
        });
        return String.join((CharSequence)".", lookParts);
    }
}

