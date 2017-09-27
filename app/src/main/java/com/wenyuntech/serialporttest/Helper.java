package com.wenyuntech.serialporttest;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/3/8 0008.
 *
 */
public class Helper {
    final static String TAG = "Helper";
    static String hexString = "0123456789ABCDEF";
    public NumberFormat nf = NumberFormat.getInstance();

    public static DecimalFormat df2 = new DecimalFormat("#.00");
    public static DecimalFormat df3 = new DecimalFormat("#.000");

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf_nointerval = new SimpleDateFormat("yyyyMMddHHmmss");
    final static String head_zip = "504b0304";

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] objectToByte(Object obj) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);

            oo.writeObject(obj);

            bo.close();
            oo.close();

            return bo.toByteArray();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String SHA(String decript) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //TODO : update function name
    public static byte[] addrToBytes(String addrstr) {
        if (addrstr.length() != 14) {
            return null;
        }
        char[] address = addrstr.toUpperCase().toCharArray();

        byte[] addrBytes = new byte[7];
        for (int i = 0; i < address.length; i = i + 2) {
            addrBytes[i / 2] = (byte) (hexString.indexOf(address[i]) * 16 +
                    hexString.indexOf(address[i + 1]));
        }
        return addrBytes;
    }

    public static String getAddress(byte[] address) {
        String str = "";
        for (int i = 0; i < address.length; i++) {
            if (i == (address.length - 1)) {
                str += toHex(address[i]);
            }else {
                str += toHex(address[i]);
            }
        }
        return str;
    }

    public static String getHex(byte[] data) {
        String result = "";
        for (byte b : data) {
            String str = Integer.toHexString(b & 0xff);
            if (str.length() == 1) {
                str = "0" + str;
            }
            result += str;
        }
        return result;
    }

    public static String toHex(byte b) {
        String result = Integer.toHexString(b & 0xFF);
        if (result.length() == 1) {
            result = '0' + result;
        }
        return result;
    }

    public static long getTime(String ts) {
        try {

            return sdf.parse(ts).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Date getDateByString(String ts) {
        try {
            if (ts.equals("")) {
                return null;
            }

            return sdf.parse(ts);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDateByDate(Date date) {
        return sdf.format(date);
    }

    public static String getJiaJieNengCJTAddress(String address) {
        String[] strArr = new String[7];
        int i = 0;
        do {
            strArr[i/2] = (address.substring(i, i + 2));
            i += 2;
        } while(i < address.length());

        return re(strArr, strArr.length);
    }

    public static String getTianGangCJTAddress(String address) {
        String[] strArr = new String[7];
        int i = 0;
        do {
            strArr[i/2] = (address.substring(i, i + 2));
            i += 2;
        } while(i < address.length());

        return re(new String[]{strArr[0], strArr[1], strArr[2], strArr[3]}, 4) + strArr[4] + strArr[5] + strArr[6];
    }

    private static String re(String[] str, int count) {
        if (count == 0)
            return "";

        return str[count-1] + re(str, count-1);
    }

    public static String getTianGangCJTAddressByByte(byte[] address) {
        byte[] newbytes = new byte[7];
        newbytes[0] = address[3];
        newbytes[1] = address[2];
        newbytes[2] = address[1];
        newbytes[3] = address[0];
        newbytes[4] = address[4];
        newbytes[5] = address[5];
        newbytes[6] = address[6];
        return getHex(newbytes);
    }

    public void parityArray(byte[] bytes) {
        for (byte it: bytes) {
            Log.i(TAG, "IDPPacket parityArray" + ((it >> 7) & 0x1) + "-" +
                    ((it >> 6) & 0x1) + "-" +
                    ((it >> 5) & 0x1) + "-" +
                    ((it >> 4) & 0x1) + "-" +
                    ((it >> 3) & 0x1) + "-" +
                    ((it >> 2) & 0x1) + "-" +
                    ((it >> 1) & 0x1) + "-" +
                    (it & 0x1));
        }
    }

    public static boolean isToDay(String oldTs, String ts) {
        String[] tsArray = ts.split(" ");
        String[] oldTsArray = oldTs.split(" ");

        String[] toDayArray = sdf.format(System.currentTimeMillis()).split(" ");

        return toDayArray[0].equals(tsArray[0]) && toDayArray[0].equals(oldTsArray[0]);
    }

    public static boolean isSameDay(String checkTs, String currTs) {
        String[] currArray = currTs.split(" ");
        String[] checkArray = checkTs.split(" ");
        //String[] toDayArray = sdf.format(System.currentTimeMillis()).split(" ");
        return currArray[0].equals(checkArray[0]);
    }

    public static boolean isToMonth(String oldTs, String ts) {
        String[] tsYMD = ts.split(" ")[0].split("-");

        String[] oldTsYMD = oldTs.split(" ")[0].split("-");

        String[] toDayYMD = sdf.format(System.currentTimeMillis()).split(" ")[0].split("-");

        return toDayYMD[0].equals(tsYMD[0]) && toDayYMD[1].equals(tsYMD[1]) &&
                toDayYMD[0].equals(oldTsYMD[0]) && toDayYMD[1].equals(oldTsYMD[1]);
    }

    public static boolean isSameMonth(String checkTs, String currts) {
        String[] currYMD = currts.split(" ")[0].split("-");
        String[] checkYMD = checkTs.split(" ")[0].split("-");
        return currYMD[0].equals(checkYMD[0]) && currYMD[1].equals(checkYMD[1]);
    }


    public static boolean isToQuarter(String oldTs, String ts) {
        int[] q1 = {1,2,3};
        int[] q2 = {4,5,6};
        int[] q3 = {7,8,9};
        int[] q4 = {10,11,12};
        int[][] quarter = {q1,q2,q3,q4};

        int[] qts = null;
        int[] qtd = null;
        int[] qots = null;

        String[] tsYMD = ts.split(" ")[0].split("-");

        for (int[] q: quarter) {
            for (int i:q) {
                if (Integer.parseInt(tsYMD[1]) == i) {
                    qts = q;
                    break;
                }
            }
        }

        String[] otsYMD = oldTs.split(" ")[0].split("-");
        for (int[] q: quarter) {
            for (int i:q) {
                if (Integer.parseInt(otsYMD[1]) == i) {
                    qots = q;
                    break;
                }
            }
        }

        String[] toDayYMD = sdf.format(System.currentTimeMillis()).split(" ")[0].split("-");

        for (int[] q: quarter) {
            for (int i:q) {
                if (Integer.parseInt(toDayYMD[1]) == i) {
                    qtd = q;
                    break;
                }
            }
        }

        return qts == qtd && qtd == qots;
    }

    public static String getHashCode(String str) {
        String currTime = System.currentTimeMillis() + "";

        String sha = SHA(str);

        return currTime.substring(currTime.length() - 5, currTime.length()) + sha.substring(0, 9);
    }

    public static boolean isIP(String addr) {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]"
                + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        return mat.find();
    }

    public static double getDouble(byte[] raw, int star, int end, double base) {
        return getDouble(raw, star, end, base, 10);
    }

    public static double getDouble(byte[] raw, int star, int end, double base, double mult) {
        if (star > end) {
            return 0;
        }
        return (raw[star] & 0x0f) * base + (raw[star] >> 4 & 0x0f) * (base*mult) +
                getDouble(raw, ++star, end, base*mult*mult, mult);
        /*0x04, 0x15, 0x56, 0x77 to 775615.04   or (405165.77)
                (raw[6] >> 4 & 0x0f) * 100000 + (raw[6] & 0x0f) * 10000 +
                (raw[5] >> 4 & 0x0f) * 1000 + (raw[5] & 0x0f) * 100 +
                (raw[4] >> 4 & 0x0f) * 10 + (raw[4] & 0x0f) +
                (raw[3] >> 4 & 0x0f) * 0.1 + (raw[3] & 0x0f) * 0.01*/
    }

    public static double getDoubleByPositive(byte[] raw, int star, int end, double base) {
        return getDoubleByPositive(raw, star, end, base, 10);
    }

    public static double getDoubleByPositive(byte[] raw, int star, int end, double base, double mult) {
        if (star > end) {
            return 0;
        }
        return (raw[end] & 0x0f) * base + (raw[end] >> 4 & 0x0f) * (base*mult) +
                getDouble(raw, star, --end, base*mult*mult, mult);
        /*0x04, 0x15, 0x56, 0x77 to 4155677.0   or (776551.4)
                ((((raw[3] & 0xf0) >> 4) * 10 + (raw[3] & 0x0f)) * 1000000 +
                (((raw[4] & 0xf0) >> 4) * 10 + (raw[4] & 0x0f)) * 10000 +
                (((raw[5] & 0xf0) >> 4) * 10 + (raw[5] & 0x0f)) * 100 +
                (((raw[6] & 0xf0) >> 4) * 10 + (raw[6] & 0x0f)) * 1.00)*/
    }

    public static String getTimesTamp(byte[] raw, int start) {
        //yyyy-MM-dd HH:mm:ss
        return (raw[start+6] >> 4 & 0x0f) + "" + (raw[start+6] & 0x0f) + "" +
                (raw[start+5] >> 4 & 0x0f) + "" + (raw[start+5] & 0x0f) + "-" +
                (raw[start+4] >> 4 & 0x0f) + "" + (raw[start+4] & 0x0f) + "-" +
                (raw[start+3] >> 4 & 0x0f) + "" + (raw[start+3] & 0x0f) + " " +
                (raw[start+2] >> 4 & 0x0f) + "" + (raw[start+2] & 0x0f) + ":" +
                (raw[start+1] >> 4 & 0x0f) + "" + (raw[start+1] & 0x0f) + ":" +
                (raw[start] >> 4 & 0x0f) + "" + (raw[start] & 0x0f);
        /*return (raw[43] >> 4 & 0x0f) + "" + (raw[43] & 0x0f) + "" +
                (raw[42] >> 4 & 0x0f) + "" + (raw[42] & 0x0f) + "-" +
                (raw[41] >> 4 & 0x0f) + "" + (raw[41] & 0x0f) + "-" +
                (raw[40] >> 4 & 0x0f) + "" + (raw[40] & 0x0f) + " " +
                (raw[39] >> 4 & 0x0f) + "" + (raw[39] & 0x0f) + ":" +
                (raw[38] >> 4 & 0x0f) + "" + (raw[38] & 0x0f) + ":" +
                (raw[37] >> 4 & 0x0f) + "" + (raw[37] & 0x0f);*/
    }

    public static boolean isZIP(File file) {
        if (file.exists()) {
            try {
                FileInputStream is = new FileInputStream(file.getAbsolutePath());
                byte head[] = new byte[4];
                is.read(head, 0, head.length);
                String fileHead = getHex(head);
                Log.i(TAG, "filehead = " + fileHead);
                if (fileHead.toLowerCase().equals(head_zip)) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean isAPK(File file) {
        if (file.exists()) {
            String path = file.getAbsolutePath();
            String[] pathArray = path.split("\\.");
            Log.i(TAG, "isAPK(" + pathArray[pathArray.length-1]);
            if ("apk".equals(pathArray[pathArray.length-1])) {
                return true;
            }
        }
        return false;
    }
}
