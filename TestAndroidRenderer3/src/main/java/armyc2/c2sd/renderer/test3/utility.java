package armyc2.c2sd.renderer.test3;
//

import armyc2.c2sd.JavaLineArray.CELineArray;
import android.graphics.Typeface;
import java.util.ArrayList;
import armyc2.c2sd.JavaLineArray.TacticalLines;
import armyc2.c2sd.JavaLineArray.POINT2;
import armyc2.c2sd.JavaTacticalRenderer.clsMETOC;
import armyc2.c2sd.JavaRendererServer.RenderMultipoints.clsRenderer;
import armyc2.c2sd.graphics2d.*;
import armyc2.c2sd.renderer.utilities.*;
import armyc2.c2sd.JavaLineArray.lineutility;
import java.util.Iterator;
import java.util.List;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Path;
import android.content.Context;
import android.util.SparseArray;
import static armyc2.c2sd.JavaLineArray.lineutility.CalcDistanceDouble;
import sec.web.json.utilities.JSONObject;
import sec.web.render.SECWebRenderer;
import sec.web.render.PointConverter;
import sec.web.render.utilities.JavaRendererUtilities;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import armyc2.c2sd.renderer.test3.R;

///**
// * 
// * @author Michael Deutch
// */

public final class utility {

    protected static double leftLongitude;
    protected static double rightLongitude;
    protected static double upperLatitude;
    protected static double lowerLatitude;

    public static String linetype = "";
    public static String T = "";
    public static String T1 = "";
    public static String H = "";
    public static String H1 = "";
    public static String W = "";
    public static String W1 = "";
    public static String linecolor = "";
    public static String textcolor = "";
    public static String fillcolor = "";
    public static String AM="";
    public static String AN="";
    public static String X="";
    public static String lineWidth="";
    //public static String extents="";
    public static String Rev="";
    public static String symbolFillIds="";//"WOS-HHDF--P----";
    /**
     * uses the PointConversion to convert to geo
     *
     * @param pts
     * @param converter
     * @return
     */
    private static ArrayList<POINT2> PixelsToLatLong(ArrayList<Point> pts,
            IPointConversion converter) {
        int j = 0;
        Point pt = null;
        Point2D pt2d = null;
        ArrayList<Point2D> pts2d = new ArrayList();
        for (j = 0; j < pts.size(); j++) {
            pt = pts.get(j);
            pt2d=new Point2D.Double(pt.x,pt.y);
            pt2d = converter.PixelsToGeo(pt2d);
            pts2d.add(pt2d);
        }
        ArrayList<POINT2> pts2 = new ArrayList();
        int n=pts2d.size();
        //for (j = 0; j < pts2d.size(); j++) 
        for (j = 0; j < n; j++) 
        {
            pts2.add(new POINT2(pts2d.get(j).getX(), pts2d.get(j).getY()));
        }

        return pts2;
    }
    // imported from tactical test for the channel types
    private static Point ComputeLastPoint(ArrayList<Point> arrLocation) {
        Point locD = new Point(0, 0);
        try {
            Point locA = new Point(arrLocation.get(1).x, arrLocation.get(1).y);
            // Get the first point (b) in pixels.
            // var locB:Point=new Point(arrLocation[0].x,arrLocation[0].y);
            Point locB = new Point(arrLocation.get(0).x, arrLocation.get(0).y);
            // diagnostic 2-27-13
            double dist = lineutility.CalcDistanceDouble(new POINT2(locA.x,
                    locA.y), new POINT2(locB.x, locB.y));
            // Compute the distance in pixels from (a) to (b).
            double dblDx = locB.x - locA.x;
            double dblDy = locB.y - locA.y;

            // Compute the dblAngle in radians from (a) to (b).
            double dblTheta = Math.atan2(-dblDy, dblDx);

            // Compute a reasonable intermediate point along the line from (a)
            // to (b).
            Point locC = new Point(0, 0);
            locC.x = (int) (locA.x + 0.85 * dblDx);
            locC.y = (int) (locA.y + 0.85 * dblDy);
            // Put the last point on the left side of the line from (a) to (b).
            double dblAngle = dblTheta + Math.PI / 2.0;
            if (dblAngle > Math.PI) {
                dblAngle = dblAngle - 2.0 * Math.PI;
            }
            if (dblAngle < -Math.PI) {
                dblAngle = dblAngle + 2.0 * Math.PI;
            }

            // Set the magnitude of the dblWidth in pixels. Make sure it is at
            // least 15 pixels.
            double dblWidth = 30;// was 15
            // diagnostic 2-27-13
            if (dblWidth > dist) {
                dblWidth = dist;
            }
            if (dblWidth < 10) {
                dblWidth = 10;
            }

            // Compute the last point in pixels.
            locD.x = (int) (locC.x + dblWidth * Math.cos(dblAngle));
            locD.y = (int) (locC.y - dblWidth * Math.sin(dblAngle));
        } catch (Exception e) {
            // CJMTKExceptions.LogException(ex, ex.Source);
            // throw e;
        }

        return locD;
    } // End ComputeLastPoint

    private static double displayWidth;
    private static double displayHeight;

    protected static void set_displayPixelsWidth(double value) {
        displayWidth = value;
    }

    protected static void set_displayPixelsHeight(double value) {
        displayHeight = value;
    }

    protected static void SetExtents(double ullon, double lrlon, double ullat,
            double lrlat) {
        leftLongitude = ullon;// -95.5;
        rightLongitude = lrlon;// -93;
        upperLatitude = ullat;// 33.5;
        lowerLatitude = lrlat;// 32;
    }

    private static String TableType(int lineType) {
        String str = "";
        switch (lineType) {
            case TacticalLines.DUMMY:
            case TacticalLines.BS_AREA:
            case TacticalLines.GENERAL:
            case TacticalLines.NFA:
            case TacticalLines.FFA:
            case TacticalLines.DMAF:
            case TacticalLines.DMA:
            case TacticalLines.AIRFIELD:
            case TacticalLines.ENCIRCLE:
            case TacticalLines.STRONG:
            case TacticalLines.FORT:
            case TacticalLines.ZONE:
            case TacticalLines.BELT:
            case TacticalLines.DRCL:
            case TacticalLines.DEPICT:
            case TacticalLines.OBSAREA:
            case TacticalLines.OBSFAREA:
            case TacticalLines.RFA:
            case TacticalLines.WFZ:
            case TacticalLines.ASSY:
            // from list
            case TacticalLines.LAA:
            case TacticalLines.RAD:
            case TacticalLines.BIO:
            case TacticalLines.CHEM:
            case TacticalLines.UXO:
            case TacticalLines.MINED:
            case TacticalLines.PEN:
            case TacticalLines.AIRHEAD:
            case TacticalLines.AO:
            case TacticalLines.ATKPOS:
            case TacticalLines.ASSAULT:
            case TacticalLines.PNO:
            case TacticalLines.RSA:
            case TacticalLines.RHA:
            case TacticalLines.EPW:
            case TacticalLines.DHA:
            case TacticalLines.BOMB:
            case TacticalLines.SERIES:
            case TacticalLines.SMOKE:
            case TacticalLines.AT:
            case TacticalLines.FARP:
            case TacticalLines.NAI:
            case TacticalLines.EA1:
            case TacticalLines.EA:
            case TacticalLines.DSA:
            case TacticalLines.BSA:
            case TacticalLines.OBJ:
            case TacticalLines.TAI:
            case TacticalLines.BATTLE:
            case TacticalLines.PZ:
            case TacticalLines.LZ:
            case TacticalLines.DZ:
            case TacticalLines.FAADZ:
            case TacticalLines.HIMEZ:
            case TacticalLines.LOMEZ:
            case TacticalLines.MEZ:
            case TacticalLines.ROZ:
            case TacticalLines.EZ:
            case TacticalLines.HIDACZ:
            case TacticalLines.FSA: // change 1
            case TacticalLines.ATI:
            case TacticalLines.CFFZ:
            case TacticalLines.SENSOR:
            case TacticalLines.CENSOR:
            case TacticalLines.DA:
            case TacticalLines.CFZ:
            case TacticalLines.ZOR:
            case TacticalLines.TBA:
            case TacticalLines.TVAR:
            case TacticalLines.KILLBOXBLUE:
            case TacticalLines.KILLBOXPURPLE:
            case TacticalLines.ACA:
            case TacticalLines.BEACH:
            case TacticalLines.BEACH_SLOPE_MODERATE:
            case TacticalLines.BEACH_SLOPE_STEEP:
            case TacticalLines.FOUL_GROUND:
            case TacticalLines.KELP:
            case TacticalLines.SWEPT_AREA:
            case TacticalLines.OIL_RIG_FIELD:
            case TacticalLines.WEIRS:
            case TacticalLines.OPERATOR_DEFINED:
            case TacticalLines.VDR_LEVEL_12:
            case TacticalLines.VDR_LEVEL_23:
            case TacticalLines.VDR_LEVEL_34:
            case TacticalLines.VDR_LEVEL_45:
            case TacticalLines.VDR_LEVEL_56:
            case TacticalLines.VDR_LEVEL_67:
            case TacticalLines.VDR_LEVEL_78:
            case TacticalLines.VDR_LEVEL_89:
            case TacticalLines.VDR_LEVEL_910:
            case TacticalLines.IFR:
            case TacticalLines.MVFR:
            case TacticalLines.TURBULENCE:
            case TacticalLines.ICING:
            case TacticalLines.NON_CONVECTIVE:
            case TacticalLines.CONVECTIVE:
            case TacticalLines.FROZEN:
            case TacticalLines.THUNDERSTORMS:
            case TacticalLines.FOG:
            case TacticalLines.SAND:
            case TacticalLines.FREEFORM:
            case TacticalLines.DEPTH_AREA:
            case TacticalLines.ISLAND:
            case TacticalLines.ANCHORAGE_AREA:
            case TacticalLines.WATER:
            case TacticalLines.FORESHORE_AREA:
            case TacticalLines.DRYDOCK:
            case TacticalLines.LOADING_FACILITY_AREA:
            case TacticalLines.PERCHES:
            case TacticalLines.UNDERWATER_HAZARD:
            case TacticalLines.TRAINING_AREA:
            case TacticalLines.DISCOLORED_WATER:
            case TacticalLines.BEACH_SLOPE_FLAT:
            case TacticalLines.BEACH_SLOPE_GENTLE:
            case TacticalLines.SOLID_ROCK:
            case TacticalLines.CLAY:
            case TacticalLines.VERY_COARSE_SAND:
            case TacticalLines.COARSE_SAND:
            case TacticalLines.MEDIUM_SAND:
            case TacticalLines.FINE_SAND:
            case TacticalLines.VERY_FINE_SAND:
            case TacticalLines.VERY_FINE_SILT:
            case TacticalLines.FINE_SILT:
            case TacticalLines.MEDIUM_SILT:
            case TacticalLines.COARSE_SILT:
            case TacticalLines.SAND_AND_SHELLS:
            case TacticalLines.PEBBLES:
            case TacticalLines.OYSTER_SHELLS:
            case TacticalLines.BOULDERS:
            case TacticalLines.BOTTOM_SEDIMENTS_LAND:
            case TacticalLines.BOTTOM_SEDIMENTS_NO_DATA:
            case TacticalLines.BOTTOM_ROUGHNESS_SMOOTH:
            case TacticalLines.BOTTOM_ROUGHNESS_MODERATE:
            case TacticalLines.BOTTOM_ROUGHNESS_ROUGH:
            case TacticalLines.CLUTTER_HIGH:
            case TacticalLines.CLUTTER_MEDIUM:
            case TacticalLines.CLUTTER_LOW:
            case TacticalLines.IMPACT_BURIAL_0:
            case TacticalLines.IMPACT_BURIAL_10:
            case TacticalLines.IMPACT_BURIAL_20:
            case TacticalLines.IMPACT_BURIAL_75:
            case TacticalLines.IMPACT_BURIAL_100:
            case TacticalLines.BOTTOM_TYPE_A1:
            case TacticalLines.BOTTOM_TYPE_A2:
            case TacticalLines.BOTTOM_TYPE_A3:
            case TacticalLines.BOTTOM_TYPE_B1:
            case TacticalLines.BOTTOM_TYPE_B2:
            case TacticalLines.BOTTOM_TYPE_B3:
            case TacticalLines.BOTTOM_TYPE_C1:
            case TacticalLines.BOTTOM_TYPE_C2:
            case TacticalLines.BOTTOM_TYPE_C3:
            case TacticalLines.BOTTOM_CATEGORY_A:
            case TacticalLines.BOTTOM_CATEGORY_B:
            case TacticalLines.BOTTOM_CATEGORY_C:
            case TacticalLines.MARITIME_AREA:
            case TacticalLines.SUBMERGED_CRIB:
            case TacticalLines.TGMF:
            case TacticalLines.TEST:
                str = "polygon";
                break;
            case TacticalLines.FORTL:
                str = "polyline";
                break;
            default:
                str = "polyline";
                break;
        }
        return str;
    }

