package armyc2.c2sd.renderer.utilities;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;


public class SymbolDimensions {
	
	/**
	 * 
	 * @param charIndex
	 * @param fontSize
	 * @return float[] [offsetX,offsetY,w,h]
	 */
        public static float[] getUnitBounds(int charIndex, float fontSize)
		{
            
			int index = charIndex;
			RectF rect = null;
			float[] dimensions = {0f,0f,0f,0f};
                
			
			switch(index - 57000)
			{
				case 800:
				case 801:
				case 802:
					//rect = RectUtilities.makeRectF(0,0,60.8f,60.8f);//unknown
					dimensions[2]=60.8f;
					dimensions[3]=60.8f;
					break;
				case 803:
				case 804:
				case 805:
                    rect = RectUtilities.makeRectF(0,0,65,47);//friendly
					dimensions[2]=65f;
					dimensions[3]=47f;
					break;
				case 806:
				case 807:
				case 808:
                    //rect = RectUtilities.makeRectF(0,0,62.5f,62.5f);//hostile
					dimensions[2]=62.5f;
					dimensions[3]=62.5f;
					break;
				case 809:
				case 810:
				case 811:
                    //rect = RectUtilities.makeRectF(0,0,50.05f,50.05f);//neutral
					dimensions[2]=50.05f;
					dimensions[3]=50.05f;
					break;
				case 812:
				case 813:
				case 814:
                    //rect = RectUtilities.makeRectF(0,0,54.75f,54.75f);//friendly equipment
					dimensions[2]=54.75f;
					dimensions[3]=54.75f;
					break;
				case 816:
				case 817:
				case 818:
				case 840:
				case 841:
				case 842:
                    //rect = RectUtilities.makeRectF(0,16,50.3f,53);//air & space hostile
                    dimensions[1]=8f;
                    dimensions[2]=50.3f;
					dimensions[3]=53f;
					break;
				case 819:
				case 820:
				case 821:
				case 843:
				case 844:
				case 845:
                    //rect = RectUtilities.makeRectF(0,11,46.5f,48);//air space friendly
                    dimensions[1]=6f;
                    dimensions[2]=46.5f;
					dimensions[3]=48f;
					break;
				case 822:
				case 823:
				case 824:
				case 846:
				case 847:
				case 848:
                    //rect = RectUtilities.makeRectF(0,11,47,48);//air space neutral
                    dimensions[1]=6f;
                    dimensions[2]=47f;
					dimensions[3]=48f;
					break;
				case 825:
				case 826:
				case 827:
				case 849:
				case 850:
				case 851:
                    //rect = RectUtilities.makeRectF(0,11,64.7f,56);//air space unknown
                    dimensions[1]=6.5f;
                    dimensions[2]=64.7f;
					dimensions[3]=56f;
					break;
				case 828:
				case 829:
				case 830:
                    //rect = RectUtilities.makeRectF(0,-17,50.3f,53);//subsurface hostile
                    dimensions[1]=-8f;
                    dimensions[2]=50.3f;
					dimensions[3]=53f;
					break;
				case 831:
				case 832:
				case 833:
                    //rect = RectUtilities.makeRectF(0,-12,46.6f,48);//subsurface friendly
					dimensions[1]=-6f;
                    dimensions[2]=46.6f;
					dimensions[3]=48f;
					break;
				case 834:
				case 835:
				case 836:
                    //rect = RectUtilities.makeRectF(0,-12,46.5f,48);//subsurface neutral
                    dimensions[1]=-6f;
                    dimensions[2]=46.5f;
					dimensions[3]=48f;
					break;
				case 837:
				case 838:
				case 839:
                    rect = RectUtilities.makeRectF(0,0,64.7f,56f);//subsurface unknown
                    dimensions[1]=-6f;
                    dimensions[2]=64.7f;
					dimensions[3]=56f;
                    //rect = RectUtilities.makeRectF(0,-12,64.7f,56);//subsurface unknown
					break;
				default:
					//rect = RectUtilities.makeRectF(0,0,54,54);
					dimensions[2]=54f;
					dimensions[3]=54f;
					break;
			}
			
			float ratio = 1;
			if(fontSize != 50)
			{
				ratio = fontSize / 50f;
				//I only measured for a font size of 50.  if we get the ratio and multiply the values
				//by it, we in theory should have a correct adjusted rectangle.
				//rect = RectUtilities.makeRectF(0,(rect.top*ratio), (rect.width()*ratio), (rect.height()*ratio));
				dimensions[1] = dimensions[1] * ratio;
				dimensions[2] = dimensions[2] * ratio;
				dimensions[3] = dimensions[3] * ratio;
			}
			
			//return RectUtilities.makeRectFromRectF(rect);
			return dimensions;
		}

        public static Rect getSymbolBounds(String symbolID, int symStd, float fontSize)
		{
        	
			SinglePointLookupInfo spli = SinglePointLookup.getInstance().getSPLookupInfo(symbolID,symStd);
			
			RectF rect = new RectF(0,0,spli.getWidth(), spli.getHeight());
			
			if(fontSize != 60)//adjust boundaries ratio if font size is not at the default setting.
			{
				double ratio = fontSize/60.0;
				
				rect = RectUtilities.makeRectF(0f,0f,(float)(rect.width()*ratio), (float)(rect.height()*ratio));
			}
			
			return new Rect((int)rect.left, (int)rect.top, (int)(rect.right + 0.5), (int)(rect.bottom + 0.5));
		}
		
