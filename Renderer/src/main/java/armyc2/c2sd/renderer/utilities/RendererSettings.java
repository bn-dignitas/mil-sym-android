/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package armyc2.c2sd.renderer.utilities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


/**
 *Static class that holds the setting for the JavaRenderer.
 * Allows different parts of the renderer to know what
 * values are being used.
 * @author michael.spinelli
 */
public class RendererSettings{

    private static RendererSettings _instance = null;
    
    private static List<SettingsChangedEventListener> _listeners = new ArrayList<SettingsChangedEventListener>();  

    //outline approach.  none, filled rectangle, outline (default),
    //outline quick (outline will not exceed 1 pixels).
    private static int _TextBackgroundMethod = 2;
    /**
     * There will be no background for text
     */
    public static final int TextBackgroundMethod_NONE = 0;

    /**
     * There will be a colored box behind the text
     */
    public static final int TextBackgroundMethod_COLORFILL = 1;

    /**
     * There will be an adjustable outline around the text
     * Outline width of 4 is recommended.
     */
    public static final int TextBackgroundMethod_OUTLINE = 2;

    /**
     * A different approach for outline which is quicker and seems to use
     * less memory.  Also, you may do well with a lower outline thickness setting
     * compared to the regular outlining approach.  Outline Width of 2 is
     * recommended.  Only works with RenderMethod_NATIVE.
     * @deprecated
     */
    public static final int TextBackgroundMethod_OUTLINE_QUICK = 3;

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     */
    private static int _TextBackgroundAutoColorThreshold = 160;

    //if TextBackgroundMethod_OUTLINE is set, This value determnies the width of that outline.
    private static int _TextOutlineWidth = 4;

    //label foreground color, uses line color of symbol if null.
    private static int _ColorLabelForeground = Color.BLACK;
    //label background color, used if TextBackGroundMethod = TextBackgroundMethod_COLORFILL && not null
    private static int _ColorLabelBackground = Color.WHITE;

    private static int _SymbolRenderMethod = 1;
    private static int _UnitRenderMethod = 1;
    private static int _TextRenderMethod = 1;
    
    private static int _SymbolOutlineWidth = 3;

    /**
     * Collapse labels for fire support areas when the symbol isn't large enough to show all
     * the labels.
     */
    private static boolean _AutoCollapseModifiers = true;

    /**
     * If true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     */
    private static Boolean _CenterOnHQStaff = true;

    /**
     * Everything that comes back from the Renderer is a Java Shape.  Simpler,
     * but can be slower when rendering modifiers or a large number of single
     * point symbols. Not recommended
     */
    public static final int RenderMethod_SHAPES = 0;
    /**
     * Adds a level of complexity to the rendering but is much faster for 
     * certain objects.  Modifiers and single point graphics will render faster.
     * MultiPoints will still be shapes.  Recommended
     */
    public static final int RenderMethod_NATIVE = 1;

    /**
     * 2525Bch2 and USAS 13/14 symbology
     */
    public static final int Symbology_2525Bch2_USAS_13_14 = 0;
    /**
     * 2525C, which includes 2525Bch2 & USAS 13/14
     */
    public static final int Symbology_2525C = 1;
    
    private static int _SymbologyStandard = 1;


    private static boolean _UseLineInterpolation = true;

	//single points
    //private static Font _ModifierFont = new Font("arial", Font.TRUETYPE_FONT, 12);
    private static String _ModifierFontName = "arial";
    //private static int _ModifierFontType = Font.TRUETYPE_FONT;
    private static int _ModifierFontType = Typeface.BOLD;
    private static int _ModifierFontSize = 18;
    private static int _ModifierFontKerning = 0;//0=off, 1=on (TextAttribute.KERNING_ON)
    //private static float _ModifierFontTracking = TextAttribute.TRACKING_LOOSE;//loose=0.4f;
    
	//multi points
    private static String _MPModifierFontName = "arial";
    //private static int _ModifierFontType = Font.TRUETYPE_FONT;
    private static int _MPModifierFontType = Typeface.BOLD;
    private static int _MPModifierFontSize = 18;
    private static int _MPModifierFontKerning = 0;//0=off, 1=on (TextAttribute.KERNING_ON)
    //private static float _ModifierFontTracking = TextAttribute.TRACKING_LOOSE;//loose=0.4f;
    private static float _KMLLabelScale = 1.0f;
    
