/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package armyc2.c2sd.JavaLineArray;
import java.util.ArrayList;
import armyc2.c2sd.renderer.utilities.ErrorLogger;
import armyc2.c2sd.renderer.utilities.RendererException;
import armyc2.c2sd.renderer.utilities.RendererSettings;
/**
 * A class for the interface between the points calculation CELineArray and
 * the tactical renderer.
 *
 * @author Michael Deutch
 */
public final class CELineArray {
    private static final String _className="CELineArray";
    /**
     * Client interface function, called by tactical renderer clsChannelUtility
     * @param lpsaUpperVBPoints the client points as an array of 2-tuples: x pixels, y pixels
     * @param lpsaLowerVBPoints generally the same as the upper points
     * @param resultVBPoints calculated points to return as 3 tuples: x,y,linestyle
     * @param vblUpperCounter the number of client points
     * @param vblLowerCounter the number of client points
     * @param vbiDrawThis the line type as a hierarchy
     * @param vblChannelWidth the channel width in pixels
     * @param usePtr currently being used as the distance from the this to the back of the arrowhead
     * @param shapes ShapeInfo object to return which will contain the GeneralPath for the line
     * @return the point count for the return array
     */
    public static int CGetChannel2Double(double[] lpsaUpperVBPoints,
            double[] lpsaLowerVBPoints,
            double[] resultVBPoints,
            int vblUpperCounter,
            int vblLowerCounter,
            int vbiDrawThis,
            int vblChannelWidth,
            int usePtr,
            ArrayList<Shape2>shapes,
            int rev) throws Exception
    {
        int lResult = -1;

        try {
            lResult = Channels.GetChannel1Double(lpsaUpperVBPoints,
                    lpsaLowerVBPoints,
                    resultVBPoints,
                    vblUpperCounter,
                    vblLowerCounter,
                    vbiDrawThis,
                    vblChannelWidth,
                    usePtr,
                    shapes,
                    rev);

            //GC.Collect();
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"CGetChannel2Double",
                    new RendererException("Failed inside CGetChannel2Double " + Integer.toString(vbiDrawThis), exc));
        }
        return lResult;
    }
    /**
    * public function to return the line count required for all of the symbols
    *
    * @param plArrayOfLongs the client points as an array of POINT2 in pixels.
    * @param lElements the number of client points.
    * @param lineType the line type
    * @param ChannelWdith the chanel width in pixels
    * @param rev the Mil-Standard-2525 revision 
    *
    * @return the number of points which will be required for the symbol.
    */
    public static int CGetLineCountDouble(double[] plArrayOfLongs,
            int lElements, //number of points
            int lineType,
            int ChannelWidth,
            int rev) {
        int lResult = 0;
        try {
            //declarations
            int lPtrcntr = 0;
            int lLowerFlotCount = 0, lUpperFlotCount = 0;
            POINT2[] pLinePoints = new POINT2[lElements];
            POINT2[] pLowerLinePoints = new POINT2[lElements],
                    pUpperLinePoints = new POINT2[lElements],
                    pUpperLowerLinePoints = new POINT2[2 * lElements + 2];
            short i = 0;
            //end declarations

            if (lElements <= 0) {
                return -1;
            }

            lineutility.InitializePOINT2Array(pLinePoints);
            lineutility.InitializePOINT2Array(pUpperLinePoints);
            lineutility.InitializePOINT2Array(pLowerLinePoints);
            for (i = 0; i < lElements; i++) {
                pLinePoints[i].x = plArrayOfLongs[lPtrcntr];
                lPtrcntr++;
                pLinePoints[i].y = plArrayOfLongs[lPtrcntr];
                lPtrcntr++;
            }
            for (i = 0; i < lElements; i++) {
                pLowerLinePoints[i] = new POINT2(pLinePoints[i]);
                pUpperLinePoints[i] = new POINT2(pLinePoints[i]);
            }

            switch (lineType) {
                case TacticalLines.CHANNEL:
                case TacticalLines.CHANNEL_FLARED:
                case TacticalLines.CHANNEL_DASHED:
                    lResult = 2 * lElements;
                    break;
                case TacticalLines.AXAD:
                case TacticalLines.MAIN:
                case TacticalLines.MAIN_STRAIGHT:
                case TacticalLines.AAFNT:
                case TacticalLines.AAFNT_STRAIGHT:
                case TacticalLines.AIRAOA:
                case TacticalLines.SPT:
                case TacticalLines.SPT_STRAIGHT:
                    //points for these need not be bounded
                    //they have an extra 8 points for the arrowhead
                    lResult = 2 * lElements + 8;
                    break;
                case TacticalLines.CATK:
                    lResult = 2 * lElements + 8;
                    break;
                case TacticalLines.CATKBYFIRE:
                    lResult = 2 * lElements + 17;
                    break;
                case TacticalLines.AAAAA:
                    lResult = 2 * lElements + 19;
                    break;
                case TacticalLines.LC:
                    pUpperLinePoints = Channels.GetChannelArray2Double(1, pUpperLinePoints, 1, lElements, lineType, ChannelWidth);
                    pLowerLinePoints = Channels.GetChannelArray2Double(1, pLowerLinePoints, 0, lElements, lineType, ChannelWidth);
                    lUpperFlotCount = flot.GetFlotCountDouble(pUpperLinePoints, lElements);
                    lLowerFlotCount = flot.GetFlotCountDouble(pLowerLinePoints, lElements);
                    lResult = lUpperFlotCount + lLowerFlotCount;
                    break;
                default:
                    //call GetCountersDouble for the remaining line types.
                    lResult = countsupport.GetCountersDouble(lineType, lElements, pLinePoints, null,rev);
                    break;
            }


            //clean up
            //pvblCounters = null;
            pLinePoints = null;
            pLowerLinePoints = null;
            pUpperLinePoints = null;
            pUpperLowerLinePoints = null;
            //GC.Collect();
        } catch (Exception exc) {
            ErrorLogger.LogException(_className ,"CGetLineCountDouble",
                    new RendererException("Failed inside CGetLineCount " + Integer.toString(lineType), exc));
        }
        return (lResult);
    }
    /**
     * Return the line type as a hierarchy from the 15 character Mil-Standard-2525 symbol code
     * @param strLine
     * @param rev the Mil-Standard-2525 revision
     * @return
     */
    public static int CGetLinetypeFromString(String strLine,
            int rev)
    {
        try
        {
            //added this section so the SEC tester can test from generic names
            //these strings will not be used by the CPOF client            
            if(strLine.length()<15)
            {
                if(rev==RendererSettings.Symbology_2525C)
                {
                    if(strLine.equalsIgnoreCase("SCREEN"))
                        return TacticalLines.SCREEN_REVC;
                    if(strLine.equalsIgnoreCase("COVER"))
                        return TacticalLines.COVER_REVC;
                    if(strLine.equalsIgnoreCase("GUARD"))
                        return TacticalLines.GUARD_REVC;                    
                    if(strLine.equalsIgnoreCase("SEIZE"))
                        return TacticalLines.SEIZE_REVC;                    
                }
                if(strLine.equalsIgnoreCase("BS_LINE"))
                    return TacticalLines.BS_LINE;
                if(strLine.equalsIgnoreCase("BS_AREA"))
                    return TacticalLines.BS_AREA;
                if(strLine.equalsIgnoreCase("BS_CROSS"))
                    return TacticalLines.BS_CROSS;
                if(strLine.equalsIgnoreCase("BS_RECTANGLE"))
                    return TacticalLines.BS_RECTANGLE;
                if(strLine.equalsIgnoreCase("PBS_RECTANGLE"))
                    return TacticalLines.PBS_RECTANGLE;
                if(strLine.equalsIgnoreCase("PBS_SQUARE"))
                    return TacticalLines.PBS_SQUARE;
                //buffered lines
                if(strLine.equalsIgnoreCase("BBS_LINE"))
                    return TacticalLines.BBS_LINE;
                if(strLine.equalsIgnoreCase("BBS_AREA"))
                    return TacticalLines.BBS_AREA;
                if(strLine.equalsIgnoreCase("BBS_POINT"))
                    return TacticalLines.BBS_POINT;
                if(strLine.equalsIgnoreCase("BBS_RECTANGLE"))
                    return TacticalLines.BBS_RECTANGLE;
                if(strLine.equalsIgnoreCase("BS_BBOX"))
                    return TacticalLines.BS_BBOX;
                
                if(strLine.equalsIgnoreCase("BS_ELLIPSE"))
                    return TacticalLines.BS_ELLIPSE;
                if(strLine.equalsIgnoreCase("PBS_ELLIPSE"))
                    return TacticalLines.PBS_ELLIPSE;
                if(strLine.equalsIgnoreCase("PBS_CIRCLE"))
                    return TacticalLines.PBS_CIRCLE;
                if(strLine.equalsIgnoreCase("OVERHEAD_WIRE"))
                    return TacticalLines.OVERHEAD_WIRE;
                if(strLine.equalsIgnoreCase("CORDONSEARCH"))
                    return TacticalLines.CORDONSEARCH;
                if(strLine.equalsIgnoreCase("CORDONKNOCK"))
                    return TacticalLines.CORDONKNOCK;
                if(strLine.equalsIgnoreCase("NAVIGATION"))
                    return TacticalLines.NAVIGATION;
                if(strLine.equalsIgnoreCase("BLOCK"))
                    return TacticalLines.BLOCK;
                if(strLine.equalsIgnoreCase("BREACH"))
                    return TacticalLines.BREACH;
                if(strLine.equalsIgnoreCase("BYPASS"))
                    return TacticalLines.BYPASS;
                if(strLine.equalsIgnoreCase("CANALIZE"))
                    return TacticalLines.CANALIZE;
                if(strLine.equalsIgnoreCase("CLEAR"))
                    return TacticalLines.CLEAR;
                if(strLine.equalsIgnoreCase("CONTAIN"))
                    return TacticalLines.CONTAIN;
                if(strLine.equalsIgnoreCase("CATK"))
                    return TacticalLines.CATK;
                if(strLine.equalsIgnoreCase("CATKBYFIRE"))
                    return TacticalLines.CATKBYFIRE;
                if(strLine.equalsIgnoreCase("DELAY"))
                    return TacticalLines.DELAY;
                if(strLine.equalsIgnoreCase("DISRUPT"))
                    return TacticalLines.DISRUPT;
                if(strLine.equalsIgnoreCase("FIX"))
                    return TacticalLines.FIX;
                if(strLine.equalsIgnoreCase("FOLLA"))
                    return TacticalLines.FOLLA;
                if(strLine.equalsIgnoreCase("FOLSP"))
                    return TacticalLines.FOLSP;
                if(strLine.equalsIgnoreCase("ISOLATE"))
                    return TacticalLines.ISOLATE;
                if(strLine.equalsIgnoreCase("OCCUPY"))
                    return TacticalLines.OCCUPY;
                if(strLine.equalsIgnoreCase("RIP"))
                    return TacticalLines.RIP;
                if(strLine.equalsIgnoreCase("RETAIN"))
                    return TacticalLines.RETAIN;
                if(strLine.equalsIgnoreCase("RETIRE"))
                    return TacticalLines.RETIRE;
                if(strLine.equalsIgnoreCase("SECURE"))
                    return TacticalLines.SECURE;
                if(strLine.equalsIgnoreCase("SCREEN"))
                    return TacticalLines.SCREEN;
                if(strLine.equalsIgnoreCase("COVER"))
                    return TacticalLines.COVER;
                if(strLine.equalsIgnoreCase("GUARD"))
                    return TacticalLines.GUARD;
                if(strLine.equalsIgnoreCase("SEZIE"))
                    return TacticalLines.SEIZE;
                if(strLine.equalsIgnoreCase("WITHDRAW"))
                    return TacticalLines.WITHDRAW;
                if(strLine.equalsIgnoreCase("WDRAWUP"))
                    return TacticalLines.WDRAWUP;

                if(strLine.equalsIgnoreCase("BOUNDARY"))
                    return TacticalLines.BOUNDARY;
                if(strLine.equalsIgnoreCase("FLOT"))
                    return TacticalLines.FLOT;
                if(strLine.equalsIgnoreCase("LC"))
                    return TacticalLines.LC;
                if(strLine.equalsIgnoreCase("PL"))
                    return TacticalLines.PL;
                if(strLine.equalsIgnoreCase("LL"))
                    return TacticalLines.LL;
                if(strLine.equalsIgnoreCase("GENERAL"))
                    return TacticalLines.GENERAL;
                if(strLine.equalsIgnoreCase("GENERIC"))
                    return TacticalLines.GENERIC;
                if(strLine.equalsIgnoreCase("ASSY"))
                    return TacticalLines.ASSY;
                if(strLine.equalsIgnoreCase("EA"))
                    return TacticalLines.EA;
                if(strLine.equalsIgnoreCase("FORT"))
                    return TacticalLines.FORT;
                if(strLine.equalsIgnoreCase("DZ"))
                    return TacticalLines.DZ;
                if(strLine.equalsIgnoreCase("EZ"))
                    return TacticalLines.EZ;
                if(strLine.equalsIgnoreCase("LZ"))
                    return TacticalLines.LZ;
                if(strLine.equalsIgnoreCase("PZ"))
                    return TacticalLines.PZ;
                if(strLine.equalsIgnoreCase("SARA"))
                    return TacticalLines.SARA;
                if(strLine.equalsIgnoreCase("LAA"))
                    return TacticalLines.LAA;
                if(strLine.equalsIgnoreCase("AIRFIELD"))
                    return TacticalLines.AIRFIELD;
                if(strLine.equalsIgnoreCase("AC"))
                    return TacticalLines.AC;
                if(strLine.equalsIgnoreCase("MRR"))
                    return TacticalLines.MRR;
                if(strLine.equalsIgnoreCase("MRR_USAS"))
                    return TacticalLines.MRR_USAS;
                if(strLine.equalsIgnoreCase("SAAFR"))
                    return TacticalLines.SAAFR;
                if(strLine.equalsIgnoreCase("UAV"))
                    return TacticalLines.UAV;
                if(strLine.equalsIgnoreCase("UAV_USAS"))
                    return TacticalLines.UAV_USAS;
                if(strLine.equalsIgnoreCase("LLTR"))
                    return TacticalLines.LLTR;

                if(strLine.equalsIgnoreCase("ROZ"))
                    return TacticalLines.ROZ;
                if(strLine.equalsIgnoreCase("SHORADZ"))
                    return TacticalLines.FAADZ;
                if(strLine.equalsIgnoreCase("FAADZ"))
                    return TacticalLines.FAADZ;
                if(strLine.equalsIgnoreCase("HIDACZ"))
                    return TacticalLines.HIDACZ;
                if(strLine.equalsIgnoreCase("MEZ"))
                    return TacticalLines.MEZ;
                if(strLine.equalsIgnoreCase("LOMEZ"))
                    return TacticalLines.LOMEZ;
                if(strLine.equalsIgnoreCase("HIMEZ"))
                    return TacticalLines.HIMEZ;
                if(strLine.equalsIgnoreCase("WFZ"))
                    return TacticalLines.WFZ;
                if(strLine.equalsIgnoreCase("DECEIVE"))
                    return TacticalLines.DECEIVE;
                if(strLine.equalsIgnoreCase("AAFNT"))
                    return TacticalLines.AAFNT;
                if(strLine.equalsIgnoreCase("DIRATKFNT"))
                    return TacticalLines.DIRATKFNT;
                if(strLine.equalsIgnoreCase("DMA"))
                    return TacticalLines.DMA;
                if(strLine.equalsIgnoreCase("DMAF"))
                    return TacticalLines.DMAF;
                if(strLine.equalsIgnoreCase("DUMMY"))
                    return TacticalLines.DUMMY;
                if(strLine.equalsIgnoreCase("FEBA"))
                    return TacticalLines.FEBA;
                if(strLine.equalsIgnoreCase("PDF"))
                    return TacticalLines.PDF;
                if(strLine.equalsIgnoreCase("BATTLE"))
                    return TacticalLines.BATTLE;
                if(strLine.equalsIgnoreCase("PNO"))
                    return TacticalLines.PNO;
                if(strLine.equalsIgnoreCase("EA1"))
                    return TacticalLines.EA1;
                if(strLine.equalsIgnoreCase("AXAD"))
                    return TacticalLines.AXAD;
                if(strLine.equalsIgnoreCase("AIRAOA"))
                    return TacticalLines.AIRAOA;
                if(strLine.equalsIgnoreCase("AAAAA"))
                    return TacticalLines.AAAAA;
                if(strLine.equalsIgnoreCase("ROTARY"))
                    return TacticalLines.AAAAA;
                if(strLine.equalsIgnoreCase("MAIN"))
                    return TacticalLines.MAIN;
                if(strLine.equalsIgnoreCase("SPT"))
                    return TacticalLines.SPT;
                if(strLine.equalsIgnoreCase("DIRATKAIR"))
                    return TacticalLines.DIRATKAIR;
                if(strLine.equalsIgnoreCase("DIRATKGND"))
                    return TacticalLines.DIRATKGND;
                if(strLine.equalsIgnoreCase("DIRATKSPT"))
                    return TacticalLines.DIRATKSPT;

                if(strLine.equalsIgnoreCase("FCL"))
                    return TacticalLines.FCL;
                if(strLine.equalsIgnoreCase("IL"))
                    return TacticalLines.IL;
                if(strLine.equalsIgnoreCase("LOA"))
                    return TacticalLines.LOA;
                if(strLine.equalsIgnoreCase("LOD"))
                    return TacticalLines.LOD;
                if(strLine.equalsIgnoreCase("LDLC"))
                    return TacticalLines.LDLC;
                if(strLine.equalsIgnoreCase("PLD"))
                    return TacticalLines.PLD;
                if(strLine.equalsIgnoreCase("ASSAULT"))
                    return TacticalLines.ASSAULT;
                if(strLine.equalsIgnoreCase("ATKPOS"))
                    return TacticalLines.ATKPOS;
                if(strLine.equalsIgnoreCase("ATKBYFIRE"))
                    return TacticalLines.ATKBYFIRE;
                if(strLine.equalsIgnoreCase("SPTBYFIRE"))
                    return TacticalLines.SPTBYFIRE;

                if(strLine.equalsIgnoreCase("ENCIRCLE"))
                    return TacticalLines.ENCIRCLE;
                if(strLine.equalsIgnoreCase("SEIZE"))
                    return TacticalLines.SEIZE;
                if(strLine.equalsIgnoreCase("OBJ"))
                    return TacticalLines.OBJ;
                if(strLine.equalsIgnoreCase("PEN"))
                    return TacticalLines.PEN;
                if(strLine.equalsIgnoreCase("PENETRATE"))
                    return TacticalLines.PENETRATE;
                if(strLine.equalsIgnoreCase("AMBUSH"))
                    return TacticalLines.AMBUSH;
                if(strLine.equalsIgnoreCase("HOLD"))
                    return TacticalLines.HOLD;
                if(strLine.equalsIgnoreCase("RELEASE"))
                    return TacticalLines.RELEASE;
                if(strLine.equalsIgnoreCase("BRDGHD"))
                    return TacticalLines.BRDGHD;
                if(strLine.equalsIgnoreCase("AO"))
                    return TacticalLines.AO;
                if(strLine.equalsIgnoreCase("AIRHEAD"))
                    return TacticalLines.AIRHEAD;
                if(strLine.equalsIgnoreCase("NAI"))
                    return TacticalLines.NAI;
                if(strLine.equalsIgnoreCase("TAI"))
                    return TacticalLines.TAI;

                if(strLine.equalsIgnoreCase("BELT"))
                    return TacticalLines.BELT;
                if(strLine.equalsIgnoreCase("BELT1"))
                    return TacticalLines.BELT1;
                if(strLine.equalsIgnoreCase("LINE"))
                    return TacticalLines.LINE;
                if(strLine.equalsIgnoreCase("ZONE"))
                    return TacticalLines.ZONE;
                if(strLine.equalsIgnoreCase("OBSFAREA"))
                    return TacticalLines.OBSFAREA;
                if(strLine.equalsIgnoreCase("OBSAREA"))
                    return TacticalLines.OBSAREA;
                if(strLine.equalsIgnoreCase("ABATIS"))
                    return TacticalLines.ABATIS;

                if(strLine.equalsIgnoreCase("ATDITCH"))
                    return TacticalLines.ATDITCH;
                if(strLine.equalsIgnoreCase("ATDITCHC"))
                    return TacticalLines.ATDITCHC;
                if(strLine.equalsIgnoreCase("ATDITCHM"))
                    return TacticalLines.ATDITCHM;
                if(strLine.equalsIgnoreCase("ATWALL"))
                    return TacticalLines.ATWALL;
                if(strLine.equalsIgnoreCase("CLUSTER"))
                    return TacticalLines.CLUSTER;
                if(strLine.equalsIgnoreCase("DEPICT"))
                    return TacticalLines.DEPICT;
                if(strLine.equalsIgnoreCase("GAP"))
                    return TacticalLines.GAP;
                if(strLine.equalsIgnoreCase("MINED"))
                    return TacticalLines.MINED;
                if(strLine.equalsIgnoreCase("MNFLDBLK"))
                    return TacticalLines.MNFLDBLK;
                if(strLine.equalsIgnoreCase("MNFLDFIX"))
                    return TacticalLines.MNFLDFIX;
                if(strLine.equalsIgnoreCase("TURN"))
                    return TacticalLines.TURN;
                if(strLine.equalsIgnoreCase("MNFLDDIS"))
                    return TacticalLines.MNFLDDIS;
                if(strLine.equalsIgnoreCase("UXO"))
                    return TacticalLines.UXO;
                if(strLine.equalsIgnoreCase("PLANNED"))
                    return TacticalLines.PLANNED;
                if(strLine.equalsIgnoreCase("ESR1"))
                    return TacticalLines.ESR1;
                if(strLine.equalsIgnoreCase("ESR2"))
                    return TacticalLines.ESR2;
                if(strLine.equalsIgnoreCase("ROADBLK"))
                    return TacticalLines.ROADBLK;
                if(strLine.equalsIgnoreCase("TRIP"))
                    return TacticalLines.TRIP;
                if(strLine.equalsIgnoreCase("UNSP"))
                    return TacticalLines.UNSP;
                if(strLine.equalsIgnoreCase("SFENCE"))
                    return TacticalLines.SFENCE;
                if(strLine.equalsIgnoreCase("DFENCE"))
                    return TacticalLines.DFENCE;
                if(strLine.equalsIgnoreCase("DOUBLEA"))
                    return TacticalLines.DOUBLEA;
                if(strLine.equalsIgnoreCase("LWFENCE"))
                    return TacticalLines.LWFENCE;
                if(strLine.equalsIgnoreCase("HWFENCE"))
                    return TacticalLines.HWFENCE;
                if(strLine.equalsIgnoreCase("SINGLEC"))
                    return TacticalLines.SINGLEC;
                if(strLine.equalsIgnoreCase("DOUBLEC"))
                    return TacticalLines.DOUBLEC;
                if(strLine.equalsIgnoreCase("TRIPLE"))
                    return TacticalLines.TRIPLE;

                if(strLine.equalsIgnoreCase("EASY"))
                    return TacticalLines.EASY;
                if(strLine.equalsIgnoreCase("BYDIF"))
                    return TacticalLines.BYDIF;
                if(strLine.equalsIgnoreCase("BYIMP"))
                    return TacticalLines.BYIMP;

                if(strLine.equalsIgnoreCase("ASLTXING"))
                    return TacticalLines.ASLTXING;
                if(strLine.equalsIgnoreCase("BRIDGE"))
                    return TacticalLines.BRIDGE;
                if(strLine.equalsIgnoreCase("FERRY"))
                    return TacticalLines.FERRY;
                if(strLine.equalsIgnoreCase("FORD"))
                    return TacticalLines.FORDSITE;
                if(strLine.equalsIgnoreCase("FORDSITE"))
                    return TacticalLines.FORDSITE;
                if(strLine.equalsIgnoreCase("FORDIF"))
                    return TacticalLines.FORDIF;
                if(strLine.equalsIgnoreCase("LANE"))
                    return TacticalLines.MFLANE;
                if(strLine.equalsIgnoreCase("MFLANE"))
                    return TacticalLines.MFLANE;
                if(strLine.equalsIgnoreCase("RAFT"))
                    return TacticalLines.RAFT;

                if(strLine.equalsIgnoreCase("FORTL"))
                    return TacticalLines.FORTL;
                if(strLine.equalsIgnoreCase("FOXHOLE"))
                    return TacticalLines.FOXHOLE;
                if(strLine.equalsIgnoreCase("STRONG"))
                    return TacticalLines.STRONG;
                if(strLine.equalsIgnoreCase("MSDZ"))
                    return TacticalLines.MSDZ;
                if(strLine.equalsIgnoreCase("RAD"))
                    return TacticalLines.RAD;
                if(strLine.equalsIgnoreCase("CHEM"))
                    return TacticalLines.CHEM;
                if(strLine.equalsIgnoreCase("BIO"))
                    return TacticalLines.BIO;
                if(strLine.equalsIgnoreCase("DRCL"))
                    return TacticalLines.DRCL;

                if(strLine.equalsIgnoreCase("LINTGT"))
                    return TacticalLines.LINTGT;
                if(strLine.equalsIgnoreCase("LINTGTS"))
                    return TacticalLines.LINTGTS;
                if(strLine.equalsIgnoreCase("FPF"))
                    return TacticalLines.FPF;
                if(strLine.equalsIgnoreCase("FSCL"))
                    return TacticalLines.FSCL;
                if(strLine.equalsIgnoreCase("CFL"))
                    return TacticalLines.CFL;
                if(strLine.equalsIgnoreCase("NFL"))
                    return TacticalLines.NFL;
                if(strLine.equalsIgnoreCase("MFP"))
                    return TacticalLines.MFP;
                if(strLine.equalsIgnoreCase("TGMF"))
                    return TacticalLines.TGMF;
                if(strLine.equalsIgnoreCase("RFL"))
                    return TacticalLines.RFL;
                if(strLine.equalsIgnoreCase("AT"))
                    return TacticalLines.AT;

                if(strLine.equalsIgnoreCase("RECTANGULAR"))
                    return TacticalLines.RECTANGULAR;
                if(strLine.equalsIgnoreCase("CIRCULAR"))
                    return TacticalLines.CIRCULAR;
                if(strLine.equalsIgnoreCase("SERIES"))
                    return TacticalLines.SERIES;
                if(strLine.equalsIgnoreCase("SMOKE"))
                    return TacticalLines.SMOKE;
                if(strLine.equalsIgnoreCase("BOMB"))
                    return TacticalLines.BOMB;

                if(strLine.equalsIgnoreCase("FSA"))
                    return TacticalLines.FSA;
                if(strLine.equalsIgnoreCase("FSA_RECTANGULAR"))
                    return TacticalLines.FSA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("FSA_CIRCULAR"))
                    return TacticalLines.FSA_CIRCULAR;

                if(strLine.equalsIgnoreCase("ACA"))
                    return TacticalLines.ACA;
                if(strLine.equalsIgnoreCase("ACA_RECTANGULAR"))
                    return TacticalLines.ACA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("ACA_CIRCULAR"))
                    return TacticalLines.ACA_CIRCULAR;

                if(strLine.equalsIgnoreCase("FFA"))
                    return TacticalLines.FFA;
                if(strLine.equalsIgnoreCase("FFA_RECTANGULAR"))
                    return TacticalLines.FFA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("FFA_CIRCULAR"))
                    return TacticalLines.FFA_CIRCULAR;

                if(strLine.equalsIgnoreCase("NFA"))
                    return TacticalLines.NFA;
                if(strLine.equalsIgnoreCase("NFA_RECTANGULAR"))
                    return TacticalLines.NFA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("NFA_CIRCULAR"))
                    return TacticalLines.NFA_CIRCULAR;

                if(strLine.equalsIgnoreCase("RFA"))
                    return TacticalLines.RFA;
                if(strLine.equalsIgnoreCase("RFA_RECTANGULAR"))
                    return TacticalLines.RFA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("RFA_CIRCULAR"))
                    return TacticalLines.RFA_CIRCULAR;

                if(strLine.equalsIgnoreCase("PAA"))
                    return TacticalLines.PAA;
                if(strLine.equalsIgnoreCase("PAA_RECTANGULAR"))
                    return TacticalLines.PAA_RECTANGULAR_REVC;
                if(strLine.equalsIgnoreCase("PAA_RECTANGULAR_REVC"))
                    return TacticalLines.PAA_RECTANGULAR_REVC;
                if(strLine.equalsIgnoreCase("PAA_CIRCULAR"))
                    return TacticalLines.PAA_CIRCULAR;

                if(strLine.equalsIgnoreCase("ATI"))
                    return TacticalLines.ATI;
                if(strLine.equalsIgnoreCase("ATI_RECTANGULAR"))
                    return TacticalLines.ATI_RECTANGULAR;
                if(strLine.equalsIgnoreCase("ATI_CIRCULAR"))
                    return TacticalLines.ATI_CIRCULAR;

                if(strLine.equalsIgnoreCase("CFFZ"))
                    return TacticalLines.CFFZ;
                if(strLine.equalsIgnoreCase("CFFZ_RECTANGULAR"))
                    return TacticalLines.CFFZ_RECTANGULAR;
                if(strLine.equalsIgnoreCase("CFFZ_CIRCULAR"))
                    return TacticalLines.CFFZ_CIRCULAR;

                if(strLine.equalsIgnoreCase("SENSOR"))
                    return TacticalLines.SENSOR;
                if(strLine.equalsIgnoreCase("SENSOR_RECTANGULAR"))
                    return TacticalLines.SENSOR_RECTANGULAR;
                if(strLine.equalsIgnoreCase("SENSOR_CIRCULAR"))
                    return TacticalLines.SENSOR_CIRCULAR;

                if(strLine.equalsIgnoreCase("CENSOR"))
                    return TacticalLines.CENSOR;
                if(strLine.equalsIgnoreCase("CENSOR_RECTANGULAR"))
                    return TacticalLines.CENSOR_RECTANGULAR;
                if(strLine.equalsIgnoreCase("CENSOR_CIRCULAR"))
                    return TacticalLines.CENSOR_CIRCULAR;

                if(strLine.equalsIgnoreCase("DA"))
                    return TacticalLines.DA;
                if(strLine.equalsIgnoreCase("DA_RECTANGULAR"))
                    return TacticalLines.DA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("DA_CIRCULAR"))
                    return TacticalLines.DA_CIRCULAR;


                if(strLine.equalsIgnoreCase("CFZ"))
                    return TacticalLines.CFZ;
                if(strLine.equalsIgnoreCase("CFZ_RECTANGULAR"))
                    return TacticalLines.CFZ_RECTANGULAR;
                if(strLine.equalsIgnoreCase("CFZ_CIRCULAR"))
                    return TacticalLines.CFZ_CIRCULAR;

                if(strLine.equalsIgnoreCase("ZOR"))
                    return TacticalLines.ZOR;
                if(strLine.equalsIgnoreCase("ZOR_RECTANGULAR"))
                    return TacticalLines.ZOR_RECTANGULAR;
                if(strLine.equalsIgnoreCase("ZOR_CIRCULAR"))
                    return TacticalLines.ZOR_CIRCULAR;

                if(strLine.equalsIgnoreCase("TBA"))
                    return TacticalLines.TBA;
                if(strLine.equalsIgnoreCase("TBA_RECTANGULAR"))
                    return TacticalLines.TBA_RECTANGULAR;
                if(strLine.equalsIgnoreCase("TBA_CIRCULAR"))
                    return TacticalLines.TBA_CIRCULAR;

                if(strLine.equalsIgnoreCase("TVAR"))
                    return TacticalLines.TVAR;
                if(strLine.equalsIgnoreCase("TVAR_RECTANGULAR"))
                    return TacticalLines.TVAR_RECTANGULAR;
                if(strLine.equalsIgnoreCase("TVAR_CIRCULAR"))
                    return TacticalLines.TVAR_CIRCULAR;
                
                if(strLine.equalsIgnoreCase("KILLBOXBLUE"))
                    return TacticalLines.KILLBOXBLUE;
                if(strLine.equalsIgnoreCase("KILLBOXBLUE_RECTANGULAR"))
                    return TacticalLines.KILLBOXBLUE_RECTANGULAR;
                if(strLine.equalsIgnoreCase("KILLBOXBLUE_CIRCULAR"))
                    return TacticalLines.KILLBOXBLUE_CIRCULAR;

                if(strLine.equalsIgnoreCase("KILLBOXPURPLE"))
                    return TacticalLines.KILLBOXPURPLE;
                if(strLine.equalsIgnoreCase("KILLBOXPURPLE_RECTANGULAR"))
                    return TacticalLines.KILLBOXPURPLE_RECTANGULAR;
                if(strLine.equalsIgnoreCase("KILLBOXPURPLE_CIRCULAR"))
                    return TacticalLines.KILLBOXPURPLE_CIRCULAR;

                if(strLine.equalsIgnoreCase("RANGE_FAN"))
                    return TacticalLines.RANGE_FAN;
                if(strLine.equalsIgnoreCase("RANGEFAN"))
                    return TacticalLines.RANGE_FAN;
                if(strLine.equalsIgnoreCase("RANGE_FANS"))
                    return TacticalLines.RANGE_FAN;
                if(strLine.equalsIgnoreCase("RANGEFANS"))
                    return TacticalLines.RANGE_FAN;

                if(strLine.equalsIgnoreCase("SECTOR"))
                    return TacticalLines.RANGE_FAN_SECTOR;

                if(strLine.equalsIgnoreCase("CONVOY"))
                    return TacticalLines.CONVOY;
                if(strLine.equalsIgnoreCase("HCONVOY"))
                    return TacticalLines.HCONVOY;

                if(strLine.equalsIgnoreCase("MSR"))
                    return TacticalLines.MSR;
                if(strLine.equalsIgnoreCase("ASR"))
                    return TacticalLines.ASR;
                if(strLine.equalsIgnoreCase("ONEWAY"))
                    return TacticalLines.ONEWAY;
                if(strLine.equalsIgnoreCase("TWOWAY"))
                    return TacticalLines.TWOWAY;
                if(strLine.equalsIgnoreCase("ALT"))
                    return TacticalLines.ALT;

                if(strLine.equalsIgnoreCase("DHA"))
                    return TacticalLines.DHA;
                if(strLine.equalsIgnoreCase("EPW"))
                    return TacticalLines.EPW;
                if(strLine.equalsIgnoreCase("FARP"))
                    return TacticalLines.FARP;
                if(strLine.equalsIgnoreCase("RHA"))
                    return TacticalLines.RHA;
                if(strLine.equalsIgnoreCase("BSA"))
                    return TacticalLines.BSA;
                if(strLine.equalsIgnoreCase("DSA"))
                    return TacticalLines.DSA;
                if(strLine.equalsIgnoreCase("RSA"))
                    return TacticalLines.RSA;

                if(strLine.equalsIgnoreCase("BEARING"))
                    return TacticalLines.BEARING;
                if(strLine.equalsIgnoreCase("ELECTRO"))
                    return TacticalLines.ELECTRO;
                if(strLine.equalsIgnoreCase("ACOUSTIC"))
                    return TacticalLines.ACOUSTIC;
                if(strLine.equalsIgnoreCase("TORPEDO"))
                    return TacticalLines.TORPEDO;
                if(strLine.equalsIgnoreCase("OPTICAL"))
                    return TacticalLines.OPTICAL;

            }
            //These are length >= 15, are for the SEC tester only
            if(strLine.equalsIgnoreCase("FSA_RECTANGULAR"))
                return TacticalLines.FSA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("ACA_RECTANGULAR"))
                return TacticalLines.ACA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("FFA_RECTANGULAR"))
                return TacticalLines.FFA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("NFA_RECTANGULAR"))
                return TacticalLines.NFA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("RFA_RECTANGULAR"))
                return TacticalLines.RFA_RECTANGULAR;

            //if(strLine.equalsIgnoreCase("PAA_RECTANGULAR"))
                //return TacticalLines.PAA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("ATI_RECTANGULAR"))
                return TacticalLines.ATI_RECTANGULAR;

            if(strLine.equalsIgnoreCase("CFFZ_RECTANGULAR"))
                return TacticalLines.CFFZ_RECTANGULAR;

            if(strLine.equalsIgnoreCase("SENSOR_RECTANGULAR"))
                return TacticalLines.SENSOR_RECTANGULAR;

            if(strLine.equalsIgnoreCase("SENSOR_CIRCULAR"))
                return TacticalLines.SENSOR_CIRCULAR;

            if(strLine.equalsIgnoreCase("CENSOR_RECTANGULAR"))
                return TacticalLines.CENSOR_RECTANGULAR;

            if(strLine.equalsIgnoreCase("CENSOR_CIRCULAR"))
                return TacticalLines.CENSOR_CIRCULAR;

            if(strLine.equalsIgnoreCase("DA_RECTANGULAR"))
                return TacticalLines.DA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("CFZ_RECTANGULAR"))
                return TacticalLines.CFZ_RECTANGULAR;

            if(strLine.equalsIgnoreCase("ZOR_RECTANGULAR"))
                return TacticalLines.ZOR_RECTANGULAR;

            if(strLine.equalsIgnoreCase("TBA_RECTANGULAR"))
                return TacticalLines.TBA_RECTANGULAR;

            if(strLine.equalsIgnoreCase("TVAR_RECTANGULAR"))
                return TacticalLines.TVAR_RECTANGULAR;

            if(strLine.equalsIgnoreCase("GENERIC---****X"))
                return TacticalLines.GENERIC;
            //end section
            
            String str1,str2,str3,str4,str5,str6,str7,str10;
            String c0=strLine.substring(0,1);
            String c1=strLine.substring(1,2);
            String c2=strLine.substring(2,3);
            //int bolUseEllipticArc=0;

            str1=strLine.substring(4,5);//was(4,1)
            str2=strLine.substring(4,6);//was(4,2)
            str3=strLine.substring(4,7);//was(4,3)
            str4=strLine.substring(4,8);//was(4,4)
            str5=strLine.substring(4,9);//was(4,5)
            str6=strLine.substring(3,9);//was(3,6)
            str7=strLine.substring(3,10);//was(3,7)
            str10=strLine.substring(3,13);//was(3,10)

            //Basic Shapes
            if(strLine.equalsIgnoreCase("BS_LINE--------"))
                return TacticalLines.BS_LINE;
            if(strLine.equalsIgnoreCase("BS_AREA--------"))
                return TacticalLines.BS_AREA;
            if(strLine.equalsIgnoreCase("BS_CROSS-------"))
                return TacticalLines.BS_CROSS;
            if(strLine.equalsIgnoreCase("BS_ELLIPSE-----"))
                return TacticalLines.BS_ELLIPSE;
            if(strLine.equalsIgnoreCase("PBS_ELLIPSE----"))
                return TacticalLines.PBS_ELLIPSE;
            if(strLine.equalsIgnoreCase("PBS_CIRCLE-----"))
                return TacticalLines.PBS_CIRCLE;
            if(strLine.equalsIgnoreCase("BS_RECTANGLE---"))
                return TacticalLines.BS_RECTANGLE;
            if(strLine.equalsIgnoreCase("PBS_RECTANGLE--"))
                return TacticalLines.PBS_RECTANGLE;
            if(strLine.equalsIgnoreCase("PBS_SQUARE-----"))
                return TacticalLines.PBS_SQUARE;
            //buffered shapes
            if(strLine.equalsIgnoreCase("BBS_LINE-------"))
                return TacticalLines.BBS_LINE;
            if(strLine.equalsIgnoreCase("BBS_AREA-------"))
                return TacticalLines.BBS_AREA;
            if(strLine.equalsIgnoreCase("BBS_POINT------"))
                return TacticalLines.BBS_POINT;
            if(strLine.equalsIgnoreCase("BBS_RECTANGLE--"))
                return TacticalLines.BBS_RECTANGLE;
            if(strLine.equalsIgnoreCase("BS_BBOX--------"))
                return TacticalLines.BS_BBOX;
            
            //METOCs
            if (c0.equals("W") && c1.equals("A"))
            {
                if (str7.equals("DPXSQ--")) {
                    return TacticalLines.SQUALL;
                }
                if (str7.equals("DPFC---")) {
                    return TacticalLines.CF;
                }
                if (str7.equals("DPFC-FG")) {
                    return TacticalLines.CFG;
                }
                if (str7.equals("DPFC-FY")) {
                    return TacticalLines.CFY;
                }
                if (str7.equals("DPFW-FG")) {
                    return TacticalLines.WFG;
                }
                if (str7.equals("DPFW-FY")) {
                    return TacticalLines.WFY;
                }
                if (str7.equals("DPFOU--")) {
                    return TacticalLines.UOF;
                }
                if (str7.equals("DPFO-FY")) {
                    return TacticalLines.OFY;
                }
                if (str7.equals("DPFSU--")) {
                    return TacticalLines.USF;
                }
                if (str7.equals("DPFS-FG")) {
                    return TacticalLines.SFG;
                }
                if (str7.equals("DPFS-FY")) {
                    return TacticalLines.SFY;
                }
                if (str7.equals("DPXIL--")) {
                    return TacticalLines.INSTABILITY;
                }
                if (str7.equals("DPXSH--")) {
                    return TacticalLines.SHEAR;
                }
                if (str7.equals("DPXITCZ")) {
                    return TacticalLines.ITC;
                }
                if (str7.equals("DPXCV--")) {
                    return TacticalLines.CONVERGANCE;
                }
                if (str7.equals("DPXITD-")) {
                    return TacticalLines.ITD;
                }
                if (str7.equals("DWJ----")) {
                    return TacticalLines.JET;
                }
                if (str7.equals("DWS----")) {
                    return TacticalLines.STREAM;
                }
                if (str7.equals("DBAIF--")) {
                    return TacticalLines.IFR;
                }
                if (str7.equals("DBAMV--")) {
                    return TacticalLines.MVFR;
                }
                if (str7.equals("DBATB--")) {
                    return TacticalLines.TURBULENCE;
                }
                if (str7.equals("DBAI---")) {
                    return TacticalLines.ICING;
                }
                if (str7.equals("DBALPNC")) {
                    return TacticalLines.NON_CONVECTIVE;
                }
                if (str7.equals("DBALPC-")) {
                    return TacticalLines.CONVECTIVE;
                }
                if (str7.equals("DBAFP--")) {
                    return TacticalLines.FROZEN;
                }
                if (str7.equals("DBAT---")) {
                    return TacticalLines.THUNDERSTORMS;
                }
                if (str7.equals("DBAFG--")) {
                    return TacticalLines.FOG;
                }
                if (str7.equals("DBAD---")) {
                    return TacticalLines.SAND;
                }
                if (str7.equals("DBAFF--")) {
                    return TacticalLines.FREEFORM;
                }
                if (str7.equals("DIPIB--")) {
                    return TacticalLines.ISOBAR;
                }
                if (str7.equals("DIPCO--")) {
                    return TacticalLines.UPPER_AIR;
                }
                if (str7.equals("DIPIS--")) {
                    return TacticalLines.ISOTHERM;
                }
                if (str7.equals("DIPIT--")) {
                    return TacticalLines.ISOTACH;
                }
                if (str7.equals("DIPID--")) {
                    return TacticalLines.ISODROSOTHERM;
                }
                if (str7.equals("DIPTH--")) {
                    return TacticalLines.ISOPLETHS;
                }
                if (str7.equals("DIPFF--")) {
                    return TacticalLines.OPERATOR_FREEFORM;
                }


                //if (strncmp(str,"PXR",3).equals(0)
                if (str3.equals("PXR")) {
                    return TacticalLines.RIDGE;
                }
                //if (strncmp(str,"PXS",3).equals(0)
                //if(str3.equals("PXS")
                //	return TacticalLines.SQUALL;
                //if (strncmp(str,"PXT",3).equals(0)
                if (str3.equals("PXT")) {
                    return TacticalLines.TROUGH;
                }
                //if (strncmp(str,"PFCU",4).equals(0)
                if (str4.equals("PFCU")) {
                    return TacticalLines.UCF;
                }
                //if (strncmp(str,"PFO",3).equals(0)
                if (str3.equals("PFO")) {
                    return TacticalLines.OCCLUDED;
                }
                //if (strncmp(str,"PFS",3).equals(0)
                if (str3.equals("PFS")) {
                    return TacticalLines.SF;
                }
                //if (strncmp(str,"PFWU",4).equals(0)
                if (str4.equals("PFWU")) {
                    return TacticalLines.UWF;
                }
                //if (strncmp(str,"PFW",3).equals(0)
                if (str3.equals("PFW")) {
                    return TacticalLines.WF;
                }
                //if (strncmp(str,"PFC",3).equals(0)
                if (str3.equals("PFC")) {
                    return TacticalLines.CF;
                }
            }
            if (c0.equals("W") && c1.equals("O"))
            {
                if (str10.equals("DHCF----L-")) {
                    return TacticalLines.FORESHORE_LINE;
                }
                if (str10.equals("DHCF-----A")) {
                    return TacticalLines.FORESHORE_AREA;
                }
                if (str10.equals("DHPBA---L-")) {
                    return TacticalLines.ANCHORAGE_LINE;
                }
                if (str10.equals("DHPBA----A")) {
                    return TacticalLines.ANCHORAGE_AREA;
                }
                if (str10.equals("DHPMO---L-")) {
                    return TacticalLines.LOADING_FACILITY_LINE;
                }
                if (str10.equals("DHPMO----A")) {
                    return TacticalLines.LOADING_FACILITY_AREA;
                }
            }
            if (c0.equals("W") && c1.equals("O"))
            {
                if (str7.equals("DIDID--")) {
                    return TacticalLines.ICE_DRIFT;
                }
                if (str7.equals("DILOV--")) {
                    return TacticalLines.LVO;
                }
                if (str7.equals("DILUC--")) {
                    return TacticalLines.UNDERCAST;
                }
                if (str7.equals("DILOR--")) {
                    return TacticalLines.LRO;
                }
                if (str7.equals("DILIEO-")) {
                    return TacticalLines.ICE_EDGE;
                }
                if (str7.equals("DILIEE-")) {
                    return TacticalLines.ESTIMATED_ICE_EDGE;
                }
                if (str7.equals("DILIER-")) {
                    return TacticalLines.ICE_EDGE_RADAR;
                }
                if (str7.equals("DIOC---")) {
                    return TacticalLines.CRACKS;
                }
                if (str7.equals("DIOCS--")) {
                    return TacticalLines.CRACKS_SPECIFIC_LOCATION;
                }
                if (str7.equals("DIOL---")) {
                    return TacticalLines.ICE_OPENINGS_LEAD;
                }
                if (str7.equals("DIOLF--")) {
                    return TacticalLines.ICE_OPENINGS_FROZEN;
                }
                if (str7.equals("DHDDL--")) {
                    return TacticalLines.DEPTH_CURVE;
                }
                if (str7.equals("DHDDC--")) {
                    return TacticalLines.DEPTH_CONTOUR;
                }
                if (str7.equals("DHDDA--")) {
                    return TacticalLines.DEPTH_AREA;
                }
                if (str7.equals("DHCC---")) {
                    return TacticalLines.COASTLINE;
                }
                if (str7.equals("DHCI---")) {
                    return TacticalLines.ISLAND;
                }
                if (str7.equals("DHCB---")) {
                    return TacticalLines.BEACH;
                }
                if (str7.equals("DHCW---")) {
                    return TacticalLines.WATER;
                }
                if (str7.equals("DHPBP--")) {
                    return TacticalLines.PIER;
                }
                if (str7.equals("-HPFF--")) {
                    return TacticalLines.WEIRS;
                }
                if (str7.equals("-HHDR--")) {
                    return TacticalLines.REEF;
                }
                if (str7.equals("DHPMD--")) {
                    return TacticalLines.DRYDOCK;
                }
                if (str7.equals("DHPMRA-")) {
                    return TacticalLines.RAMP_ABOVE_WATER;
                }
                if (str7.equals("DHPMRB-")) {
                    return TacticalLines.RAMP_BELOW_WATER;
                }
                if (str7.equals("DHPSPA-")) {
                    return TacticalLines.JETTY_ABOVE_WATER;
                }
                if (str7.equals("DHPSPB-")) {
                    return TacticalLines.JETTY_BELOW_WATER;
                }
                if (str7.equals("DHPSPS-")) {
                    return TacticalLines.SEAWALL;
                }
                if (str7.equals("DHABP--")) {
                    return TacticalLines.PERCHES;
                }
                if (str7.equals("DHALLA-")) {
                    return TacticalLines.LEADING_LINE;
                }
                if (str7.equals("DHHD---")) {
                    return TacticalLines.UNDERWATER_HAZARD;
                }
                if (str7.equals("DHHDF--")) {
                    return TacticalLines.FOUL_GROUND;
                }
                if (str7.equals("DHHDK--")) {
                    return TacticalLines.KELP;
                }
                if (str7.equals("DHHDB--")) {
                    return TacticalLines.BREAKERS;
                }
                if (str7.equals("DHHDD--")) {
                    return TacticalLines.DISCOLORED_WATER;
                }
                if (str7.equals("DTCCCFE")) {
                    return TacticalLines.EBB_TIDE;
                }
                if (str7.equals("DTCCCFF")) {
                    return TacticalLines.FLOOD_TIDE;
                }
                if (str7.equals("DL-RA--")) {
                    return TacticalLines.RESTRICTED_AREA;
                }
                if (str7.equals("DMPA---")) {
                    return TacticalLines.PIPE;
                }
                if (str7.equals("DL-TA--")) {
                    return TacticalLines.TRAINING_AREA;
                }
                if (str7.equals("DOBVA--")) {
                    return TacticalLines.VDR_LEVEL_12;
                }
                if (str7.equals("DOBVB--")) {
                    return TacticalLines.VDR_LEVEL_23;
                }
                if (str7.equals("DOBVC--")) {
                    return TacticalLines.VDR_LEVEL_34;
                }
                if (str7.equals("DOBVD--")) {
                    return TacticalLines.VDR_LEVEL_45;
                }
                if (str7.equals("DOBVE--")) {
                    return TacticalLines.VDR_LEVEL_56;
                }
                if (str7.equals("DOBVF--")) {
                    return TacticalLines.VDR_LEVEL_67;
                }
                if (str7.equals("DOBVG--")) {
                    return TacticalLines.VDR_LEVEL_78;
                }
                if (str7.equals("DOBVH--")) {
                    return TacticalLines.VDR_LEVEL_89;
                }
                if (str7.equals("DOBVI--")) {
                    return TacticalLines.VDR_LEVEL_910;
                }
                if (str7.equals("DBSF---")) {
                    return TacticalLines.BEACH_SLOPE_FLAT;
                }
                if (str7.equals("DBSG---")) {
                    return TacticalLines.BEACH_SLOPE_GENTLE;
                }
                if (str7.equals("DBSM---")) {
                    return TacticalLines.BEACH_SLOPE_MODERATE;
                }
                if (str7.equals("DBST---")) {
                    return TacticalLines.BEACH_SLOPE_STEEP;
                }
                if (str7.equals("DGMSR--")) {
                    return TacticalLines.SOLID_ROCK;
                }
                if (str7.equals("DGMSC--")) {
                    return TacticalLines.CLAY;
                }
                if (str7.equals("DGMSSVS")) {
                    return TacticalLines.VERY_COARSE_SAND;
                }
                if (str7.equals("DGMSSC-")) {
                    return TacticalLines.COARSE_SAND;
                }
                if (str7.equals("DGMSSM-")) {
                    return TacticalLines.MEDIUM_SAND;
                }
                if (str7.equals("DGMSSF-")) {
                    return TacticalLines.FINE_SAND;
                }
                if (str7.equals("DGMSSVF")) {
                    return TacticalLines.VERY_FINE_SAND;
                }
                if (str7.equals("DGMSIVF")) {
                    return TacticalLines.VERY_FINE_SILT;
                }
                if (str7.equals("DGMSIF-")) {
                    return TacticalLines.FINE_SILT;
                }
                if (str7.equals("DGMSIM-")) {
                    return TacticalLines.MEDIUM_SILT;
                }
                if (str7.equals("DGMSIC-")) {
                    return TacticalLines.COARSE_SILT;
                }
                if (str7.equals("DGMSB--")) {
                    return TacticalLines.BOULDERS;
                }
                if (str7.equals("DGMS-CO")) {
                    return TacticalLines.OYSTER_SHELLS;
                }
                if (str7.equals("DGMS-PH")) {
                    return TacticalLines.PEBBLES;
                }
                if (str7.equals("DGMS-SH")) {
                    return TacticalLines.SAND_AND_SHELLS;
                }
                if (str7.equals("DGML---")) {
                    return TacticalLines.BOTTOM_SEDIMENTS_LAND;
                }
                if (str7.equals("DGMN---")) {
                    return TacticalLines.BOTTOM_SEDIMENTS_NO_DATA;
                }
                if (str7.equals("DGMRS--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_SMOOTH;
                }
                if (str7.equals("DGMRM--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_MODERATE;
                }
                if (str7.equals("DGMRR--")) {
                    return TacticalLines.BOTTOM_ROUGHNESS_ROUGH;
                }
                if (str7.equals("DGMCL--")) {
                    return TacticalLines.CLUTTER_LOW;
                }
                if (str7.equals("DGMCM--")) {
                    return TacticalLines.CLUTTER_MEDIUM;
                }
                if (str7.equals("DGMCH--")) {
                    return TacticalLines.CLUTTER_HIGH;
                }
                if (str7.equals("DGMIBA-")) {
                    return TacticalLines.IMPACT_BURIAL_0;
                }
                if (str7.equals("DGMIBB-")) {
                    return TacticalLines.IMPACT_BURIAL_10;
                }
                if (str7.equals("DGMIBC-")) {
                    return TacticalLines.IMPACT_BURIAL_20;
                }
                if (str7.equals("DGMIBD-")) {
                    return TacticalLines.IMPACT_BURIAL_75;
                }
                if (str7.equals("DGMIBE-")) {
                    return TacticalLines.IMPACT_BURIAL_100;
                }
                if (str7.equals("DGMBCA-")) {
                    return TacticalLines.BOTTOM_CATEGORY_A;
                }
                if (str7.equals("DGMBCB-")) {
                    return TacticalLines.BOTTOM_CATEGORY_B;
                }
                if (str7.equals("DGMBCC-")) {
                    return TacticalLines.BOTTOM_CATEGORY_C;
                }
                if (str7.equals("DGMBTA-")) {
                    return TacticalLines.BOTTOM_TYPE_A1;
                }
                if (str7.equals("DGMBTB-")) {
                    return TacticalLines.BOTTOM_TYPE_A2;
                }
                if (str7.equals("DGMBTC-")) {
                    return TacticalLines.BOTTOM_TYPE_A3;
                }
                if (str7.equals("DGMBTD-")) {
                    return TacticalLines.BOTTOM_TYPE_B1;
                }
                if (str7.equals("DGMBTE-")) {
                    return TacticalLines.BOTTOM_TYPE_B2;
                }
                if (str7.equals("DGMBTF-")) {
                    return TacticalLines.BOTTOM_TYPE_B3;
                }
                if (str7.equals("DGMBTG-")) {
                    return TacticalLines.BOTTOM_TYPE_C1;
                }
                if (str7.equals("DGMBTH-")) {
                    return TacticalLines.BOTTOM_TYPE_C2;
                }
                if (str7.equals("DGMBTI-")) {
                    return TacticalLines.BOTTOM_TYPE_C3;
                }
                if (str7.equals("DL-SA--")) {
                    return TacticalLines.SWEPT_AREA;
                }
                if (str7.equals("DMOA---")) {
                    return TacticalLines.OIL_RIG_FIELD;
                }
                if (str7.equals("DMCC---")) {
                    return TacticalLines.SUBMERGED_CRIB;
                }
                if (str7.equals("DMCA---")) {
                    return TacticalLines.CABLE;
                }
                if (str7.equals("DL-ML--")) {
                    return TacticalLines.MARITIME_LIMIT;
                }
                if (str7.equals("DL-MA--")) {
                    return TacticalLines.MARITIME_AREA;
                }
                if (str7.equals("DMCD---")) {
                    return TacticalLines.CANAL;
                }
                if (str7.equals("DL-O---")) {
                    return TacticalLines.OPERATOR_DEFINED;
                }
            }
            //end METOC section

            //SPT
            if(str5.equals("OLAGS") && c0.equals("G") && c2.equals("G")){
                return TacticalLines.SPT;
            }
            //MAIN
            if(str5.equals("OLAGM") && c0.equals("G") && c2.equals("G")){
                return TacticalLines.MAIN;
            }
            //DIRATKGND
            if (str5.equals("OLKGM") && c0.equals("G") && c2.equals("G")){
                return TacticalLines.DIRATKGND;
            }
            //DIRATKSPT
            if (str5.equals("OLKGS") && c0.equals("G") && c2.equals("G")){
                return TacticalLines.DIRATKSPT;
            }
            //AIRAOA
            if (str4.equals("OLAA") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AIRAOA;
            }
            //AAAAA
            if (str4.equals("OLAR") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AAAAA;
            }
            //DIRATKAIR
            if (str4.equals("OLKA") && c0.equals("G") && c2.equals("G")) {
                return  TacticalLines.DIRATKAIR;
            }
            //AXAD
            if (str4.equals("OLAV") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AXAD;
            }
            //ATDITCH
            if (str4.equals("OADU") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ATDITCH;
            }
            //ATDITCHC
            if (str4.equals("OADC") && c0.equals("G") && c2.equals("M")) {
                return  TacticalLines.ATDITCHC;
            }
            //LOMEZ
            if (str4.equals("AAML") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LOMEZ;
            }
            //HIMEZ
            if (str4.equals("AAMH") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.HIMEZ;
            }
            //PNO
            if (str4.equals("DABP") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.PNO;
            }
            //TRIPLE
            if (str4.equals("OWCT") && c0.equals("G") && c2.equals("M")){
                    return TacticalLines.TRIPLE;
            }
            //DOUBLEC
            if (str4.equals("OWCD") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DOUBLEC;
            }
            //SINGLEC
            if (str4.equals("OWCS") && c0.equals("G") && c2.equals("M")) {
                    return TacticalLines.SINGLEC;
            }
            //PAA
            if (str4.equals("ACPR") && c0.equals("G") && c2.equals("F")) //change 1
            {   //toggle two lines for Mil-Std-2525 Rev B, Rev C
                //return TacticalLines.PAA_RECTANGULAR;
                return TacticalLines.PAA_RECTANGULAR_REVC;
            }
            //FSA
            if (str4.equals("ACSI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FSA;
            }
            //FSA_RECTANGULAR
            if (str4.equals("ACSR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FSA_RECTANGULAR;
            }
            //FSA_CIRCULAR
            if (str4.equals("ACSC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FSA_CIRCULAR;
            }
            //ACA
            if (str4.equals("ACAI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ACA;
            }
            //ACA_RECTANGULAR
            if (str4.equals("ACAR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ACA_RECTANGULAR;
            }
            //ACA_CIRCULAR
            if (str4.equals("ACAC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ACA_CIRCULAR;
            }
            //FFA
            if (str4.equals("ACFI") && c0.equals("G") && c2.equals( "F")) //change 1
            {
                return TacticalLines.FFA;
            }
            //FFA_RECTANGULAR
            if (str4.equals("ACFR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FFA_RECTANGULAR;
            }
            //FFA_CIRCULAR
            if (str4.equals("ACFC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FFA_CIRCULAR;
            }
            //NFA
            if (str4.equals("ACNI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.NFA;
            }
            //NFA_RECTANGULAR
            if (str4.equals("ACNR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.NFA_RECTANGULAR;
            }
            //NFA_CIRCULAR
            if (str4.equals("ACNC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.NFA_CIRCULAR;
            }
            //RFA
            if (str4.equals("ACRI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RFA;
            }
            //RFA_RECTANGULAR
            if (str4.equals("ACRR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RFA_RECTANGULAR;
            }
            //RFA_CIRCULAR
            if (str4.equals("ACRC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RFA_CIRCULAR;
            }
            //RFA_CIRCULAR
            if (str4.equals("ACPC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.PAA_CIRCULAR;
            }
            //ATI
            if (str4.equals("AZII") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ATI;
            }
            //ATI_RECTANGULAR
            if (str4.equals("AZIR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ATI_RECTANGULAR;
            }
            //ATI_CIRCULAR
            if (str4.equals("AZIC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ATI_CIRCULAR;
            }
            //CFFZ
            if (str4.equals("AZXI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFFZ;
            }
            //CFFZ_RECTANGULAR
            if (str4.equals("AZXR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFFZ_RECTANGULAR;
            }
            //CFFZ_CIRCULAR
            if (str4.equals("AZXC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFFZ_CIRCULAR;
            }
            //SENSOR
            if (str4.equals("AZSI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR;
            }
            if (str4.equals("ACEI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR;
            }
            //SENSOR_RECTANGULAR
            if (str4.equals("AZSR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR_RECTANGULAR;
            }
            if (str4.equals("ACER") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR_RECTANGULAR;
            }
            //SENSOR_CIRCULAR
            if (str4.equals("AZSC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR_CIRCULAR;
            }
            if (str4.equals("ACEC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.SENSOR_CIRCULAR;
            }
            //CENSOR
            if (str4.equals("AZCI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CENSOR;
            }
            //CENSOR_RECTANGULAR
            if (str4.equals("AZCR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CENSOR_RECTANGULAR;
            }
            //CENSOR_CIRCULAR
            if (str4.equals("AZCC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CENSOR_CIRCULAR;
            }
            //DA
            if (str4.equals("AZDI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.DA;
            }
            if (str4.equals("ACDI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.DA;
            }
            //DA_RECTANGULAR
            if (str4.equals("AZDR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.DA_RECTANGULAR;
            }
            if (str4.equals("ACDR") && c0.equals("G") && c2.equals("F")) //rev C
            {
                return TacticalLines.DA_RECTANGULAR;
            }
            //DA_CIRCULAR
            if (str4.equals("AZDC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.DA_CIRCULAR;
            }
            if (str4.equals("ACDC") && c0.equals("G") && c2.equals("F")) //rev C
            {
                return TacticalLines.DA_CIRCULAR;
            }
            //CFZ
            if (str4.equals("ACFZ") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFZ;
            }
            if (str4.equals("AZFI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFZ;
            }
            //CFZ_RECTANGULAR
            if (str4.equals("AZFR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFZ_RECTANGULAR;
            }
            //CFZ_CIRCULAR
            if (str4.equals("AZFC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFZ_CIRCULAR;
            }
            //ZOR
            if (str4.equals("AZZI") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ZOR;
            }
            if (str4.equals("AZOR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.ZOR;
            }
            if (str4.equals("ACZI") && c0.equals("G") && c2.equals("F")) //rev C
            {
                return TacticalLines.ZOR;
            }
            //ZOR_RECTANGULAR
            if (str4.equals("AZZR") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.ZOR_RECTANGULAR;
            }
            if (str4.equals("ACZR") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.ZOR_RECTANGULAR;
            }
            //ZOR_CIRCULAR
            if (str4.equals("AZZC") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.ZOR_CIRCULAR;
            }
            if (str4.equals("ACZC") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.ZOR_CIRCULAR;
            }
            //TBA
            if (str4.equals("AZBI") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.TBA;
            }
            if (str4.equals("ATBA") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.TBA;
            }
            if (str4.equals("ACBI") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.TBA;
            }
            //TBA_RECTANGULAR
            if (str4.equals("AZBR") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.TBA_RECTANGULAR;
            }
            if (str4.equals("ACBR") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.TBA_RECTANGULAR;
            }
            //TBA_CIRCULAR
            if (str4.equals("AZBC") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.TBA_CIRCULAR;
            }
            if (str4.equals("ACBC") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.TBA_CIRCULAR;
            }
            //TVAR
            if (str4.equals("AZVI") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.TVAR;
            }
            if (str4.equals("ACVI") && c0.equals("G") && c2.equals("F"))//rev C
            {
                return TacticalLines.TVAR;
            }
            //TVAR_RECTANGULAR
            if (str4.equals("AZVR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.TVAR_RECTANGULAR;
            }
            if (str4.equals("ACVR") && c0.equals("G") && c2.equals("F")) //rev C
            {
                return TacticalLines.TVAR_RECTANGULAR;
            }
            //TVAR_CIRCULAR
            if (str4.equals("AZVC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.TVAR_CIRCULAR;
            }
            if (str4.equals("ACVC") && c0.equals("G") && c2.equals("F")) //rev C
            {
                return TacticalLines.TVAR_CIRCULAR;
            }
            
            //KILLBOXBLUE
            if (str4.equals("AKBI") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.KILLBOXBLUE;
            }
            //KILLBOXBLUE_RECTANGULAR
            if (str4.equals("AKBR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.KILLBOXBLUE_RECTANGULAR;
            }
            //KILLBOXBLUE_CIRCULAR
            if (str4.equals("AKBC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.KILLBOXBLUE_CIRCULAR;
            }
            //KILLBOXPURPLE
            if (str4.equals("AKPI") && c0.equals("G") && c2.equals("F"))//change 1
            {
                return TacticalLines.KILLBOXPURPLE;
            }
            //KILLBOXPURPLE_RECTANGULAR
            if (str4.equals("AKPR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.KILLBOXPURPLE_RECTANGULAR;
            }
            //KILLBOXPURPLE_CIRCULAR
            if (str4.equals("AKPC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.KILLBOXPURPLE_CIRCULAR;
            }
            
            
            
            
            //RFL
            if (str3.equals("LCR") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RFL;
            }
            //NFL
            if (str3.equals("LCN") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.NFL;
            }
            //MFP
            if (str3.equals("LCM") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.MFP;
            }
            //CFL
            if (str3.equals("LCC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.CFL;
            }
            //FSCL
            if (str3.equals("LCF") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FSCL;
            }
            //LINTGTS
            if (str3.equals("LTS") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.LINTGTS;
            }
            //RANGE_FAN
            if (str3.equals("AXC") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RANGE_FAN;
            }
            //RANGE_FAN_SECTOR
            if (str3.equals("AXS") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.RANGE_FAN_SECTOR;
            }
            //FPF
            if (str3.equals("LTF") && c0.equals("G") && c2.equals("F")) //change 1
            {
                return TacticalLines.FPF;
            }
            //SFENCE
            if (str3.equals("OWS") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.SFENCE;
            }
            //DFENCE
            if (str3.equals("OWD") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DFENCE;
            }
            //DOUBLEA
            if (str3.equals("OWA") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DOUBLEA;
            }
            //UNSP
            if (str3.equals("OWU") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.UNSP;
            }
            //LWFENCE
            if (str3.equals("OWL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.LWFENCE;
            }
            //HWFENCE
            if (str3.equals("OWH") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.HWFENCE;
            }
            //ATDITCHM
            if (str3.equals("OAR") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ATDITCHM;
            }
            //BELT
            if (str3.equals("OGB") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.BELT; //use BELT for area, BELT1 for line
                //return TacticalLines.BELT1;
            }
            //ATWALL
            if (str3.equals("OAW") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ATWALL;
            }
            //AMBUSH
            if (str3.equals("SLA") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AMBUSH;
            }
            //ROZ
            if (str3.equals("AAR") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.ROZ;
            }
            //IL
            if (str3.equals("OLI") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.IL;
            }
            //PLANNED
            if (str3.equals("ORP") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.PLANNED;
            }
            //ESR1
            if (str3.equals("ORS") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ESR1;
            }
            //ESR2
            if (str3.equals("ORA") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ESR2;
            }
            //ROADBLK
            if (str3.equals("ORC") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ROADBLK;
            }
            //PLD
            if (str3.equals( "OLP") && c0.equals( "G") && c2.equals( "G")) {
                return TacticalLines.PLD;
            }
            //BYDIF
            if (str3.equals( "BDD") && c0.equals( "G") && c2.equals( "M")) {
                return TacticalLines.BYDIF;
            }
            //BYIMP
            if (str3.equals( "BDI") && c0.equals( "G") && c2.equals( "M")) {
                return TacticalLines.BYIMP;
            }
            //EASY
            if (str3.equals( "BDE") && c0.equals( "G") && c2.equals( "M")) {
                return TacticalLines.EASY;
            }
            //FLOT
            if (str3.equals("GLF") && c0.equals("G") && c2.equals("G")) {
                    return TacticalLines.FLOT;
            }
            //LC
            if (str3.equals("GLC") && c0.equals("G") && c2.equals("G"))  {
                    return TacticalLines.LC;
            }
            //LDLC
            if (str3.equals("OLC") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.LDLC;
            }
            //ZONE
            if (str3.equals("OGZ") && c0.equals("G") && c2.equals("M"))  {
                return TacticalLines.ZONE;
            }
            //ENCIRCLE
            if (str3.equals("SAE") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.ENCIRCLE;
            }
            //areas
            //BATTLE
            if (str3.equals("DAB") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.BATTLE;
            }
            //ASSY
            if (str3.equals("GAA") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.ASSY;
            }
            //TAI
            if (str3.equals("SAT") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.TAI;
            }
            //BSA
            if (str3.equals("ASB") && c0.equals("G") && c2.equals("S"))  {
                return TacticalLines.BSA;
            }
            //DSA
            if (str3.equals("ASD") && c0.equals("G") && c2.equals("S"))  {
                return TacticalLines.DSA;
            }
            //EA
            if (str3.equals("GAE") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.EA;
            }
            //NAI
            if (str3.equals("SAN") && c0.equals("G") && c2.equals("G"))  {
                return TacticalLines.NAI;
            }

            //FORT
            if (str3.equals("GAF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.FORT;
            }
            //OBSAREA
            if (str3.equals("OGR") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.OBSAREA;
            }
            //OBSFAREA
            if (str3.equals("OGF") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.OBSFAREA;
            }
            //PZ
            if (str3.equals("GAP") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.PZ;
            }
            //LZ
            if (str3.equals("GAL") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LZ;
            }
            //DZ
            if (str3.equals("GAD") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DZ;
            }
            //FAADZ
            if (str3.equals("AAF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.FAADZ;
            }
            //MEZ
            if (str3.equals("AAM") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.MEZ;
            }
            //WFZ
            if (str3.equals("AAW") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.WFZ;
            }
            //FEBA
            if (str3.equals("DLF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.FEBA;
            }
            //SAAFR
            if (str3.equals("ALS") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.SAAFR;
            }
            //LLFR
            if (str3.equals("ALL") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LLTR;
            }
            //AC
            if (str3.equals("ALC") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AC;
            }
            //MRR
            if (str3.equals("ALM") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.MRR;
            }
            //UAV
            if (str3.equals("ALU") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.UAV;
            }
            //FERRY
            if (str3.equals("BCF") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FERRY;
            }
            //BRIDGE
            if (str3.equals("BCB") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.BRIDGE;
            }
            //ASLTXING
            if (str3.equals("BCA") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ASLTXING;
            }
            //GAP
            if (str3.equals("OFG") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.GAP;
            }
            //OVERHEAD_WIRE
            if (str3.equals("OHO") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.OVERHEAD_WIRE;
            }
            //FORDSITE
            if (str3.equals("BCE") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FORDSITE;
            }
            //MFLANE
            if (str3.equals("BCL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MFLANE;
            }
            //RAFT
            if (str3.equals("BCR") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.RAFT;
            }
            //MNFLDDIS
            if (str3.equals("OED") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MNFLDDIS;
            }
            //MSR
            if (str3.equals("LRM") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.MSR;
            }
            //ASR
            if (str3.equals("LRA") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.ASR;
            }
            //LL
            if (str3.equals("GLL") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LL;
            }
            //LOD
            if (str3.equals("OLT") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LOD;
            }
            //PL
            if (str3.equals("GLP") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.PL;
            }
            //FCL
            if (str3.equals("OLF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.FCL;
            }
            //LOA
            if (str3.equals("OLL") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LOA;
            }
            //BRDGHD
            if (str3.equals("SLB") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.BRDGHD;
            }
            //MNFLDFIX
            if (str3.equals("OEF") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MNFLDFIX;
            }
            //MNFLDBLK
            if (str3.equals("OEB") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MNFLDBLK;
            }
            //ONEWAY
            if (str3.equals("LRO") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.ONEWAY;
            }
            //ALT
            if (str3.equals("LRT") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.ALT;
            }
            //TWOWAY
            if (str3.equals("LRW") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.TWOWAY;
            }
            //RSA
            if (str3.equals("ASR") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.RSA;
            }
            //BOUNDARY
            if (str3.equals("GLB") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.BOUNDARY;
            }
            //GENERAL
            if (str3.equals("GAG") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.GENERAL;
            }
            //EZ
            if (str3.equals("GAX") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.EZ;
            }
            //AIRFIELD
            if (str3.equals("GAZ") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AIRFIELD;
            }
            //HIDACZ
            if (str3.equals("AAH") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.HIDACZ;
            }
            //EA1
            if (str3.equals("DAE") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.EA1;
            }
            //ASSAULT
            if (str3.equals("OAA") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.ASSAULT;
            }
            //ATKPOS
            if (str3.equals("OAK") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.ATKPOS;
            }
            //OBJ
            if (str3.equals("OAO") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.OBJ;
            }
            //AO
            if (str3.equals("SAO") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AO;
            }
            //AIRHEAD
            if (str3.equals("SAA") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AIRHEAD;
            }

            //PEN
            if (str3.equals("OAP") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.PEN;
            }
            //HOLD
            if (str3.equals("SLH") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.HOLD;
            }
            //RELEASE
            if (str3.equals("SLR") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.RELEASE;
            }
            //DEPICT
            if (str3.equals("OFD") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DEPICT;
            }
            //MINED
            if (str3.equals("OFA") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MINED;
            }
            //SARA
            if (str3.equals("GAS") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.SARA;
            }
            //LAA
            if (str3.equals("GAY") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.LAA;
            }
            //PDF
            if (str3.equals("DLP") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.PDF;
            }

            //ATKBYFIRE
            if (str3.equals("OAF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.ATKBYFIRE;
            }
            //SPTBYFIRE
            if (str3.equals("OAS") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.SPTBYFIRE;
            }
            //CLUSTER
            if (str3.equals("OMC") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.CLUSTER;
            }
            //TURN
            if (str3.equals("OET") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.TURN;
            }
            //CONVOY
            if (str3.equals("LCM") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.CONVOY;
            }
            //HCONVOY
            if (str3.equals("LCH") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.HCONVOY;
            }
            //LINE
            if (str3.equals("OGL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.LINE;
            }
            //FORDIF
            if (str3.equals("BCD") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FORDIF;
            }
            //TRP
            if (str3.equals("DPT") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.TRP;
            }

            //RECTANGULAR
            if (str3.equals("ATR") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.RECTANGULAR;
            }
            //CIRCULAR
            if (str3.equals("ATC") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.CIRCULAR;
            }
            //SERIES
            if (str3.equals("ATG") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.SERIES;
            }
            //SMOKE
            if (str3.equals("ATS") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.SMOKE;   // Mitch 7-9-07
            }				//BOMB
            if (str3.equals("ATB") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.BOMB;
            }
            //TGMF
            if (str3.equals("ACT") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.TGMF;
            }

            //TRIP
            if (str2.equals("OT") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.TRIP;
            }
            //ABATIS
            if (str2.equals("OS") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.ABATIS;
            }
            //UXO
            if (str2.equals("OU") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.UXO;
            }
            //DHA
            if (str2.equals("AD") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.DHA;
            }
            //EPW
            if (str2.equals("AE") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.EPW;
            }
            //RHA
            if (str2.equals("AH") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.RHA;
            }

            if (str2.equals("NL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DRCL;
            }
            //RAD
            if (str2.equals("NR") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.RAD;
            }
            //CHEM
            if (str2.equals("NC") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.CHEM;
            }
            //BIO
            if (str2.equals("NB") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.BIO;
            }

            if (str2.equals("AF") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.FFA;
            }
            //FSA
            if (str2.equals("AA") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.FSA;
            }
            //RFA
            if (str2.equals("AR") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.RFA;
            }
            //FOLSP
            if (str2.equals("AS") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.FOLSP;
            }
            //FARP
            if (str2.equals("AR") && c0.equals("G") && c2.equals("S")) {
                return TacticalLines.FARP;
            }
            //AAFNT
            if (str2.equals("PA") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.AAFNT;
            }
            //DIRATKFNT
            if (str2.equals("PF") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DIRATKFNT;
            }
            //STRONG
            if (str2.equals("SP") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.STRONG;
            }
            //LINTGT
            if (str2.equals("LT") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.LINTGT;
            }
            //FORTL
            if (str2.equals("SL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FORTL;
            }
            //FPF
            if (str2.equals("LP") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.FPF;
            }
            //ACA
            if (str2.equals("AC") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.ACA;
            }
            //AT
            if (str2.equals("AT") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.AT;
            }
            //SMOKE
            if (str2.equals("AK") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.SMOKE;
            }
            //SERIES
            if (str2.equals("AS") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.SERIES;
            }
            //BOMB
            if (str2.equals("AB") && c0.equals("G") && c2.equals("F")) {
                return TacticalLines.BOMB;
            }
            //ELECTRO
            if (str2.equals("BE") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.ELECTRO;
            }
            //ACOUSTIC
            if (str2.equals("BA") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.ACOUSTIC;
            }
            //TORPEDO
            if (str2.equals("BT") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.TORPEDO;
            }
            //OPTICAL
            if (str2.equals("BO") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.OPTICAL;
            }
            //DECOY
            if (str2.equals("PC") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DUMMY;
            }

            //DUMMY_STATIC
            if (str2.equals("PN") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DUMMY_STATIC;
            }
            //COVER
            if (str2.equals("UC") && c0.equals("G") && c2.equals("T")) {
                if(rev==RendererSettings.Symbology_2525C)
                    return TacticalLines.COVER_REVC;
                else
                    return TacticalLines.COVER;
            }
            //SCREEN
            if (str2.equals("US") && c0.equals("G") && c2.equals("T")) {
                if(rev==RendererSettings.Symbology_2525C)
                    return TacticalLines.SCREEN_REVC;
                else
                    return TacticalLines.SCREEN;
            }
            //GUARD
            if (str2.equals("UG") && c0.equals("G") && c2.equals("T")) {
                if(rev==RendererSettings.Symbology_2525C)
                    return TacticalLines.GUARD_REVC;
                else
                    return TacticalLines.GUARD;
            }
            //DECOY MINED AREA
            if (str2.equals("PM") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DMA;
            }
            //DECOY MINED AREA FENCED
            if (str2.equals("PY") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DMAF;
            }
            //DECEIVE
            if (str2.equals("PD") && c0.equals("G") && c2.equals("G")) {
                return TacticalLines.DECEIVE;
            }
            //FOXHOLE
            if (str2.equals("SW") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FOXHOLE;
            }
            //CATKBYFIRE
            if (str2.equals("KF") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CATKBYFIRE;
            }
            //DRCL
            if (str2.equals("NL") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.DRCL;
            }
            //MSDZ
            if (str2.equals("NM") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.MSDZ;
            }
            //WDRAWUP
            if (str2.equals("WP") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.WDRAWUP;
            }
            //NAVIGATION
            if (str2.equals("HN") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.NAVIGATION;
            }
            //FORTP
            if (str2.equals("SF") && c0.equals("G") && c2.equals("M")) {
                return TacticalLines.FORTP;
            }

            //RIP
            if (str1.equals("R") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.RIP;
            }
            //FOLLA
            if (str1.equals("A") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.FOLLA;
            }
            //RETIRE
            if (str1.equals("M") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.RETIRE;
            }
            //BEARING
            if (str1.equals("B") && c0.equals("G") && c2.equals("O")) {
                return TacticalLines.BEARING;
            }
            //DELAY
            if (str1.equals("L") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.DELAY;
            }

            //CODON AND SEARCH
            if (str1.equals("V") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CORDONSEARCH;
            }
            //CODON AND KNOCK
            if (str1.equals("2") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CORDONKNOCK;
            }

            //WITHDRAW
            if (str1.equals("W") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.WITHDRAW;
            }
            //BREACH
            if (str1.equals("H") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.BREACH;
            }
            //CANALIZE
            if (str1.equals("C") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CANALIZE;
            }
            //BYPASS
            if (str1.equals("Y") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.BYPASS;
            }
            //BLOCK
            if (str1.equals("B") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.BLOCK;
            }
            //DISRUPT
            if (str1.equals("T") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.DISRUPT;
            }
            //SEIZE
            if (str1.equals("Z") && c0.equals("G") && c2.equals("T")) {
                //return TacticalLines.SEIZE;
                if(rev==RendererSettings.Symbology_2525C)
                    return TacticalLines.SEIZE_REVC;
                else
                    return TacticalLines.SEIZE;
            }
            //SECURE
            if (str1.equals("S") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.SECURE;
            }
            //RETAIN
            if (str1.equals("Q") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.RETAIN;
            }
            //PENETRATE
            if (str1.equals("P") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.PENETRATE;
            }
            //ISOLATE
            if (str1.equals("E") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.ISOLATE;
            }
            //CLEAR
            if (str1.equals("X") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CLEAR;
            }
            //CONTAIN
            if (str1.equals("J") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CONTAIN;
            }
            //OCCUPY
            if (str1.equals("O") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.OCCUPY;
            }
            //FIX
            if (str1.equals("F") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.FIX;
            }
            //CATK:
            if (str1.equals("K") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.CATK;
            }
            //DESTROY
            if (str1.equals("D") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.DESTROY;
            }
            //INTERDICT
            if (str1.equals("I") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.INTERDICT;
            }
            //NEUTRALIZE
            if (str1.equals("N") && c0.equals("G") && c2.equals("T")) {
                return TacticalLines.NEUTRALIZE;
            }

        }
        catch(Exception exc)
        {
            ErrorLogger.LogException(_className ,"CGetLinetypeFromString",
                    new RendererException("Failed inside CGetLinetypeFromString " + strLine, exc));
        }
        return -1;
    }
    /**
     * Return true is the line type is a channel type
     * @param lineType line type
     * @return
     */
    public static int CIsChannel(int lineType) {
        int lResult = 0;
        try {
            switch (lineType) {
                case TacticalLines.CATK:
                case TacticalLines.CATKBYFIRE:
                case TacticalLines.LC:
                case TacticalLines.LC2:
                case TacticalLines.AAFNT:
                case TacticalLines.AAFNT_STRAIGHT:
                case TacticalLines.AXAD:
                case TacticalLines.AIRAOA:
                case TacticalLines.AAAAA:
                case TacticalLines.MAIN:
                case TacticalLines.MAIN_STRAIGHT:
                case TacticalLines.SPT:
                case TacticalLines.SPT_STRAIGHT:
                case TacticalLines.UNSP:
                case TacticalLines.SFENCE:
                case TacticalLines.DFENCE:
                case TacticalLines.DOUBLEA:
                case TacticalLines.LWFENCE:
                case TacticalLines.HWFENCE:
                case TacticalLines.BBS_LINE:
                case TacticalLines.SINGLEC:
                case TacticalLines.SINGLEC2:
                case TacticalLines.DOUBLEC:
                case TacticalLines.DOUBLEC2:
                case TacticalLines.TRIPLE:
                case TacticalLines.TRIPLE2:
                case TacticalLines.CHANNEL:
                case TacticalLines.CHANNEL_FLARED:
                case TacticalLines.CHANNEL_DASHED:
                    //case TacticalLines.BELT:	//change 2
                    lResult = 1;
                    break;
                default:
                    lResult = 0;
                    break;
            }
        } 
        catch (Exception exc)
        {
            ErrorLogger.LogException(_className ,"CIsChannel",
                    new RendererException("Failed inside CIsChannel " + Integer.toString(lineType), exc));
        }
        return lResult;
    }
    private static String _client="";
    public static void setClient(String value)
    {
        _client=value;
        Channels.setClient(value);
    }
    public static String getClient()
    {
        return _client;
    }
//    public static void setMinLength(double value)
//    {
//        DISMSupport.setMinLength(value);
//        arraysupport.setMinLength(value);
//        countsupport.setMinLength(value);
//        return;
//    }
}
