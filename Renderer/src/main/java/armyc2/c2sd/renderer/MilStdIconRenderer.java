package armyc2.c2sd.renderer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import armyc2.c2sd.renderer.utilities.Color;
import armyc2.c2sd.renderer.utilities.ErrorLogger;
import armyc2.c2sd.renderer.utilities.FontManager;
import armyc2.c2sd.renderer.utilities.ImageInfo;
import armyc2.c2sd.renderer.utilities.MilStdAttributes;
import armyc2.c2sd.renderer.utilities.RendererSettings;
import armyc2.c2sd.renderer.utilities.SinglePointLookup;
import armyc2.c2sd.renderer.utilities.SymbolDef;
import armyc2.c2sd.renderer.utilities.SymbolDefTable;
//import armyc2.c2sd.renderer.utilities.SymbolSVGTable;
import armyc2.c2sd.renderer.utilities.SymbolUtilities;
import armyc2.c2sd.renderer.utilities.TacticalGraphicLookup;
import armyc2.c2sd.renderer.utilities.UnitDef;
import armyc2.c2sd.renderer.utilities.UnitDefTable;
import armyc2.c2sd.renderer.utilities.UnitFontLookup;
//import armyc2.c2sd.renderer.utilities.UnitSVGTable;

public class MilStdIconRenderer/* implements IIconRenderer */ {

    private String TAG = "MilStdIconRenderer";
    private String _cacheDir = null;

    private static MilStdIconRenderer _instance = null;
    private Boolean _initSucces = false;
    private SinglePointRenderer _SPR = null;
    private SinglePointSVGRenderer _SPSVGR = null;

    public static synchronized MilStdIconRenderer getInstance()
    {
        if (_instance == null)
        {
            _instance = new MilStdIconRenderer();
        }
        return _instance;
    }

    /**
     *
     * @param cacheDir
     */
    public synchronized void init(String cacheDir)//List<Typeface> fonts, List<String> xml
    {
        try
        {
            if (!_initSucces)
            {

                _cacheDir = cacheDir;

                //setup fonts
                FontManager.getInstance().init(cacheDir);

                //get xml
                String unitcontantsb;
                unitcontantsb = getXML("unitconstantsb.xml");

                String unitfontmappingsb;
                unitfontmappingsb = getXML("unitfontmappingsb.xml");

                String unitconstantsc;
                unitconstantsc = getXML("unitconstantsc.xml");

                String unitfontmappingsc;
                unitfontmappingsc = getXML("unitfontmappingsc.xml");

                String symbolconstantsb;
                symbolconstantsb = getXML("symbolconstantsb.xml");

                String singlepointmappingsb;
                singlepointmappingsb = getXML("singlepointb.xml");

                String symbolconstantsc;
                symbolconstantsc = getXML("symbolconstantsc.xml");

                String singlepointmappingsc;
                singlepointmappingsc = getXML("singlepointc.xml");

                String tacticalgraphics;
                tacticalgraphics = getXML("tacticalgraphics.xml");

                String[] unitConstants =
                {
                    unitcontantsb, unitconstantsc
                };
                String[] unitMappings =
                {
                    unitfontmappingsb, unitfontmappingsc
                };
                String[] symbolConstants =
                {
                    symbolconstantsb, symbolconstantsc
                };
                String[] symbolMappings =
                {
                    singlepointmappingsb, singlepointmappingsc
                };

                UnitDefTable.getInstance().init(unitConstants);
                UnitFontLookup.getInstance().init(unitMappings);
                SymbolDefTable.getInstance().init(symbolConstants);
                SinglePointLookup.getInstance().init(symbolMappings);//*/
                TacticalGraphicLookup.getInstance().init(tacticalgraphics);

                 //PROTOTYPE SVG////////////////////////////////////////
                 //works, but half speed
                 /*String USVG = getXML("unitfontsvg.svg");
                 UnitSVGTable.getInstance().init(USVG);
                 String SPSVG = getXML("singlepointsvg.svg");
                 SymbolSVGTable.getInstance().init(SPSVG);//*/
                //////////////////////////////////////////////////////
                
                //setup single point renderer
                _SPR = SinglePointRenderer.getInstance();
                //_SPSVGR = SinglePointSVGRenderer.getInstance();

                _initSucces = true;
            }

        }
        catch (Exception exc)
        {
            Log.e(TAG, exc.getMessage(), exc);
        }

    }

