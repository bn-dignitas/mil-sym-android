/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package armyc2.c2sd.renderer.utilities;

/*import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;
 import org.w3c.dom.*;
 import org.xml.sax.SAXException;
 import java.io.*;
 import java.util.HashMap;
 import java.util.Map;*/
import android.R;
import android.graphics.Typeface;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Responsible for loading tactical graphic symbol definitions into a hash
 * table.
 *
 * @author michael.spinelli
 */
@SuppressWarnings("unused")
public class SymbolDefTable {

    private static SymbolDefTable _instance = null;
    private static Boolean _initCalled = false;
    //private static SymbolTableThingy
    private static Map<String, SymbolDef> _SymbolDefinitionsB = null;
    private static ArrayList<SymbolDef> _SymbolDefDupsB = null;
    private String TAG = "SymbolDefTable";

    private static Map<String, SymbolDef> _SymbolDefinitionsC = null;
    private static ArrayList<SymbolDef> _SymbolDefDupsC = null;

    private static String propSymbolID = "SYMBOLID";
    private static String propGeometry = "GEOMETRY";
    private static String propDrawCategory = "DRAWCATEGORY";
    private static String propMaxPoint = "MAXPOINTS";
    private static String propMinPoints = "MINPOINTS";
    private static String propHasWidth = "HASWIDTH";
    private static String propModifiers = "MODIFIERS";
    private static String propDescription = "DESCRIPTION";
    private static String propHierarchy = "HIERARCHY";

    /*
     * Holds SymbolDefs for all symbols.  (basicSymbolID, Description,
     * MinPoint, MaxPoints, etc...)
     * Call getInstance().
     *
     * */
    private SymbolDefTable() {
        //init(null);
        //_initCalled=true;
    }

    public static synchronized SymbolDefTable getInstance() {
        if (_instance == null) {
            _instance = new SymbolDefTable();
        }

        return _instance;
    }

