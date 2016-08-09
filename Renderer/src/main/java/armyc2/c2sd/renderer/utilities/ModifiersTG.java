/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package armyc2.c2sd.renderer.utilities;

import java.util.ArrayList;

import android.util.SparseArray;

/**
 * This class contains keys to the modifiers.  A number of these we feed off of
 * the symbol code instead of using the property.  But those modifiers remain
 * for completeness.
 *
 * Tactical Graphics:
 * P = points, L = lines, A = areas, BL = boundary lines, N = nuclear, B/C = bio/chem.
 * G = graphic modifier so there is no length.  Most of these we feed off of the symbol
 * code so they aren't actually used.
 * @author michael.spinelli
 */
public class ModifiersTG {

        //public static final int SYMBOL_ID = "Symbol ID";
    //public static final int SOURCE = "Source";
    //public static final int EDITOR_CLASS_TYPE = "Editor Class Type";
    //public static final int URN = "URN";
    //public static final int UIC = "UIC";
    //public static final int ANGLE_OF_ROTATION = "Angle of Rotation";
    /**
     * The innermost part of a symbol that represents a warfighting object
     * Here for completeness, not actually used as this comes from the
     * symbol code.
     * SIDC positions 3, 5-104
     * TG: P,L,A,BL,N,B/C
     * Length: G
     */
    public static final int A_SYMBOL_ICON = 0;
    /**
     * The basic graphic (see 5.5.1).
     * We feed off of the symbol code so this isn't used
     * SIDC positions 11 and 12
     * TG: L,A,BL
     * Length: G
     */
    public static final int B_ECHELON = 1;
    /**
     * A graphic modifier in a boundary graphic that
     * identifies command level (see 5.5.2.2, table V, and
     * figures 10 and 12).
     * TG: N
     * Length: 6
     */
    public static final int C_QUANTITY = 2;
    /**
     * A text modifier for tactical graphics; content is
     * implementation specific.
     * TG: P,L,A,N,B/C
     * Length: 20
     */
    public static final int H_ADDITIONAL_INFO_1 = 3;
    /**
     * A text modifier for tactical graphics; content is
     * implementation specific.
     * TG: P,L,A,N,B/C
     * Length: 20
     */
    public static final int H1_ADDITIONAL_INFO_2 = 4;
    /**
     * A text modifier for tactical graphics; content is
     * implementation specific.
     * TG: P,L,A,N,B/C
     * Length: 20
     */
    public static final int H2_ADDITIONAL_INFO_3 = 5;
    /**
     * A text modifier for tactical graphics; letters "ENY" denote hostile symbols.
     * TG: P,L,A,BL,N,B/C
     * Length: 3
     */
    public static final int N_HOSTILE = 6;
    /**
     * A graphic modifier for CBRN events that
     * identifies the direction of movement (see 5.5.2.1
     * and figure 11).
     * TG: N,B/C
     * Length: G
     */
    public static final int Q_DIRECTION_OF_MOVEMENT = 7;
    /**
     * A graphic modifier for points and CBRN events
     * used when placing an object away from its actual
     * location (see 5.5.2.3 and figures 10, 11, and 12).
     * TG: P,N,B/C
     * Length: G
     */
    public static final int S_OFFSET_INDICATOR = 8;
    /**
     * A text modifier that uniquely identifies a particular
     * tactical graphic; track number.
     * Nuclear: delivery unit (missile, aircraft, satellite,
     * etc.)
     * TG:P,L,A,BL,N,B/C
     * Length: 15 (35 for BL)
     */
    public static final int T_UNIQUE_DESIGNATION_1 = 9;
    /**
     * A text modifier that uniquely identifies a particular
     * tactical graphic; track number.
     * Nuclear: delivery unit (missile, aircraft, satellite,
     * etc.)
     * TG:P,L,A,BL,N,B/C
     * Length: 15 (35 for BL)
     */
    public static final int T1_UNIQUE_DESIGNATION_2 = 10;
    /**
     * A text modifier that indicates nuclear weapon type.
     * TG: N
     * Length: 20
     */
    public static final int V_EQUIP_TYPE = 11;
    /**
     * A text modifier for units, equipment, and installations that displays DTG format:
     * DDHHMMSSZMONYYYY or â€œO/Oâ€� for on order (see 5.5.2.6).
     * TG:P,L,A,N,B/C
     * Length: 16
     */
    public static final int W_DTG_1 = 12;
    /**
     * A text modifier for units, equipment, and installations that displays DTG format:
     * DDHHMMSSZMONYYYY or â€œO/Oâ€� for on order (see 5.5.2.6).
     * TG:P,L,A,N,B/C
     * Length: 16
     */
    public static final int W1_DTG_2 = 13;
    /**
     * A text modifier that displays the minimum,
     * maximum, and/or specific altitude (in feet or
     * meters in relation to a reference datum), flight
     * level, or depth (for submerged objects in feet
     * below sea level). See 5.5.2.5 for content.
     * TG:P,L,A,N,B/C
     * Length: 14
     */
    public static final int X_ALTITUDE_DEPTH = 14;
    /**
     * A text modifier that displays a graphicâ€™s location
     * in degrees, minutes, and seconds (or in UTM or
     * other applicable display format).
     *  Conforms to decimal
     *  degrees format:
     *  xx.dddddhyyy.dddddh
     *  where
     *  xx = degrees latitude
     *  yyy = degrees longitude
     *  .ddddd = decimal degrees
     *  h = direction (N, E, S, W)
     * TG:P,L,A,BL,N,B/C
     * Length: 19
     */
    public static final int Y_LOCATION = 15;