    private boolean _scaleEchelon = false;
    private boolean _DrawAffiliationModifierAsLabel = true;
    
    private float _SPFontSize = 60f;
    private float _UnitFontSize = 50f;
    private int _PixelSize = 35;
    private int _DPI = 96;
    
    private static int _CacheSize = 1024;
    private static int _VMSize = 10240;

    private RendererSettings()
    {
        Init();
    }


    public static synchronized RendererSettings getInstance()
    {
        if(_instance == null)
        {
            _instance = new RendererSettings();
        }

        return _instance;
    }
    
    private void Init()
    {
        try
        {
            _VMSize = (int)Runtime.getRuntime().maxMemory();
            _CacheSize = Math.round(_VMSize * 0.05f);
        }
        catch(Exception exc)
        {
            ErrorLogger.LogException("RendererSettings", "Init", exc, Level.WARNING);
        }
    }

    private void throwEvent(SettingsChangedEvent sce)
    {
    	for (SettingsChangedEventListener listener : _listeners) {
    		listener.onSettingsChanged(sce);
		}
    }
    
    public void addEventListener(SettingsChangedEventListener scel)
    {
    	_listeners.add(scel);
    }
    
    /**
     * None, outline (default), or filled background.
     * If set to OUTLINE, TextOutlineWidth changed to default of 4.
     * If set to OUTLINE_QUICK, TextOutlineWidth changed to default of 2.
     * Use setTextOutlineWidth if you'd like a different value.
     * @param method like RenderSettings.TextBackgroundMethod_NONE
     */
    synchronized public void setTextBackgroundMethod(int textBackgroundMethod)
    {
        _TextBackgroundMethod = textBackgroundMethod;
        if(_TextBackgroundMethod == TextBackgroundMethod_OUTLINE)
            _TextOutlineWidth = 4;
        else if(_TextBackgroundMethod == TextBackgroundMethod_OUTLINE_QUICK)
            _TextOutlineWidth = 2;
    }

    /**
     * None, outline (default), or filled background.
     * @return method like RenderSettings.TextBackgroundMethod_NONE
     */
    synchronized public int getTextBackgroundMethod()
    {
        return _TextBackgroundMethod;
    }
    

    /*public void setUnitFontSize(float size)
    {
    	_UnitFontSize = size;
    }//*/
    
    public float getUnitFontSize()
    {
    	return _UnitFontSize;
    }
    
    /*public void setSPFontSize(float size)
    {
    	_SPFontSize = size;
    }//*/
    
    public float getSPFontSize()
    {
    	return _SPFontSize;
    }
    
    public void setDefaultPixelSize(int size)
    {
    	_PixelSize = size;
    }
    
    public int getDefaultPixelSize()
    {
    	return _PixelSize;
    }
    
    public int getDeviceDPI()
    {
    	return _DPI;
    }
    
    public void setDeviceDPI(int size)
    {
    	_DPI = size;
    }
    
    /**
     * Controls what symbols are supported.
     * Set this before loading the renderer.
     * @param symbologyStandard
     * Like RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public void setSymbologyStandard(int standard)
    {
        _SymbologyStandard = standard;
    }

    /**
     * Current symbology standard
     * @return symbologyStandard
     * Like RendererSettings.Symbology_2525Bch2_USAS_13_14
     */
    public int getSymbologyStandard()
    {
        return _SymbologyStandard;
    }
    
    /**
     * For lines symbols with "decorations" like FLOT or LOC, when points are 
     * too close together, we will start dropping points until we get enough 
     * space between 2 points to draw the decoration.  Without this, when points
     * are too close together, you run the chance that the decorated line will
     * look like a plain line because there was no room between points to
     * draw the decoration.
     * @param value 
     */
    public void setUseLineInterpolation(boolean value)
    {
        _UseLineInterpolation = value;
    }
    
    /**
     * Returns the current setting for Line Interpolation.
     * @return 
     */
    public boolean getUseLineInterpolation()
    {
        return _UseLineInterpolation;
    }