    private
    String getXML(String xmlName)
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

    //@Override
    public Boolean CanRender(String symbolID, SparseArray<String> modifiers, SparseArray<String> attributes)
    {

        String message = null;
        String basicSymbolID = symbolID;
        basicSymbolID = SymbolUtilities.getBasicSymbolID(basicSymbolID);
        //ErrorLogger.LogMessage("TEST");

        int symStd = -1;
        String temp;
        if (attributes.indexOfKey(MilStdAttributes.SymbologyStandard) >= 0)
        {
            temp = attributes.get(MilStdAttributes.SymbologyStandard);
            if (SymbolUtilities.isNumber(temp))
            {
                symStd = Integer.parseInt(temp);
            }
        }

        if (symStd < 0 || symStd > RendererSettings.Symbology_2525C)
        {
            symStd = RendererSettings.getInstance().getSymbologyStandard();
        }

        try
        {
            // message = "Cannot draw: " + symbolCode + " (" + basicSymbolID + ")";
            SymbolDefTable sdt = SymbolDefTable.getInstance();
            if (SymbolUtilities.isTacticalGraphic(basicSymbolID))
            {

                SymbolDef sd = sdt.getSymbolDef(basicSymbolID, symStd);
                if (sd != null)
                {

                    if (sd.getDrawCategory() == 8)//make sure we can find the character in the font.
                    {
                        int index = -1;
                        index = SinglePointLookup.getInstance().getCharCodeFromSymbol(symbolID, symStd);
                        if (index > 0)
                        {
                            return true;
                        }
                        else
                        {
                            message = "Bad font lookup for: " + symbolID + " (" + basicSymbolID + ")";
                        }
                    }
                    else //check with icon renderer for multipoints
                    {
                        message = "Cannot draw: " + symbolID + " (" + basicSymbolID + ")";
                    }

                }
                else
                {
                    message = "Cannot draw symbolID: " + symbolID + " (" + basicSymbolID + ")";
                }
            }
            else
            {
                UnitDef ud = UnitDefTable.getInstance().getUnitDef(basicSymbolID, symStd);
                //UnitFontLookupInfo ufli = UnitFontLookup.getInstance().getLookupInfo(basicSymbolID,symStd);
                if (ud != null)
                {
                    return true;
                }
                else
                {
                    message = "JavaRenderer.CanRender() - Cannot draw symbolID: " + symbolID + " (" + basicSymbolID + ")";
                }
            }

            if (message != null && !message.equals(""))
            {
                ErrorLogger.LogMessage(this.getClass().getName(), "CanRender()", message, Level.FINE);
                //System.err.println(message);
                //System.out.println("");
                //System.out.println("INFO: CanRender - " + message);
                //Exception foo = new Exception("Stack?");
                //foo.printStackTrace();
            }
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("MilStdIconRenderer", "CanRender", exc);
        }
        return false;
    }

    private ImageInfo renderTacticalMultipointIcon(String symbolID, SparseArray<String> attributes)
    {
        Color lineColor = SymbolUtilities.getLineColorOfAffiliation(symbolID);
        if (attributes.indexOfKey(MilStdAttributes.LineColor) >= 0)
        {
            lineColor = new Color(attributes.get(MilStdAttributes.LineColor));
        }
        int size = RendererSettings.getInstance().getDefaultPixelSize();//35;
        if (attributes.indexOfKey(MilStdAttributes.PixelSize) >= 0)
        {
            size = Integer.parseInt(attributes.get(MilStdAttributes.PixelSize));
        }

        ImageInfo ii = TacticalGraphicIconRenderer.getIcon(symbolID, size, lineColor);
        return ii;
    }