    private String getXML(String xmlName)
    {
        String xmlFolder = "res/raw/";
        String xml = null;
        Typeface tf = null;
        InputStream is = null;
        try
        {
            is = this.getClass().getClassLoader().getResourceAsStream(xmlFolder + xmlName);
            if (is != null)
            {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader r = new BufferedReader(isr);
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null)
                {
                    total.append(line);
                }
                xml = total.toString();

                //cleanup
                r.close();
                isr.close();
                is.close();
                r = null;
                isr = null;
                is = null;
                total = null;
            }
        }
        catch (Exception exc)
        {
            Log.e(TAG, exc.getMessage(), exc);
        }
        return xml;
    }

    public final synchronized  void init()
    {
        if (_initCalled == false)
        {
            String[] xml = new String[2];
            xml[0] = getXML("symbolconstantsb.xml");
            xml[1] = getXML("symbolconstantsc.xml");
            init(xml);
        }
    }

    public final synchronized void init(String[] symbolConstantsXML)
    {
        if (_initCalled == false) {
            _SymbolDefinitionsB = new HashMap<String, SymbolDef>();
            _SymbolDefDupsB = new ArrayList<SymbolDef>();

            _SymbolDefinitionsC = new HashMap<String, SymbolDef>();
            _SymbolDefDupsC = new ArrayList<SymbolDef>();
	    String lookupXmlB = symbolConstantsXML[0];//FileHandler.InputStreamToString(xmlStreamB);
	    String lookupXmlC = symbolConstantsXML[1];;//FileHandler.InputStreamToString(xmlStreamC);
	    populateLookup(lookupXmlB, RendererSettings.Symbology_2525Bch2_USAS_13_14);
	    populateLookup(lookupXmlC, RendererSettings.Symbology_2525C);	    
	    _initCalled = true;
        }
    }

    private void populateLookup(String xml, int symStd) {
        SymbolDef sd = null;
        ArrayList<String> al = XMLUtil.getItemList(xml, "<SYMBOL>", "</SYMBOL>");
        for (int i = 0; i < al.size(); i++) {
            String data = (String) al.get(i);
            String symbolID = XMLUtil.parseTagValue(data, "<SYMBOLID>", "</SYMBOLID>");
            String geometry = XMLUtil.parseTagValue(data, "<GEOMETRY>", "</GEOMETRY>");
            String drawCategory = XMLUtil.parseTagValue(data, "<DRAWCATEGORY>", "</DRAWCATEGORY>");
            String maxpoints = XMLUtil.parseTagValue(data, "<MAXPOINTS>", "</MAXPOINTS>");
            String minpoints = XMLUtil.parseTagValue(data, "<MINPOINTS>", "</MINPOINTS>");
            String modifiers = XMLUtil.parseTagValue(data, "<MODIFIERS>", "</MODIFIERS>");
            String description = XMLUtil.parseTagValue(data, "<DESCRIPTION>", "</DESCRIPTION>");
            description = description.replaceAll("&amp;", "&");
            String hierarchy = XMLUtil.parseTagValue(data, "<HIERARCHY>", "</HIERARCHY>");
            //String alphaHierarchy = XMLUtil.parseTagValue(data, "<ALPHAHIERARCHY>", "</ALPHAHIERARCHY>");
            String path = XMLUtil.parseTagValue(data, "<PATH>", "</PATH>");

            sd = new SymbolDef(symbolID, description, Integer.valueOf(drawCategory), hierarchy, Integer.valueOf(minpoints), Integer.valueOf(maxpoints), modifiers, path);

            boolean isMCSSpecific = SymbolUtilities.isMCSSpecificTacticalGraphic(sd);
            if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14) {
                if (_SymbolDefinitionsB.containsKey(symbolID) == false && isMCSSpecific == false) {
                    _SymbolDefinitionsB.put(symbolID, sd);
                } else if (isMCSSpecific == false) {
                    _SymbolDefDupsB.add(sd);
                }
            } else if (symStd == RendererSettings.Symbology_2525C) {
                if (_SymbolDefinitionsC.containsKey(symbolID) == false && isMCSSpecific == false) {
                    _SymbolDefinitionsC.put(symbolID, sd);
                } else if (isMCSSpecific == false) {
                    _SymbolDefDupsC.add(sd);
                }
            }
        }

    }

    /**
     * @name getSymbolDef
     *
     * @desc Returns a SymbolDef from the SymbolDefTable that matches the passed
     * in Symbol Id
     *
     * @param basicSymbolID - IN - A 15 character MilStd code
     * @param symStd 0 or 1.
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525Bch2_USAS_13_14
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525C
     * @return SymbolDef whose Symbol Id matches what is passed in
     */
    public SymbolDef getSymbolDef(String basicSymbolID, int symStd) {
        SymbolDef returnVal = null;
        if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14) {
            returnVal = (SymbolDef) _SymbolDefinitionsB.get(basicSymbolID);
        } else if (symStd == RendererSettings.Symbology_2525C) {
            returnVal = (SymbolDef) _SymbolDefinitionsC.get(basicSymbolID);
        }
        return returnVal;
    }

    /**
     * Returns a Map of all the symbol definitions, keyed on basic symbol code.
     *
     * @param symStd 0 or 1.
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525Bch2_USAS_13_14
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525C
     * @return
     */
    public Map<String, SymbolDef> GetAllSymbolDefs(int symStd) {
        if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14) {
            return _SymbolDefinitionsB;
        } else if (symStd == RendererSettings.Symbology_2525C) {
            return _SymbolDefinitionsC;
        } else {
            return null;
        }
    }

    /**
     * SymbolIDs are no longer unique.
     *
     * @param symStd 0 or 1.
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525Bch2_USAS_13_14
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525C
     * @return
     */
    public ArrayList<SymbolDef> GetAllSymbolDefDups(int symStd) {
        if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14) {
            return _SymbolDefDupsB;
        } else if (symStd == RendererSettings.Symbology_2525C) {
            return _SymbolDefDupsC;
        } else {
            return null;
        }
    }

    /**
     *
     * @param basicSymbolID
     * @param symStd 0 or 1.
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525Bch2_USAS_13_14
     * @see ArmyC2.C2SD.Utilities.RendererSettings#Symbology_2525C
     * @return
     */
    public Boolean HasSymbolDef(String basicSymbolID, int symStd) {
        if (basicSymbolID != null && basicSymbolID.length() == 15) {
            if (symStd == RendererSettings.Symbology_2525Bch2_USAS_13_14) {
                return _SymbolDefinitionsB.containsKey(basicSymbolID);
            } else if (symStd == RendererSettings.Symbology_2525C) {
                return _SymbolDefinitionsC.containsKey(basicSymbolID);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if symbol is a multipoint symbol
     *
     * @param symbolID
     * @param symStd
     * @return
     */
    public Boolean isMultiPoint(String symbolID, int symStd) {

        String basicSymbolID;

        char codingScheme = symbolID.charAt(0);
        Boolean returnVal = false;
        if (codingScheme == 'G' || codingScheme == 'W') {
            if (symbolID.charAt(1) != '*') {
                basicSymbolID = SymbolUtilities.getBasicSymbolID(symbolID);
            } else {
                basicSymbolID = symbolID;
            }
            SymbolDef sd = this.getSymbolDef(basicSymbolID, symStd);
            if (sd != null) {
                if (sd.getMaxPoints() > 1) {
                    returnVal = true;
                } else {
                    switch (sd.getDrawCategory()) {
                        case 15://SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                        case 16://SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                        case 17://SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE: 
                        case 18://SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE:
                        case 19://SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                        case 20://SymbolDef.DRAW_CATEGORY_ROUTE:
                            returnVal = true;
                            break;
                        default:
                            returnVal = false;
                    }
                }
                return returnVal;
            } else {
                return false;
            }
        } else if (symbolID.startsWith("BS_") || symbolID.startsWith("BBS_") || symbolID.startsWith("PBS_")) {
            return true;
        } else {
            return false;
        }
    }

}