    protected static int GetLinetype(String str, int rev) {
        int linetype = -1;
        linetype = clsMETOC.IsWeather(str);
        if (linetype < 0) {
            linetype = CELineArray.CGetLinetypeFromString(str, rev);
        }
        if (linetype >= 0) {
            return linetype;
        }
        if (str.equalsIgnoreCase("track")) {
            return -1;
        }
        if (str.equalsIgnoreCase("route")) {
            return -1;
        }
        if (str.equalsIgnoreCase("cylinder")) {
            return -1;
        }
        if (str.equalsIgnoreCase("curtain")) {
            return -1;
        }
        if (str.equalsIgnoreCase("polyarc")) {
            return -1;
        }
        if (str.equalsIgnoreCase("polygon")) {
            return -1;
        }
        if (str.equalsIgnoreCase("radarc")) {
            return -1;
        }

        if (str.equalsIgnoreCase("BS_AREA")) {
            linetype = TacticalLines.BS_AREA;
        } else if (str.equalsIgnoreCase("BS_LINE")) {
            linetype = TacticalLines.BS_LINE;
        } else if (str.equalsIgnoreCase("BS_CROSS")) {
            linetype = TacticalLines.BS_CROSS;
        } else if (str.equalsIgnoreCase("BS_ELLIPSE")) {
            linetype = TacticalLines.BS_ELLIPSE;
        } else if (str.equalsIgnoreCase("BS_RECTANGLE")) {
            linetype = TacticalLines.BS_RECTANGLE;
        } else if (str.equalsIgnoreCase("CFL")) {
            linetype = TacticalLines.CFL;
        } else if (str.equalsIgnoreCase("OVERHEAD_WIRE")) {
            linetype = TacticalLines.OVERHEAD_WIRE;
        } else if (str.equalsIgnoreCase("AMBUSH")) {
            linetype = TacticalLines.AMBUSH;
        } else if (str.equalsIgnoreCase("EASY")) {
            linetype = TacticalLines.EASY;
        } else if (str.equalsIgnoreCase("FOXHOLE")) {
            linetype = TacticalLines.FOXHOLE;
        } // TASKS
        else if (str.equalsIgnoreCase("BLOCK")) {
            linetype = TacticalLines.BLOCK;
        } else if (str.equalsIgnoreCase("BREACH")) {
            linetype = TacticalLines.BREACH;
        } else if (str.equalsIgnoreCase("BYPASS")) {
            linetype = TacticalLines.BYPASS;
        } else if (str.equalsIgnoreCase("CANALIZE")) {
            linetype = TacticalLines.CANALIZE;
        } else if (str.equalsIgnoreCase("CLEAR")) {
            linetype = TacticalLines.CLEAR;
        } else if (str.equalsIgnoreCase("CONTAIN")) {
            linetype = TacticalLines.CONTAIN;
        } else if (str.equalsIgnoreCase("DELAY")) {
            linetype = TacticalLines.DELAY;
        } else if (str.equalsIgnoreCase("DISRUPT")) {
            linetype = TacticalLines.DISRUPT;
        } else if (str.equalsIgnoreCase("FIX")) {
            linetype = TacticalLines.FIX;
        } else if (str.equalsIgnoreCase("MNFLDFIX")) {
            linetype = TacticalLines.MNFLDFIX;
        } else if (str.equalsIgnoreCase("FOLLA")) {
            linetype = TacticalLines.FOLLA;
        } else if (str.equalsIgnoreCase("FOLSP")) {
            linetype = TacticalLines.FOLSP;
        } else if (str.equalsIgnoreCase("ISOLATE")) {
            linetype = TacticalLines.ISOLATE;
        } else if (str.equalsIgnoreCase("OCCUPY")) {
            linetype = TacticalLines.OCCUPY;
        } else if (str.equalsIgnoreCase("PENETRATE")) {
            linetype = TacticalLines.PENETRATE;
        } else if (str.equalsIgnoreCase("RIP")) {
            linetype = TacticalLines.RIP;
        } else if (str.equalsIgnoreCase("RETAIN")) {
            linetype = TacticalLines.RETAIN;
        } else if (str.equalsIgnoreCase("RETIRE")) {
            linetype = TacticalLines.RETIRE;
        } else if (str.equalsIgnoreCase("SECURE")) {
            linetype = TacticalLines.SECURE;
        } else if (str.equalsIgnoreCase("SCREEN")) {
            linetype = TacticalLines.SCREEN;
        } else if (str.equalsIgnoreCase("COVER")) {
            linetype = TacticalLines.COVER;
        } else if (str.equalsIgnoreCase("GUARD")) {
            linetype = TacticalLines.GUARD;
        } else if (str.equalsIgnoreCase("SEIZE")) {
            linetype = TacticalLines.SEIZE;
        } else if (str.equalsIgnoreCase("WITHDRAW")) {
            linetype = TacticalLines.WITHDRAW;
        } else if (str.equalsIgnoreCase("WDRAWUP")) {
            linetype = TacticalLines.WDRAWUP;
        } else if (str.equalsIgnoreCase("BOUNDARY")) {
            linetype = TacticalLines.BOUNDARY;
        } else if (str.equalsIgnoreCase("FLOT")) {
            linetype = TacticalLines.FLOT;
        } else if (str.equalsIgnoreCase("PL")) {
            linetype = TacticalLines.PL;
        } else if (str.equalsIgnoreCase("LL")) {
            linetype = TacticalLines.LL;
        } else if (str.equalsIgnoreCase("GENERAL")) {
            linetype = TacticalLines.GENERAL;
        } else if (str.equalsIgnoreCase("GENERIC")) {
            linetype = TacticalLines.GENERIC;
        } else if (str.equalsIgnoreCase("ASSY")) {
            linetype = TacticalLines.ASSY;
        } else if (str.equalsIgnoreCase("EA")) {
            linetype = TacticalLines.EA;
        } else if (str.equalsIgnoreCase("FORT")) {
            linetype = TacticalLines.FORT;
        } else if (str.equalsIgnoreCase("DZ")) {
            linetype = TacticalLines.DZ;
        } else if (str.equalsIgnoreCase("EZ")) {
            linetype = TacticalLines.EZ;
        } else if (str.equalsIgnoreCase("LZ")) {
            linetype = TacticalLines.LZ;
        } else if (str.equalsIgnoreCase("PZ")) {
            linetype = TacticalLines.PZ;
        } else if (str.equalsIgnoreCase("SARA")) {
            linetype = TacticalLines.SARA;
        } else if (str.equalsIgnoreCase("LAA")) {
            linetype = TacticalLines.LAA;
        } else if (str.equalsIgnoreCase("AIRFIELD")) {
            linetype = TacticalLines.AIRFIELD;
        } else if (str.equalsIgnoreCase("AC")) {
            linetype = TacticalLines.AC;
        } else if (str.equalsIgnoreCase("MRR")) {
            linetype = TacticalLines.MRR;
        } else if (str.equalsIgnoreCase("MRR_USAS")) {
            linetype = TacticalLines.MRR_USAS;
        } else if (str.equalsIgnoreCase("SAAFR")) {
            linetype = TacticalLines.SAAFR;
        } else if (str.equalsIgnoreCase("UAV")) {
            linetype = TacticalLines.UAV;
        } else if (str.equalsIgnoreCase("UAV_USAS")) {
            linetype = TacticalLines.UAV_USAS;
        } else if (str.equalsIgnoreCase("LLTR")) {
            linetype = TacticalLines.LLTR;
        } else if (str.equalsIgnoreCase("ROZ")) {
            linetype = TacticalLines.ROZ;
        } else if (str.equalsIgnoreCase("FAADZ")) {
            linetype = TacticalLines.FAADZ;
        } else if (str.equalsIgnoreCase("HIDACZ")) {
            linetype = TacticalLines.HIDACZ;
        } else if (str.equalsIgnoreCase("MEZ")) {
            linetype = TacticalLines.MEZ;
        } else if (str.equalsIgnoreCase("LOMEZ")) {
            linetype = TacticalLines.LOMEZ;
        } else if (str.equalsIgnoreCase("HIMEZ")) {
            linetype = TacticalLines.HIMEZ;
        } else if (str.equalsIgnoreCase("WFZ")) {
            linetype = TacticalLines.WFZ;
        } else if (str.equalsIgnoreCase("DECEIVE")) {
            linetype = TacticalLines.DECEIVE;
        } else if (str.equalsIgnoreCase("DIRATKFNT")) {
            linetype = TacticalLines.DIRATKFNT;
        } else if (str.equalsIgnoreCase("DMA")) {
            linetype = TacticalLines.DMA;
        } else if (str.equalsIgnoreCase("LINTGT")) {
            linetype = TacticalLines.LINTGT;
        } else if (str.equalsIgnoreCase("LINTGTS")) {
            linetype = TacticalLines.LINTGTS;
        } else if (str.equalsIgnoreCase("FPF")) {
            linetype = TacticalLines.FPF;
        } else if (str.equalsIgnoreCase("DMAF")) {
            linetype = TacticalLines.DMAF;
        } else if (str.equalsIgnoreCase("DUMMY")) {
            linetype = TacticalLines.DUMMY;
        } else if (str.equalsIgnoreCase("FEBA")) {
            linetype = TacticalLines.FEBA;
        } else if (str.equalsIgnoreCase("PDF")) {
            linetype = TacticalLines.PDF;
        } else if (str.equalsIgnoreCase("BATTLE")) {
            linetype = TacticalLines.BATTLE;
        } else if (str.equalsIgnoreCase("PNO")) {
            linetype = TacticalLines.PNO;
        } else if (str.equalsIgnoreCase("EA1")) {
            linetype = TacticalLines.EA1;
        } else if (str.equalsIgnoreCase("DIRATKAIR")) {
            linetype = TacticalLines.DIRATKAIR;
        } else if (str.equalsIgnoreCase("DIRATKGND")) {
            linetype = TacticalLines.DIRATKGND;
        } else if (str.equalsIgnoreCase("DIRATKSPT")) {
            linetype = TacticalLines.DIRATKSPT;
        } else if (str.equalsIgnoreCase("FCL")) {
            linetype = TacticalLines.FCL;
        } else if (str.equalsIgnoreCase("IL")) {
            linetype = TacticalLines.IL;
        } else if (str.equalsIgnoreCase("LOA")) {
            linetype = TacticalLines.LOA;
        } else if (str.equalsIgnoreCase("LOD")) {
            linetype = TacticalLines.LOD;
        } else if (str.equalsIgnoreCase("LDLC")) {
            linetype = TacticalLines.LDLC;
        } else if (str.equalsIgnoreCase("PLD")) {
            linetype = TacticalLines.PLD;
        } else if (str.equalsIgnoreCase("ASSAULT")) {
            linetype = TacticalLines.ASSAULT;
        } else if (str.equalsIgnoreCase("ATKPOS")) {
            linetype = TacticalLines.ATKPOS;
        } else if (str.equalsIgnoreCase("ATKBYFIRE")) {
            linetype = TacticalLines.ATKBYFIRE;
        } else if (str.equalsIgnoreCase("SPTBYFIRE")) {
            linetype = TacticalLines.SPTBYFIRE;
        } else if (str.equalsIgnoreCase("OBJ")) {
            linetype = TacticalLines.OBJ;
        } else if (str.equalsIgnoreCase("PEN")) {
            linetype = TacticalLines.PEN;
        } else if (str.equalsIgnoreCase("HOLD")) {
            linetype = TacticalLines.HOLD;
        } else if (str.equalsIgnoreCase("RELEASE")) {
            linetype = TacticalLines.RELEASE;
        } else if (str.equalsIgnoreCase("BRDGHD")) {
            linetype = TacticalLines.BRDGHD;
        } else if (str.equalsIgnoreCase("AO")) {
            linetype = TacticalLines.AO;
        } else if (str.equalsIgnoreCase("AIRHEAD")) {
            linetype = TacticalLines.AIRHEAD;
        } else if (str.equalsIgnoreCase("ENCIRCLE")) {
            linetype = TacticalLines.ENCIRCLE;
        } else if (str.equalsIgnoreCase("NAI")) {
            linetype = TacticalLines.NAI;
        } else if (str.equalsIgnoreCase("TAI")) {
            linetype = TacticalLines.TAI;
        } else if (str.equalsIgnoreCase("BELT")) {
            linetype = TacticalLines.BELT;
        } else if (str.equalsIgnoreCase("LINE")) {
            linetype = TacticalLines.LINE;
        } else if (str.equalsIgnoreCase("ZONE")) {
            linetype = TacticalLines.ZONE;
        } else if (str.equalsIgnoreCase("OBSFAREA")) {
            linetype = TacticalLines.OBSFAREA;
        } else if (str.equalsIgnoreCase("OBSAREA")) {
            linetype = TacticalLines.OBSAREA;
        } else if (str.equalsIgnoreCase("ABATIS")) {
            linetype = TacticalLines.ABATIS;
        } else if (str.equalsIgnoreCase("ATDITCH")) {
            linetype = TacticalLines.ATDITCH;
        } else if (str.equalsIgnoreCase("ATDITCHC")) {
            linetype = TacticalLines.ATDITCHC;
        } else if (str.equalsIgnoreCase("ATDITCHM")) {
            linetype = TacticalLines.ATDITCHM;
        } else if (str.equalsIgnoreCase("ATWALL")) {
            linetype = TacticalLines.ATWALL;
        } else if (str.equalsIgnoreCase("CLUSTER")) {
            linetype = TacticalLines.CLUSTER;
        } else if (str.equalsIgnoreCase("DEPICT")) {
            linetype = TacticalLines.DEPICT;
        } else if (str.equalsIgnoreCase("GAP")) {
            linetype = TacticalLines.GAP;
        } else if (str.equalsIgnoreCase("MINED")) {
            linetype = TacticalLines.MINED;
        } else if (str.equalsIgnoreCase("MNFLDBLK")) {
            linetype = TacticalLines.MNFLDBLK;
        } else if (str.equalsIgnoreCase("TURN")) {
            linetype = TacticalLines.TURN;
        } else if (str.equalsIgnoreCase("MNFLDDIS")) {
            linetype = TacticalLines.MNFLDDIS;
        } else if (str.equalsIgnoreCase("UXO")) {
            linetype = TacticalLines.UXO;
        } else if (str.equalsIgnoreCase("PLANNED")) {
            linetype = TacticalLines.PLANNED;
        } else if (str.equalsIgnoreCase("ESR1")) {
            linetype = TacticalLines.ESR1;
        } else if (str.equalsIgnoreCase("ESR2")) {
            linetype = TacticalLines.ESR2;
        } else if (str.equalsIgnoreCase("ROADBLK")) {
            linetype = TacticalLines.ROADBLK;
        } else if (str.equalsIgnoreCase("TRIP")) {
            linetype = TacticalLines.TRIP;
        } else if (str.equalsIgnoreCase("UNSP")) {
            linetype = TacticalLines.UNSP;
        } else if (str.equalsIgnoreCase("BYDIF")) {
            linetype = TacticalLines.BYDIF;
        } else if (str.equalsIgnoreCase("BYIMP")) {
            linetype = TacticalLines.BYIMP;
        } else if (str.equalsIgnoreCase("ASLTXING")) {
            linetype = TacticalLines.ASLTXING;
        } else if (str.equalsIgnoreCase("BRIDGE")) {
            linetype = TacticalLines.BRIDGE;
        } else if (str.equalsIgnoreCase("FERRY")) {
            linetype = TacticalLines.FERRY;
        } else if (str.equalsIgnoreCase("FORDSITE")) {
            linetype = TacticalLines.FORDSITE;
        } else if (str.equalsIgnoreCase("FORDIF")) {
            linetype = TacticalLines.FORDIF;
        } else if (str.equalsIgnoreCase("MFLANE")) {
            linetype = TacticalLines.MFLANE;
        } else if (str.equalsIgnoreCase("RAFT")) {
            linetype = TacticalLines.RAFT;
        } else if (str.equalsIgnoreCase("STRONG")) {
            linetype = TacticalLines.STRONG;
        } else if (str.equalsIgnoreCase("MSDZ")) {
            linetype = TacticalLines.MSDZ;
        } else if (str.equalsIgnoreCase("RAD")) {
            linetype = TacticalLines.RAD;
        } else if (str.equalsIgnoreCase("CHEM")) {
            linetype = TacticalLines.CHEM;
        } else if (str.equalsIgnoreCase("BIO")) {
            linetype = TacticalLines.BIO;
        } else if (str.equalsIgnoreCase("DRCL")) {
            linetype = TacticalLines.DRCL;
        } else if (str.equalsIgnoreCase("FSCL")) {
            linetype = TacticalLines.FSCL;
        } else if (str.equalsIgnoreCase("NFL")) {
            linetype = TacticalLines.NFL;
        } else if (str.equalsIgnoreCase("MFP")) {
            linetype = TacticalLines.MFP;
        } else if (str.equalsIgnoreCase("RFL")) {
            linetype = TacticalLines.RFL;
        } else if (str.equalsIgnoreCase("AT")) {
            linetype = TacticalLines.AT;
        } else if (str.equalsIgnoreCase("RECTANGULAR")) {
            linetype = TacticalLines.RECTANGULAR;
        } else if (str.equalsIgnoreCase("CIRCULAR")) {
            linetype = TacticalLines.CIRCULAR;
        } else if (str.equalsIgnoreCase("SERIES")) {
            linetype = TacticalLines.SERIES;
        } else if (str.equalsIgnoreCase("SMOKE")) {
            linetype = TacticalLines.SMOKE;
        } else if (str.equalsIgnoreCase("BOMB")) {
            linetype = TacticalLines.BOMB;
        } else if (str.equalsIgnoreCase("FORTL")) {
            linetype = TacticalLines.FORTL;
        } // FIRE SUPPORT AREAS
        else if (str.equalsIgnoreCase("FSA")) {
            linetype = TacticalLines.FSA;
        } else if (str.equalsIgnoreCase("FSA_RECTANGULAR")) {
            linetype = TacticalLines.FSA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("FSA_CIRCULAR")) {
            linetype = TacticalLines.FSA_CIRCULAR;
        } else if (str.equalsIgnoreCase("ACA")) {
            linetype = TacticalLines.ACA;
        } else if (str.equalsIgnoreCase("ACA_RECTANGULAR")) {
            linetype = TacticalLines.ACA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("ACA_CIRCULAR")) {
            linetype = TacticalLines.ACA_CIRCULAR;
        } else if (str.equalsIgnoreCase("FFA")) {
            linetype = TacticalLines.FFA;
        } else if (str.equalsIgnoreCase("FFA_RECTANGULAR")) {
            linetype = TacticalLines.FFA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("FFA_CIRCULAR")) {
            linetype = TacticalLines.FFA_CIRCULAR;
        } else if (str.equalsIgnoreCase("NFA")) {
            linetype = TacticalLines.NFA;
        } else if (str.equalsIgnoreCase("NFA_RECTANGULAR")) {
            linetype = TacticalLines.NFA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("NFA_CIRCULAR")) {
            linetype = TacticalLines.NFA_CIRCULAR;
        } else if (str.equalsIgnoreCase("RFA")) {
            linetype = TacticalLines.FFA;
        } else if (str.equalsIgnoreCase("RFA_RECTANGULAR")) {
            linetype = TacticalLines.RFA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("RFA_CIRCULAR")) {
            linetype = TacticalLines.RFA_CIRCULAR;
        } else if (str.equalsIgnoreCase("PAA")) {
            linetype = TacticalLines.PAA;
        } else if (str.equalsIgnoreCase("PAA_RECTANGULAR")) {
            linetype = TacticalLines.PAA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("PAA_RECTANGULAR_REVC")) {
            linetype = TacticalLines.PAA_RECTANGULAR_REVC;
        } else if (str.equalsIgnoreCase("PAA_CIRCULAR")) {
            linetype = TacticalLines.PAA_CIRCULAR;
        } else if (str.equalsIgnoreCase("ATI")) {
            linetype = TacticalLines.ATI;
        } else if (str.equalsIgnoreCase("ATI_RECTANGULAR")) {
            linetype = TacticalLines.ATI_RECTANGULAR;
        } else if (str.equalsIgnoreCase("ATI_CIRCULAR")) {
            linetype = TacticalLines.ATI_CIRCULAR;
        } else if (str.equalsIgnoreCase("CFFZ")) {
            linetype = TacticalLines.CFFZ;
        } else if (str.equalsIgnoreCase("CFFZ_RECTANGULAR")) {
            linetype = TacticalLines.CFFZ_RECTANGULAR;
        } else if (str.equalsIgnoreCase("CFFZ_CIRCULAR")) {
            linetype = TacticalLines.CFFZ_CIRCULAR;
        } else if (str.equalsIgnoreCase("SENSOR")) {
            linetype = TacticalLines.SENSOR;
        } else if (str.equalsIgnoreCase("SENSOR_RECTANGULAR")) {
            linetype = TacticalLines.SENSOR_RECTANGULAR;
        } else if (str.equalsIgnoreCase("SENSOR_CIRCULAR")) {
            linetype = TacticalLines.SENSOR_CIRCULAR;
        } else if (str.equalsIgnoreCase("CENSOR")) {
            linetype = TacticalLines.CENSOR;
        } else if (str.equalsIgnoreCase("CENSOR_RECTANGULAR")) {
            linetype = TacticalLines.CENSOR_RECTANGULAR;
        } else if (str.equalsIgnoreCase("CENSOR_CIRCULAR")) {
            linetype = TacticalLines.CENSOR_CIRCULAR;
        } else if (str.equalsIgnoreCase("DA")) {
            linetype = TacticalLines.DA;
        } else if (str.equalsIgnoreCase("DA_RECTANGULAR")) {
            linetype = TacticalLines.DA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("DA_CIRCULAR")) {
            linetype = TacticalLines.DA_CIRCULAR;
        } else if (str.equalsIgnoreCase("CFZ")) {
            linetype = TacticalLines.CFZ;
        } else if (str.equalsIgnoreCase("CFZ_RECTANGULAR")) {
            linetype = TacticalLines.CFZ_RECTANGULAR;
        } else if (str.equalsIgnoreCase("CFZ_CIRCULAR")) {
            linetype = TacticalLines.CFZ_CIRCULAR;
        } else if (str.equalsIgnoreCase("ZOR")) {
            linetype = TacticalLines.ZOR;
        } else if (str.equalsIgnoreCase("ZOR_RECTANGULAR")) {
            linetype = TacticalLines.ZOR_RECTANGULAR;
        } else if (str.equalsIgnoreCase("ZOR_CIRCULAR")) {
            linetype = TacticalLines.ZOR_CIRCULAR;
        } else if (str.equalsIgnoreCase("TBA")) {
            linetype = TacticalLines.TBA;
        } else if (str.equalsIgnoreCase("TBA_RECTANGULAR")) {
            linetype = TacticalLines.TBA_RECTANGULAR;
        } else if (str.equalsIgnoreCase("TBA_CIRCULAR")) {
            linetype = TacticalLines.TBA_CIRCULAR;
        } else if (str.equalsIgnoreCase("TVAR")) {
            linetype = TacticalLines.TVAR;
        } else if (str.equalsIgnoreCase("TVAR_RECTANGULAR")) {
            linetype = TacticalLines.TVAR_RECTANGULAR;
        } else if (str.equalsIgnoreCase("TVAR_CIRCULAR")) {
            linetype = TacticalLines.TVAR_CIRCULAR;
        } else if (str.equalsIgnoreCase("KILLBOXBLUE")) {
            linetype = TacticalLines.KILLBOXBLUE;
        } else if (str.equalsIgnoreCase("KILLBOXBLUE_RECTANGULAR")) {
            linetype = TacticalLines.KILLBOXBLUE_RECTANGULAR;
        } else if (str.equalsIgnoreCase("KILLBOXBLUE_CIRCULAR")) {
            linetype = TacticalLines.KILLBOXBLUE_CIRCULAR;
        } else if (str.equalsIgnoreCase("KILLBOXPURPLE")) {
            linetype = TacticalLines.KILLBOXPURPLE;
        } else if (str.equalsIgnoreCase("KILLBOXPURPLE_RECTANGULAR")) {
            linetype = TacticalLines.KILLBOXPURPLE_RECTANGULAR;
        } else if (str.equalsIgnoreCase("KILLBOXPURPLE_CIRCULAR")) {
            linetype = TacticalLines.KILLBOXPURPLE_CIRCULAR;
        } // RANGE FANS
        else if (str.equalsIgnoreCase("RANGE_FAN")) {
            linetype = TacticalLines.RANGE_FAN;
        } else if (str.equalsIgnoreCase("SECTOR")) {
            linetype = TacticalLines.RANGE_FAN_SECTOR;
        } else if (str.equalsIgnoreCase("RANGE_FAN_SECTOR")) {
            linetype = TacticalLines.RANGE_FAN_SECTOR;
        } else if (str.equalsIgnoreCase("CONVOY")) {
            linetype = TacticalLines.CONVOY;
        } else if (str.equalsIgnoreCase("HCONVOY")) {
            linetype = TacticalLines.HCONVOY;
        } else if (str.equalsIgnoreCase("MSR")) {
            linetype = TacticalLines.MSR;
        } else if (str.equalsIgnoreCase("ASR")) {
            linetype = TacticalLines.ASR;
        } else if (str.equalsIgnoreCase("ONEWAY")) {
            linetype = TacticalLines.ONEWAY;
        } else if (str.equalsIgnoreCase("TWOWAY")) {
            linetype = TacticalLines.TWOWAY;
        } else if (str.equalsIgnoreCase("ALT")) {
            linetype = TacticalLines.ALT;
        } else if (str.equalsIgnoreCase("DHA")) {
            linetype = TacticalLines.DHA;
        } else if (str.equalsIgnoreCase("EPW")) {
            linetype = TacticalLines.EPW;
        } else if (str.equalsIgnoreCase("FARP")) {
            linetype = TacticalLines.FARP;
        } else if (str.equalsIgnoreCase("RHA")) {
            linetype = TacticalLines.RHA;
        } else if (str.equalsIgnoreCase("BSA")) {
            linetype = TacticalLines.BSA;
        } else if (str.equalsIgnoreCase("DSA")) {
            linetype = TacticalLines.DSA;
        } else if (str.equalsIgnoreCase("RSA")) {
            linetype = TacticalLines.RSA;
        } else if (str.equalsIgnoreCase("NAVIGATION")) {
            linetype = TacticalLines.NAVIGATION;
        } else if (str.equalsIgnoreCase("BEARING")) {
            linetype = TacticalLines.BEARING;
        } else if (str.equalsIgnoreCase("ELECTRO")) {
            linetype = TacticalLines.ELECTRO;
        } else if (str.equalsIgnoreCase("ACOUSTIC")) {
            linetype = TacticalLines.ACOUSTIC;
        } else if (str.equalsIgnoreCase("TORPEDO")) {
            linetype = TacticalLines.TORPEDO;
        } else if (str.equalsIgnoreCase("OPTICAL")) {
            linetype = TacticalLines.OPTICAL;
        } // the channel types
        else if (str.equalsIgnoreCase("CATK")) {
            linetype = TacticalLines.CATK;
        } else if (str.equalsIgnoreCase("AAFNT")) {
            linetype = TacticalLines.AAFNT;
        } else if (str.equalsIgnoreCase("AXAD")) {
            linetype = TacticalLines.AXAD;
        } else if (str.equalsIgnoreCase("MAIN")) {
            linetype = TacticalLines.MAIN;
        } else if (str.equalsIgnoreCase("AIRAOA")) {
            linetype = TacticalLines.AIRAOA;
        } else if (str.equalsIgnoreCase("SPT")) {
            linetype = TacticalLines.SPT;
        } else if (str.equalsIgnoreCase("CATKBYFIRE")) {
            linetype = TacticalLines.CATKBYFIRE;
        } else if (str.equalsIgnoreCase("AAAAA")) {
            linetype = TacticalLines.AAAAA;
        } else if (str.equalsIgnoreCase("ROTARY")) {
            linetype = TacticalLines.AAAAA;
        } else if (str.equalsIgnoreCase("TRIPLE")) {
            linetype = TacticalLines.TRIPLE;
        } else if (str.equalsIgnoreCase("DOUBLEC")) {
            linetype = TacticalLines.DOUBLEC;
        } else if (str.equalsIgnoreCase("SINGLEC")) {
            linetype = TacticalLines.SINGLEC;
        } else if (str.equalsIgnoreCase("SINGLE")) {
            linetype = TacticalLines.SINGLEC;
        } else if (str.equalsIgnoreCase("HWFENCE")) {
            linetype = TacticalLines.HWFENCE;
        } else if (str.equalsIgnoreCase("LWFENCE")) {
            linetype = TacticalLines.LWFENCE;
        } // else if(str.equalsIgnoreCase("UNSP"))
        // linetype=TacticalLines.UNSP;
        else if (str.equalsIgnoreCase("DOUBLEA")) {
            linetype = TacticalLines.DOUBLEA;
        } else if (str.equalsIgnoreCase("SFENCE")) {
            linetype = TacticalLines.SFENCE;
        } else if (str.equalsIgnoreCase("DFENCE")) {
            linetype = TacticalLines.DFENCE;
        } else if (str.equalsIgnoreCase("LC")) {
            linetype = TacticalLines.LC;
        }

        // rev C settings
        if (RendererSettings.getInstance().getSymbologyStandard() == RendererSettings.Symbology_2525C) {
            if (str.equalsIgnoreCase("SCREEN")) {
                linetype = TacticalLines.SCREEN_REVC;
            } else if (str.equalsIgnoreCase("COVER")) {
                linetype = TacticalLines.COVER_REVC;
            } else if (str.equalsIgnoreCase("GUARD")) {
                linetype = TacticalLines.GUARD_REVC;
            } else if (str.equalsIgnoreCase("SEIZE")) {
                linetype = TacticalLines.SEIZE_REVC;
            }
        }
        return linetype;
    }

