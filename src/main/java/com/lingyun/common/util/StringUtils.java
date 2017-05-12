package com.lingyun.common.util;


import java.io.UnsupportedEncodingException;
import java.util.Random;

public class StringUtils {
	
	private StringUtils(){}
    public static String firstUpperCase(String word) {
        if (isBlank(word)) return null;
        if (word.length()==1) return word.substring(0, 1).toUpperCase();
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
    public static String firstLowerCase(String word) {
        if (word==null) return null;
        if (word.length()==1) return word.substring(0, 1).toLowerCase();
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }


    /**
     * 短字符串在长字符串中出现的次数
     * @param longString
     * @param shortString
     * @return
     */
    public static int occurrenceNumberInString (String longString, String shortString){
        longString = " "+ longString;
        if (longString.length()<shortString.length()) return -1;
        if (longString.indexOf(shortString)<0) return -1;
        if(longString.endsWith(shortString)){
            return longString.split(shortString).length;
        }else{
            return longString.split(shortString).length - 1;
        }
    }
	public static String join(Iterable<?> objects, String sep) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(Object object: objects){
			if (i > 0) {
				builder.append(sep);
			}
			builder.append(object);
			i++;
		}
		return builder.toString();
	}

	public static String insertFileNameSuffixToUrl(String url, String suffix){
		int index = url.lastIndexOf('.');
		if(index > 0){
			StringBuilder sb = new StringBuilder(url);
			sb.insert(index, suffix);
			return sb.toString();
		}
		return url;
	}
	
    /**
     * 数字金额大写转换
     */
    public static String chineseAmountUppercase(double amount){
        double amountUppercase = amount;
        String fraction[] = {"角", "分"};
        String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String unit[][] = {{"元", "万", "亿"},
                     {"", "拾", "佰", "仟"}};
 
        String head = amountUppercase < 0? "负": "";
        amountUppercase = Math.abs(amountUppercase);
         
        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int)(Math.floor(amountUppercase * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if(s.length()<1){
            s = "整";   
        }
        int integerPart = (int)Math.floor(amountUppercase);
 
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p ="";
            for (int j = 0; j < unit[1].length && amountUppercase > 0; j++) {
                p = digit[integerPart%10]+unit[1][j] + p;
                integerPart = integerPart/10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    //---------------------- 以下方法来自apache common lang 2.6 -------------------------
    
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    /**
     * 生成制定长度随机字符
     * @param length
     * @return
     */
    public static String generateRandomString(int length){
        char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1' };
        Random random = new Random();
        if (length > 0) {
            int index = 0;
            char[] temp = new char[length];
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];//取后面六位，记得对应的二进制是以补码形式存在的。
                num >>= 6;//63的二进制为:111111
                // 为什么要右移6位？因为数组里面一共有64个有效字符。为什么要除5取余？因为一个int型要用4个字节表示，也就是32位。
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        }
        else if (length == 0) {
            return "";
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
//        String membershipPath="/3fdd/xx/xxx/xxxx/aa/xxxxxx/bb";
//        String membershipPath="/aa/xxxxxx/bb/jikjk";
//        String userId="aa";
//        String lowerOrHigherId="bb";
//        String longString=membershipPath.substring(membershipPath.indexOf("/"+userId)+userId.length()+1,membershipPath.indexOf(lowerOrHigherId));
//        String shortString="/";
//        System.out.println(longString);
//        int occ=StringUtils.occurrenceNumberInString(longString,shortString);
//        System.out.println(occ*-1);

        String detail_data1 = new String("59151fc7a7e9bc12003b7e721494556695568^185246042@qq.com^谢宇星^0.14^用户提现".getBytes(),"UTF-8");
        String detail_data2 = new String("59151fc7a7e9bc12003b7e721494556695568^185246042@qq.com^谢宇星^0.14^用户提现".getBytes("ISO-8859-1"),"UTF-8");
        String detail_data3 = new String("59151fc7a7e9bc12003b7e721494556695568^185246042@qq.com^谢宇星^0.14^用户提现".getBytes("UTF-8"),"ISO-8859-1");
        System.out.println(detail_data1);
        System.out.println(detail_data2);
        System.out.println(detail_data3);
    }
}