    /**
     * Collapse Modifiers for fire support areas when the symbol isn't large enough to show all
     * the labels.  Identifying label will always be visible.  Zooming in, to make the symbol larger,
     * will make more modifiers visible.  Resizing the symbol can also make more modifiers visible.
     * @param value
     */
    public void setAutoCollapseModifiers(boolean value) {_AutoCollapseModifiers = value;}

    public boolean getAutoCollapseModifiers() {return _AutoCollapseModifiers;}

    /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.
     * @param method like RendererSetting.RenderMethod_SHAPES
     */
    public void setUnitRenderMethod(int symbolRenderMethod)
    {
        _UnitRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     * @return method like RendererSetting.RenderMethod_NATIVE
     */
    public int getUnitRenderMethod()
    {
        return _UnitRenderMethod;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     * @param value
     */
    public void setCenterOnHQStaff(Boolean value)
    {
        _CenterOnHQStaff = value;
    }

    /**
     * if true (default), when HQ Staff is present, location will be indicated by the free
     * end of the staff
     * @param value
     */
    public Boolean getCenterOnHQStaff()
    {
        return _CenterOnHQStaff;
    }

     /**
     * determines what kind of java objects will be generated when processing
     * a symbol. RenderMethod_SHAPES is simpler as everything is treated
     * the same. RenderMethod_NATIVE is faster but, in addition to shapes,
     * uses GlyphVectors and TextLayouts.  In the case of text, NATIVE tends to
     * render sharper and clearer text.
     * @param method like RendererSetting.RenderMethod_SHAPES
     */
    public void setTextRenderMethod(int symbolRenderMethod)
    {
        _TextRenderMethod = symbolRenderMethod;
    }

    /**
     * Maps to RendererSetting.RenderMethod_SHAPES or
     * RendererSetting.RenderMethod_NATIVE
     * @return
     */
    public int getTextRenderMethod()
    {
        return _TextRenderMethod;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used,
     * the outline will be this many pixels wide.
     * @param method
     */
    synchronized public void setTextOutlineWidth(int width)
    {
        _TextOutlineWidth = width;
    }

    /**
     * if RenderSettings.TextBackgroundMethod_OUTLINE is used,
     * the outline will be this many pixels wide.
     * @param method
     * @return
     */
    synchronized public int getTextOutlineWidth()
    {
        return _TextOutlineWidth;
    }

     /**
     * Refers to text color of modifier labels
     * @return
      *  
     */
    public int getLabelForegroundColor()
    {
        return _ColorLabelForeground;
    }

    /**
     * Refers to text color of modifier labels
     * Default Color is Black.  If NULL, uses line color of symbol
     * @param value
     * 
     */
    synchronized public void setLabelForegroundColor(int value)
    {
       _ColorLabelForeground = value;
    }

    /**
     * Refers to background color of modifier labels
     * @return
     * 
     */
    public int getLabelBackgroundColor()
    {
        return _ColorLabelBackground;
    }

    /**
     * Refers to text color of modifier labels
     * Default Color is White.
     * Null value means the optimal background color (black or white)
     * will be chose based on the color of the text.
     * @param value
     * 
     */
    synchronized public void setLabelBackgroundColor(int value)
    {
        _ColorLabelBackground = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     * @param value
     */
    public void setTextBackgroundAutoColorThreshold(int value)
    {
        _TextBackgroundAutoColorThreshold = value;
    }

    /**
     * Value from 0 to 255. The closer to 0 the lighter the text color has to be
     * to have the outline be black. Default value is 160.
     * @return
     */
    public int getTextBackgroundAutoColorThreshold()
    {
        return _TextBackgroundAutoColorThreshold;
    }
    
    /**
     * This applies to Single Point Tactical Graphics.
     * Setting this will determine the default value for milStdSymbols when created.
     * 0 for no outline,
     * 1 for outline thickness of 1 pixel, 
     * 2 for outline thickness of 2 pixels,
     * greater than 2 is not currently recommended.
     * @param width
     */
    synchronized public void setSinglePointSymbolOutlineWidth(int width)
    {
        _SymbolOutlineWidth = width;
    }

    /**
     * This applies to Single Point Tactical Graphics.
     * @return
     */
    synchronized public int getSinglePointSymbolOutlineWidth()
    {
        return _SymbolOutlineWidth;
    }
    
    /**
     * false to use label font size
     * true to scale it using symbolPixelBounds / 3.5
     * @param value 
     */
    public void setScaleEchelon(boolean value)
    {
        _scaleEchelon = value;
    }
    /**
     * Returns the value determining if we scale the echelon font size or
     * just match the font size specified by the label font.
     * @return true or false
     */
    public boolean getScaleEchelon()
    {
        return _scaleEchelon;
    }
    
     /**
     * Determines how to draw the Affiliation modifier.
     * True to draw as modifier label in the "E/F" location.
     * False to draw at the top right corner of the symbol
     */
    public void setDrawAffiliationModifierAsLabel(boolean value)
    {
        _DrawAffiliationModifierAsLabel = value;
    }
    /**
     * True to draw as modifier label in the "E/F" location.
     * False to draw at the top right corner of the symbol
     */
    public boolean getDrawAffiliationModifierAsLabel()
    {
        return _DrawAffiliationModifierAsLabel;
    }    
    /**
     * 
     * @param name Like "arial"
     * @param type Like Font.BOLD
     * @param size Like 12
     * @param kerning - default false. The default advances of single characters are not
     * appropriate for some character sequences, for example "To" or
     * "AWAY".  Without kerning the adjacent characters appear to be
     * separated by too much space.  Kerning causes selected sequences
     * of characters to be spaced differently for a more pleasing
     * visual appearance. 
     * @param Tracking - default 0.04 (TextAttribute.TRACKING_LOOSE).  
     * The tracking value is multiplied by the font point size and
     * passed through the font transform to determine an additional
     * amount to add to the advance of each glyph cluster.  Positive
     * tracking values will inhibit formation of optional ligatures.
     * Tracking values are typically between <code>-0.1</code> and
     * <code>0.3</code>; values outside this range are generally not
     * desireable.
     * @deprecated
     */
    public void setModifierFont(String name, int type, int size, Boolean kerning, float tracking)
    {
        _ModifierFontName = name;
        _ModifierFontType = type;
        _ModifierFontSize = size;
        /*if(kerning==false)
            _ModifierFontKerning = 0;
        else
            _ModifierFontKerning = TextAttribute.KERNING_ON;
        _ModifierFontTracking = tracking;//*/
    }
    
    /**
     * 
     * @param name
     * @param type Typeface
     * @param size
     */
    public void setModifierFont(String name, int type, int size)
    {
        _ModifierFontName = name;
        _ModifierFontType = type;
        _ModifierFontSize = size;
        throwEvent(new SettingsChangedEvent(SettingsChangedEvent.EventType_FontChanged));
    }
    
    
    /**
     * Sets the font to be used for multipoint modifier labels
     * @param name Like "arial"
     * @param type Like Font.TRUETYPE_FONT
     * @param size Like 12
     */
    public void setMPModifierFont(String name, int type, int size)
    {
        _MPModifierFontName = name;
        _MPModifierFontType = type;
        _MPModifierFontSize = size;
        _KMLLabelScale = 1.0f;
        throwEvent(new SettingsChangedEvent(SettingsChangedEvent.EventType_FontChanged));
    }
    
    /**
     * Sets the font to be used for multipoint modifier labels
     * @param name Like "arial"
     * @param type Like Font.TRUETYPE_FONT
     * @param size Like 12
     * @param kmlScale only set if you're rendering in KML (default 1.0)
     */
    public void setMPModifierFont(String name, int type, int size, float kmlScale)
    {
        _MPModifierFontName = name;
        _MPModifierFontType = type;
        _MPModifierFontSize = Math.round(size * kmlScale);
        _KMLLabelScale = kmlScale;
        throwEvent(new SettingsChangedEvent(SettingsChangedEvent.EventType_FontChanged));
    }

    


    /**
     * get font object used for labels
     * @return Font object
     */
    public Paint getModiferFont()
    {
    	Paint p = null;
        try
        {
        	//need to create a paint and set it's typeface along with it's properties
        	Typeface tf = Typeface.create(_ModifierFontName, _ModifierFontType);
        	p = new Paint();
        	p.setTextSize(_ModifierFontSize);
        	p.setAntiAlias(true);
        	p.setColor(_ColorLabelForeground);
			//p.setTextAlign(Align.CENTER);
        	p.setTypeface(tf);
        	
			p.setStrokeCap(Cap.BUTT);
			p.setStrokeJoin(Join.MITER);
			p.setStrokeMiter(3f);
        	
        }
        catch(Exception exc)
        {
            String message = "font creation error, returning \"" + _ModifierFontName + "\" font, " + _ModifierFontSize + "pt. Check font name and type.";
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", message);
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", exc.getMessage());
            try
            {
            	Typeface tf = Typeface.create("arial", Typeface.BOLD);
	        	p = new Paint();
	        	p.setTextSize(12);
	        	p.setAntiAlias(true);
	        	p.setColor(Color.BLACK);
	        	p.setTypeface(tf);
	        	
	        	p.setStrokeCap(Cap.BUTT);
				p.setStrokeJoin(Join.MITER);
				p.setStrokeMiter(3f);
            }
            catch(Exception exc2)
            {
            	//failed to make a default font, return null
            	p=null;
            }
        }
        return p;
    }
    
    /**
     * get font object used for labels
     * @return Font object
     */
    public Paint getMPModifierFont()
    {
    	Paint p = null;
        try
        {
        	//need to create a paint and set it's typeface along with it's properties
        	Typeface tf = Typeface.create(_MPModifierFontName, _MPModifierFontType);
        	p = new Paint();
        	p.setTextSize(_MPModifierFontSize);
        	p.setAntiAlias(true);
        	p.setColor(_ColorLabelForeground);
			//p.setTextAlign(Align.CENTER);
        	p.setTypeface(tf);
        	
        }
        catch(Exception exc)
        {
            String message = "font creation error, returning \"" + _MPModifierFontName + "\" font, " + _MPModifierFontSize + "pt. Check font name and type.";
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", message);
            ErrorLogger.LogMessage("RendererSettings", "getLabelFont", exc.getMessage());
            try
            {
            	Typeface tf = Typeface.create("arial", Typeface.BOLD);
	        	p = new Paint();
	        	p.setTextSize(12);
	        	p.setAntiAlias(true);
	        	p.setColor(Color.BLACK);
	        	p.setTypeface(tf);
            }
            catch(Exception exc2)
            {
            	//failed to make a default font, return null
            	p=null;
            }
        }
        return p;
    }
    
    /**
     * the font name to be used for modifier labels
     * @return name of the label font
     */
    public String getMPModifierFontName()
    {
        return _MPModifierFontName;
    }
    /**
     * Like Font.BOLD
     * @return type of the label font
     */
    public int getMPModifierFontType()
    {
        return _MPModifierFontType;
    }
    /**
     * get font point size
     * @return size of the label font
     */
    public int getMPModifierFontSize()
    {
        return _MPModifierFontSize;
    }
    
    public float getKMLLabelScale()
    {
    	return _KMLLabelScale;
    }

    /**
     * Set the cache size as a percentage of VM memory available to the app.
     * Renderer won't let you set a value greater than 10% of the available VM memory.
     * @param percentage 
     */
    public void setCacheSize(float percentage)
    {
        if(percentage > 0.10f)
            percentage = 0.10f;
        _CacheSize = Math.round(_VMSize * percentage);
        throwEvent(new SettingsChangedEvent(SettingsChangedEvent.EventType_CacheSizeChanged));
    }
    
    /**
     * Set the cache size in bytes.
     * Renderer won't let you set a value greater than10% of the available VM memory.
     * @param bytes 
     */
    public void setCacheSize(int bytes)
    {
        if(bytes > _VMSize / 10)
            bytes = _VMSize / 10;
        _CacheSize = bytes;
        throwEvent(new SettingsChangedEvent(SettingsChangedEvent.EventType_CacheSizeChanged));
    }
    
    public int getCacheSize()
    {
        return _CacheSize;
    }
}