		/**
		 * 
		 * */
		public static Point getSymbolCenter(String symbolID, RectF bounds)
		{
			String basicID = SymbolUtilities.getBasicSymbolID(symbolID);
            PointF center = new PointF();
			
			if(basicID.equals("G*G*GPUUB-****X")||
				basicID.equals("G*G*GPUUL-****X") ||
				basicID.equals("G*G*GPUUS-****X") ||
				basicID.equals("G*G*GPRI--****X") ||
				basicID.equals("G*G*GPWE--****X") ||
				basicID.equals("G*G*GPWG--****X") ||
				basicID.equals("G*G*GPWM--****X")||
				basicID.equals("G*G*GPP---****X") ||
				basicID.equals("G*G*GPPC--****X") ||
				basicID.equals("G*G*GPPL--****X") ||
				basicID.equals("G*G*GPPP--****X") ||
				basicID.equals("G*G*GPPR--****X") ||
				basicID.equals("G*G*GPPA--****X") ||
				basicID.equals("G*G*APD---****X") ||
				basicID.equals("G*G*OPP---****X") ||
				basicID.substring(0,7).equals("G*M*OAO") ||//antitank obstacles
				basicID.equals("G*M*BCP---****X") ||
				basicID.equals("G*F*PCS---****X") ||
				basicID.equals("G*F*PCB---****X") ||
				basicID.equals("G*F*PCR---****X") ||
				basicID.equals("G*F*PCH---****X") ||
				basicID.equals("G*F*PCL---****X") ||
                basicID.substring(0, 5).equals("G*S*P") ||//combat service suppport/points
				basicID.equals("G*O*ED----****X") ||
				basicID.equals("G*O*EP----****X") ||
				basicID.equals("G*O*EV----****X") ||
				basicID.equals("G*O*SB----****X") ||
				basicID.equals("G*O*SBM---****X") ||
				basicID.equals("G*O*SBN---****X") ||
				basicID.equals("G*O*SS----****X") ||
				basicID.equals("G*G*GPPN--****X") || //entry control point
				basicID.equals("G*S*PX----****X") || //ambulance exchange point
				basicID.equals("G*O*ES----****X") || //emergency distress call
				SymbolUtilities.isNBC(basicID) ||
				SymbolUtilities.isDeconPoint(basicID) ||
				SymbolUtilities.isCheckPoint(basicID))
			{
				//center on bottom middle
				center.x = bounds.width()/2;
				center.y = bounds.height();
			}
			else if(SymbolUtilities.isSonobuoy(basicID))
			{
				//bottom third
				center.x = bounds.width()/2;
                center.y = (int)((bounds.height() * 0.66));
			}
            else if ((basicID.substring(0, 7).equals("G*G*GPO") && (basicID.charAt(7) == ('-')) == false))//antitank mine w/ handling device
			{
				//upper third
				center.x = bounds.width()/2;
				center.y = (int)((bounds.height() * 0.33));
			}
			else if(basicID.equals("G*M*OMD---****X"))
			{
				//upper third
				center.x = bounds.width()/2;
				center.y = (int)((bounds.height() * 0.28));
			}
            else if (basicID.substring(0, 7).equals("G*G*DPO"))//OBSERVATION POST/OUTPOST
			{
                if (basicID.charAt(7) == ('C'))//combat outpost
				{
					center.x = bounds.width()/2;
					center.y = (int)((bounds.height() * 0.55));
				}
				else//everything else under OBSERVATION POST/OUTPOST
				{
					center.x = bounds.width()/2;
					center.y = (int)((bounds.height() * 0.65));
				}
			}
			else if(basicID.equals("G*G*GPWD--****X")||//drop point
				basicID.equals("G*G*PN----****X") ||//dummy minefield static
				basicID.equals("G*M*OB----****X") ||//booby trap
				basicID.equals("G*M*OME---****X") ||//antitank mine directional
				basicID.equals("G*M*OMW---****X") ||//wide area mines
				basicID.equals("G*M*OMP---****X") ||//anti-personnel mines
				basicID.equals("G*M*OHTL--****X") ||//Aviation/tower/low
				basicID.equals("G*M*OHTH--****X") ||//Aviation/tower/high
				basicID.equals("G*O*HM----****X") ||//
				basicID.equals("G*O*HI----****X") ||//
				basicID.equals("G*O*SM----****X"))
			{
				if(basicID.equals("G*G*GPWD--****X"))//drop point
				{
					center.x = bounds.width()/2;
					center.y = (int)((bounds.height() * 0.87));
				}
				else if(basicID.equals("G*G*PN----****X"))//dummy minefield static
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.69));
				}
				else if(basicID.equals("G*M*OB----****X"))//booby trap
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.79));
				}
				else if(basicID.equals("G*M*OME---****X"))//antitank mine directional
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.77));
				}
				else if(basicID.equals("G*M*OMW---****X"))//wide area mines
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.3));
				}
				else if(basicID.equals("G*M*OMP---****X"))//anti personnel mines
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height()) * 0.64);
				}
				else if(basicID.equals("G*M*OHTL--****X"))//Aviation/tower/low//2525C
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.88));
				}
				else if(basicID.equals("G*M*OHTH--****X"))//Aviation/tower/high//2525C
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.90));
				}
				else if(basicID.equals("G*O*HM----****X"))//sea mine-like
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.65));
				}
				else if(basicID.equals("G*O*HI----****X"))
				{
					center.x = bounds.width()/2;
                    center.y = (int)((bounds.height() * 0.58));
				}
				else if(basicID.equals("G*O*SM----****X"))
				{
					center.x = 0;
                    center.y = (int)((bounds.height() * 0.5));
				}
				
				
			}
			else
			{
				//center on center
				center.x = bounds.width()/2;
				center.y = bounds.height()/2;
			}
			
			return new Point(Math.round(center.x),Math.round(center.y));
		}
    }