    protected static int GetAutoshapeQty(String str, int rev) {
        int numPts = -1;
        // linetype=utility.GetLinetype(jTextField1.getText());
        int linetype = GetLinetype(str, rev);
        if (str.equalsIgnoreCase("cylinder"))
            return 1;
        if (str.equalsIgnoreCase("cylinder-------"))
            return 1;
        if (str.equalsIgnoreCase("radarc"))
            return 1;
        if (str.equalsIgnoreCase("radarc---------"))
            return 1;
                
        switch (linetype) {
            case TacticalLines.RANGE_FAN_SECTOR:
            case TacticalLines.RANGE_FAN:
                numPts=1;
                break;
            case TacticalLines.FIX:
            case TacticalLines.FOLLA:
            case TacticalLines.FOLSP:
            case TacticalLines.ISOLATE:
            case TacticalLines.CORDONKNOCK:
            case TacticalLines.CORDONSEARCH:
            case TacticalLines.OCCUPY:
            case TacticalLines.RETAIN:
            case TacticalLines.SECURE:
            case TacticalLines.MRR:
            case TacticalLines.UAV:
            // case TacticalLines.LLTR:
            case TacticalLines.CLUSTER:
            case TacticalLines.MNFLDFIX:
            case TacticalLines.FERRY:
            case TacticalLines.MFLANE:
            case TacticalLines.RAFT:
            case TacticalLines.FOXHOLE:
            // case TacticalLines.LINTGT:
            // case TacticalLines.LINTGTS:
            case TacticalLines.FPF:
            case TacticalLines.CONVOY:
            case TacticalLines.HCONVOY:
            case TacticalLines.BEARING:
            case TacticalLines.NAVIGATION:
            case TacticalLines.ELECTRO:
            case TacticalLines.ACOUSTIC:
            case TacticalLines.TORPEDO:
            case TacticalLines.OPTICAL:
                numPts = 2;
                break;
            case TacticalLines.RECTANGULAR:
            case TacticalLines.BS_CROSS:
                // case TacticalLines.RANGE_FAN://rev c
                // case TacticalLines.RANGE_FAN_SECTOR://rev c
                numPts = 1; // for RECTANGULAR change to 3 if using points
                break;
            case TacticalLines.PAA_CIRCULAR:
            case TacticalLines.FSA_CIRCULAR:
            case TacticalLines.ACA_CIRCULAR:
            case TacticalLines.FFA_CIRCULAR:
            case TacticalLines.NFA_CIRCULAR:
            case TacticalLines.RFA_CIRCULAR:
            case TacticalLines.ATI_CIRCULAR:
            case TacticalLines.CFFZ_CIRCULAR:
            case TacticalLines.SENSOR_CIRCULAR:
            case TacticalLines.CENSOR_CIRCULAR:
            case TacticalLines.DA_CIRCULAR:
            case TacticalLines.CFZ_CIRCULAR:
            case TacticalLines.ZOR_CIRCULAR:
            case TacticalLines.TBA_CIRCULAR:
            case TacticalLines.TVAR_CIRCULAR:
            case TacticalLines.CIRCULAR:
            case TacticalLines.KILLBOXBLUE_CIRCULAR:
            case TacticalLines.KILLBOXPURPLE_CIRCULAR:
                numPts = 1; // change to 2 if using points
                break;
            case TacticalLines.PAA_RECTANGULAR:
            case TacticalLines.PAA_RECTANGULAR_REVC:
            case TacticalLines.FSA_RECTANGULAR:
            case TacticalLines.ACA_RECTANGULAR:
            case TacticalLines.FFA_RECTANGULAR:
            case TacticalLines.NFA_RECTANGULAR:
            case TacticalLines.RFA_RECTANGULAR:
            case TacticalLines.ATI_RECTANGULAR:
            case TacticalLines.CFFZ_RECTANGULAR:
            case TacticalLines.SENSOR_RECTANGULAR:
            case TacticalLines.CENSOR_RECTANGULAR:
            case TacticalLines.DA_RECTANGULAR:
            case TacticalLines.CFZ_RECTANGULAR:
            case TacticalLines.ZOR_RECTANGULAR:
            case TacticalLines.TBA_RECTANGULAR:
            case TacticalLines.TVAR_RECTANGULAR:
            case TacticalLines.KILLBOXBLUE_RECTANGULAR:
            case TacticalLines.KILLBOXPURPLE_RECTANGULAR:
            case TacticalLines.BS_RECTANGLE:
                numPts = 2; // change to 3 if using points
                break;
            case TacticalLines.BLOCK:
            case TacticalLines.BREACH:
            case TacticalLines.BYPASS:
            case TacticalLines.CANALIZE:
            case TacticalLines.CLEAR:
            case TacticalLines.CONTAIN:
            case TacticalLines.DELAY:
            case TacticalLines.DISRUPT:
            case TacticalLines.PENETRATE:
            case TacticalLines.RETIRE:
            case TacticalLines.SCREEN:
            case TacticalLines.COVER:
            case TacticalLines.GUARD:
            case TacticalLines.SEIZE:
            case TacticalLines.WDRAWUP:
            case TacticalLines.WITHDRAW:
            case TacticalLines.SARA:
            case TacticalLines.DECEIVE:
            // case TacticalLines.DUMMY:
            case TacticalLines.PDF:
            case TacticalLines.ATKBYFIRE:
            case TacticalLines.AMBUSH:
            // case TacticalLines.HOLD:
            // case TacticalLines.BRDGHD:
            case TacticalLines.MNFLDBLK:
            case TacticalLines.TURN:
            case TacticalLines.MNFLDDIS:
            case TacticalLines.PLANNED:
            case TacticalLines.ESR1:
            case TacticalLines.ESR2:
            case TacticalLines.ROADBLK:
            case TacticalLines.TRIP:
            case TacticalLines.EASY:
            case TacticalLines.BYDIF:
            case TacticalLines.BYIMP:
            case TacticalLines.FORDSITE:
            case TacticalLines.FORDIF:
            case TacticalLines.IL:
            case TacticalLines.BS_ELLIPSE:
                // case TacticalLines.JET: //test only, then remove
                numPts = 3;
                break;
            case TacticalLines.RIP:
            case TacticalLines.SPTBYFIRE:
            case TacticalLines.GAP:
            case TacticalLines.ASLTXING:
            case TacticalLines.BRIDGE:
            case TacticalLines.MSDZ:
                numPts = 4;
                break;
            // rev c
            case TacticalLines.SCREEN_REVC:
            case TacticalLines.COVER_REVC:
            case TacticalLines.GUARD_REVC:
            case TacticalLines.SEIZE_REVC:
                numPts = 4;
                break;            
            default:
                numPts = 1000;
                break;
        }
        if (rev == RendererSettings.Symbology_2525C) {
            switch (linetype) {
                case TacticalLines.UAV:
                case TacticalLines.MRR:
                    numPts = 1000;
                    break;
            }
        }
        return numPts;
    }
    private static ArrayList<POINT2> PointsToPOINT2(ArrayList<Point> pts) {
        ArrayList<POINT2> pts2 = new ArrayList();
        int j = 0;
        Point pt = null;
        POINT2 pt2 = null;
        int n=pts.size();
        //for (j = 0; j < pts.size(); j++) 
        for (j = 0; j < n; j++) 
        {
            pt = pts.get(j);
            pt2 = new POINT2(pt.getX(), pt.getY());
            pts2.add(pt2);
        }
        return pts2;
    }

