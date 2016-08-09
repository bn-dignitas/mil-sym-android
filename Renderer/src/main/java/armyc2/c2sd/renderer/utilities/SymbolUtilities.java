/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package armyc2.c2sd.renderer.utilities;

import armyc2.c2sd.renderer.utilities.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 *
 * @author michael.spinelli
 */
public class SymbolUtilities
{

    private static SimpleDateFormat dateFormatFront = new SimpleDateFormat("ddHHmmss");
    private static SimpleDateFormat dateFormatBack = new SimpleDateFormat("MMMyy");
    private static SimpleDateFormat dateFormatFull = new SimpleDateFormat("ddHHmmssZMMMyy");
    private static SimpleDateFormat dateFormatZulu = new SimpleDateFormat("Z");

    /**
     * @name getBasicSymbolID
     *
     * @desc Returns a formatted string that has only the necessary static
     * characters needed to draw a symbol. For instance
     * GetBasicSymbolID("GFTPGLB----K---") returns "G*T*GLB---****X"
     *
     * @param strSymbolID - IN - A 15 character MilStd code
     * @return A properly formated basic symbol ID
     */
    public static String getBasicSymbolID(String strSymbolID)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            if ((strSymbolID != null) && (strSymbolID.length() == 15))
            {
                // Check to make sure it is a tactical graphic symbol.
                if ((isWeather(strSymbolID)) || (isBasicShape(strSymbolID)))
                {
                    return strSymbolID;
                }
                else if (isTacticalGraphic(strSymbolID) == true)
                {
                    sb.append(strSymbolID.charAt(0));
                    sb.append("*");
                    sb.append(strSymbolID.charAt(2));
                    sb.append("*");
                    sb.append(strSymbolID.substring(4, 10));
                    sb.append("****");
                    sb.append("X");

                    if (isEMSNaturalEvent(strSymbolID) == true)
                    {
                        sb.deleteCharAt(14).append("*");
                    }

                    return sb.toString();
                }
                else if (isWarfighting(strSymbolID))
                {
                	
                    sb.append(strSymbolID.charAt(0));
                    sb.append("*");
                    sb.append(strSymbolID.charAt(2));
                    sb.append("*");
                    sb.append(strSymbolID.substring(4, 10));

                	if(isSIGINT(strSymbolID))
                		sb.append("--***");
                	else if(isInstallation(strSymbolID))
                		sb.append("H****");
                	else
                    {
                        sb.append("*****");
                        UnitDefTable udt = UnitDefTable.getInstance();
                        String temp = sb.toString();
                        for(int i = 0; i < 2; i++)
                        {
                            if(udt.hasUnitDef(temp,i)==true)
                            {
                                return temp;
                            }
                            else
                            {
                                temp = temp.substring(0,10) + "H****";
                                if(udt.hasUnitDef(temp,i)==true)
                                {
                                    return temp;
                                }
                                else
                                {
                                    temp = temp.substring(0,10) + "MO***";
                                    if(udt.hasUnitDef(temp,i)==true)
                                    {
                                        return temp;
                                    }
                                }
                            }
                            temp = temp.substring(0,10) + "*****";
                        }

                    }

                    return sb.toString();
                }
                else // Don't do anything for bridge symbols
                {
                    return strSymbolID;
                }
            }
            else
            {
                return strSymbolID;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return "";
    }

    /**
     * Only for renderer use.  Please use getBasicSymbolID.
     * @param strSymbolID
     * @return
     */
    public static String getBasicSymbolIDStrict(String strSymbolID)
    {
        StringBuilder sb = new StringBuilder();
        char scheme = strSymbolID.charAt(0);
        if(strSymbolID != null && strSymbolID.length() == 15)
        {
            if (scheme == 'G')
            {
                sb.append(strSymbolID.charAt(0));
                sb.append("*");
                sb.append(strSymbolID.charAt(2));
                sb.append("*");
                sb.append(strSymbolID.substring(4, 10));
                sb.append("****X");
            }
            else if (scheme != 'W' && scheme != 'B' && scheme != 'P')
            {
                sb.append(strSymbolID.charAt(0));
                sb.append("*");
                sb.append(strSymbolID.charAt(2));
                sb.append("*");
                sb.append(strSymbolID.substring(4, 10));
                sb.append("*****");
            }
            else
            {
                return strSymbolID;
            }
            return sb.toString();
        }
        return strSymbolID;
    }

    public static String reconcileSymbolID(String symbolID)
    {
        return reconcileSymbolID(symbolID, false);
    }

    public static String reconcileSymbolID(String symbolID, boolean isMultiPoint)
    {
        StringBuilder sb = new StringBuilder("");
        char codingScheme = symbolID.charAt(0);

        if (symbolID.startsWith("BS_") || symbolID.startsWith("BBS_") || symbolID.startsWith("PBS_"))
        {
            return symbolID;
        }

        if (symbolID.length() < 15)
        {
            while (symbolID.length() < 15)
            {
                symbolID += "-";
            }
        }
        if (symbolID.length() > 15)
        {
            symbolID = symbolID.substring(0, 15);
        }

        if (symbolID != null && symbolID.length() == 15)
        {
            if (codingScheme == 'S' || //warfighting
                    codingScheme == 'I' ||//sigint
                    codingScheme == 'O' ||//stability operation
                    codingScheme == 'E')//emergency management
            {
                sb.append(codingScheme);

                if (SymbolUtilities.hasValidAffiliation(symbolID) == false)
                {
                    sb.append('U');
                }
                else
                {
                    sb.append(symbolID.charAt(1));
                }

                if (SymbolUtilities.hasValidBattleDimension(symbolID) == false)
                {
                    sb.append('Z');
                    sb.replace(0, 1, "S");
                }
                else
                {
                    sb.append(symbolID.charAt(2));
                }

                if (SymbolUtilities.hasValidStatus(symbolID) == false)
                {
                    sb.append('P');
                }
                else
                {
                    sb.append(symbolID.charAt(3));
                }

                sb.append("------");
                sb.append(symbolID.substring(10, 15));

            }
            else if (codingScheme == 'G')//tactical
            {
                sb.append(codingScheme);

                if (SymbolUtilities.hasValidAffiliation(symbolID) == false)
                {
                    sb.append('U');
                }
                else
                {
                    sb.append(symbolID.charAt(1));
                }

                //if(SymbolUtilities.hasValidBattleDimension(SymbolID)==false)
                sb.append('G');
                //else
                //    sb.append(SymbolID.charAt(2));

                if (SymbolUtilities.hasValidStatus(symbolID) == false)
                {
                    sb.append('P');
                }
                else
                {
                    sb.append(symbolID.charAt(3));
                }

                if (isMultiPoint)
                {
                    sb.append("GAG---");//return a boundary
                }
                else
                {
                    sb.append("GPP---");//return an action point
                }
                sb.append(symbolID.substring(10, 15));

            }
            else if (codingScheme == 'W')//weather
            {//no default weather graphic
                return "SUZP-----------";//unknown
            }
            else//bad codingScheme
            {
                sb.append('S');
                if (SymbolUtilities.hasValidAffiliation(symbolID) == false)
                {
                    sb.append('U');
                }
                else
                {
                    sb.append(symbolID.charAt(1));
                }

                if (SymbolUtilities.hasValidBattleDimension(symbolID) == false)
                {
                    sb.append('Z');
                    //sb.replace(0, 1, "S");
                }
                else
                {
                    sb.append(symbolID.charAt(2));
                }

                if (SymbolUtilities.hasValidStatus(symbolID) == false)
                {
                    sb.append('P');
                }
                else
                {
                    sb.append(symbolID.charAt(3));
                }

                sb.append("------");
                sb.append(symbolID.substring(10, 15));
            }
        }
        else
        {
            return "SUZP-----------";//unknown
        }
        return sb.toString();
    }

