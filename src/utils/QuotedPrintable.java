package utils;

import java.io.UnsupportedEncodingException;



	/**
	 * A class containing static methods to perform decoding from <b>quoted
	 * printable</b> content transfer encoding and to encode into
	 */
	public class QuotedPrintable {

	    private final static byte TAB 	= 0x09;     // /t
	    private final static byte LF 		= 0x0A;     // /n
	    private final static byte CR 		= 0x0D;     // /r
//	    private final static byte SPACE 	= 0x20;		// ' '
	    private final static byte EQUALS 	= 0x3D;		// '='
	    
	    private final static byte LIT_START = 0x21;
	    private final static byte LIT_END	= 0x7e;
	    
	    private final static int MAX_LINE_LENGTH = 76; 
	    
	    private static int mCurrentLineLength = 0;

	    public static int decode(byte [] qp) {
	        int qplen = qp.length;
	        int retlen = 0;

	        for (int i=0; i < qplen; i++) {
	            // Handle encoded chars
	            if (qp[i] == '=') {
	                if (qplen - i > 2) {
	                    // The sequence can be complete, check it
	                    if (qp[i+1] == CR && qp[i+2] == LF) {
	                        // soft line break, ignore it
	                        i += 2;
	                        continue;

	                    } else if (isHexDigit(qp[i+1]) && isHexDigit(qp[i+2]) ) {
	                        qp[retlen++]=(byte)(getHexValue(qp[i+1])*16
	                                       + getHexValue(qp[i+2]));

	                        i += 2;
	                        continue;

	                    } else {
	                    }
	                }
	            }

	            if( (qp[i] >= 0x20 && qp[i] <= 0x7f) ||
	                 qp[i] == TAB || qp[i] == CR || qp[i] == LF) {
	                qp[retlen++] = qp[i];
	            }
	        }

	        return retlen;
	    }

	    private static boolean isHexDigit(byte b) {
	        return ( (b>=0x30 && b<=0x39) || (b>=0x41&&b<=0x46) );
	    }

	    private static byte getHexValue(byte b) {
	        return (byte)Character.digit((char)b, 16);
	    }

	    /**
	     * 
	     * @param qp Byte array to decode
	     * @param enc The character encoding of the returned string
	     * @return The decoded string.
	     */
	    public static String decode(byte[] qp, String enc) {
	        int len=decode(qp);
	        try {
	            return new String(qp, 0, len, enc);
	        } catch (UnsupportedEncodingException e) {
	            return new String(qp, 0, len);
	        }
	    }

	    /**
		 * A method to encode data in quoted printable
		 * 
		 * @param content
		 *            The string to be encoded
		 * @param enc
		 *            The character encoding of the content string       
		 * @return The encoded string. If the content is null, return null. 
		 */
		public static String encode(String content, String enc) {
			if (content == null)
				return null;
			
			byte[] str = null;
			try {
				str = content.getBytes(enc);
			} catch (UnsupportedEncodingException e) {
				str = content.getBytes();
			}
			return encode(str);
		}
	    /**
	     * A method to encode data in quoted printable
	     *
	     * @param content
	     *            The byte array of the string to be encoded
	     * @return The encoded string. If the content is null, return null.
	     */
	    public static String encode(byte[] content) {
	    	if (content == null)
	    		return null;
	 		
			StringBuilder out = new StringBuilder();
			
			mCurrentLineLength = 0;
			int requiredLength = 0;
			
			for (int index = 0; index < content.length; index++) {
				byte c = content[index];
				
				if (c >= LIT_START && c <= LIT_END && c != EQUALS) {
					requiredLength = 1;
					checkLineLength(requiredLength, out);
					out.append((char)c);
				} else {
					requiredLength = 3;
					checkLineLength(requiredLength, out);
					out.append('=');
					out.append(String.format("%02X", c));
				}
			}
			return out.toString();
	    }
	    
	    private static void checkLineLength(int required, StringBuilder out) {
	    	if (required + mCurrentLineLength > MAX_LINE_LENGTH - 1) {
				out.append("=/r/n");
				mCurrentLineLength = required;
			}  else 
				mCurrentLineLength += required;
	    }
//	    public static void main(String [] args) {
//	    	String str= "<html><head><title>=E6=AC=A2=E8=BF=8E=E5=8F=82=E5=8A=A0=E4=B8=AD=E5=9B=BD=" + 
//	    			"=E5=A4=A7=E5=AD=A6MOOC=E3=80=8A=E8=AE=A1=E7=AE=97=E6=9C=BA=E7=BD=91=E7=BB=" + 
//	    			"=9C=E3=80=8B=E8=AF=BE=E7=A8=8B</title></head><body><div style=3D\"width:600p=" + 
//	    			"x;\">    <div style=3D\"border: 1px solid #DDD;\">        <img src=3D\"http://i=" + 
//	    			"mg2.ph.126.net/qwBohcsdZ-lcaIoa3N2y4w=3D=3D/6608207819446101140.png\" width=" + 
//	    			"=3D\"600\" height=3D\"55\"/>        <div style=3D\"min-height:300px;padding:20px=" + 
//	    			" 18px 60px;\">            <p style=3D\"font-size:16px;font-family:'microsoft =" + 
//	    			"yahei';margin: 0;\">2016141463048=E7=8E=8B=E7=A3=8A=E7=8C=9Bmooc425616381883=" + 
//	    			"25521</p>        =09<div><div style=3D\"min-height:100px;padding:20px 18px 6=" + 
//	    			"0px;\">=09=E6=AC=A2=E8=BF=8E=E4=BD=A0=E5=8F=82=E5=8A=A0=E4=B8=AD=E5=9B=BD=E5=" + 
//	    			"=A4=A7=E5=AD=A6MOOC=E3=80=8A=E8=AE=A1=E7=AE=97=E6=9C=BA=E7=BD=91=E7=BB=9C=" + 
//	    			"=E3=80=8B=E8=AF=BE=E7=A8=8B=E3=80=82<br>=09=E4=BD=A0=E6=98=AF=E5=90=A6=E5=" + 
//	    			"=AF=B9=E8=AF=BE=E7=A8=8B=E5=86=85=E5=AE=B9=E6=9C=89=E6=89=80=E7=96=91=E6=83=" + 
//	    			"=91=EF=BC=9F=E4=BD=A0=E6=98=AF=E5=90=A6=E5=AF=B9=E8=AF=BE=E7=A8=8B=E5=AE=89=" + 
//	    			"=E6=8E=92=E4=B8=8D=E7=94=9A=E4=BA=86=E8=A7=A3=EF=BC=9F=E4=BD=A0=E6=98=AF=E5=" + 
//	    			"=90=A6=E5=AF=B9=E8=AF=81=E4=B9=A6=E8=A6=81=E6=B1=82=E4=B8=80=E7=9F=A5=E5=8D=" + 
//	    			"=8A=E8=A7=A3=EF=BC=9F<br>=09=E8=AF=B7=E6=B5=8F=E8=A7=88=E8=AF=BE=E7=A8=8B=" + 
//	    			"=E4=BB=8B=E7=BB=8D=E9=A1=B5=E9=9D=A2=EF=BC=8C=E6=88=96=E8=80=85=E5=9C=A8=E5=" + 
//	    			"=BC=80=E8=AF=BE=E5=90=8E=E8=BF=9B=E5=85=A5=E5=AD=A6=E4=B9=A0=E9=A1=B5=E9=9D=" + 
//	    			"=A2=E8=BF=9B=E8=A1=8C=E6=9F=A5=E7=9C=8B=EF=BC=8C=E5=B9=B6=E5=8F=8A=E6=97=B6=" + 
//	    			"=E5=85=B3=E6=B3=A8=E5=85=AC=E5=91=8A=E6=9B=B4=E6=96=B0=E3=80=82=E5=A6=82=E6=" + 
//	    			"=9C=89=E4=BB=BB=E4=BD=95=E9=97=AE=E9=A2=98=EF=BC=8C=E9=83=BD=E5=8F=AF=E4=BB=" + 
//	    			"=A5=E5=88=B0=E2=80=9C=E8=BF=9B=E5=85=A5=E5=AD=A6=E4=B9=A0-=E8=AE=A8=E8=AE=" + 
//	    			"=BA=E5=8C=BA=E2=80=9D=E6=8F=90=E9=97=AE=EF=BC=8C=E6=88=91=E4=BB=AC=E7=9A=84=" + 
//	    			"=E8=AF=BE=E7=A8=8B=E5=9B=A2=E9=98=9F=E6=88=90=E5=91=98=E4=BC=9A=E5=B0=BD=E5=" + 
//	    			"=BF=AB=E7=BB=99=E4=BA=88=E5=9B=9E=E5=A4=8D=E3=80=82</div><div style=3D\"padd=" + 
//	    			"ing:20px 18px 60px 20px;font-family:microsoft yahei;font-size:14px;text-ali=" + 
//	    			"gn:right;\">=E3=80=8A=E8=AE=A1=E7=AE=97=E6=9C=BA=E7=BD=91=E7=BB=9C=E3=80=8B=" + 
//	    			"=E8=AF=BE=E7=A8=8B=E5=9B=A2=E9=98=9F</div></div>        </div>        <div =" + 
//	    			"style=3D\"background-color:#f6f6f6;height:110px;width:568px;padding:16px 16p=" + 
//	    			"x 30px;border-top:1px solid #eeeeee;\">            <h3 style=3D\"font-family:=" + 
//	    			"'microsoft yahei';font-size:15px;color:#666666;margin:0 0 10px;\">=E9=82=AE=" + 
//	    			"=E4=BB=B6=E6=9D=A5=E8=87=AA=EF=BC=9A</h3>            <div style=3D\"backgrou=" + 
//	    			"nd-color:#ffffff;width:559px;height:70px;padding:3px;\">                <a h=" + 
//	    			"ref=3D\"http://www.icourse163.org/learn/SCUT-1002700002\" target=3D\"_blank\" s=" + 
//	    			"tyle=3D\"display:block;width:125px;heigt:70px;float:left;margin:0 6px 0 0;\">=" + 
//	    			"<img src=3D\"http://edu-image.nosdn.127.net/C30091FF95D91CC6745E8A885641E603=" + 
//	    			"..png?imageView&thumbnail=3D510y288&quality=3D100\" width=3D125 height=3D70><=" + 
//	    			"/a>                <h4 style=3D\"font-family:'microsoft yahei';font-size:14p=" + 
//	    			"x;color:#333333;height:40px;width:415px;float:left;margin:0;\">=E8=AE=A1=E7=" + 
//	    			"=AE=97=E6=9C=BA=E7=BD=91=E7=BB=9C</h4>                <p style=3D\"float:lef=" + 
//	    			"t;width:415px;height: 21px;font-size:12px;\">                    <span style=" + 
//	    			"=3D\"color:#666666;float: left;width: 346px;display: block;overflow: hidden;=" + 
//	    			"height:20px;text-overflow: ellipsis;white-space: nowrap;\">=E5=8D=8E=E5=8D=" + 
//	    			"=97=E7=90=86=E5=B7=A5=E5=A4=A7=E5=AD=A6 &nbsp; &nbsp; &nbsp; &nbsp;=E8=A2=" + 
//	    			"=81=E5=8D=8E=E3=80=81=E5=BC=A0=E5=87=8C=E3=80=81=E6=9D=9C=E5=B9=BF=E9=BE=99=" + 
//	    			"</span>                    <a target=3D\"_blank\" href=3D\"http://www.icourse1=" + 
//	    			"63.org/learn/SCUT-1002700002\" style=3D\"color:#13a654;float:right;text-decor=" + 
//	    			"ation:none;\">=E8=BF=9B=E5=85=A5=E5=AD=A6=E4=B9=A0>></a>                </p>=" + 
//	    			"            </div>        </div>    </div>    <div style=3D\"width:600px;hei=" + 
//	    			"ght:30px;line-height:30px;text-align:center;margin:10px 0 0;\">=E8=AF=B7=E4=" + 
//	    			"=B8=8D=E8=A6=81=E5=9B=9E=E5=A4=8D=E8=AF=A5=E9=82=AE=E4=BB=B6=EF=BC=8C=E6=82=" + 
//	    			"=A8=E6=94=B6=E5=88=B0=E8=BF=99=E5=B0=81=E9=82=AE=E4=BB=B6=EF=BC=8C=E6=98=AF=" + 
//	    			"=E5=9B=A0=E4=B8=BA=E5=B7=B2=E7=BB=8F=E6=B3=A8=E5=86=8C=E4=BA=86=E4=B8=AD=E5=" + 
//	    			"=9B=BD=E5=A4=A7=E5=AD=A6MOOC=E3=80=82</div>=09<div style=3D\"width:600px;hei=" + 
//	    			"ght:30px;line-height:30px;text-align:center;\">=09=09<a target=3D\"_blank\" st=" + 
//	    			"yle=3D\"margin-right:10px\" href=3D\"http://www.icourse163.org/about/aboutus.h=" + 
//	    			"tm?utm_source=3Dmoocemail&utm_medium=3Demail&utm_campaign=3Dbusiness#/about=" + 
//	    			"\">=E4=B8=AD=E5=9B=BD=E5=A4=A7=E5=AD=A6MOOC</a>=09=09<a target=3D\"_blank\" st=" + 
//	    			"yle=3D\"margin-right:10px\" href=3D\"http://www.icourse163.org/help/help.htm?u=" + 
//	    			"tm_source=3Dmoocemail&utm_medium=3Demail&utm_campaign=3Dbusiness#/hf\">=E5=" + 
//	    			"=B8=B8=E8=A7=81=E9=97=AE=E9=A2=98</a>=09=09<a target=3D\"_blank\" href=3D\"htt=" + 
//	    			"p://www.icourse163.org/user/setting/accountSetting.htm?type=3D3\">=E9=80=80=" + 
//	    			"=E8=AE=A2=E9=82=AE=E4=BB=B6</a>=09=09<a target=3D\"_blank\" href=3D\"http://ww=" + 
//	    			"w.icourse163.org/appDownload.htm?from=3Dmoocemail&utm_source=3Dmoocemail&ut=" + 
//	    			"m_medium=3Demail&utm_campaign=3Dbusiness\">=E7=A7=BB=E5=8A=A8=E5=AE=A2=E6=88=" + 
//	    			"=B7=E7=AB=AF</a>=09</div></div></body></html>";
//	    	System.out.println(decode(str.getBytes(), "utf-8"));
//	    }
	}