    private static ArrayList<Point2D> POINT2ToPoint2D(
            ArrayList<POINT2> pts) {
        ArrayList<Point2D> pts2d = new ArrayList();
        int n=pts.size();
        //for (int j = 0; j < pts.size(); j++) 
        for (int j = 0; j < n; j++) 
        {
            pts2d.add(new Point2D.Double(pts.get(j).x, pts.get(j).y));
        }

        return pts2d;
    }

    /**
     * Creates an MSS from the points and symbolid for testing
     *
     * @param symbolId
     * @param uniqueId
     * @param pts
     * @return
     */
    private static MilStdSymbol CreateMSS(String symbolId, String uniqueId,
            ArrayList<POINT2> pts) {
        // geo points
        //ArrayList<Point2D.Double> pts2d = POINT2ToPoint2D(pts);
        ArrayList<Point2D> pts2d = POINT2ToPoint2D(pts);
        int rev = RendererSettings.getInstance().getSymbologyStandard();
        int linetype = CELineArray.CGetLinetypeFromString(symbolId, rev);
        MilStdSymbol mss = null;
        try {
            mss = new MilStdSymbol(symbolId, uniqueId, pts2d, null);
        } catch (Exception e) {

        }
        //mss.setSymbologyStandard(RendererSettings.Symbology_2525C);
        Color fillColor = null;
        if(fillcolor.isEmpty()==false)
        {    
        	fillColor=SymbolUtilities.getColorFromHexString(fillcolor);
        }
        if(fillColor != null)
        	mss.setFillColor(fillColor);
        
        Color lineColor = null;
        Color textColor = null;
        if(linecolor.isEmpty())
        {
        	if(SymbolUtilities.isWeather(symbolId))
        		lineColor = SymbolUtilities.getLineColorOfWeather(symbolId);
        	else
        		lineColor=SymbolUtilities.getLineColorOfAffiliation(symbolId);
        }
        else
        {
        	lineColor=SymbolUtilities.getColorFromHexString(linecolor);
        }
        if(textcolor.isEmpty())
            textColor=lineColor;
        else
            textColor=SymbolUtilities.getColorFromHexString(textcolor);
            
        if(AM.isEmpty())
            AM="7000,6000,5000";
        if(AN.isEmpty())
            AN="45,315";
        if(X.isEmpty())
            X="27,54";
        String[]am=AM.split(",");
        String[] an=AN.split(",");
        String[]x=X.split(",");
        
        Double[]amd=new Double[am.length];
        Double[]and=new Double[an.length];
        Double[]xd=new Double[x.length];
        int j=0;
        for(j=0;j<am.length;j++)
            amd[j]=Double.parseDouble(am[j]);
        for(j=0;j<an.length;j++)
            and[j]=Double.parseDouble(an[j]);
        for(j=0;j<x.length;j++)
            xd[j]=Double.parseDouble(x[j]);
        
        mss.setLineColor(lineColor);
        mss.setTextColor(textColor);
        //mss.setLineWidth(2);
        if(!lineWidth.isEmpty())
            mss.setLineWidth(Integer.parseInt(lineWidth));
        else
            mss.setLineWidth(2);
        mss.setModifier(ModifiersTG.T_UNIQUE_DESIGNATION_1, T);
        mss.setModifier(ModifiersTG.T1_UNIQUE_DESIGNATION_2, T1);
        mss.setModifier(ModifiersTG.H_ADDITIONAL_INFO_1, H);
        mss.setModifier(ModifiersTG.H1_ADDITIONAL_INFO_2, H1);
        mss.setModifier(ModifiersTG.H2_ADDITIONAL_INFO_3, "H2");
        mss.setModifier(ModifiersTG.W_DTG_1, W);
        mss.setModifier(ModifiersTG.W1_DTG_2, W1);
        mss.setModifier(ModifiersTG.N_HOSTILE, "ENY");
        for(j=0;j<amd.length;j++)            
            mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[j], j);
        for(j=0;j<and.length;j++)            
            mss.setModifier_AM_AN_X(ModifiersTG.AN_AZIMUTH, and[j], j);
        for(j=0;j<xd.length;j++)            
            mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[j], j);
        