    /**
     * Returns true if the SymbolID has a valid Status (4th character)
     *
     * @param SymbolID
     * @return
     */
    public static
            Boolean hasValidStatus(String SymbolID)
    {
        if (SymbolID != null && SymbolID.length() >= 10)
        {
            char status = SymbolID.charAt(3);

            char codingScheme = SymbolID.charAt(0);

            if (codingScheme == 'S' || //warfighting
                    codingScheme == 'I' ||//sigint
                    codingScheme == 'O' ||//stability operation
                    codingScheme == 'E')//emergency management
            {
                if (status == 'A'
                        || status == 'P'
                        || status == 'C'
                        || status == 'D'
                        || status == 'X'
                        || status == 'F')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'G')
            {
                if (status == 'A'
                        || status == 'S'
                        || status == 'P'
                        || status == 'K')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'W')
            {
                return true;//doesn't apply
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if the SymbolID has a valid Affiliation (2nd character)
     *
     * @param SymbolID
     * @return
     */
    public static
            Boolean hasValidAffiliation(String SymbolID)
    {
        if (SymbolID != null && SymbolID.length() >= 10 && isWeather(SymbolID) == false)
        {
            char affiliation = SymbolID.charAt(1);
            if (affiliation == 'P'
                    || affiliation == 'U'
                    || affiliation == 'A'
                    || affiliation == 'F'
                    || affiliation == 'N'
                    || affiliation == 'S'
                    || affiliation == 'H'
                    || affiliation == 'G'
                    || affiliation == 'W'
                    || affiliation == 'M'
                    || affiliation == 'D'
                    || affiliation == 'L'
                    || affiliation == 'J'
                    || affiliation == 'K')
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static
            Boolean hasValidCodingScheme(String symbolID)
    {
        if (symbolID != null && symbolID.length() > 0)
        {
            char codingScheme = symbolID.charAt(0);
            if (codingScheme == 'S'
                    || codingScheme == 'G'
                    || codingScheme == 'W'
                    || codingScheme == 'I'
                    || codingScheme == 'O'
                    || codingScheme == 'E')
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if the SymbolID has a valid BattleDimension (3rd character)
     * "Category" for tactical graphics
     *
     * @param SymbolID 15 character String
     * @return
     */
    public static
            Boolean hasValidBattleDimension(String SymbolID)
    {
        if (SymbolID != null && SymbolID.length() >= 10)
        {
            char codingScheme = SymbolID.charAt(0);
            char bd = SymbolID.charAt(2);

            if (codingScheme == 'S')//warfighting
            {
                if (bd == 'P'
                        || bd == 'A'
                        || bd == 'G'
                        || bd == 'S'
                        || bd == 'U'
                        || bd == 'F'
                        || //status == 'X' ||//doesn't seem to be a valid use for this one
                        bd == 'Z')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'O')//stability operation
            {
                if (bd == 'V'
                        || bd == 'L'
                        || bd == 'O'
                        || bd == 'I'
                        || bd == 'P'
                        || bd == 'G'
                        || bd == 'R')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'E')//emergency management
            {
                if (bd == 'I'
                        || bd == 'N'
                        || bd == 'O'
                        || bd == 'F')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'G')//tactical grahpic
            {
                if (bd == 'T'
                        || bd == 'G'
                        || bd == 'M'
                        || bd == 'F'
                        || bd == 'S'
                        || bd == 'O')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (codingScheme == 'W')//weather
            {
                return true;//doesn't apply
            }
            else if (codingScheme == 'I')//sigint
            {
                if (bd == 'P'
                        || bd == 'A'
                        || bd == 'G'
                        || bd == 'S'
                        || bd == 'U'
                        || //status == 'X' ||//doesn't seem to be a valid use for this one
                        bd == 'Z')
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else//bad codingScheme, can't confirm battle dimension
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static
            Boolean hasValidCountryCode(String symbolID)
    {
        if (Character.isLetter(symbolID.charAt(12))
                && Character.isLetter(symbolID.charAt(13)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * converts a Java Date object into a properly formated String for W or W1
     *
     * @param time
     * @return
     */
    public static
            String getDateLabel(Date time)
    {

        String modifierString = null;

        String zulu = "";
        zulu = dateFormatZulu.format(time);

        if (zulu != null && zulu.length() == 5)
        {

            if (zulu.startsWith("+"))//Integer.valueOf doesn't like '+'
            {
                zulu = zulu.substring(1, 3);
            }
            else
            {
                zulu = zulu.substring(0, 3);
            }

            int intZulu = Integer.valueOf(zulu);

            zulu = getZuluCharFromTimeZoneOffset(intZulu);
        }
        else
        {
            zulu = getZuluCharFromTimeZoneOffset(time);
        }

        modifierString = dateFormatFront.format(time) + zulu + dateFormatBack.format(time);

        return modifierString.toUpperCase();
    }

    /**
     * Given date, return character String representing which NATO time zone
     * you're in.
     *
     * @param time
     * @return
     */
    private static
            String getZuluCharFromTimeZoneOffset(Date time)
    {
        TimeZone tz = TimeZone.getDefault();
        Date offset = new Date(tz.getOffset(time.getTime()));
        long lOffset = offset.getTime() / 3600000;//3600000 = (1000(ms)*60(s)*60(m))

        int hour = (int) lOffset;

        return getZuluCharFromTimeZoneOffset(hour);
    }

    /**
     * Given hour offset from Zulu return character String representing which
     * NATO time zone you're in.
     *
     * @param hour
     * @return
     */
    private static
            String getZuluCharFromTimeZoneOffset(int hour)
    {
        if (hour == 0)
        {
            return "Z";
        }
        else if (hour == -1)
        {
            return "N";
        }
        else if (hour == -2)
        {
            return "O";
        }
        else if (hour == -3)
        {
            return "P";
        }
        else if (hour == -4)
        {
            return "Q";
        }
        else if (hour == -5)
        {
            return "R";
        }
        else if (hour == -6)
        {
            return "S";
        }
        else if (hour == -7)
        {
            return "T";
        }
        else if (hour == -8)
        {
            return "U";
        }
        else if (hour == -9)
        {
            return "V";
        }
        else if (hour == -10)
        {
            return "W";
        }
        else if (hour == -11)
        {
            return "X";
        }
        else if (hour == -12)
        {
            return "Y";
        }
        else if (hour == 1)
        {
            return "A";
        }
        else if (hour == 2)
        {
            return "B";
        }
        else if (hour == 3)
        {
            return "C";
        }
        else if (hour == 4)
        {
            return "D";
        }
        else if (hour == 5)
        {
            return "E";
        }
        else if (hour == 6)
        {
            return "F";
        }
        else if (hour == 7)
        {
            return "G";
        }
        else if (hour == 8)
        {
            return "H";
        }
        else if (hour == 9)
        {
            return "I";
        }
        else if (hour == 10)
        {
            return "K";
        }
        else if (hour == 11)
        {
            return "L";
        }
        else if (hour == 12)
        {
            return "M";
        }
        else
        {
            return "-";
        }
    }

    /**
     *
     * @param symbolID
     * @param unitModifier
     * @return
     */
    public static
            boolean canUnitHaveModifier(String symbolID, int unitModifier)
    {
        boolean returnVal = false;
        try
        {
            if (unitModifier == (ModifiersUnits.B_ECHELON))
            {
                return (SymbolUtilities.isUnit(symbolID) || SymbolUtilities.isSTBOPS(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.C_QUANTITY))
            {
                return (SymbolUtilities.isEquipment(symbolID)
                        || SymbolUtilities.isEMSEquipment(symbolID)
                        || SymbolUtilities.isEMSIncident(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.D_TASK_FORCE_INDICATOR))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isSTBOPS(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.F_REINFORCED_REDUCED))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isSTBOPS(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.G_STAFF_COMMENTS))
            {
                return (SymbolUtilities.isEMS(symbolID) == false);
            }
            else if (unitModifier == (ModifiersUnits.H_ADDITIONAL_INFO_1))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.J_EVALUATION_RATING))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.K_COMBAT_EFFECTIVENESS))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isSTBOPS(symbolID)
                        || (SymbolUtilities.hasInstallationModifier(symbolID) && SymbolUtilities.isEMS(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.L_SIGNATURE_EQUIP))
            {
                return (SymbolUtilities.isEquipment(symbolID)
                        || SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.M_HIGHER_FORMATION))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.N_HOSTILE))
            {
                return (SymbolUtilities.isEquipment(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.P_IFF_SIF))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isEquipment(symbolID)
                        || (SymbolUtilities.hasInstallationModifier(symbolID) && SymbolUtilities.isEMS(symbolID) == false)
                        || SymbolUtilities.isSTBOPS(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.Q_DIRECTION_OF_MOVEMENT))
            {
                return ((SymbolUtilities.hasInstallationModifier(symbolID) == false)
                        && (SymbolUtilities.isSIGINT(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.R_MOBILITY_INDICATOR))
            {
                return (SymbolUtilities.isEquipment(symbolID)
                        || SymbolUtilities.isEMSEquipment(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.R2_SIGNIT_MOBILITY_INDICATOR))
            {
                return (SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.S_HQ_STAFF_OR_OFFSET_INDICATOR))
            {
                return (SymbolUtilities.isSIGINT(symbolID) == false);
            }
            else if (unitModifier == (ModifiersUnits.T_UNIQUE_DESIGNATION_1))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.V_EQUIP_TYPE))
            {
                return (SymbolUtilities.isEquipment(symbolID)
                        || SymbolUtilities.isSIGINT(symbolID)
                        || SymbolUtilities.isEMSEquipment(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.W_DTG_1))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.X_ALTITUDE_DEPTH))
            {
                return (SymbolUtilities.isSIGINT(symbolID) == false);
            }
            else if (unitModifier == (ModifiersUnits.Y_LOCATION))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.Z_SPEED))
            {
                return ((SymbolUtilities.hasInstallationModifier(symbolID) == false)
                        && (SymbolUtilities.isSIGINT(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.AA_SPECIAL_C2_HQ))
            {
                return (SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.isSTBOPS(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.AB_FEINT_DUMMY_INDICATOR))
            {
                return ((SymbolUtilities.isSIGINT(symbolID) == false)
                        && (SymbolUtilities.isEMS(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.AC_INSTALLATION))
            {
                return (SymbolUtilities.isSIGINT(symbolID) == false);
            }
            else if (unitModifier == (ModifiersUnits.AD_PLATFORM_TYPE))
            {
                return (SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.AE_EQUIPMENT_TEARDOWN_TIME))
            {
                return (SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.AF_COMMON_IDENTIFIER))
            {
                return (SymbolUtilities.isSIGINT(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.AG_AUX_EQUIP_INDICATOR))
            {
                return (SymbolUtilities.isEquipment(symbolID));
            }
            else if (unitModifier == (ModifiersUnits.AH_AREA_OF_UNCERTAINTY)
                    || unitModifier == (ModifiersUnits.AI_DEAD_RECKONING_TRAILER)
                    || unitModifier == (ModifiersUnits.AJ_SPEED_LEADER))
            {
                return ((SymbolUtilities.isSIGINT(symbolID) == false)
                        && (SymbolUtilities.hasInstallationModifier(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.AK_PAIRING_LINE))
            {
                return ((SymbolUtilities.isSIGINT(symbolID) == false)
                        && (SymbolUtilities.isEMS(symbolID) == false)
                        && (SymbolUtilities.hasInstallationModifier(symbolID) == false));
            }
            else if (unitModifier == (ModifiersUnits.AL_OPERATIONAL_CONDITION))
            {
                return (SymbolUtilities.isUnit(symbolID) == false);
            }
            else if (unitModifier == (ModifiersUnits.AO_ENGAGEMENT_BAR))
            {
                return ((SymbolUtilities.isEquipment(symbolID)
                        || SymbolUtilities.isUnit(symbolID)
                        || SymbolUtilities.hasInstallationModifier(symbolID))
                        && SymbolUtilities.isEMS(symbolID) == false);
            } //out of order because used less often
            else if (unitModifier == (ModifiersUnits.A_SYMBOL_ICON))
            {
                return true;
            }
            else if (unitModifier == (ModifiersUnits.E_FRAME_SHAPE_MODIFIER))
            {
                //return (SymbolUtilities.isSIGINT(symbolID)==false);
                //not sure why milstd say sigint don't have it.
                //they clearly do.
                return true;
            }
            else if(unitModifier == (ModifiersUnits.SCC_SONAR_CLASSIFICATION_CONFIDENCE))
            {
                if(SymbolUtilities.isSubSurface(symbolID))
                {
                    //these symbols only exist in 2525C
                    String temp = symbolID.substring(4, 10);
                    if(temp.equals("WMGC--") ||
                            temp.equals("WMMC--") ||
                            temp.equals("WMFC--") ||
                            temp.equals("WMC---"))
                    {
                        return true;
                    }
                }
                return false;
            }
            else
            {
                return false;
            }

        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("SymbolUtilties", "canHaveModifier", exc);
        }
        return returnVal;
    }

    public static
            Boolean hasModifier(String symbolID, int modifier)
    {
        return hasModifier(symbolID, modifier, RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     *
     * @param symbolID
     * @param modifier - from the constants ModifiersUnits or ModifiersTG
     * @param symStd - 0=2525B, 1=2525C. Constants available in
     * RendererSettings.
     * @return
     */
    public static
            Boolean hasModifier(String symbolID, int modifier, int symStd)
    {
        Boolean returnVal = false;

        if (isTacticalGraphic(symbolID) == true)
        {
            returnVal = canSymbolHaveModifier(symbolID, modifier, symStd);
        }
        else
        {
            returnVal = canUnitHaveModifier(symbolID, modifier);
        }
        return returnVal;
    }

    ;

      /**
       * Checks if a tactical graphic has the passed modifier.
       * @param symbolID - symbolID of Tactical Graphic
       * @param tgModifier - ModifiersTG.AN_AZIMUTH
       * @return 
       */
      public static
            boolean canSymbolHaveModifier(String symbolID, int tgModifier)
    {
        return canSymbolHaveModifier(symbolID, tgModifier, RendererSettings.getInstance().getSymbologyStandard());
    }

    /**
     * Checks if a tactical graphic has the passed modifier.
     *
     * @param symbolID - symbolID of Tactical Graphic
     * @param tgModifier - ModifiersTG.AN_AZIMUTH
     * @param symStd - like RendererSettings.Symbology_2525C
     * @return
     */
    public static boolean canSymbolHaveModifier(String symbolID, int tgModifier, int symStd)
    {
        String basic = null;
        SymbolDef sd = null;
        boolean returnVal = false;
        String modCode = ModifiersTG.getModifierLetterCode(tgModifier);
        try
        {

            basic = SymbolUtilities.getBasicSymbolID(symbolID);
            sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
            if (sd != null)
            {
                int dc = sd.getDrawCategory();
                if (tgModifier == (ModifiersTG.AM_DISTANCE))
                {
                    switch (dc)
                    {
                        case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                        case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                        case SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE:
                            returnVal = true;
                            break;
                        case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE:
                        case SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                            returnVal = true;
                            break;
                        case SymbolDef.DRAW_CATEGORY_LINE://air corridor
                        	if(sd.getModifiers().indexOf(modCode + ".") > -1)
                        		returnVal = true;
                            break;
                        default:
                            returnVal = false;
                    }
                }
                else if (tgModifier == (ModifiersTG.AN_AZIMUTH))
                {
                    switch (dc)
                    {
                        case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                        case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                            returnVal = true;
                            break;
                        default:
                            returnVal = false;
                    }
                }
                else
                {
                    if (sd.getModifiers().indexOf(modCode + ".") > -1)
                    {
                        returnVal = true;
                    }
                }
            }

            return returnVal;

        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("SymbolUtilities", "canSymbolHaveModifier", exc);
        }
        return returnVal;
    }


    /**
     * Gets line color used if no line color has been set. The color is
     * specified based on the affiliation of the symbol and whether it is a unit
     * or not.
     *
     * @param symbolID
     * @return
     */
    public static Color getLineColorOfAffiliation(String symbolID)
    {
        Color retColor = null;
        String basicSymbolID = getBasicSymbolID(symbolID);
        try
        {
            // We can't get the fill color if there is no symbol id, since that also means there is no affiliation
            if ((symbolID == null) || (symbolID.equals("")))
            {
                return retColor;
            }

            if (SymbolUtilities.isTacticalGraphic(symbolID))// && !SymbolUtilities.isTGWithFill(symbolID))
            {
                if ((symbolID.substring(0, 4).equals("ESRI")) || SymbolUtilities.isJWARN(symbolID))
                {
                    retColor = Color.BLACK;//0x000000;	// Black
                }
                else if (SymbolUtilities.isWeather(symbolID))
                {
                    retColor = getLineColorOfWeather(symbolID);
                }
                else if (SymbolUtilities.isObstacle(symbolID))
                {
                    retColor = Color.GREEN;	// Green
                }
                else if ((SymbolUtilities.isNBC(symbolID))
                        && (basicSymbolID.equals("G*M*NR----****X") == true || //Radioactive Area
                        basicSymbolID.equals("G*M*NC----****X") == true || //Chemically Contaminated Area
                        basicSymbolID.equals("G*M*NB----****X") == true)) //Biologically Contaminated Area
                {
                    retColor = Color.BLACK;//0xffff00;
                }
                else if(SymbolUtilities.isEMSNaturalEvent(symbolID))
                {
                    retColor = Color.BLACK;//0xffff00;
                }
                else
                {
                    String switchChar = symbolID.substring(1, 2);
                    if (switchChar.equals("F")
                            || switchChar.equals("A")
                            || switchChar.equals("D")
                            || switchChar.equals("M"))
                    {
                        retColor = Color.BLACK;//0x000000;	// Black
                    }
                    else if (switchChar.equals("H")
                            || switchChar.equals("S")
                            || switchChar.equals("J")
                            || switchChar.equals("K"))
                    {

                        if (SymbolUtilities.getBasicSymbolID(symbolID).equals("G*G*GLC---****X")) // Line of Contact
                        {
                            retColor = Color.BLACK;//0x000000;	// Black
                        }
                        else
                        {
                            retColor = Color.RED;//0xff0000;	// Red
                        }

                    }
                    else if (switchChar.equals("N")
                            || switchChar.equals("L")) // Neutral:
                    {
                        retColor = Color.GREEN;//0x00ff00;	// Green

                    }
                    else if (switchChar.equals("U")
                            || switchChar.equals("P")
                            || switchChar.equals("O")
                            || switchChar.equals("G")
                            || switchChar.equals("W"))
                    {
                        if (symbolID.substring(0, 8).equals("WOS-HDS-"))
                        {
                            retColor = Color.GRAY;//0x808080;	// Gray
                        }
                        else
                        {
                            retColor = Color.YELLOW;//0xffff00;	// Yellow
                        }

                    }
                    else
                    {
                        retColor = Color.black;//null;//0;//Color.Empty;

                    }	// End default

                }	// End else
            }// End if (SymbolUtilities.IsTacticalGraphic(this.SymbolID))
            else
            {
                //stopped doing check because all warfighting
                //should have black for line color.
                retColor = Color.BLACK;

            }	// End else
        } // End try
        catch (Exception e)
        {
            // Log Error
            ErrorLogger.LogException("SymbolUtilties", "getLineColorOfAffiliation", e);
            //throw e;
        }	// End catch
        return retColor;
    }	// End get LineColorOfAffiliation

    /**
     * Is the fill color used if no fill color has been set. The color is
     * specified based on the affiliation of the symbol and whether it is a unit
     * or not.
     *
     * @param symbolID
     * @return
     */
    public static
            Color getFillColorOfAffiliation(String symbolID)
    {
        Color retColor = null;
        String basicSymbolID = getBasicSymbolID(symbolID);

        try
        {
            char switchChar;
            // We can't get the fill color if there is no symbol id, since that also means there is no affiliation
            if ((symbolID == null) || (symbolID.equals("")))
            {
                return retColor;
            }

            if (basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                    //basicSymbolID.equals("G*M*NF----****X") || //fallout producing
                    basicSymbolID.equals("G*M*NEB---****X") ||//biological
                    basicSymbolID.equals("G*M*NEC---****X"))//chemical
            {
                retColor = AffiliationColors.UnknownUnitFillColor;//  Color.yellow;
            }
            else if (SymbolUtilities.isTacticalGraphic(symbolID) && !SymbolUtilities.isTGSPWithFill(symbolID))
            {
                if (basicSymbolID.equals("G*M*NZ----****X") ||//ground zero
                        //basicSymbolID.equals("G*M*NF----****X") || //fallout producing
                        basicSymbolID.equals("G*M*NEB---****X") ||//biological
                        basicSymbolID.equals("G*M*NEC---****X"))//chemical
                {
                    retColor = Color.yellow;
                }
                else
                {
                    switchChar = symbolID.charAt(1);
                    if (switchChar == 'F'
                            || switchChar == 'A'
                            || switchChar == 'D'
                            || switchChar == 'M')
                    {
                        retColor = AffiliationColors.FriendlyGraphicFillColor;//0x00ffff;	// Cyan

                    }
                    else if (switchChar == 'H'
                            || switchChar == 'S'
                            || switchChar == 'J'
                            || switchChar == 'K')
                    {
                        retColor = AffiliationColors.HostileGraphicFillColor;//0xfa8072;	// Salmon

                    }
                    else if (switchChar == 'N'
                            || switchChar == 'L')
                    {
                        retColor = AffiliationColors.NeutralGraphicFillColor;//0x7fff00;	// Light Green

                    }
                    else if (switchChar == 'U'
                            || switchChar == 'P'
                            || switchChar == 'O'
                            || switchChar == 'G'
                            || switchChar == 'W')
                    {
                        retColor = new Color(255, 250, 205); //0xfffacd;	// LemonChiffon 255 250 205
                    }
                    else
                    {
                        retColor = null;
                    }
                }
            } // End if(SymbolUtilities.IsTacticalGraphic(this._strSymbolID))
            else
            {
                switchChar = symbolID.charAt(1);
                if (switchChar == 'F'
                        || switchChar == 'A'
                        || switchChar == 'D'
                        || switchChar == 'M')
                {
                    retColor = AffiliationColors.FriendlyUnitFillColor;//0x00ffff;	// Cyan

                }
                else if (switchChar == 'H'
                        || switchChar == 'S'
                        || switchChar == 'J'
                        || switchChar == 'K')
                {
                    retColor = AffiliationColors.HostileUnitFillColor;//0xfa8072;	// Salmon

                }
                else if (switchChar == 'N'
                        || switchChar == 'L')
                {
                    retColor = AffiliationColors.NeutralUnitFillColor;//0x7fff00;	// Light Green

                }
                else if (switchChar == 'U'
                        || switchChar == 'P'
                        || switchChar == 'O'
                        || switchChar == 'G'
                        || switchChar == 'W')
                {
                    retColor = AffiliationColors.UnknownUnitFillColor;//new Color(255,250, 205); //0xfffacd;	// LemonChiffon 255 250 205
                }
                else
                {
                    retColor = AffiliationColors.UnknownUnitFillColor;//null;
                }

            }	// End else
        } // End try
        catch (Exception e)
        {
            // Log Error
            ErrorLogger.LogException("SymbolUtilties", "getFillColorOfAffiliation", e);
            //throw e;
        }	// End catch

        return retColor;
    }	// End FillColorOfAffiliation

    public static
            Color getLineColorOfWeather(String symbolID)
    {
    	Color retColor = Color.BLACK;
        // Get the basic id
        //String symbolID = SymbolUtilities.getBasicSymbolID(symbolID);

        //if(symbolID.equals(get))
        if(symbolID.equals("WAS-WSGRL-P----") || // Hail - Light not Associated With Thunder
            symbolID.equals("WAS-WSGRMHP----") || // Hail - Moderate/Heavy not Associated with Thunder
            symbolID.equals("WAS-PL----P----") || // Low Pressure Center - Pressure Systems
            symbolID.equals("WAS-PC----P----") || // Cyclone Center - Pressure Systems
            symbolID.equals("WAS-WSIC--P----") || // Ice Crystals (Diamond Dust)
            symbolID.equals("WAS-WSPLL-P----") || // Ice Pellets - Light
            symbolID.equals("WAS-WSPLM-P----") || // Ice Pellets - Moderate
            symbolID.equals("WAS-WSPLH-P----") || // Ice Pellets - Heavy
            symbolID.equals("WAS-WST-NPP----") || // Thunderstorm - No Precipication
            symbolID.equals("WAS-WSTMR-P----") || // Thunderstorm Light to Moderate with Rain/Snow - No Hail
            symbolID.equals("WAS-WSTHR-P----") || // Thunderstorm Heavy with Rain/Snow - No Hail
            symbolID.equals("WAS-WSTMH-P----") || // Thunderstorm Light to Moderate - With Hail
            symbolID.equals("WAS-WSTHH-P----") || // Thunderstorm Heavy - With Hail
            symbolID.equals("WAS-WST-FCP----") || // Funnel Cloud (Tornado/Waterspout)
            symbolID.equals("WAS-WST-SQP----") || // Squall
            symbolID.equals("WAS-WST-LGP----") || // Lightning
            symbolID.equals("WAS-WSFGFVP----") || // Fog - Freezing, Sky Visible
            symbolID.equals("WAS-WSFGFOP----") || // Fog - Freezing, Sky not Visible
            symbolID.equals("WAS-WSTSD-P----") || // Tropical Depression
            symbolID.equals("WAS-WSTSS-P----") || // Tropical Storm
            symbolID.equals("WAS-WSTSH-P----") || // Hurricane/Typhoon
            symbolID.equals("WAS-WSRFL-P----") || // Freezing Rain - Light
            symbolID.equals("WAS-WSRFMHP----") || // Freezing Rain - Moderate/Heavy
            symbolID.equals("WAS-WSDFL-P----") || // Freezing Drizzle - Light
            symbolID.equals("WAS-WSDFMHP----") || // Freezing Drizzle - Moderate/Heavy
            symbolID.equals("WOS-HHDMDBP----") || //mine-naval (doubtful)
            symbolID.equals("WOS-HHDMDFP----") || // mine-naval (definited)
            symbolID.substring(0,7).equals("WA-DPFW") || //warm front
            //symbolID.substring(0,7).equals("WA-DPFS")//stationary front (actually, it's red & blue)
            symbolID.equals("WA-DBAIF----A--") || // INSTRUMENT FLIGHT RULE (IFR)
            symbolID.equals("WA-DBAFP----A--") || // 
            symbolID.equals("WA-DBAT-----A--") || // 
            symbolID.equals("WA-DIPIS---L---") || // 
            symbolID.equals("WA-DIPTH---L---") || // 
            symbolID.equals("WA-DWJ-----L---") || // Jet Stream  
            symbolID.equals("WO-DGMSB----A--") || //
            symbolID.equals("WO-DGMRR----A--") ||
            symbolID.equals("WO-DGMCH----A--") ||
            symbolID.equals("WO-DGMIBE---A--") ||
            symbolID.equals("WO-DGMBCC---A--") ||
            symbolID.equals("WO-DOBVI----A--"))

        {
            retColor = Color.RED;//0xff0000;	// Red
        }
        else if(symbolID.equals("WAS-PH----P----") || // High Pressure Center - Pressure Systems
                symbolID.equals("WAS-PA----P----")  || // Anticyclone Center - Pressure Systems
                symbolID.equals("WA-DBAMV----A--")  || // MARGINAL VISUAL FLIGHT RULE (MVFR)
                symbolID.equals("WA-DBATB----A--")  || // BOUNDED AREAS OF WEATHER / TURBULENCE
                symbolID.substring(0,5).equals("WAS-T")  || // Turbulence
                symbolID.substring(0,7).equals("WA-DPFC") || //cold front
                symbolID.equals("WO-DGMIBA---A--"))
        {
            retColor = Color.BLUE;
        }
        else if(
        symbolID.equals("WAS-WSFGPSP----") || // Fog - Shallow Patches
        symbolID.equals("WAS-WSFGCSP----") || // Fog - Shallow Continuous
        symbolID.equals("WAS-WSFGP-P----") || // Fog - Patchy
        symbolID.equals("WAS-WSFGSVP----") || // Fog - Sky Visible
        symbolID.equals("WAS-WSFGSOP----") || // Fog - Sky Obscured
        symbolID.equals("WA-DBAFG----A--") || // Fog
        symbolID.equals("WO-DGMRM----A--") ||
        symbolID.equals("WO-DGMCM----A--") ||
        symbolID.equals("WO-DGMIBC---A--") ||
        symbolID.equals("WO-DGMBCB---A--") ||
        symbolID.equals("WO-DGMBTE---A--") ||
        symbolID.equals("WAS-WSBR--P----")) // Mist
        {
            retColor = Color.YELLOW;//0xffff00;	// Yellow
        }
        else if(
        symbolID.equals("WAS-WSFU--P----") || // Smoke
        symbolID.equals("WAS-WSHZ--P----") || // Haze
        symbolID.equals("WAS-WSDSLMP----") || // Dust/Sand Storm - Light to Moderate
        symbolID.equals("WAS-WSDSS-P----") || // Dust/Sand Storm - Severe
        symbolID.equals("WAS-WSDD--P----") || // Dust Devil
        symbolID.equals("WA-DBAD-----A--") || // Dust or Sand
        symbolID.equals("WAS-WSBD--P----")) // Blowing Dust or Sand
        {
            retColor = new Color(165,42,42);  //165 42 42 //0xa52a2a;	// Brown
        }
        else if(
        symbolID.equals("WA-DBALPNC--A--") || // 
        symbolID.equals("WA-DBALPC---A--") || // 
        symbolID.equals("WA-DIPID---L---") || // 
        symbolID.equals("WO-DHCF----L---") || // 
        symbolID.equals("WO-DHCF-----A--") || // 
        symbolID.equals("WO-DGMSIM---A--") || //
        symbolID.equals("WO-DGMRS----A--") ||
        symbolID.equals("WO-DGMCL----A--") ||
        symbolID.equals("WO-DGMIBB---A--") ||
        symbolID.equals("WO-DGMBCA---A--") ||
        symbolID.equals("WAS-WSR-LIP----") || // Rain - Intermittent Light
        symbolID.equals("WAS-WSR-LCP----") || // Rain - Continuous Light
        symbolID.equals("WAS-WSR-MIP----") || // Rain - Intermittent Moderate
        symbolID.equals("WAS-WSR-MCP----") || // Rain - Continuous Moderate
        symbolID.equals("WAS-WSR-HIP----") || // Rain - Intermittent Heavy
        symbolID.equals("WAS-WSR-HCP----") || // Rain - Continuous Heavy
        symbolID.equals("WAS-WSRSL-P----") || // Rain Showers - Light
        symbolID.equals("WAS-WSRSMHP----") || // Rain Showers - Moderate/Heavy
        symbolID.equals("WAS-WSRST-P----") || // Rain Showers - Torrential
        symbolID.equals("WAS-WSD-LIP----") || // Drizzle - Intermittent Light
        symbolID.equals("WAS-WSD-LCP----") || // Drizzle - Continuous Light
        symbolID.equals("WAS-WSD-MIP----") || // Drizzle - Intermittent Moderate
        symbolID.equals("WAS-WSD-MCP----") || // Drizzle - Continuous Moderate
        symbolID.equals("WAS-WSD-HIP----") || // Drizzle - Intermittent Heavy
        symbolID.equals("WAS-WSD-HCP----") || // Drizzle - Continuous Heavy
        symbolID.equals("WAS-WSM-L-P----") || // Rain or Drizzle and Snow - Light
        symbolID.equals("WAS-WSM-MHP----") || // Rain or Drizzle and Snow - Moderate/Heavy
        symbolID.equals("WAS-WSMSL-P----") || // Rain and Snow Showers - Light
        symbolID.equals("WAS-WSMSMHP----") || // Rain and Snow Showers - Moderate/Heavy
        symbolID.equals("WAS-WSUKP-P----") || // Precipitation of unknown type & intensity
        symbolID.equals("WAS-WSS-LIP----") || // Snow - Intermittent Light
        symbolID.equals("WAS-WSS-LCP----") || // Snow - Continuous Light
        symbolID.equals("WAS-WSS-MIP----") || // Snow - Intermittent Moderate
        symbolID.equals("WAS-WSS-MCP----") || // Snow - Continuous Moderate
        symbolID.equals("WAS-WSS-HIP----") || // Snow - Intermittent Heavy
        symbolID.equals("WAS-WSS-HCP----") || // Snow - Continuous Heavy
        symbolID.equals("WAS-WSSBLMP----") || // Blowing Snow - Light/Moderate
        symbolID.equals("WAS-WSSBH-P----") || // Blowing Snow - Heavy
        symbolID.equals("WAS-WSSG--P----") || // Snow Grains
        symbolID.equals("WAS-WSSSL-P----") || // Snow Showers - Light
        symbolID.equals("WAS-WSSSMHP----")) // Snow Showers - Moderate/Heavy
        {
            retColor = Color.GREEN;// 0x00ff00;	// Green
        }
        else if(symbolID.startsWith("WAS-IC") || // Clear Icing
                                    symbolID.startsWith("WAS-IR")  || // Rime Icing
                                    symbolID.startsWith("WAS-IM")) // Mixed Icing
        {
            retColor = new Color(128,96,16);
        }
        else if(symbolID.equals("WOS-HDS---P----")|| // Soundings
            symbolID.equals("WOS-HHDF--P----")||//foul ground
            symbolID.equals("WO-DHHDF----A--")||//foul ground
            symbolID.equals("WOS-HPFS--P----")||//fish stakes/traps/weirs
            symbolID.equals("WOS-HPFS---L---")||//fish stakes
            symbolID.equals("WOS-HPFF----A--")||//fish stakes/traps/weirs
            symbolID.equals("WO-DHDDL---L---")||//depth curve
            symbolID.equals("WO-DHDDC---L---")||//depth contour
            symbolID.equals("WO-DHCC----L---")||//coastline
            symbolID.equals("WO-DHPBP---L---")||//ports
            symbolID.equals("WO-DHPMO---L---")||//offshore loading
            symbolID.equals("WO-DHPSPA--L---")||//sp above water
            symbolID.equals("WO-DHPSPB--L---")||//sp below water
            symbolID.equals("WO-DHPSPS--L---")||//sp sea wall
            symbolID.equals("WO-DHHDK--P----")||//kelp seaweed
            symbolID.equals("WO-DHHDK----A--")||//kelp seaweed
            symbolID.equals("WO-DHHDB---L---")||//breakers
            symbolID.equals("WO-DTCCCFE-L---")||//current flow - ebb
            symbolID.equals("WO-DTCCCFF-L---")||//current flow - flood
            symbolID.equals("WOS-TCCTD-P----")||//tide data point    
            symbolID.equals("WO-DHCW-----A--")||
            symbolID.equals("WO-DMOA-----A--") ||
            symbolID.equals("WO-DMPA----L---"))//water
            retColor = Color.GRAY;//0x808080;	// Gray
        else if(
            symbolID.equals("WO-DBSM-----A--") ||
            symbolID.equals("WO-DBSF-----A--") ||
            symbolID.equals("WO-DGMN-----A--")) // 
        {
                retColor = new Color(230,230,230);//230,230,230;	// light gray
        }
        else if(
            symbolID.equals("WO-DBSG-----A--")) // 
        {
                retColor = new Color(169,169,169);//169,169,169;	// dark gray
        }
        else if(
        symbolID.equals("WAS-WSVE--P----") || // Volcanic Eruption
        symbolID.equals("WAS-WSVA--P----") || // Volcanic Ash
        symbolID.equals("WAS-WST-LVP----") || // Tropopause Level
        symbolID.equals("WAS-WSF-LVP----")) // Freezing Level
        {
                retColor = Color.BLACK;//0x000000;	// Black
        }
        else if(
        symbolID.equals("WOS-HPBA--P----") || // anchorage
        symbolID.equals("WOS-HPBA---L---") || // anchorage
        symbolID.equals("WOS-HPBA----A--") || // anchorage
        symbolID.equals("WOS-HPCP--P----") || // call in point
        symbolID.equals("WOS-HPFH--P----") || // fishing harbor
        symbolID.equals("WOS-HPM-FC-L---") || //ferry crossing
        symbolID.equals("WOS-HABM--P----") || //marker
        symbolID.equals("WOS-HAL---P----") || //light
        symbolID.equals("WA-DIPIT---L---") || //ISOTACH
        symbolID.equals("WOS-TCCTG-P----") || // Tide gauge
        symbolID.equals("WO-DL-ML---L---") ||
        symbolID.equals("WOS-HPM-FC-L---") ||
        symbolID.equals("WO-DL-RA---L---") ||
        symbolID.equals("WO-DHPBA---L---") ||
        symbolID.equals("WO-DMCA----L---") ||
        symbolID.equals("WO-DHPBA----A--") ||
        symbolID.equals("WO-DL-MA----A--") ||
        symbolID.equals("WO-DL-SA----A--") ||
        symbolID.equals("WO-DL-TA----A--") ||
        symbolID.equals("WO-DGMSR----A--")) 
        {
            retColor = new Color(255,0,255);//magenta
        }
        else if(symbolID.substring(0,7).equals("WA-DPFO")//occluded front
        )
        {
            retColor = new Color(226,159,255);//light purple
        }
        else if(
        symbolID.equals("WA-DPXITCZ-L---") || // inter-tropical convergance zone oragne?
        symbolID.equals("WO-DL-O-----A--") ||
        symbolID.equals("WA-DPXCV---L---")) // 
        {
            retColor = new Color(255,165,0);//orange
        }
        else if(
        symbolID.equals("WA-DBAI-----A--") || //BOUNDED AREAS OF WEATHER / ICING
        symbolID.startsWith("WAS-IC") || // clear icing
        symbolID.startsWith("WAS-IR") || // rime icing
        symbolID.startsWith("WAS-IM")) // mixed icing
        {
            retColor = new Color(128,96,16);//mud?
        }
        else if(
        symbolID.equals("WO-DHCI-----A--") || //Island
        symbolID.equals("WO-DHCB-----A--") || //Beach
        symbolID.equals("WO-DHPMO---L---")||//offshore loading
        symbolID.equals("WO-DHCI-----A--")) // mixed icing
        {
            retColor = new Color(210,176,106);//light/soft brown
        }
        else if(symbolID.substring(0,7).equals("WO-DOBVA----A--")
        )
        {
            retColor = new Color(26,153,77);//dark green
        }
        else if(symbolID.substring(0,7).equals("WO-DGMBTI---A--")
        )
        {
            retColor = new Color(255,48,0);//orange red
        }
        else if(symbolID.substring(0,7).equals("WO-DGMBTH---A--")
        )
        {
            retColor = new Color(255,80,0);//dark orange
        }
        //255,127,0
        //WO-DGMBTG---A--
        else if (symbolID.equals("WO-DGMBTG---A--")) {
            retColor = new Color(255, 127, 0);
        }
        //255,207,0
        //WO-DGMBTF---A--
        else if (symbolID.equals("WO-DGMBTF---A--")) {
            retColor = new Color(255, 207, 0);
        }
        //048,255,0
        //WO-DGMBTA---A--
        else if (symbolID.equals("WO-DGMBTA---A--")) {
            retColor = new Color(48, 255, 0);
        }
        //220,220,220
        //WO-DGML-----A--
        else if (symbolID.equals("WO-DGML-----A--")) {
            retColor = new Color(220, 220, 220);
        }
        //255,220,220
        //WO-DGMS-SH--A--
        else if (symbolID.equals("WO-DGMS-SH--A--")) {
            retColor = new Color(255, 220, 220);
        }
        //255,190,190
        //WO-DGMS-PH--A--
        else if (symbolID.equals("WO-DGMS-PH--A--")) {
            retColor = new Color(255, 190, 190);
        }
        //lime green 128,255,51
        //WO-DOBVC----A--
        else if (symbolID.equals("WO-DOBVC----A--")) {
            retColor = new Color(128, 255, 51);
        }
        //255,255,0
        //WO-DOBVE----A--
        else if (symbolID.equals("WO-DOBVE----A--")) {
            retColor = new Color(255, 255, 0);
        }
        //255,150,150
        //WO-DGMS-CO--A--
        else if (symbolID.equals("WO-DGMS-CO--A--")) {
            retColor = new Color(255, 150, 150);
        }
        //175,255,0
        //WO-DGMBTC---A--
        else if (symbolID.equals("WO-DGMBTC---A--")) {
            retColor = new Color(175, 255, 0);
        }
        //207,255,0
        //WO-DGMBTD---A--
        else if (symbolID.equals("WO-DGMBTD---A--")) {
            retColor = new Color(207, 255, 0);
        }
        //127,255,0
        //WO-DGMBTB---A--
        else if (symbolID.equals("WO-DGMBTB---A--")) {
            retColor = new Color(127, 255, 0);
        }
        //255,127,0
        //WO-DGMIBD---A--
        else if (symbolID.equals("WO-DGMIBD---A--")) {
            retColor = new Color(255, 127, 0);
        }
        else if (symbolID.equals("WO-DGMSIF---A--")) {
            retColor = new Color(25, 255, 230);
        }
        //0,215,255
        //WO-DGMSIVF--A--
        else if (symbolID.equals("WO-DGMSIVF--A--")) {
            retColor = new Color(0, 215, 255);
        }
        //255,255,220
        //WO-DGMSSVF--A--
        else if (symbolID.equals("WO-DGMSSVF--A--")) {
            retColor = new Color(255, 255, 220);
        }
        //255,255,140
        //WO-DGMSSF---A--
        else if (symbolID.equals("WO-DGMSSF---A--")) {
            retColor = new Color(255, 255, 140);
        }
        //255,235,0
        //WO-DGMSSM---A--
        else if (symbolID.equals("WO-DGMSSM---A--")) {
            retColor = new Color(255, 235, 0);
        }
        //255,215,0
        //WO-DGMSSC---A--
        else if (symbolID.equals("WO-DGMSSC---A--")) {
            retColor = new Color(255, 215, 0);
        }
        //255,180,0
        //WO-DGMSSVS--A--
        else if (symbolID.equals("WO-DGMSSVS--A--")) {
            retColor = new Color(255, 180, 0);
        }
        //200,255,105
        //WO-DGMSIC---A--
        else if (symbolID.equals("WO-DGMSIC---A--")) {
            retColor = new Color(200, 255, 105);
        }
        //100,130,255
        //WO-DGMSC----A--
        else if (symbolID.equals("WO-DGMSC----A--")) {
            retColor = new Color(100, 130, 255);
        }
        //255,77,0
        //WO-DOBVH----A--
        else if (symbolID.equals("WO-DOBVH----A--")) {
            retColor = new Color(255, 77, 0);
        }
        //255,128,0
        //WO-DOBVG----A--
        else if (symbolID.equals("WO-DOBVG----A--")) {
            retColor = new Color(255, 128, 0);
        }
        //255,204,0
        //WO-DOBVF----A--
        else if (symbolID.equals("WO-DOBVF----A--")) {
            retColor = new Color(255, 204, 0);
        }
        //204,255,26
        //WO-DOBVD----A--
        else if (symbolID.equals("WO-DOBVD----A--")) {
            retColor = new Color(204, 255, 26);
        }
        else
        {
            retColor = Color.BLACK;//0x000000;	// Black
        }
        
        return retColor;
    }

    /**
     * Only for single points at the moment
     *
     * @param symbolID
     * @return
     */
    public static
            Color getFillColorOfWeather(String symbolID)
    {
    	if(symbolID.equals("WOS-HPM-R-P----"))//landing ring - brown 148,48,0
            return new Color(148,48,0);
        else if(symbolID.equals("WOS-HPD---P----"))//dolphin facilities - brown
            return new Color(148,48,0);
        else if(symbolID.equals("WO-DHCB-----A--"))//
            return new Color(249,243,241);
        else if(symbolID.equals("WOS-HABB--P----"))//buoy default - 255,0,255
            return new Color(255,0,255);//magenta
        else if(symbolID.equals("WOS-HHRS--P----"))//rock submerged - 0,204,255
            return new Color(0,204,255);//a type of blue
        else if(symbolID.equals("WOS-HHDS--P----"))//snags/stumps - 0,204,255
            return new Color(0,204,255);
        else if(symbolID.equals("WOS-HHDWB-P----"))//wreck - 0,204,255
            return new Color(0,204,255);
        else if(symbolID.equals("WOS-TCCTG-P----"))//tide gauge - 210, 176, 106
            return new Color(210,176,106);
        else if(symbolID.equals("WO-DHCW-----A--"))//water
            return new Color(255,255,255);
        else if (symbolID.equals("WO-DHABP----A--") ||
            symbolID.equals("WO-DHHD-----A--") ||
            symbolID.equals("WO-DHHDD----A--") ||
            symbolID.equals("WO-DMCC-----A--")) 
        {
            return new Color(0,0,255);
        }
        else if(symbolID.equals("WO-DHPMD----A--"))//drydock
            return new Color(188,153,58);
        else return null;
    }

    /**
     * @name hierarchyToSymbolID
     *
     * @desc Takes in the hierarchy passed in and, returns a formatted string
     * that has only the necessary static characters needed to draw a symbol.
     *
     * @param strHierarchy - IN - A MilStd hierarchy
     * @return A properly formated basic symbol ID
     * @deprecated - Not all symbols have a hierarchy anymore
     */
    public static
            String hierarchyToSymbolID(String strHierarchy)
    {/*
         try
         {
         String strBasicID = "";
         SymbolDefTable symDefTable = SymbolDefTable.getInstance();
         UnitDefTable unitDefTable = UnitDefTable.getInstance();
         // var arrUD:ArrayCollection;
         // var arrSD:ArrayCollection;
         String[] arrUD;
         String[] arrSD;
         if(strHierarchy.charAt(0) == ("1")
         || strHierarchy.charAt(0) == ("4")
         || strHierarchy.charAt(0) == ("5"))
         {
         arrUD = unitDefTable.searchByHierarchy(strHierarchy);
         if((arrUD != null) && (arrUD.length > 0) && (arrUD[0] != null))
         {
         // strBasicID = UnitDef(arrUD[0]).basicSymbolId;
         }
         }
         else if(strHierarchy.charAt(0) == ("2") || strHierarchy.charAt(0) == ("3"))
         {
         // arrSD = symDefTable.searchByHierarchy(strHierarchy);
         arrSD = new String[] {};
         if((arrSD != null) && (arrSD.length > 0) && (arrSD[0] != null))
         {
         // strBasicID = SymbolDef(arrSD[0]).basicSymbolId;
         }
         }
         else
         {
         arrUD = unitDefTable.searchByHierarchy(strHierarchy);
         // If symbol is in UnitDefTable
         if((arrUD != null) && (arrUD.length > 0) && (arrUD[0] != null))
         {
         // strBasicID = UnitDef(arrUD[0]).basicSymbolId;
         }
         else
         {
         // Symbol is not in UnitDefTable, see if it is in SymbolDefTable
         // arrSD = symDefTable.searchByHierarchy(strHierarchy);
         arrSD = new String[] {};
         if((arrSD != null) && (arrSD.length > 0) && (arrSD[0] != null))
         {
         // strBasicID = SymbolDef(arrSD[0]).basicSymbolId;
         }
         }
         }
         return strBasicID;
         }
         catch(Throwable t)
         {
         System.out.println(t);
         }*/

        return "";
    }


    /**
     * Determines if the symbol is a tactical graphic
     *
     * @param strSymbolID
     * @return true if symbol starts with "G", or is a weather graphic, or a
     * bridge graphic
     */
    public static boolean isTacticalGraphic(String strSymbolID)
    {
        try
        {
            if (strSymbolID == null) // Error handling
            {
                return false;
            }
            if ((strSymbolID.charAt(0) == 'G') || (isWeather(strSymbolID))
                    || isEMSNaturalEvent(strSymbolID)
                    || isBasicShape(strSymbolID))
            {
                return true;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    }

    public static boolean isBasicShape(String symbolID)
    {
        if (symbolID != null && symbolID.length() >= 2)
        {
            if (symbolID.startsWith("BS_") || symbolID.startsWith("BBS_") || symbolID.startsWith("PBS_"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Determines if symbols is a warfighting symbol.
     *
     * @param strSymbolID
     * @return True if code starts with "O", "S", or "I". (or "E" in 2525C)
     */
    public static
            boolean isWarfighting(String strSymbolID)
    {
        try
        {
            if (strSymbolID == null) // Error handling
            {
                return false;
            }
            if ((strSymbolID.charAt(0) == 'O' || (strSymbolID.charAt(0) == 'S')
                    || (strSymbolID.charAt(0) == 'I') || (strSymbolID.charAt(0) == 'E' && strSymbolID.charAt(2) != 'N')))
            {
                return true;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    }

    /**
     * Determines if the symbol is a weather graphic
     *
     * @param strSymbolID
     * @return true if symbolID starts with a "W"
     */
    public static
            boolean isWeather(String strSymbolID)
    {
        try
        {
            if(strSymbolID.charAt(0) == 'W')
            	return true;
            else
            	return false;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    }

    /**
     * Determines if a String represents a valid number
     *
     * @param text
     * @return "1.56" == true, "1ab" == false
     */
    public static
            boolean isNumber(String text)
    {
        if (text != null && text.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public static String colorToHexString(Color color, Boolean withAlpha)
    {
    	String hex = color.toHexString();
    	if(withAlpha)
    		return hex;
    	else
    		return hex.substring(2);
    }

    /**
     *
     * @param hexValue - String representing hex value (formatted "0xRRGGBB"
     * i.e. "0xFFFFFF") OR formatted "0xAARRGGBB" i.e. "0x00FFFFFF" for a color
     * with an alpha value I will also put up with "RRGGBB" and "AARRGGBB"
     * without the starting "0x"
     * @return
     */
    public static Color getColorFromHexString(String hexValue)
    {
        try
        {
            if(hexValue==null || hexValue.isEmpty())
                return null;
            String hexOriginal = hexValue;

            String hexAlphabet = "0123456789ABCDEF";

            if (hexValue.charAt(0) == '#')
            {
                hexValue = hexValue.substring(1);
            }
            if (hexValue.substring(0, 2).equals("0x") || hexValue.substring(0, 2).equals("0X"))
            {
                hexValue = hexValue.substring(2);
            }

            hexValue = hexValue.toUpperCase();

            int count = hexValue.length();
            int[] value = null;
            int k = 0;
            int int1 = 0;
            int int2 = 0;

            if (count == 8 || count == 6)
            {
                value = new int[(count / 2)];
                for (int i = 0; i < count; i += 2)
                {
                    int1 = hexAlphabet.indexOf(hexValue.charAt(i));
                    int2 = hexAlphabet.indexOf(hexValue.charAt(i + 1));
                    value[k] = (int1 * 16) + int2;
                    k++;
                }

                if (count == 8)
                {
                    return new Color(value[1], value[2], value[3], value[0]);
                }
                else if (count == 6)
                {
                    return new Color(value[0], value[1], value[2]);
                }
            }
            else
            {
                ErrorLogger.LogMessage("SymbolUtilties", "getColorFromHexString", "Bad hex value: " + hexOriginal, Level.WARNING);
            }
            return null;

            /*//Old Approach
             Color returnVal = null;
         
             if(hexValue.startsWith("0x"))//0xRRGGBB or 0xAARRGGBB
             {
             if(hexValue.length()==8)
             {
             returnVal = Color.decode(hexValue);
             }
             else if(hexValue.length()==10)
             {
             String color = "0x"+hexValue.substring(4);
             String alpha = "0x"+hexValue.substring(2,4);
             returnVal = Color.decode(color);
             returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
             }
             }
             else if(hexValue.startsWith("#"))//#RRGGBB or #AARRGGBB
             {
             if(hexValue.length()==7)
             {
             returnVal = Color.decode("0x"+hexValue.substring(1, 7));
             }
             else if(hexValue.length()==9)
             {
             String color = "0x"+hexValue.substring(3);
             String alpha = "0x"+hexValue.substring(1,3);
             returnVal = Color.decode(color);
             returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
             }
             }
             else//just RRGGBB or AARRGGBB without the starting 0x
             {
             if(hexValue.length()==6)
             {
             returnVal = Color.decode("0x"+hexValue);
             }
             else if(hexValue.length()==8)
             {
             String color = "0x"+hexValue.substring(2);
             String alpha = "0x"+hexValue.substring(0,2);
             returnVal = Color.decode(color);
             returnVal = new Color(returnVal.getRed(), returnVal.getGreen(), returnVal.getBlue(), Integer.decode(alpha));
             }
             }
         
             return returnVal;*/
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("SymbolUtilities", "getColorFromHexString", exc);
            return null;
        }
    }


    /**
     * Symbols that don't exist outside of MCS
     *
     * @param sd
     * @return
     */
    public static
            boolean isMCSSpecificTacticalGraphic(SymbolDef sd)
    {
        if (sd.getHierarchy().startsWith("2.X.7") || //Engineering Overlay graphics (ESRI----)
                sd.getHierarchy().startsWith("2.X.5.2.3") || //Route Critical Points
                sd.getBasicSymbolId().startsWith("G*R*") || //Route Critical Points
                sd.getHierarchy().startsWith("21.X") || //JCID (21.X)
                sd.getBasicSymbolId().startsWith("G*E*"))//MCS Eng (20.X)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static
            boolean is3dAirspace(String symbolID)
    {
        if (symbolID == ("CYLINDER-------")
                || symbolID == ("ORBIT----------")
                || symbolID == ("ROUTE----------")
                || symbolID == ("POLYGON--------")
                || symbolID == ("RADARC---------")
                || symbolID == ("POLYARC--------")
                || symbolID == ("CAKE-----------")
                || symbolID == ("TRACK----------")
                || symbolID == ("CURTAIN--------"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Symbols that don't exist outside of MCS or units that are no longer
     * supported like those from the SASO Proposal.
     *
     * @param ud
     * @return
     */
    public static
            boolean isMCSSpecificForceElement(UnitDef ud)
    {
        if (isSASO(ud))//SASO
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Just checks the symbolID if it could be rendered in 3D. Does not check
     * for needed modifiers.
     *
     * @param symbolID
     * @return
     */
    public static
            Boolean is3dGraphic(String symbolID)
    {
        String symbolId = symbolID.substring(4, 10);

        if (symbolId.equals("ACAI--") || // Airspace Coordination Area Irregular
                symbolId.equals("ACAR--") || // Airspace Coordination Area Rectangular
                symbolId.equals("ACAC--") || // Airspace Coordination Area Circular
                symbolId.equals("AKPC--") || // Kill box circular
                symbolId.equals("AKPR--") || // Kill box rectangular
                symbolId.equals("AKPI--") || // Kill box irregular
                symbolId.equals("ALC---") || // Air corridor
                symbolId.equals("ALM---") || // 
                symbolId.equals("ALS---") || // SAAFR
                symbolId.equals("ALU---") || // UAV
                symbolId.equals("ALL---") || // Low level transit route
                symbolId.equals("AAR---")
                || symbolId.equals("AAF---")
                || symbolId.equals("AAH---")
                || symbolId.equals("AAM---") || // MEZ
                symbolId.equals("AAML--") || // LOMEZ
                symbolId.equals("AAMH--"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * Symbols from the SASO Proposal. Most were replaced by the USAS 13-14
     * update or 2525C.
     *
     * @param sd
     * @return
     */
    public static
            boolean isSASO(UnitDef sd)
    {
        if (sd.getHierarchy().startsWith("5.X.10") || //SASOP Individuals
                sd.getHierarchy().startsWith("5.X.11") || //SASOP Organization/groups
                sd.getHierarchy().startsWith("5.X.12") ||//SASOP //replaced by USAS 13-14 update
                sd.getHierarchy().startsWith("5.X.13") || //SASOP Structures
                sd.getHierarchy().startsWith("5.X.14")) //SASOP Equipment/Weapons
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static
            boolean isCheckPoint(String strSymbolID)
    {
        try
        {
            String strBasicSymbolID = getBasicSymbolID(strSymbolID);
            boolean blRetVal = false;
            if (strBasicSymbolID.equals("G*G*GPPE--****X")//release point
                    || strBasicSymbolID.equals("G*G*GPPK--****X")//check point
                    || strBasicSymbolID.equals("G*G*GPPS--****X"))//start point
            {
                blRetVal = true;
            }
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsCheckPoint

    /**
     * @name IsCriticalPoint
     *
     * @desc Returns true if the symbolID is a critical point.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * critical point
     * @return True if the graphic is a critical point, false otherwise.
     */
    public static
            boolean isCriticalPoint(String strSymbolID)
    {
        try
        {
            String strBasicSymbolID = getBasicSymbolID(strSymbolID);
            boolean blRetVal = false;
            if (isTacticalGraphic(strBasicSymbolID))
            {
                String[] arr = new String[]
                {
                    "G*M*BDD---****X",
                    "G*M*BDE---****X",
                    "G*M*BDI---****X",
                    "G*R*CN----****X",
                    "G*R*CP----****X",
                    "G*R*FD----****X",
                    "G*R*FR----****X",
                    "G*R*PCC---****X",
                    "G*R*PCO---****X",
                    "G*R*PDC---****X",
                    "G*R*PHP---****X",
                    "G*R*PMC---****X",
                    "G*R*PO----****X",
                    "G*R*PPO---****X",
                    "G*R*PTO---****X",
                    "G*R*RLGC--****X",
                    "G*R*SG----****X",
                    "G*R*SSC---****X",
                    "G*R*SC----****X",
                    "G*R*TN----****X",
                    "G*R*UP----****X"
                };
                int arrLength = arr.length;
                for (int i = 0; i < arrLength; i++)
                {
                    if (arr[i].equals(strBasicSymbolID))
                    {
                        blRetVal = true;
                        break;
                    }
                }
            }
            else
            {
                if (strBasicSymbolID.equals("O*E*AL---------")
                        || strBasicSymbolID.equals("O*E*AM---------")
                        || strBasicSymbolID.equals("S*G*IMNB-------"))
                {
                    blRetVal = true;
                }
            }
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    }

    /**
     * @name IsRoute
     *
     * @desc Returns true if the symbolID is a route.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * route
     * @return True if the graphic is a route, false otherwise.
     */
    public static
            boolean isRoute(String strSymbolID)
    {
        try
        {
            String strBasicSymbolID = getBasicSymbolID(strSymbolID);
            boolean blRetVal = false;
            if (strBasicSymbolID.equals("G*S*LRA---****X") || strBasicSymbolID.equals("G*S*LRM---****X"))
            {
                blRetVal = true;
            }
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsRoute

    /**
     * @name IsRoad
     *
     * @desc Returns true if the symbolID is a road.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * road
     * @return True if the graphic is a road, false otherwise.
     */
    public static
            boolean isRoad(String strSymbolID)
    {
        try
        {
            String strBasicSymbolID = getBasicSymbolID(strSymbolID);
            boolean blRetVal = false;
            if (strBasicSymbolID.equals("ROAD------****X"))
            {
                blRetVal = true;
            }
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsRoad

    /**
     * @name IsJWARN
     *
     * @desc Returns true if the symbolID is a JWARN symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * JWARN graphic
     * @return True if the graphic is a JWARN symbol, false otherwise.
     */
    public static
            boolean isJWARN(String strSymbolID)
    {
        try
        {
            if (strSymbolID.substring(0, 5).equals("JWARN"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsJWARN

    /**
     * @name IsMOOTW
     *
     * @desc Returns true if the symbolID is a MOOTW symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * MOOTW graphic
     * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B or
     * STBOPS in 2525C, false otherwise.
     */
    public static
            boolean isMOOTW(String strSymbolID)
    {
        try
        {
            if (strSymbolID.charAt(0) == 'O')
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsMOOTW

    /**
     * @name isSTBOPS
     *
     * @desc Returns true if the symbolID is a Stability Operations (STBOPS)
     * symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * isStabilityOperations graphic
     * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B or
     * STBOPS in 2525C, false otherwise.
     */
    public static
            boolean isSTBOPS(String strSymbolID)
    {
        try
        {
            if (strSymbolID.charAt(0) == 'O')
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isStabilityOperations

    /**
     * @name IsMOOTW
     *
     * @desc Returns true if the symbolID is an event symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * MOOTW graphic
     * @return True if the graphic is a MOOTW symbol in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isEvent(String strSymbolID)
    {
        try
        {
            String[] arr = null;
            char category = strSymbolID.charAt(2);
            String strBasicSymbolID = getBasicSymbolID(strSymbolID);
            if (isMOOTW(strSymbolID)
                    || (isEMS(strSymbolID)
                    && (category == 'I' || category == 'N' || category == 'O')))
            {
                return true;
            }
            else
            {

                arr = new String[]
                {
                    "S*G*EXI---*****",
                    "S*G*EXI---MO***"
                };
                int arrLength = arr.length;
                for (int i = 0; i < arrLength; i++)
                {
                    if (arr[i].equals(strBasicSymbolID))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsMOOTW

    /**
     * @name isHQ
     *
     * @desc Determines if the symbol id passed in contains a flag for one of
     * the various HQ options Pos 11 of the symbol code
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a HQ
     * @return True if the graphic is a HQ symbol in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isHQ(String strSymbolID)
    {
        try
        {
            char mod1 = strSymbolID.charAt(10);
            boolean blRetVal = (mod1 == 'A'
                    || mod1 == 'B'
                    || mod1 == 'C' || mod1 == 'D');
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isHQ

    /**
     * @name isTaskForce
     *
     * @desc Returns whether or not the given symbol id contains task force.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it contains
     * task force
     * @return Returns true if the symbol id contains task force, false
     * otherwise.
     */
    public static
            boolean isTaskForce(String strSymbolID)
    {
        try
        {
            // Return whether or not task force is included in the symbol id.
            char mod1 = strSymbolID.charAt(10);
            boolean blRetVal = (mod1 == 'B'
                    || mod1 == 'D'
                    || mod1 == 'E' || mod1 == 'G');
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsTaskForce

    /**
     * @name isFeintDummy
     *
     * @desc Returns whether or not the given symbol id contains FeintDummy.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it contains
     * feint dummy
     * @return Returns true if the symbol id contains FeintDummy, false
     * otherwise.
     */
    public static
            boolean isFeintDummy(String strSymbolID)
    {
        try
        {
            char mod1 = strSymbolID.charAt(10);
            // Return whether or not feintdummy is included in the symbol id.
            boolean blRetVal = (mod1 == 'C'
                    || mod1 == 'D'
                    || mod1 == 'F' || mod1 == 'G');

            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsFeintDummy

    /**
     * @name isMobilityWheeled
     *
     * @desc Determines if the symbol id passed in contains a flag for the
     * various Wheeled Mobility options Pos 11 and 12 of the symbol code
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * Wheeled Mobility
     * @return True if the graphic (equipment only) is a Wheeled Mobility in the
     * MIL-STD 2525B, false otherwise.
     */
    public static
            boolean isMobilityWheeled(String strSymbolID)
    {
        boolean mobilityWheeledIsOn = false;
        try
        {
            // See if the mobility wheeled modifier is on.
            mobilityWheeledIsOn = (isEquipment(strSymbolID)
                    && strSymbolID.charAt(10) == 'M'
                    && strSymbolID.charAt(11) == 'O');
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        // Return whether or not the mobility wheeled modifier is on.
        return mobilityWheeledIsOn;
    }

    /**
     * Symbol has a mobility modifier
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isMobility(String strSymbolID)
    {
        boolean mobilityIsOn = false;
        try
        {
            String mod = strSymbolID.substring(10, 12);
            //if(isEquipment(strSymbolID))
            //{
            if (mod.equals("MO")
                    || mod.equals("MP")
                    || mod.equals("MQ")
                    || mod.equals("MR")
                    || mod.equals("MS")
                    || mod.equals("MT")
                    || mod.equals("MU")
                    || mod.equals("MV")
                    || mod.equals("MW")
                    || mod.equals("MX")
                    || mod.equals("MY")
                    || mod.equals("NS")
                    || mod.equals("NL"))
            {
                mobilityIsOn = true;
            }
            //}
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        // Return whether or not the mobility wheeled modifier is on.
        return mobilityIsOn;
    }

    /**
     * Returns true if Symbol is a Target
     *
     * @param strSymbolID
     * @return
     */
    public static Boolean isTarget(String strSymbolID)
    {
        String basicID = SymbolUtilities.getBasicSymbolID(strSymbolID);
        String sub = basicID.substring(0, 6);
        if (sub.equals("G*F*PT") ||//fire support/point/point target
                sub.equals("G*F*LT") ||//fire support/lines/linear target
                sub.equals("G*F*AT"))//fire support/area/area target
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if Symbol is an Air Track
     *
     * @param strSymbolID
     * @return
     */
    public static Boolean isAirTrack(String strSymbolID)
    {
        if (strSymbolID.charAt(0) == 'S'
                && strSymbolID.charAt(2) == 'A')
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @name isObstacle
     *
     * @desc Returns true if the symbol id passed in is an Obstacle symbol code.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is an
     * Obstacle
     * @return True if the graphic is an Obstacle in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isObstacle(String strSymbolID)
    {
        try
        {
            // An Obstacle is denoted by the symbol code "G*M*O"
            // So see if it is a tactical graphic then check to see
            // if we have the M and then the O in the correct position.
            boolean blRetVal = ((isTacticalGraphic(strSymbolID)) && ((strSymbolID.charAt(2) == 'M') && (strSymbolID.charAt(4) == 'O')));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isObstacle

    /**
     * @name isDeconPoint
     *
     * @desc Returns true if the symbol id is a DECON (NBC graphic) point
     * symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * Decon Point
     * @return True if the graphic is a Decon Point in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isDeconPoint(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((isNBC(strSymbolID)) && (strSymbolID.substring(4, 6).equals("ND")));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isDeconPoint

    /**
     * @name isEchelonGraphic
     *
     * @desc Returns true if the graphic is to have an echelon associated with
     * it.
     *
     *
     * Here's the rules: Single point tactical graphics do not have an echelon
     * (see ms2525b ch2 5.5.2.2) Weather graphics do not have an echelon - don't
     * set one Neither do Decon Point graphics Neither do Bridge graphics Pretty
     * much only Obstacles, Units, and SOF get echelons
     *
     * @param strSymbolID - IN - A basic MilStd2525B symbolID
     * @param symStd RendererSettings.Symbology_2525C
     * @return True if the graphic displays an echelon, false if it ignores the
     * echelon field
     * @deprecated
     */
    public static
            boolean isEchelonGraphic(String strSymbolID, int symStd)
    {
        try
        {
            // Here's the rules:
            // Single point tactical graphics do not have an echelon (see ms2525b ch2
            // 5.5.2.2)
            // Weather graphics do not have an echelon - don't set one
            // Neither do Decon Point graphics
            // Neither do Bridge graphics
            // Pretty much only Obstacles, Units, and SOF get echelons
            boolean blIsSinglePointTG = false;
            String basicID = getBasicSymbolID(strSymbolID);
            if (isTacticalGraphic(strSymbolID))
            {
                SymbolDefTable symDefTable = SymbolDefTable.getInstance();
                SymbolDef sd = symDefTable.getSymbolDef(basicID, symStd);
                /*if (sd.getGeometry().equals("point"))
                {
                    blIsSinglePointTG = true;
                }//*/
                if(sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POINT)
                {
                	blIsSinglePointTG = true;
                }
            }
            boolean blRetVal = (((isUnit(strSymbolID))/* || (isMOOTW(strSymbolID))*/
                    || (isSOF(strSymbolID))
                    || (isMOOTW(strSymbolID))
                    || ((SymbolUtilities.isObstacle(strSymbolID)) && (!blIsSinglePointTG))
                    || (basicID.equals("G*G*GLB---****X"))
                    || (basicID.equals("G*G*DAB---****X"))
                    || (basicID.equals("G*G*DABP--****X"))
                    || (basicID.equals("G*M*SP----****X"))));

            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isEchelonGraphic

    /**
     * Reads the Symbol ID string and returns the text that represents the
     * echelon code.
     *
     * @param echelon
     * @return
     */
    public static
            String getEchelonText(String echelon)
    {
        char[] dots = new char[3];
        dots[0] = (char) 8226;
        dots[1] = (char) 8226;
        dots[2] = (char) 8226;
        String dot = new String(dots);
        String text = null;
        if (echelon != null)
        {
            if (echelon.equals("A"))
            {
                text = "0";
            }
            else if (echelon.equals("B"))
            {
                text = dot.substring(0, 1);
            }
            else if (echelon.equals("C"))
            {
                text = dot.substring(0, 2);
            }
            else if (echelon.equals("D"))
            {
                text = dot;
            }
            else if (echelon.equals("E"))
            {
                text = "|";
            }
            else if (echelon.equals("F"))
            {
                text = "||";
            }
            else if (echelon.equals("G"))
            {
                text = "|||";
            }
            else if (echelon.equals("H"))
            {
                text = "X";
            }
            else if (echelon.equals("I"))
            {
                text = "XX";
            }
            else if (echelon.equals("J"))
            {
                text = "XXX";
            }
            else if (echelon.equals("K"))
            {
                text = "XXXX";
            }
            else if (echelon.equals("L"))
            {
                text = "XXXXX";
            }
            else if (echelon.equals("M"))
            {
                text = "XXXXXX";
            }
            else if (echelon.equals("N"))
            {
                text = "++";
            }
        }
        return text;
    }

    /**
     * @name isUnit
     *
     * @desc Returns true if the symbolID is a unit.
     *
     * @param strSymbolID - IN - SymbolID we are checking on
     * @return True if the graphic is a unit in the MIL-STD 2525B or is a
     * special operation forces unit, false otherwise.
     */
    public static
            boolean isUnit(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S')
                    && (strSymbolID.charAt(2) == 'G')
                    && (strSymbolID.charAt(4) == 'U'));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isUnit

    /**
     * @name isNBC
     *
     * @desc Returns true if the symbol id passed in is a NBC symbol code.
     *
     * @param strSymbolID - IN - SymbolID we are checking on
     * @return True if the graphic is a NBC in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isNBC(String strSymbolID)
    {
        try
        {
            String temp = getBasicSymbolID(strSymbolID);
            boolean blRetVal = ((isTacticalGraphic(strSymbolID)) && (temp.substring(0, 5).equals("G*M*N")));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isNBC

    /**
     * returns true if the symbol code represents a symbol that has control
     * points
     *
     * @param symStd RendererSettings.Symbology_2525C
     * @param strSymbolID
     * @return
     */
    public static
            boolean isTGWithControlPoints(String strSymbolID, int symStd)
    {
        String temp = getBasicSymbolID(strSymbolID);
        SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(temp, symStd);

        if (sd != null && sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_ROUTE)
        {
            return true;
        }
        else
        {
            return false;//blRetVal;
        }
    }

    /**
     * There's a handful of single point tactical graphics with unique modifier
     * positions.
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isTGSPWithSpecialModifierLayout(String strSymbolID)
    {
        String temp = getBasicSymbolID(strSymbolID);

        boolean blRetVal = (temp.equals("G*G*GPH---****X"))//Harbor(General) - center
                || (temp.equals("G*G*GPPC--****X")) //Contact Point - center
                || (temp.equals("G*G*GPPD--****X"))//Decisions Point - center
                || (temp.equals("G*G*GPPW--****X")) //Waypoint - right of center
                || (temp.equals("G*G*APP---****X"))//ACP - circle, just below center
                || (temp.equals("G*G*APC---****X"))//CCP - circle, just below center
                || (temp.equals("G*G*DPT---****X")) //Target Reference - target special
                || (temp.equals("G*F*PTS---****X"))//Point/Single Target - target special
                || (temp.equals("G*F*PTN---****X"))//Nuclear Target - target special
                || (temp.equals("G*F*PCF---****X")) //Fire Support Station - right of center
                || (temp.equals("G*M*NZ----****X")) //NUCLEAR DETINATIONS GROUND ZERO
                || (temp.equals("G*M*NEB---****X"))//BIOLOGICAL
                || (temp.equals("G*M*NEC---****X"))//CHEMICAL
                || (temp.equals("G*G*GPRI--****X"))//Point of Interest
                || (temp.equals("G*M*OFS---****X"));//Minefield
        return blRetVal;//blRetVal;
    }

    /**
     * Is a single point tactical graphic that has integral text (like the NBC
     * single points)
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isTGSPWithIntegralText(String strSymbolID)
    {
        String temp = getBasicSymbolID(strSymbolID);

        // ErrorLogger.LogMessage("SU", "integraltext?", temp);
        boolean blRetVal = (temp.equals("G*G*GPRD--****X"))//DLRP (D)
                || (temp.equals("G*G*APU---****X")) //pull-up point (PUP)
                || (temp.equals("G*M*NZ----****X")) //Nuclear Detonation Ground Zero (N)
                || (temp.equals("G*M*NF----****X"))//Fallout Producing (N)
                || (temp.equals("G*M*NEB---****X"))//Release Events Chemical (BIO, B)
                || (temp.equals("G*M*NEC---****X"));//Release Events Chemical (CML, C)

        //if(temp.equals("G*G*GPRD--****X"))
        //    ErrorLogger.LogMessage("DLRP");
        return blRetVal;//blRetVal;
    }

    /**
     * Is tactical graphic with fill
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isTGSPWithFill(String strSymbolID)
    {
        String temp = getBasicSymbolID(strSymbolID);
        boolean blRetVal = isDeconPoint(temp)//Decon Points
                || temp.startsWith("G*S*P")//TG/combat service support/points
                || (temp.equals("G*G*GPP---****X"))//Action points (general)
                || (temp.equals("G*G*GPPK--****X"))//Check Point
                || (temp.equals("G*G*GPPL--****X"))//Linkup Point
                || (temp.equals("G*G*GPPP--****X"))//Passage Point
                || (temp.equals("G*G*GPPR--****X"))//Rally Point
                || (temp.equals("G*G*GPPE--****X"))//Release Point
                || (temp.equals("G*G*GPPS--****X"))//Start Point
                || (temp.equals("G*G*GPPA--****X"))//Amnesty Point
                || (temp.equals("G*G*GPPN--****X"))//Entry Control Point
                || (temp.equals("G*G*APD---****X"))//Down Aircrew Pickup Point
                || (temp.equals("G*G*OPP---****X"))//Point of Departure
                || (temp.equals("G*F*PCS---****X"))//Survey Control Point
                || (temp.equals("G*F*PCB---****X"))//Firing Point
                || (temp.equals("G*F*PCR---****X"))//Reload Point
                || (temp.equals("G*F*PCH---****X"))//Hide Point
                || (temp.equals("G*F*PCL---****X"))//Launch Point
                || (temp.equals("G*M*BCP---****X"))//Engineer Regulating Point
                || (temp.equals("G*O*ES----****X"))//Emergency Distress Call

                //star
                || (temp.startsWith("G*G*GPPD-"))//Decision Point    

                //circle
                || (temp.equals("G*G*GPPO--****X"))//Coordination Point
                || (temp.equals("G*G*APP---****X"))//ACP
                || (temp.equals("G*G*APC---****X"))//CCP
                || (temp.equals("G*G*APU---****X"))//PUP

                //circle with squiggly
                || (temp.startsWith("G*G*GPUY"))//SONOBUOY and those that fall under it

                //reference point
                || ((temp.startsWith("G*G*GPR") && temp.charAt(7) != 'I'))
                //NBC
                || (temp.equals("G*M*NEB---****X"))//BIO
                || (temp.equals("G*M*NEC---****X")) //CHEM
                || (temp.equals("G*M*NF----****X")) //fallout producing
                || (temp.equals("G*M*NZ----****X"));//NUC

        return blRetVal;
    }

    public static
            boolean hasDefaultFill(String strSymbolID)
    {
        if (SymbolUtilities.isTacticalGraphic(strSymbolID))
        {
            String temp = SymbolUtilities.getBasicSymbolID(strSymbolID);
            //SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(temp);
            if ((temp.equals("G*M*NEB---****X"))//BIO
                    || (temp.equals("G*M*NEC---****X")) //CHEM
                    // || (temp.equals("G*M*NF----****X")) //fallout producing
                    || (temp.equals("G*M*NZ----****X")))//NUC)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     *
     * @param strSymbolID
     * @return
     */
    public static
            String getTGFillSymbolCode(String strSymbolID)
    {
        String temp = getBasicSymbolID(strSymbolID);
        if (temp.equals("G*M*NEB---****X"))
        {
            return "NBCBIOFILL****X";
        }
        if (temp.equals("G*M*NEC---****X"))
        {
            return "NBCCMLFILL****X";
        }
        if (temp.equals("G*M*NZ----****X") || temp.equals("G*M*NF----****X"))
        {
            return "NBCNUCFILL****X";
        }
        if (temp.startsWith("G*G*GPUY"))
        {
            return "SONOBYFILL****X";
        }
        if ((temp.equals("G*G*GPPO--****X"))//Coordination Point
                || (temp.equals("G*G*APP---****X"))//ACP
                || (temp.equals("G*G*APC---****X"))//CCP
                || (temp.equals("G*G*APU---****X")))//PUP)
        {
            return "CPOINTFILL****X";
        }
        if (isDeconPoint(temp)//Decon Points
                || temp.startsWith("G*S*P")//TG/combat service support/points
                || (temp.equals("G*G*GPP---****X"))//Action points (general)
                || (temp.equals("G*G*GPPK--****X"))//Check Point
                || (temp.equals("G*G*GPPL--****X"))//Linkup Point
                || (temp.equals("G*G*GPPP--****X"))//Passage Point
                || (temp.equals("G*G*GPPR--****X"))//Rally Point
                || (temp.equals("G*G*GPPE--****X"))//Release Point
                || (temp.equals("G*G*GPPS--****X"))//Start Point
                || (temp.equals("G*G*GPPA--****X"))//Amnesty Point
                || (temp.equals("G*G*APD---****X"))//Down Aircrew Pickup Point
                || (temp.equals("G*G*OPP---****X"))//Point of Departure
                || (temp.equals("G*F*PCS---****X"))//Survey Control Point
                || (temp.equals("G*F*PCB---****X"))//Firing Point
                || (temp.equals("G*F*PCR---****X"))//Reload Point
                || (temp.equals("G*F*PCH---****X"))//Hide Point
                || (temp.equals("G*F*PCL---****X"))//Launch Point
                || (temp.equals("G*G*GPPN--****X"))//Entry Control Point
                || (temp.equals("G*O*ES----****X"))//Emergency Distress Call
                || (temp.equals("G*M*BCP---****X")))//Engineer Regulating Point
        {
            return "CHKPNTFILL****X";
        }
        if (temp.startsWith("G*G*GPR") && temp.charAt(7) != 'I')
        {
            return "REFPNTFILL****X";
        }
        if (temp.startsWith("G*G*GPPD"))
        {
            return "DECPNTFILL****X";
        }

        return null;
    }

    public static
            boolean isWeatherSPWithFill(String symbolID)
    {
        if (symbolID.equals("WOS-HPM-R-P----") ||//landing ring - brown 148,48,0
                symbolID.equals("WOS-HPD---P----") ||//dolphin facilities - brown
                symbolID.equals("WOS-HABB--P----") ||//buoy default - 255,0,255
                symbolID.equals("WOS-HHRS--P----") ||//rock submerged - 0,204,255
                symbolID.equals("WOS-HHDS--P----") ||//snags/stumps - 0,204,255
                symbolID.equals("WOS-HHDWB-P----") ||//wreck - 0,204,255
                symbolID.equals("WOS-TCCTG-P----"))//tide gauge - 210, 176, 106
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @name isSOF
     *
     * @desc Returns true if the symbolID is an SOF (special operations forces)
     * graphic
     *
     * @param strSymbolID - IN - SymbolID we are checking on
     * @return True if the graphic is a SOF in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isSOF(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S') && (strSymbolID.charAt(2) == 'F'));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isSOF

    /**
     * @desc Returns true if the symbol id is a Sonobuoy point symbol.
     *
     * @param strSymbolID - IN - Symbol Id we are checking to see if it is a
     * Sonobuoy Point
     * @return True if the graphic is a Decon Point in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isSonobuoy(String strSymbolID)
    {
        try
        {
            String basic = getBasicSymbolID(strSymbolID);
            boolean blRetVal = (basic.substring(0, 8) == "G*G*GPUY");
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isSOF

    /**
     * @name isSeaSurface
     *
     * @desc Returns true if the symbolID is an warfighting/seasurface graphic
     *
     * @param strSymbolID - IN - SymbolID we are checking on
     * @return True if the graphic is a seasurface in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isSeaSurface(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S') && (strSymbolID.charAt(2) == 'S'));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isSOF

    /**
     * @name isSubSurface
     *
     * @desc Returns true if the symbolID is an warfighting/subsurface graphic
     *
     * @param strSymbolID - IN - SymbolID we are checking on
     * @return True if the graphic is a subsurface in the MIL-STD 2525B, false
     * otherwise.
     */
    public static
            boolean isSubSurface(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S') && (strSymbolID.charAt(2) == 'U'));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End isSOF


    /**
     * @name isEquipment
     *
     * @desc Returns true if the symbol id is an Equipment Id (S*G*E).
     *
     * @param strSymbolID - IN - A MilStd2525B symbolID
     * @return True if symbol is Equipment, false otherwise.
     */
    public static
            boolean isEquipment(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S')
                    && (strSymbolID.charAt(2) == 'G')
                    && (strSymbolID.charAt(4) == 'E'));
            // || isEMSEquipment(strSymbolID); //uncomment when supporting 2525C
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsEquipment

    /**
     * determines if an EMS symbol (a symbol code that starts with 'E') Is an
     * equipment type. There is no logical pattern to EMS equipment symbol codes
     * so all we can do is check against a list of codes.
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isEMSEquipment(String strSymbolID)
    {
        String basicCode = getBasicSymbolID(strSymbolID);
        boolean blRetVal = false;
        try
        {
            if (strSymbolID.startsWith("E"))
            {
                if (basicCode.equals("E*O*AB----*****") || //equipment
                        basicCode.equals("E*O*AE----*****") ||//ambulance
                        basicCode.equals("E*O*AF----*****") ||//medivac helicopter
                        basicCode.equals("E*O*BB----*****") ||//emergency operation equipment
                        basicCode.equals("E*O*CB----*****") ||//fire fighting operation equipment
                        basicCode.equals("E*O*CC----*****") ||//fire hydrant
                        basicCode.equals("E*O*DB----*****") ||//law enforcement operation equipment
                        //equipment for different service departments
                        (basicCode.startsWith("E*O*D") && basicCode.endsWith("B---*****"))
                        || //different sensor types
                        (basicCode.startsWith("E*O*E") && basicCode.endsWith("----*****"))
                        || basicCode.equals("E*F*BA----*****") ||//ATM
                        basicCode.equals("E*F*LF----*****") ||//Heli Landing site
                        basicCode.equals("E*F*MA----*****") ||//control valve
                        basicCode.equals("E*F*MC----*****"))// ||//discharge outfall
                {
                    blRetVal = true;
                }
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return blRetVal;
    } // End IsEquipment

    /**
     * determines if an symbol code represents an EMS (Emergency Management
     * Symbol). Returns true only for those that start with 'E'
     *
     * @return
     */
    public static
            boolean isEMS(String strSymbolID)
    {
        //String basicCode = getBasicSymbolID(strSymbolID);
        boolean blRetVal = false;
        try
        {
            if (strSymbolID.startsWith("E"))
            {
                blRetVal = true;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return blRetVal;
    }

    /**
     * Determines if a symbol is an EMS Natural Event
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isEMSNaturalEvent(String strSymbolID)
    {
        boolean blRetVal = false;
        try
        {
            if (strSymbolID.charAt(0) == 'E' && strSymbolID.charAt(2) == 'N')
            {
                blRetVal = true;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return blRetVal;
    }

    /**
     * Determines if a symbol is an EMS Incident
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean isEMSIncident(String strSymbolID)
    {
        boolean blRetVal = false;
        try
        {
            if (strSymbolID.charAt(0) == 'E' && strSymbolID.charAt(2) == 'I')
            {
                blRetVal = true;
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return blRetVal;
    }

    /**
     * @name isInstallation Warfighting ground installations. They are always
     * installations.
     * @desc Returns true if the symbol id is an installation (S*G*I).
     *
     * @param strSymbolID - IN - A MilStd2525B symbolID
     * @return True if symbol is an Installation, false otherwise.
     */
    public static
            boolean isInstallation(String strSymbolID)
    {
        try
        {
            boolean blRetVal = ((strSymbolID.charAt(0) == 'S') && (strSymbolID.charAt(2) == 'G') && (strSymbolID.charAt(4) == 'I'));
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsInstallation

    /**
     * @name isSIGINT
     *
     * @desc Returns true if the symbol id is Signals Intelligence (SIGINT)
     * (starts with 'I').
     *
     * @param strSymbolID - IN - A MilStd2525B symbolID
     * @return True if symbol is a Signals Intelligence, false otherwise.
     */
    public static
            boolean isSIGINT(String strSymbolID)
    {
        try
        {
            boolean blRetVal = (strSymbolID.charAt(0) == 'I');
            return blRetVal;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return false;
    } // End IsInstallation

    /**
     * @name isFeintDummyInstallation
     *
     * @desc Returns true if the symbol id has a feint dummy installation
     * modifier
     *
     * @param strSymbolID - IN - A MilStd2525B symbolID
     * @return True if symbol has a feint dummy installation modifier, false
     * otherwise.
     */
    public static
            boolean isFeintDummyInstallation(String strSymbolID)
    {
        boolean feintDummyInstallationIsOn = false;
        try
        {
            // See if the feint dummy installation is on.
            feintDummyInstallationIsOn = (strSymbolID.charAt(10) == 'H' && strSymbolID.charAt(11) == 'B');
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        // Return whether or not the feint dummy installation is on.
        return feintDummyInstallationIsOn;
    }


    /**
     * has an 'H' in the 11th position Any symbol can have this character added
     * to make it an installation.
     *
     * @param strSymbolID
     * @return
     */
    public static
            boolean hasInstallationModifier(String strSymbolID)
    {
        boolean hasInstallationModifier = false;
        try
        {
            // See if the feint dummy installation is on.
            hasInstallationModifier = (strSymbolID.charAt(10) == ('H'));
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        // Return whether or not the feint dummy installation is on.
        return hasInstallationModifier;
    }

    /**
     * @name getAffiliation
     *
     * @desc This operation will return the affiliation enumeration for the
     * given symbol id. If the symbol has an unknown or offbeat affiliation, the
     * affiliation of "U" will be returned.
     *
     * @param strSymbolID - IN - Symbol Id we want the affiliation of
     * @return The affiliation of the Symbol Id that was passed in.
     */
    public static
            char getAffiliation(String strSymbolID)
    {
        try
        {
            return strSymbolID.charAt(1);
        } // End try
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return 'U';
    } // End GetAffiliation

    /**
     * @name getStatus
     *
     * @desc Returns the status (present / planned) for the symbol id provided.
     * If the symbol contains some other status than planned or present, present
     * is returned by default (no unknown available).
     *
     * @param strSymbolID - IN - 15 char symbol code.
     * @return The status of the Symbol Id that was passed in.
     */
    public static
            String getStatus(String strSymbolID)
    {
        try
        {
            String strStatus = strSymbolID.substring(3, 4);
            return strStatus;
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return "P";
    } // End getStatus

    /**
     * @name getEchelon
     *
     * @desc Returns the echelon enumeration for the symbol id provided. Note;
     * this works only with the sub-set of echelon codes tracked in the SymbolID
     * class. 2525 contains more codes than are tracked here. The 11th char of
     * the symbol id is used to determine the echelon. If we are unable to
     * determine the echelon, we return "NULL".
     *
     * @param strSymbolID - IN - 15 char symbol code.
     * @return The echelon of the Symbol Id that was passed in.
     */
    public static
            String getEchelon(String strSymbolID)
    {
        try
        {
            char tenth = strSymbolID.charAt(10);
            String strSubEch = null;
            if (tenth != 'H' && tenth != 'M' && tenth != 'N')
            {
                strSubEch = strSymbolID.substring(11, 12);
            }
            return strSubEch;
        } // End try
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return "-";
    } // End getEchelon

    /**
     * @name getSymbolModifier
     *
     * @desc Returns the enumeration belonging to the type of symbol modifier
     * used for this symbol ID.
     *
     * @param strSymbolID - IN - A valid 15 char MilStd2525B code.
     * @return The enumeration indicating which symbol modifier is turned on.
     * @deprecated 9/6/2013
     */
    public static
            String getSymbolModifier(String strSymbolID)
    {
        try
        {
            String strModifiers = strSymbolID.substring(10, 11);
            String subModifier;
            if (strModifiers.equals("A"))
            {
                return "A";
            }
            else if (strModifiers.equals("B"))
            {
                return "B";
            }
            else if (strModifiers.equals("C"))
            {
                return "C";
            }
            else if (strModifiers.equals("D"))
            {
                return "D";
            }
            else if (strModifiers.equals("E"))
            {
                return "E";
            }
            else if (strModifiers.equals("F"))
            {
                return "F";
            }
            else if (strModifiers.equals("G"))
            {
                return "G";
            }
            else if (strModifiers.equals("H"))
            {
                subModifier = strSymbolID.substring(11, 12);
                if (subModifier.equals("B"))
                {
                    return "HB";
                }
                else
                {
                    return "H";
                }
            }
            else if (strModifiers.equals("M"))
            {
                subModifier = strSymbolID.substring(11, 12);
                if (subModifier.equals("O"))
                {
                    return "MO";
                }
                else
                {
                    return "M";
                }
            }
        } // End try
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return "-";
    } // End GetSymbolModifier


    public static String getUnitAffiliationModifier(String symbolID, int symStd)
    {
        String textChar = null;
        char affiliation;

        try
        {
            affiliation = symbolID.charAt(1);

            if (affiliation == ('F')
                    || affiliation == ('H')
                    || affiliation == ('U')
                    || affiliation == ('N')
                    || affiliation == ('P'))
            {
                textChar = null;
            }
            else if (affiliation == ('A')
                    || affiliation == ('S'))
            {
                if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14)
                {
                    textChar = "?";
                }
                else
                {
                    textChar = null;
                }
            }
            else if (affiliation == ('J'))
            {
                textChar = "J";
            }
            else if (affiliation == ('K'))
            {
                textChar = "K";
            }
            else if (affiliation == ('D')
                    || affiliation == ('L')
                    || affiliation == ('G')
                    || affiliation == ('W'))
            {
                textChar = "X";
            }
            else if (affiliation == ('M'))
            {
                if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14)
                {
                    textChar = "X?";
                }
                else
                {
                    textChar = "X";
                }
            }

            //check sea mine symbols
            if (symStd == RendererSettings.Symbology_2525C)
            {
                if (symbolID.charAt(0) == 'S' && symbolID.indexOf("WM") == 4)
                {//variuos sea mine exercises
                    if (symbolID.indexOf("GX") == 6
                            || symbolID.indexOf("MX") == 6
                            || symbolID.indexOf("FX") == 6
                            || symbolID.indexOf("X") == 6
                            || symbolID.indexOf("SX") == 6)
                    {
                        textChar = "X";
                    }
                    else
                    {
                        textChar = null;
                    }
                }
            }
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("SymbolUtilities",
                    "getUnitAffiliationModifier", exc, Level.WARNING);
            return null;
        }
        return textChar;
    }

    /**
     * checks symbol code to see if graphic has a DOM (Q) modifier
     *
     * @param symbolID
     * @param symStd RendererSettings.Symbology_2525C
     * @return
     */
    public static
            boolean hasDirectionOfMovement(String symbolID, int symStd)
    {
        SymbolDef temp = null;
        if (isNBC(symbolID))//just 3 NBCs have DOM
        {
            temp = SymbolDefTable.getInstance().getSymbolDef(getBasicSymbolID(symbolID), symStd);
            if (temp != null)
            {
                if (temp.getModifiers().indexOf("Q.") != -1)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
            else
            {
                return false;
            }
        }
        else if (isWarfighting(symbolID))//all warfighting has DOM
        {
            if (SymbolUtilities.isSIGINT(symbolID) == false)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static
            Boolean hasAMmodifierWidth(String symbolID)
    {
        return hasAMmodifierWidth(symbolID, RendererSettings.getInstance().getSymbologyStandard());
    }

    public static Boolean hasAMmodifierWidth(String symbolID, int symStd)
    {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);

        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if (sd != null)
        {
            int dc = sd.getDrawCategory();

            switch (dc)
            {
                case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE://width
                case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                case SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE:
                    returnVal = true;
                    break;
                default:
                    returnVal = false;
            }
        }

        return returnVal;
    }

    public static Boolean hasANmodifier(String symbolID)
    {
        return hasANmodifier(symbolID, RendererSettings.getInstance().getSymbologyStandard());
    }

    public static Boolean hasANmodifier(String symbolID, int symStd)
    {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);

        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if (sd != null)
        {
            int dc = sd.getDrawCategory();

            switch (dc)
            {
                case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE://width
                case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                    returnVal = true;
                    break;
                /*case SymbolDef.DRAW_CATEGORY_LINE://air corridor
                	if(sd.getModifiers().indexOf(ModifiersTG.AN_AZIMUTH + ".") > -1)
                		returnVal = true;
                	break;//*/
                default:
                    returnVal = false;
            }
        }

        return returnVal;
    }

    public static Boolean hasAMmodifierRadius(String symbolID)
    {
        return hasAMmodifierRadius(symbolID, RendererSettings.getInstance().getSymbologyStandard());
    }

    public static Boolean hasAMmodifierRadius(String symbolID, int symStd)
    {
        SymbolDef sd = null;
        Boolean returnVal = false;
        String basic = SymbolUtilities.getBasicSymbolID(symbolID);

        sd = SymbolDefTable.getInstance().getSymbolDef(basic, symStd);
        if (sd != null)
        {
            int dc = sd.getDrawCategory();

            switch (dc)
            {
                case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE://radius
                case SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                    returnVal = true;
                    break;
                default:
                    returnVal = false;
            }
        }

        return returnVal;
    }

    /**
     * @name setAffiliation
     *
     * @desc Sets the affiliation for a Mil-Std 2525B symbol ID.
     *
     * @param strSymbolID - IN - A 15 character symbol ID
     * @param strSymbolID - IN - The affiliation we want to change the ID to.
     * @return A string with the affiliation changed to affiliationID
     */
    public static String setAffiliation(String strSymbolID, String strAffiliationID)
    {
        try
        {
            if (strSymbolID != null && strSymbolID.length() == 15 && isWeather(strSymbolID)==false
                    && strAffiliationID != null && strAffiliationID.length() == 1)
            {
                String strChangedID = strSymbolID.substring(0, 1) + strAffiliationID.toUpperCase() + strSymbolID.substring(2, 15);
                if (hasValidAffiliation(strChangedID))
                {
                    return strChangedID;
                }
                else
                {
                    return strSymbolID;
                }
            }
            else
            {
                return strSymbolID;
            }
        } // End try
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return "";
    } // End SetAffiliation }

    /**
     * @name setEchelon
     *
     * @desc Sets the echelon for a Mil-Std 2525B symbol ID.
     *
     * @param strSymbolID - IN - A 15 character symbol ID
     * @param strSymbolID - IN - A string representing the echelon we want to
     * change the ID to. The case of the string does not matter, it can be upper
     * or lower. The string is the name of the echelon and can be of the
     * following choices:
     *
     * Null, //- Team, Crew, //A Squad, //B Section, //C Platoon, Detachment //D
     * Company, Battery, Troop //E Battalion, Squadron, //F Regiment, Group, //G
     * Brigade, //H Division, //I Corps, Mef, //J Army, //K Army Group, Front,
     * //L Region //M
     * @return A symbol ID with the echelon changed to echelonID
     */
    public static
            String setEchelon(String strSymbolID, String strEchelon)
    {
        String strChangedID = strSymbolID;
        try
        {
            if (strSymbolID.length() == 15)
            {
                String strUppercaseEchelon = strEchelon.toUpperCase();
                strChangedID = strSymbolID.substring(0, 11) + strUppercaseEchelon + strSymbolID.substring(12, 15);
            } // End if (strSymbolID.Length == 15 &&
            // !SymbolUtilities.IsDrawingPrimitive(strSymbolID))
        } // End try
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return strChangedID;
    } // End SetEchelon }}}}

    /**
     * @name setStatus
     *
     * @desc Sets the status for a Mil-Std 2525B symbol ID.
     *
     * @param strSymbolID - IN - A 15 character symbol ID
     * @param strStatusID - IN - The status we want to change the ID to.
     * "present", "planned", "anticipated", "plannedanticipated"
     * @return A string with the status changed to statusID
     * @deprecated
     */
    public static String setStatus(String strSymbolID, String strStatusID)
    {
        // PlannedAnticipated, //A
        // Present //P
        String changedID = strSymbolID;
        try
        {
            if ((strSymbolID.length() == 15)
                    && (!isWeather(strSymbolID)) && (!isBasicShape(strSymbolID)))
            {
                if (strStatusID.toLowerCase().equals("present"))
                {
                    changedID = strSymbolID.substring(0, 3) + "P" + strSymbolID.substring(4, 15);
                }
                else if (strStatusID.equalsIgnoreCase("planned")
                        || strStatusID.equalsIgnoreCase("anticipated")
                        || strStatusID.equalsIgnoreCase("plannedanticipated"))
                {
                    changedID = strSymbolID.substring(0, 3) + "A" + strSymbolID.substring(4, 15);
                }
            } // End if((strSymbolID.Length == 15) && (!
            // SymbolUtilities.IsDrawingPrimitive(strSymbolID)))
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        return changedID;
    } // End SetStatus

    /**
     * @name setSymbolModifier
     *
     * @desc Sets the symbol modifier for a Mil-Std 2525B symbol ID.
     *
     * @param strSymbolID - IN - A 15 character symbol ID
     * @param symbolModifierID - IN - The symbolModifier we want to change the
     * ID to.
     * @return A symbol ID with the symbolModifier changed to symbolModifierID
     * @deprecated
     */
    public static
            String setSymbolModifier(String strSymbolID, String symbolModifierID)
    {
        String strChangedID = strSymbolID;
        if (strSymbolID.length() == 15)
        {
            if (symbolModifierID.equals("-"))
            {
                if (isMobilityWheeled(strSymbolID) || isFeintDummyInstallation(strSymbolID))
                {
                    strChangedID = strSymbolID.substring(0, 10) + "--" + strSymbolID.substring(12, 15);
                }
                else
                {
                    strChangedID = strSymbolID.substring(0, 10) + "-" + strSymbolID.substring(11, 15);
                }
            }
            else if (symbolModifierID.equals("A"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "A" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("B"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "B" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("C"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "C" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("D"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "D" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("E"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "E" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("F"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "F" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("G"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "G" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("HB"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "HB" + strSymbolID.substring(12, 15);
            }
            else if (symbolModifierID.equals("HH"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "H" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("M"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "M" + strSymbolID.substring(11, 15);
            }
            else if (symbolModifierID.equals("MO"))
            {
                strChangedID = strSymbolID.substring(0, 10) + "MO" + strSymbolID.substring(12, 15);
            }
        } // End if
        return strChangedID;
    } // End SetSymbolModifier

}