    /**
     * For Tactical Graphics
     * A numeric modifier that displays a minimum,
     * maximum, or a specific distance (range, radius,
     * width, length, etc.), in meters.
     * 0 - 999,999 meters
     * TG: P.L.A
     * Length: 6
     */
    public static final int AM_DISTANCE = 16;
    /**
     * For Tactical Graphics
     * A numeric modifier that displays an angle
     * measured from true north to any other line in
     * degrees.
     * 0 - 359 degrees
     * TG: P.L.A
     * Length: 3
     */
    public static final int AN_AZIMUTH = 17;

    public static final int SYMBOL_FILL_IDS = 90;


    public static final int LENGTH = 30;
    public static final int WIDTH = 31;
    public static final int RADIUS = 32;
    public static final int ANGLE = 33;
    //public static final int SEGMENT_DATA = "Segment Data";
    
    

    /**
     * Returns an Arraylist of the modifer names for tactical graphics
     * @return
     */
    public synchronized static ArrayList<Integer> GetModifierList()
    {
        ArrayList<Integer> list = new ArrayList<Integer>();

        //list.add(ModifierType.A_SYMBOL_ICON);//graphical, feeds off of symbol code
        //list.add(ModifierType.B_ECHELON);//graphical, feeds off of symbol code
        list.add(C_QUANTITY);
        list.add(H_ADDITIONAL_INFO_1);
        list.add(H1_ADDITIONAL_INFO_2);
        list.add(H2_ADDITIONAL_INFO_3);
        list.add(N_HOSTILE);
        list.add(Q_DIRECTION_OF_MOVEMENT);
        list.add(T_UNIQUE_DESIGNATION_1);
        list.add(T1_UNIQUE_DESIGNATION_2);
        list.add(V_EQUIP_TYPE);
        list.add(W_DTG_1);
        list.add(W1_DTG_2);
        list.add(X_ALTITUDE_DEPTH);
        list.add(Y_LOCATION);

        list.add(AM_DISTANCE);//2525C
        //list.add(AM1_DISTANCE);//2525C
        list.add(AN_AZIMUTH);//2525C
        //list.add(AN1_AZIMUTH);//2525C

        //back compat
        list.add(LENGTH);
        list.add(WIDTH);
        list.add(RADIUS);
        list.add(ANGLE);



        return list;
    }

    /**
     *
     * @param modifier like ModifiersTG.C_QUANTITY
     * @return modifier name based on mofidier constants
     */
    public static String getModifierName(int modifier)
    {
        switch(modifier)
        {
            //case A_SYMBOL_ICON:
            //    return "Symbol Icon";
            case B_ECHELON:
                return "Echelon";
            case C_QUANTITY:
                return "Quantity";
            case H_ADDITIONAL_INFO_1:
                return "Additional Info 1";
            case H1_ADDITIONAL_INFO_2:
                return "Additional Info 2";
            case H2_ADDITIONAL_INFO_3:
                return "Additional Info 3";
            case N_HOSTILE:
                return "Hostile";
            case Q_DIRECTION_OF_MOVEMENT:
                return "Direction of Movement";
            //case S_OFFSET_INDICATOR:
            //    return "Offset Indicator";
            case T_UNIQUE_DESIGNATION_1:
                return "Unique Designation 1";
            case T1_UNIQUE_DESIGNATION_2:
                return "Unique Designation 2";
            case V_EQUIP_TYPE:
                return "Equipment Type";
            case W_DTG_1:
                return "Date Time Group 1";
            case W1_DTG_2:
                return "Date Time Group 2";
            case X_ALTITUDE_DEPTH:
                return "Altitude Depth";
            case Y_LOCATION:
                return "Location";
            case AM_DISTANCE:
                return "Distance";
            case AN_AZIMUTH:
                return "Azimuth";
            default:
                return "";

        }
    }

    /**
     *
     * @param modifier like ModifiersTG.C_QUANTITY
     * @return modifier name based on mofidier constants
     */
    public static String getModifierLetterCode(int modifier) {
        switch (modifier) {
            //case A_SYMBOL_ICON:
            //    return "Symbol Icon";
            case B_ECHELON:
                return "B";
            case C_QUANTITY:
                return "C";
            case H_ADDITIONAL_INFO_1:
                return "H";
            case H1_ADDITIONAL_INFO_2:
                return "H1";
            case H2_ADDITIONAL_INFO_3:
                return "H2";
            case N_HOSTILE:
                return "N";
            case Q_DIRECTION_OF_MOVEMENT:
                return "Q";
            //case S_OFFSET_INDICATOR:
            //    return "Offset Indicator";
            case T_UNIQUE_DESIGNATION_1:
                return "T";
            case T1_UNIQUE_DESIGNATION_2:
                return "T1";
            case V_EQUIP_TYPE:
                return "V";
            case W_DTG_1:
                return "W";
            case W1_DTG_2:
                return "W1";
            case X_ALTITUDE_DEPTH:
                return "X";
            case Y_LOCATION:
                return "Y";
            case AM_DISTANCE:
                return "AM";
            case AN_AZIMUTH:
                return "AN";
            default:
                return "";

        }
    }
}