//        if (linetype == TacticalLines.AC || linetype == TacticalLines.SAAFR
//                || linetype == TacticalLines.LLTR
//                || linetype == TacticalLines.UAV
//                || linetype == TacticalLines.MRR) {
//            // try custon sizes
//            mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[0], 0); // 4000
//            mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[1], 1); // 3000
//            mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[0], 0);//27
//            mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[1], 1);//54
//        } else if (linetype == TacticalLines.RANGE_FAN
//                || linetype == TacticalLines.RANGE_FAN_SECTOR) {
//            for(j=0;j<amd.length;j++)            
//                mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[j], j);
//            for(j=0;j<amd.length;j++)            
//                mss.setModifier_AM_AN_X(ModifiersTG.AN_AZIMUTH, and[j], j);
//            for(j=0;j<xd.length;j++)            
//                mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[j], j);
//                
//            
//        } else// fire support areas
//        {
//            mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[0], 0);// radius
//            // for
//            // circles
//            // or
//            // width
//            // for
//            // rectangles,
//            // rectangular
//            // tgt
//            mss.setModifier_AM_AN_X(ModifiersTG.AM_DISTANCE, amd[0], 1);// length
//            // rectangular
//            // tgt
//            mss.setModifier_AM_AN_X(ModifiersTG.AN_AZIMUTH, and[0], 0); // attitude
//            // rectangulat
//            // tgt
//            mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[0], 0);// alt
//            // kill
//            // box
//            // purple
//            mss.setModifier_AM_AN_X(ModifiersTG.X_ALTITUDE_DEPTH, xd[1], 1);// alt
//            // kill
//            // box
//            // purple
//        }
        return mss;
    }

    private static String addAltitudes(String controlPtsStr) {
        //ArrayList<String>alStr=new ArrayList();
        String result = "";
        String[] origPts = controlPtsStr.split(" ");
        int j = 0;
        String coords = "";
        int n=origPts.length;
        //for (j = 0; j < origPts.length; j++) 
        for (j = 0; j < n; j++) 
        {
            coords = origPts[j];
            coords += ",0";
            if (j < origPts.length - 1) {
                coords += " ";
            }
            result += coords;
        }
        return result;
    }

    /**
     * string format is lon,lat lon,lat ...
     *
     * @param pts
     * @return
     */
    private static String controlPointsToString(ArrayList<POINT2> pts) {
        String str = "";
        int j = 0;
        int n=pts.size();
        //for (j = 0; j < pts.size(); j++) 
        for (j = 0; j < n; j++) 
        {
            str += Double.toString(pts.get(j).x);
            str += ",";
            str += Double.toString(pts.get(j).y);
            if (j < pts.size() - 1) {
                str += " ";
            }
        }
        return str;
    }

    private static String GetLinetype2(String str, int rev) {
        //String str2=str.toString();
        int linetype = GetLinetype(str, rev);
        String str2 = str;
        str2=str2.toUpperCase();
        if (linetype < 0) {
            //return a valid string the client can use for the symbol id
            int n=str2.length();
            //for (int j = str2.length(); j < 15; j++) 
            for (int j = n; j < 15; j++) 
            {
                str2 += "-";
            }
        }
        return str2;
    }
    /**
     * computes the channel point for axis of advance symbols
     * @param pts
     * @param linetype 
     */
    private static void computePoint(ArrayList<Point>pts,int linetype)
    {
        switch (linetype) {
            case TacticalLines.CATK:
            case TacticalLines.CATKBYFIRE:
            case TacticalLines.AAFNT:
            case TacticalLines.AAAAA:
            case TacticalLines.AIRAOA:
            case TacticalLines.MAIN:
            case TacticalLines.SPT:
            case TacticalLines.AXAD:
            case TacticalLines.CHANNEL:
                Point pt = utility.ComputeLastPoint(pts);
                pts.add(pt);
                break;
            default:
                break;
        }        
    }

    /**
     * The tester for the Google Earth plugin. Assumes pixels only are provided.
     * If the linetype is fire support area then assume they are geo coords.
     * Then use a best fit approach to convert them to pixels.
     *
     * @param pts
     * @param defaultText
     * @param g
     */
    protected static String DoubleClickGE(ArrayList<Point> pts,
            String defaultText, Canvas g2d, Context context) {
        String strResult = "";
        boolean renderAirControls = isAirspace(defaultText);
        if (renderAirControls) {
            return strResult;
        }
        //test text scaling
        //RendererSettings.getInstance().setMPModifierFont("arial", Typeface.BOLD, 18, 2f);
        //Object obj = System.getProperty("java.version");
        ArrayList<Point2D> clipArea = new ArrayList();
        defaultText=defaultText.toUpperCase();
        clipArea.add(new Point2D.Double(0, 0));
        clipArea.add(new Point2D.Double(displayWidth, 0));
        clipArea.add(new Point2D.Double(displayWidth, displayHeight));
        clipArea.add(new Point2D.Double(0, displayHeight));
        clipArea.add(new Point2D.Double(0, 0));
        int rev = 1;
        if(Rev.isEmpty()==false)
        {
            if(Rev.equalsIgnoreCase("B"))                        
                rev=0;            
            else
                rev=1;
        }
        RendererSettings.getInstance().setSymbologyStandard(rev);
        int linetype = utility.GetLinetype(defaultText, rev);
        if (linetype < 0) {
            defaultText = utility.GetLinetype2(defaultText, rev);
            linetype = utility.GetLinetype(defaultText, rev);
        }
        //compute channel point for axis of advance
        computePoint(pts,linetype);
        //utility.ClosePolygon(pts, linetype);
        //"-84.102854,39.799488,-84.100343,39.801342"
        //leftLongitude=-84.102854;
        //rightLongitude=-84.100343;
        //lowerLatitude=39.799488;
        //upperLatitude=39.801342;
        IPointConversion converter = new PointConversion((int) displayWidth,
                (int) displayHeight, upperLatitude, leftLongitude,
                lowerLatitude, rightLongitude);
        String symbolCode = clsSymbolCodeUtility.GetSymbolCode(linetype, rev);
        if (defaultText.substring(0, 2).equalsIgnoreCase("10")) 
        {
            if (defaultText.length() == 16) {
                defaultText += "0000";
            }
            String symbolSet=defaultText.substring(4, 6);
            String entityCode=defaultText.substring(10,16);
            linetype=clsRenderer.getCMLineType(symbolSet, entityCode);
            computePoint(pts,linetype);
            symbolCode=defaultText;
        }
        ArrayList<POINT2> pts2 = PixelsToLatLong(pts, converter);
        if (defaultText.length() == 15) {
            symbolCode = defaultText;
        }
        MilStdSymbol mss = CreateMSS(symbolCode, "0", pts2);
        boolean useDashArray=false;
        //uncomment following line if client intends to calculate dashed lines to improve performance
        //comment the line to allow renderer to calculate the dashes
        useDashArray=true;
        mss.setUseDashArray(useDashArray);
        mss.setSymbologyStandard(rev);
        
        //diagnostic
        boolean renderAsMilStdSymbol=false;
        //uncomment this line to run RendermultiPointAsMilstdSymbol
        //renderAsMilStdSymbol=true;
        if(T.equalsIgnoreCase("renderAsMSS"))
            renderAsMilStdSymbol=true;
        if(!renderAsMilStdSymbol)
        {
            clsRenderer.renderWithPolylines(mss, converter, clipArea, context);                
            drawShapeInfosGE(g2d, mss.getSymbolShapes(),useDashArray,mss.getSymbolID(),null);
            drawShapeInfosText(g2d, mss.getModifierShapes(),null);
        }
        else
        {
            double sizeSquare = Math.abs(rightLongitude - leftLongitude);
            if (sizeSquare > 180) {
                sizeSquare = 360 - sizeSquare;
            }
            //end section
            double scale = 541463 * sizeSquare;
            String rectStr = getRectString(0, 0);
            String controlPtsStr = controlPointsToString(pts2);
            String altitudeMode = "";
            SparseArray<String> modifiers = new SparseArray<String>();
            SparseArray<String> attributes = new SparseArray<String>();
            modifiers.put(ModifiersTG.T_UNIQUE_DESIGNATION_1, T);
            modifiers.put(ModifiersTG.T1_UNIQUE_DESIGNATION_2, T1);
            modifiers.put(ModifiersTG.AM_DISTANCE, AM);
            modifiers.put(ModifiersTG.AN_AZIMUTH, AN);
            modifiers.put(ModifiersTG.X_ALTITUDE_DEPTH, X);
            modifiers.put(ModifiersTG.H1_ADDITIONAL_INFO_2, H1);
            modifiers.put(ModifiersTG.W_DTG_1, W);
            modifiers.put(ModifiersTG.W1_DTG_2, W1);
            attributes.put(MilStdAttributes.FillColor, fillcolor);
            attributes.put(MilStdAttributes.LineColor, linecolor);
            attributes.put(MilStdAttributes.TextColor, textcolor);
            attributes.put(MilStdAttributes.SymbologyStandard, Integer.toString(rev));
            mss=sec.web.render.SECWebRenderer.RenderMultiPointAsMilStdSymbol("id", "name", "description", symbolCode, controlPtsStr, altitudeMode, scale, rectStr, modifiers, attributes, rev);
            drawShapeInfosGE(g2d, mss.getSymbolShapes(),useDashArray,mss.getSymbolID(),converter);
            drawShapeInfosText(g2d, mss.getModifierShapes(),converter);
        }
        //end diagnostic

        return strResult;
    }

    private static void drawSECRendererCoords3d(Canvas g, ArrayList<String> coordStrings, IPointConversion converter) {
        Paint paint = new Paint();
        //BasicStroke stroke = new BasicStroke(2);
        paint.setStyle(Paint.Style.FILL);
        String coords = "";
        String[] coordArray = null;
        int j = 0, k = 0;
        String[] triple = null;
        double x1 = 0, y1 = 0, x2, y2;
        int n=coordStrings.size();
        //for (j = 0; j < coordStrings.size(); j++) 
        for (j = 0; j < n; j++) 
        {
            coords = coordStrings.get(j);
            coordArray = coords.split(" ");
            int t=coordArray.length;
            //for (k = 0; k < coordArray.length - 1; k++) 
            for (k = 0; k < t - 1; k++) 
            {
                triple = coordArray[k].split(",");
                if (triple.length < 3) {
                    continue;
                }
                x1 = Double.parseDouble(triple[0]);
                y1 = Double.parseDouble(triple[1]);
                //Point2D pt=new Point2D.Double(x1,y1);
                Point2D pt = new Point2D.Double(x1, y1);
                pt = converter.GeoToPixels(pt);
                x1 = pt.getX();
                y1 = pt.getY();
                triple = coordArray[k + 1].split(",");
                if (triple.length < 3) {
                    continue;
                }
                x2 = Double.parseDouble(triple[0]);
                y2 = Double.parseDouble(triple[1]);
                //pt=new Point2D.Double(x2,y2);
                pt = new Point2D.Double(x2, y2);
                pt = converter.GeoToPixels(pt);
                x2 = pt.getX();
                y2 = pt.getY();
                g.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
            }
        }
    }

    private static String getRectString(double deltax, double deltay) {
        String str = "";
        // normalize deltas to start
        deltax = Math.abs(deltax);
        deltay = Math.abs(deltay);
        double deltaLHS = 0, deltaRHS = 0, deltaTop = 0, deltaBottom = 0;
        if (leftLongitude - rightLongitude > 180)// 179 to -179
        {
            deltaLHS = deltax;
            deltaRHS = -deltax;
        } else if (leftLongitude - rightLongitude < -180)// -179 to 179
        {
            deltaLHS = -deltax;
            deltaRHS = deltax;
        } else if (leftLongitude < rightLongitude) {
            deltaLHS = deltax;
            deltaRHS = -deltax;
        } else if (leftLongitude > rightLongitude) {
            deltaLHS = -deltax;
            deltaRHS = deltax;
        }

        if (upperLatitude > lowerLatitude) {
            deltaTop = -deltay;
            deltaBottom = deltay;
        } else {
            deltaTop = deltay;
            deltaBottom = -deltay;
        }

        str += Double.toString(leftLongitude + deltaLHS) + ",";
        str += Double.toString(lowerLatitude + deltaBottom) + ",";
        str += Double.toString(rightLongitude + deltaRHS) + ",";
        str += Double.toString(upperLatitude + deltaTop);
        return str;
    }

    /**
     * draw the ArrayLists of polylines for the GoogleEarth project tester
     *
     * @param g
     * @param l
     */
    private static void drawShapeInfosGE(Canvas canvas, List<ShapeInfo> l, boolean useDashedLines, String symbolId,IPointConversion converter) {
        try {
            Iterator i = l.iterator();
            int j = 0;
            ArrayList<ArrayList<Point2D>> polylines = null;
            ArrayList<Point2D> polyline = null;
            int type = -1;
            Path path = new Path();
            Paint paint = new Paint();
            BasicStroke stroke = null;
            while (i.hasNext()) {
                ShapeInfo spec = (ShapeInfo) i.next();
                polylines = spec.getPolylines();
                type = spec.getShapeType();
                stroke = (BasicStroke) spec.getStroke();
                if (spec.getFillColor() != null) {
                    paint.setColor(spec.getFillColor().toARGB());
                }

                paint.setStyle(Paint.Style.FILL);
                if (spec.getShader() != null) {
                    paint.setShader(spec.getShader());
                }
                Point2D pt2d=null;
                Point2D pixels=null;
                if (spec.getFillColor() != null && spec.getFillColor().getAlpha() > 0) {
                    int n=polylines.size();
                    for (j = 0; j < n; j++) 
                    {
                        path = new Path();
                        polyline = polylines.get(j);
                        //diagnostic
                        if(converter==null)
                            path.moveTo((int) polyline.get(0).getX(), (int) polyline.get(0).getY());
                        else
                        {
                            pt2d=polyline.get(0);
                            pixels=converter.GeoToPixels(pt2d);
                            path.moveTo((int) pixels.getX(), (int) pixels.getY());
                        }
                        int t=polyline.size();
                        for (int k = 1; k < t; k++) 
                        {
                            if(converter==null)
                                path.lineTo((int) polyline.get(k).getX(), (int) polyline.get(k).getY());
                            else
                            {
                                pt2d=polyline.get(k);
                                pixels=converter.GeoToPixels(pt2d);
                                path.lineTo((int) pixels.getX(), (int) pixels.getY());
                            }
                        }
                        //end diagnostic
                        canvas.drawPath(path, paint);
                    }
                }
                BasicStroke s = (BasicStroke) spec.getStroke();
                float[] dash = s.getDashArray();
                
                
                if (spec.getLineColor() != null)
                    if(dash==null || useDashedLines==false)
                {
                    paint = new Paint();
                    paint.setColor(spec.getLineColor().toARGB());
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stroke.getLineWidth());                    
                    int n=polylines.size();
                    //for (j = 0; j < polylines.size(); j++) 
                    for (j = 0; j < n; j++) 
                    {
                        polyline = polylines.get(j);
                        path = new Path();
                        //diagnostic
                        if(converter==null)
                            path.moveTo((int) polyline.get(0).getX(), (int) polyline.get(0).getY());
                        else
                        {
                            pt2d=polyline.get(0);
                            pixels=converter.GeoToPixels(pt2d);
                            path.moveTo((int) pixels.getX(), (int) pixels.getY());
                        }
                        int t=polyline.size();
                        //for (int k = 1; k < polyline.size(); k++) 
                        for (int k = 1; k < t; k++) 
                        {
                            if(converter==null)
                                path.lineTo((int) polyline.get(k).getX(), (int) polyline.get(k).getY());
                            else
                            {
                                pt2d=polyline.get(k);
                                pixels=converter.GeoToPixels(pt2d);
                                path.lineTo((int) pixels.getX(), (int) pixels.getY());
                            }
                        }
                        //end diagnostic
                        canvas.drawPath(path, paint);
                    }
                }
                if (spec.getLineColor() != null && dash!=null && useDashedLines==true)
                {
                    drawDashedPolylines(symbolId,polylines,spec,canvas);
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            return;
        }
    }

    /**
     *
     * @param g
     * @param l
     */
    private static void drawShapeInfosText(Canvas g2d, List<ShapeInfo> l, IPointConversion converter) {
        try {
            Iterator i = l.iterator();
            AffineTransform tx = null;
            Point2D position = null;
            double stringAngle = 0;
            Paint paint = null;
            int size = 0;
            float x = 0, y = 0;
            String str = "";
            while (i.hasNext()) {
                int n = g2d.save();
                ShapeInfo spec = (ShapeInfo) i.next();
                TextLayout tl = spec.getTextLayout();
                size = tl.getBounds().height;
                position = spec.getGlyphPosition();
                if(converter != null)
                {
                    position=spec.getModifierStringPosition();
                    position=converter.GeoToPixels(position);
                }
                stringAngle = spec.getModifierStringAngle();
                g2d.rotate((float) stringAngle, (float) position.getX(), (float) position.getY());
                //draw the text twice
                paint = new Paint();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setStrokeWidth(2);
                //paint.setColor(Color.WHITE.toARGB());
                if(textcolor != null && !textcolor.isEmpty())
                    paint.setColor(SymbolUtilities.getColorFromHexString(textcolor).toARGB());
                else
                    paint.setColor(spec.getLineColor().toARGB());
                paint.setTextSize(size);
                paint.setStyle(Paint.Style.STROKE);               
                //paint.setTextAlign(Paint.Align.LEFT);                      
                x = (float) position.getX();
                y = (float) position.getY();
                str = spec.getModifierString();
                g2d.drawText(str, x, y, paint);
                //g2d.drawText(spec.getModifierString(), (float)position.getX(), (float)position.getY(),paint);
                //g2d.rotate(-(float)stringAngle);
                paint = new Paint();
                paint.setTextSize(size);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(spec.getLineColor().toARGB());
                paint.setStyle(Paint.Style.FILL);
                //paint.setTextAlign(Paint.Align.LEFT);                               
                //g2d.drawText(spec.getModifierString(), (float)position.getX(), (float)position.getY(),paint);
                g2d.drawText(str, x, y, paint);
                g2d.restore();
                //g2d.rotate(-(float)stringAngle);
                //paint.setTextSize(10);
            }//end whilc
        }//end try
        catch (Exception e) {
            String s = e.getMessage();
            return;
        }
    }
    private static Boolean isAirspace(String defaultText) {
        if (defaultText.equalsIgnoreCase("cake")) {
            defaultText = "CAKE-----------";
        }
        if (defaultText.equalsIgnoreCase("line")) {
            defaultText = "LINE-----------";
        }
        if (defaultText.equalsIgnoreCase("radarc")) {
            defaultText = "RADARC---------";
        }
        if (defaultText.equalsIgnoreCase("polyarc")) {
            defaultText = "POLYARC--------";
        }
        if (defaultText.equalsIgnoreCase("polygon")) {
            defaultText = "POLYGON--------";
        }
        if (defaultText.equalsIgnoreCase("cylinder")) {
            defaultText = "CYLINDER-------";
        }
        if (defaultText.equalsIgnoreCase("track")) {
            defaultText = "TRACK----------";
        }
        if (defaultText.equalsIgnoreCase("route")) {
            defaultText = "ROUTE----------";
        }
        if (defaultText.equalsIgnoreCase("orbit")) {
            defaultText = "ORBIT----------";
        }
        if (defaultText.equalsIgnoreCase("curtain")) {
            defaultText = "CURTAIN--------";
        }
        //boolean renderWithPolylines = true;
        boolean renderAirControls = false;
        if (defaultText.equals("CAKE-----------")) {
            renderAirControls = true;
        } else if (defaultText.equals("CYLINDER-------")) {
            renderAirControls = true;
        } else if (defaultText.equals("ROUTE----------")) {
            renderAirControls = true;
        } else if (defaultText.equals("RADARC---------")) {
            renderAirControls = true;
        } else if (defaultText.equals("POLYARC--------")) {
            renderAirControls = true;
        } else if (defaultText.equals("POLYGON--------")) {
            renderAirControls = true;
        } else if (defaultText.equals("TRACK----------")) {
            renderAirControls = true;
        } else if (defaultText.equals("ORBIT----------")) {
            renderAirControls = true;
        } else if (defaultText.equals("CURTAIN--------")) {
            renderAirControls = true;
        } else if (defaultText.equals("LINE--------")) {
            renderAirControls = true;
        }
        return renderAirControls;
    }

    protected static String DoubleClickSECRenderer(ArrayList<Point> pts,
            String defaultText, Canvas g2d) {
        String strResult = "";
        int rev = 1;
        if(Rev.isEmpty()==false)
        {
            if(Rev.equalsIgnoreCase("B"))                        
                rev=0;            
            else
                rev=1;
        }
        RendererSettings.getInstance().setSymbologyStandard(rev);
        int linetype = utility.GetLinetype(defaultText, rev);
        if (linetype < 0) {
            defaultText = utility.GetLinetype2(defaultText, rev);
            linetype = utility.GetLinetype(defaultText, rev);
        }
        if (linetype > 0 && defaultText.length() != 15) {
            defaultText = clsSymbolCodeUtility.GetSymbolCode(linetype, rev);
            linetype = utility.GetLinetype(defaultText, rev);
        }

        //utility.ClosePolygon(pts, linetype);

        ArrayList<POINT2> pts2 = PointsToPOINT2(pts);
        double sizeSquare = Math.abs(rightLongitude - leftLongitude);
        if (sizeSquare > 180) {
            sizeSquare = 360 - sizeSquare;
        }

        double scale = 541463 * sizeSquare;

        Point2D ptPixels = null;
        Point2D ptGeo = null;

        IPointConversion converter = null;
        converter = new PointConverter(leftLongitude, upperLatitude, scale);

        int j = 0;
        POINT2 pt2 = null;
        POINT2 pt2Geo = null;
        ArrayList<POINT2> latLongs = new ArrayList();
        int n=pts2.size();
        //for (j = 0; j < pts2.size(); j++) 
        for (j = 0; j < n; j++) 
        {
            pt2 = pts2.get(j);
            ptPixels = new Point2D.Double(pt2.x, pt2.y);
            ptGeo = converter.PixelsToGeo(ptPixels);
            pt2Geo = new POINT2(ptGeo.getX(), ptGeo.getY());
            latLongs.add(pt2Geo);
        }

        String hString = "0:FFFF00FF,1:FFFF00FF,2:FFFF00FF,3:FFFF00FF,4:FFFF00FF,5:FF00FFFF,6:FF00FFFF,7:FF00FFFF,8:FF00FFFF,9:FF00FFFF,10:FF00FFFF,11:FF00FFFF,12:FFFFFF00,13:FFFFFF00,14:FFFFFF00,15:FFFFFF00,16:FFFFFF00,17:FFFFFF00,18:FFFFFF00,19:FF0000FF,20:FF0000FF,21:FF0000FF,22:FF0000FF,23:FF0000FF,24:FFFF00FF,25:FFFF00FF,26:FFFF00FF,27:FFFF00FF,28:FFFF00FF,29:FFFF0000,30:FFFF0000,31:FFFF0000,32:FFFF0000,33:FFFF0000,34:FF00FFFF,35:FF00FFFF,36:FF00FFFF,37:FF00FFFF,38:FF00FFFF";

        SparseArray<String> modifiers = new SparseArray<String>();
        modifiers.put(ModifiersTG.T_UNIQUE_DESIGNATION_1, T);
        modifiers.put(ModifiersTG.T1_UNIQUE_DESIGNATION_2, T1);
        modifiers.put(ModifiersTG.H_ADDITIONAL_INFO_1, H);
        modifiers.put(ModifiersTG.H1_ADDITIONAL_INFO_2, H1);
        modifiers.put(ModifiersTG.H1_ADDITIONAL_INFO_2, "H2");
        modifiers.put(ModifiersTG.W_DTG_1, W);
        modifiers.put(ModifiersTG.W1_DTG_2, W1);
        //diagnostic
        if(!symbolFillIds.isEmpty())
            modifiers.put(ModifiersTG.SYMBOL_FILL_IDS, symbolFillIds);

        SparseArray<String> attributes = new SparseArray<String>();

        String rectStr = getRectString(0, 0);
        String controlPtsStr = controlPointsToString(latLongs);
        String altitudeMode = "";

        boolean renderAirControls=isAirspace(defaultText);
        //Mil-Std-2525 symbols
        if (!renderAirControls) {
            modifiers.put(ModifiersTG.T_UNIQUE_DESIGNATION_1, T);
            modifiers.put(ModifiersTG.T1_UNIQUE_DESIGNATION_2, T1);
            modifiers.put(ModifiersTG.AM_DISTANCE, AM);
            modifiers.put(ModifiersTG.AN_AZIMUTH, AN);
            modifiers.put(ModifiersTG.X_ALTITUDE_DEPTH, X);
            modifiers.put(ModifiersTG.H1_ADDITIONAL_INFO_2, H1);
            modifiers.put(ModifiersTG.W_DTG_1, W);
            modifiers.put(ModifiersTG.W1_DTG_2, W1);
            attributes.put(MilStdAttributes.FillColor, fillcolor);
            attributes.put(MilStdAttributes.LineColor, linecolor);
            attributes.put(MilStdAttributes.TextColor, textcolor);
            attributes.put(MilStdAttributes.SymbologyStandard, Integer.toString(rev));
            if (JavaRendererUtilities.is3dSymbol(defaultText, modifiers)) {

                attributes.put(MilStdAttributes.FillColor, "FF00FF00");
            }
            SECWebRenderer sec = new SECWebRenderer();
            int format=0;
            //format=2;
            String strRender="";
            boolean twod=false;
            //twod=true;
            if(!twod)
            {
                strRender = sec.RenderSymbol("id", "name", "description", defaultText, controlPtsStr, altitudeMode, scale, rectStr, modifiers, attributes, format, rev);                
            }
            else
                strRender = sec.RenderSymbol2D("id", "name", "description", defaultText, controlPtsStr, (int)displayWidth, (int)displayHeight, rectStr, modifiers, attributes, format, rev);
                
            //WriteKMLFile(strRender);
            strResult = strRender;
        }
        else    //Airspaces 
        {            
            if(AM.isEmpty())
            {
                AM="6000,10000,4000,4000,5000,2000";
                AN="15,345,60,100,30,150";
                X="200,500,300,600,100,400";
            }           
            String[]am=AM.split(",");
            String[]an=AN.split(",");
            String[]x=X.split(",");
            //{attributes:[{radius1:5000, radius2:7500, minalt:0, maxalt:4000, leftAzimuth:180, rightAzimuth:270},{radius1:6000, radius2:8000, minalt:0, maxalt:8000, leftAzimuth:160, rightAzimuth:230}]}
            String str="";
            try
            {
                //attributesJSON=new JSONObject();               
                for(j=0;j<am.length/2;j++)
                {
                    str+="{radius1:"+am[2*j]+", "+"radius2:"+am[2*j+1]+", "+"minalt:"+x[2*j]+", "+"maxalt:"+x[2*j+1]+", "+"leftAzimuth:"+an[2*j]+", "+"rightAzimuth:"+an[2*j+1]+"}";
                    if(j<am.length/2-1)
                        str+=",";
                }                
            }
            catch(Exception e)
            {
                
            }
            str="["+str+"]";
            String acAttributes="{attributes:"+str+"}";
            //must add altitudes to control pts 
            controlPtsStr = addAltitudes(controlPtsStr);
            //String acAttributes = "{attributes:[{radius1:5000, radius2:7500, minalt:0, maxalt:100, leftAzimuth:20, rightAzimuth:180}]}";
            //acAttributes = "{attributes:[{radius1:5000, radius2:7500, minalt:0, maxalt:100, leftAzimuth:120, rightAzimuth:180},{radius1:6000, radius2:8000, minalt:0, maxalt:70, leftAzimuth:160, rightAzimuth:230}]}";
            //acAttributes = "{attributes:[{radius1:5000, radius2:7500, minalt:0, maxalt:4000, leftAzimuth:180, rightAzimuth:270},{radius1:6000, radius2:8000, minalt:0, maxalt:8000, leftAzimuth:160, rightAzimuth:230}]}";
            String strCake = "";
            SECWebRenderer sec = new SECWebRenderer();
            //acAttributes=str;
            strCake = sec.Render3dSymbol("name", "id", defaultText, "", "ff0000ff", "800000ff", "", controlPtsStr, acAttributes);
            strResult = strCake;
            ArrayList<String> coordStrings = new ArrayList();
            try {
                //parseKML.parseLLTR(strRender,coordStrings);
                parseKML.parseLLTR(strCake, coordStrings);
            } catch (Exception exc) {

            }

            if (coordStrings.size() > 0) {
                drawSECRendererCoords3d(g2d, coordStrings, converter);
            }
        }

        return strResult;
    }
    private static Point2D ExtendAlongLineDouble2(POINT2 pt1, POINT2 pt2, double dist) {
        double x = 0, y = 0;
        try {
            double dOriginalDistance = CalcDistanceDouble(pt1, pt2);
            if (dOriginalDistance == 0 || dist == 0) {
                return new Point2D.Double(pt1.x, pt1.y);
            }

            x = (dist / dOriginalDistance * (pt2.x - pt1.x) + pt1.x);
            y = (dist / dOriginalDistance * (pt2.y - pt1.y) + pt1.y);
        } catch (Exception exc) {
            ErrorLogger.LogException("utility", "ExtendAlongLineDouble2",
                    new RendererException("Failed inside ExtendAlongLineDouble2", exc));
        }
        //return pt3;
        return new Point2D.Double(x, y);
    }
    /**
     * This function was added as a performance enhancement. The renderer normally creates a new array of length 2 points
     * for each dash in the polyline. If the client sets useDashArray then the renderer skips this step so the client must compute
     * the dashes based on the stroke dash array in the shape object.
     *
     * @param symbolId
     * @param polylines
     * @param shape
     */
    private static void drawDashedPolylines(String symbolId, ArrayList<ArrayList<Point2D>> polylines, ShapeInfo shape, Canvas g2d) {
        try {
            //android.graphics.Point aPt = new android.graphics.Point();
            int rev = RendererSettings.getInstance().getSymbologyStandard();
            int linetype = GetLinetype(symbolId, rev);
            Paint paint = new Paint();
            paint.setColor(shape.getLineColor().toARGB());
            paint.setStyle(Paint.Style.STROKE);
            BasicStroke stroke=(BasicStroke)shape.getStroke();
            paint.setStrokeWidth(stroke.getLineWidth());                     
            if (shape.getLineColor() == null) {
                return;
            }
            float[] dash = stroke.getDashArray();
            float lineThickness = stroke.getLineWidth();
            if (dash == null || dash.length < 2) {
                return;
            }

            if (dash.length == 8)//dotted line
            {
                dash = new float[2];
                dash[0] = 2f;
                dash[1] = 2f;
                stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 2f, dash, 0f);
                shape.setStroke(stroke);
            }
            if (dash.length == 4) {
                if (dash[0] == lineThickness * 2f && dash[1] == lineThickness * 2f && dash[2] == lineThickness * 2f && dash[3] == lineThickness * 2f)//this really looks awful in GE
                {
                    dash = new float[2];
                    dash[0] = lineThickness;
                    dash[1] = lineThickness;
                }
            }

            int j = 0, k = 0, i = 0, l = 0, n = 0;
            ArrayList<Point2D> polyline = null;
            Point2D pt2d0 = null, pt2d1 = null, pt2d2 = null, pt2d3 = null;
            POINT2 pt0 = null, pt1 = null;
            double dist = 0;
            double patternLength = 0;
            int numSegments = 0;
            int t = dash.length;
            for (j = 0; j < t; j++) {
                patternLength += dash[j];
            }
            //sum is the end length of eash dash element
            float sum[] = new float[dash.length];
            double remainder = 0;
            t = sum.length;
            for (j = 0; j < t; j++) {
                for (k = 0; k <= j; k++) {
                    sum[j] += dash[k];
                }
            }

            boolean noShortSegments = false;
            switch (linetype) {
                case TacticalLines.LINTGT:
                case TacticalLines.LINTGTS:
                case TacticalLines.FPF:
                case TacticalLines.HWFENCE:
                case TacticalLines.LWFENCE:
                case TacticalLines.DOUBLEA:
                case TacticalLines.DFENCE:
                case TacticalLines.SFENCE:
                case TacticalLines.UNSP:
                    noShortSegments = true;
                    break;
                default:
                    break;
            }
            t = polylines.size();
            for (j = 0; j < t; j++) {
                polyline = polylines.get(j);
                int u = polyline.size();
                for (k = 0; k < u - 1; k++) {
                    pt2d0 = polyline.get(k);
                    pt2d1 = polyline.get(k + 1);
                    pt0 = new POINT2(pt2d0.getX(), pt2d0.getY());
                    pt1 = new POINT2(pt2d1.getX(), pt2d1.getY());
                    dist = lineutility.CalcDistanceDouble(pt0, pt1);
                    numSegments = (int) (dist / patternLength);

                    if (noShortSegments) {
                        if (dist < 25) {
                            numSegments = 1;
                        }
                    }

                    for (l = 0; l < numSegments; l++) {
                        int v = dash.length;
                        for (i = 0; i < v; i++) {
                            //latlngs.clear();
                            if (i % 2 == 0) {
                                if (i == 0) {
                                    pt2d2 = ExtendAlongLineDouble2(pt0, pt1, l * patternLength);
                                } else {
                                    pt2d2 = ExtendAlongLineDouble2(pt0, pt1, l * patternLength + sum[i - 1]);
                                }
                                pt2d3 = ExtendAlongLineDouble2(pt0, pt1, l * patternLength + sum[i]);
                                g2d.drawLine((float)pt2d2.getX(), (float)pt2d2.getY(), (float)pt2d3.getX(), (float)pt2d3.getY(), paint);
                            }
                        }
                    }//end l loop
                    //for the remainder split the difference
                    remainder = dist - numSegments * patternLength;
                    if (remainder > 0) {
                        pt2d2 = ExtendAlongLineDouble2(pt0, pt1, numSegments * patternLength + remainder / 2);
                        g2d.drawLine((float)pt2d1.getX(), (float)pt2d1.getY(), (float)pt2d2.getX(), (float)pt2d2.getY(), paint);

                    }
                }//end k loop
            }//end j loop
        } catch (Exception exc) {
            ErrorLogger.LogException("utility", "createDashedPolylines",
                    new RendererException("Failed inside createDashedPolylines", exc));
        }
    }
}