    //@Override
    public ImageInfo RenderIcon(String symbolID, SparseArray<String> modifiers, SparseArray<String> attributes)
    {

        int symStd = 1;

        if (attributes != null && attributes.indexOfKey(MilStdAttributes.SymbologyStandard) >= 0)
        {
            symStd = Integer.parseInt(attributes.get(MilStdAttributes.SymbologyStandard));
        }

        ImageInfo temp = null;
        if (SymbolUtilities.isTacticalGraphic(symbolID))
        {
            String basicSymbolID = SymbolUtilities.getBasicSymbolIDStrict(symbolID);
            SymbolDef sd = SymbolDefTable.getInstance().getSymbolDef(basicSymbolID, symStd);
            if (sd == null)
            {
                symbolID = SymbolUtilities.reconcileSymbolID(symbolID);
                basicSymbolID = SymbolUtilities.getBasicSymbolIDStrict(symbolID);
                sd = SymbolDefTable.getInstance().getSymbolDef(basicSymbolID, symStd);
            }

            if(sd != null)
            {
	            if (sd.getDrawCategory() == SymbolDef.DRAW_CATEGORY_POINT)
	            {
	                temp = _SPR.RenderSP(symbolID, modifiers, attributes);
	            }
	            else
	            {
	                return renderTacticalMultipointIcon(symbolID, attributes);
	            }
            }
            else
            {
            	temp = _SPR.RenderUnit(symbolID, modifiers, attributes);
            }

        }
        else
        {
        	temp = _SPR.RenderUnit(symbolID, modifiers, attributes);
            /*if(RendererSettings.getInstance().getIconEngine() == RendererSettings.IconEngine_FONT)
            {
                temp = _SPR.RenderUnit(symbolID, modifiers, attributes);
            }
            else
            {
                temp = _SPSVGR.RenderUnit(symbolID, modifiers,attributes);
            }//*/
            
            
        }

        return temp;
    }

    //@Override
    public
            String getRendererID()
    {

        return "milstd2525";
    }

    private SparseArray<String> getDefaultAttributes(String symbolID)
    {
        SparseArray<String> map = new SparseArray<String>();
        try
        {
            if (symbolID == null || symbolID.length() != 15)
            {
                if (symbolID == null)
                {
                    symbolID = "null";
                }
                ErrorLogger.LogMessage("MilStdIconRenderer", "getDefaultAttributes", "getDefaultAttributes passed bad symbolID: " + symbolID);
                return null;
            }

            map.put(MilStdAttributes.Alpha, "1.0");
            if (SymbolUtilities.hasDefaultFill(symbolID))
            {
                map.put(MilStdAttributes.FillColor, SymbolUtilities.getFillColorOfAffiliation(symbolID).toHexString());
            }

            map.put(MilStdAttributes.LineColor, SymbolUtilities.getLineColorOfAffiliation(symbolID).toHexString());

            map.put(MilStdAttributes.OutlineSymbol, "false");
            //attribute[MilStdAttributes.SymbolOutlineColor] = null;
            //map.put(MilStdAttributes.OutlineWidth,"1");

            map.put(MilStdAttributes.DrawAsIcon, "false");

            RendererSettings rs = RendererSettings.getInstance();

            if (SymbolUtilities.isWarfighting(symbolID))
            {
                if (rs != null)
                {
                    map.put(MilStdAttributes.FontSize, String.valueOf(rs.getUnitFontSize()));//50;
                }
                else
                {
                    map.put(MilStdAttributes.FontSize, "50");//50;
                }
                map.put(MilStdAttributes.PixelSize, "-1");
            }
            else
            {
                if (rs != null)
                {
                    map.put(MilStdAttributes.FontSize, String.valueOf(rs.getSPFontSize()));//60;
                }
                else
                {
                    map.put(MilStdAttributes.FontSize, "60");
                }

                map.put(MilStdAttributes.PixelSize, "-1");
            }
            map.put(MilStdAttributes.KeepUnitRatio, "true");
            return map;
        }
        catch (Exception exc)
        {
            ErrorLogger.LogException("MilStdIconRenderer", "getDefaultAttributes", exc);
        }
        return map;
    }

}
