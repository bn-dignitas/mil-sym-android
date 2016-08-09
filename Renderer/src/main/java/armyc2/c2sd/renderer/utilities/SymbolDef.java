/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package armyc2.c2sd.renderer.utilities;

/**
*
* @author michael.spinelli
*/
public class SymbolDef {

   String _strBasicSymbolId = "";
   String _strDescription = "";
   String _strSymbolType = "";
   String _strGeometry = "";
   String _strDrawCategory = "";
   int _intDrawCategory = 99;
   int _intMinPoints = 0;
   int _intMaxPoints = 0;
   String _strModifiers = "";
   String _strHierarchy = "";
   String _strFullPath = "";
   



   /**
    * Just a category in the milstd hierarchy.
    * Not something we draw.
    * WILL NOT RENDER
    */
   static public final int DRAW_CATEGORY_DONOTDRAW = 0;

   /**
    * A polyline, a line with n number of points.
    * 0 control points
    */
   static public final int DRAW_CATEGORY_LINE = 1;

   /**
    * An animated shape, uses the animate function to draw.
    * 0 control points (every point shapes symbol)
    */
   static public final int DRAW_CATEGORY_AUTOSHAPE = 2;

   /**
    * An enclosed polygon with n points
    * 0 control points
    */
   static public final int DRAW_CATEGORY_POLYGON = 3;
   /**
    * A polyline with n points (entered in reverse order)
    * 0 control points
    */
   static public final int DRAW_CATEGORY_ARROW = 4;
   /**
    * A graphic with n points whose last point defines the width of the graphic.
    * 1 control point
    */
   static public final int DRAW_CATEGORY_ROUTE = 5;
   /**
    * A line defined only by 2 points, and cannot have more.
    * 0 control points
    */
   static public final int DRAW_CATEGORY_TWOPOINTLINE = 6;
   /**
    * Shape is defined by a single point
    * 0 control points
    */
   static public final int DRAW_CATEGORY_POINT = 8;
   /**
    * A polyline with 2 points (entered in reverse order).
    * 0 control points
    */
   static public final int DRAW_CATEGORY_TWOPOINTARROW = 9;
   /**
    * An animated shape, uses the animate function to draw. Super Autoshape draw
    * in 2 phases, usually one to define length, and one to define width.
    * 0 control points (every point shapes symbol)
    *
    */
   static public final int DRAW_CATEGORY_SUPERAUTOSHAPE = 15;
    /**
    * Circle that requires 1 AM modifier value.
    * See ModifiersTG.java for modifier descriptions and constant key strings.
    */
   static public final int DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE = 16;
   /**
    * Rectangle that requires 2 AM modifier values and 1 AN value.";
    * See ModifiersTG.java for modifier descriptions and constant key strings.
    */
   static public final int DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE = 17;
   /**
    * Requires 2 AM values and 2 AN values per sector.  
    * The first sector can have just one AM value although it is recommended 
    * to always use 2 values for each sector.  X values are not required
    * as our rendering is only 2D for the Sector Range Fan symbol.
    * See ModifiersTG.java for modifier descriptions and constant key strings.
    */
   static public final int DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE = 18;
   /**
    *  Requires at least 1 distance/AM value"
    *  See ModifiersTG.java for modifier descriptions and constant key strings.
    */
   static public final int DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE = 19;
   /**
    * Requires 1 AM value.
    * See ModifiersTG.java for modifier descriptions and constant key strings.
    */
   static public final int DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE = 20;
   
   /**
    * 3D airspace, not a milstd graphic.
    */
   static public final int DRAW_CATEGORY_3D_AIRSPACE = 40;
   
   /**
    * UNKNOWN.
    */
   static public final int DRAW_CATEGORY_UNKNOWN = 99;

   public SymbolDef(String basicSymbolID, String description, int drawCategory, String hierarchy, int minPoints, int maxPoints, String modifiers, String fullPath)
   {
	   _strBasicSymbolId = basicSymbolID;
	   _strDescription = description;
	   _intDrawCategory = drawCategory;
	   _strHierarchy = hierarchy;
	   _intMinPoints = minPoints;  
	   _intMaxPoints = maxPoints;
	   _strModifiers = modifiers;
	   _strFullPath = fullPath;
	      
   }



   /**
    * The basic 15 character basic symbol Id.
    */

   public String getBasicSymbolId()
   {
           return _strBasicSymbolId;
   }

   /**
    * The description of this tactical graphic.  Typically the name of the tactical graphic in MIL-STD-2525B.
    */
   public String getDescription()
   {
           return _strDescription;
   }



   /**
    * How does this draw? (autoshape, superautoshape, polygon)
    * 
    */
   public int getDrawCategory()
   {
           return _intDrawCategory;
   }

   
   /**
    * Defines the minimum points fields.
    */
   public int getMinPoints()
   {
           return _intMinPoints;
   }

   /**
    * Defines the maximum points fields.
    */
   public int getMaxPoints()
   {
           return _intMaxPoints;
   }

   public Boolean isMultiPoint()
   {
       char codingScheme = _strBasicSymbolId.charAt(0);
       Boolean returnVal = false;
       if (codingScheme == 'G' || codingScheme == 'W') 
       {

           if(_intMaxPoints > 1)
           {
               returnVal = true;
           }
           else
           {
               switch(_intDrawCategory)
               {
                   case SymbolDef.DRAW_CATEGORY_RECTANGULAR_PARAMETERED_AUTOSHAPE:
                   case SymbolDef.DRAW_CATEGORY_SECTOR_PARAMETERED_AUTOSHAPE:
                   case SymbolDef.DRAW_CATEGORY_TWO_POINT_RECT_PARAMETERED_AUTOSHAPE: 
                   case SymbolDef.DRAW_CATEGORY_CIRCULAR_PARAMETERED_AUTOSHAPE:
                   case SymbolDef.DRAW_CATEGORY_CIRCULAR_RANGEFAN_AUTOSHAPE:
                   case SymbolDef.DRAW_CATEGORY_ROUTE:
                       returnVal = true;
                       break;
                   default:
                       returnVal = false;
               }
           }
           return returnVal;
       } 
       else if(_strBasicSymbolId.startsWith("BS_") || _strBasicSymbolId.startsWith("BBS_") || _strBasicSymbolId.startsWith("PBS_"))
       {
           return true;
       }
       else 
       {
           return false;
       }
   }
   

   /**
    *
    */
   public String getModifiers()
   {
           return _strModifiers;
   }

   /**
    * Defines where the symbol goes in the ms2525 hierarchy.
    * 2.X.etc...
    */
   public String getHierarchy()
   {
           return _strHierarchy;
   }

    /**
    * Defines where the symbol goes in the ms2525 hierarchy.
     * TacticalGraphics/Areas/stuff...
    */
   public String getFullPath()
   {
           return _strFullPath;
   }

}
