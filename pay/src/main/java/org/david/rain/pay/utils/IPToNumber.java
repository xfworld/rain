package org.david.rain.pay.utils;

/**
 * Created by IntelliJ IDEA.
 * User: niuchunwang
 * Date: 2010-11-01
 * Time: 14:29:00
 * IP转换成数字和数字转换成IP
 */
public class IPToNumber {

    //将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理
    public static long ipToLong(String strIp) {
        if (null == strIp || strIp.indexOf(".") == -1) {
            return 0;
        }
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    //将十进制整数形式转换成127.0.0.1形式的ip地址
    public static String longToIP(long longIp) {
        if (longIp == 0) {
            return "Hidden";
        }
        StringBuffer sb = new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        //将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        //将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    public static void main(String[] args) {
        String ipStr = "127.0.0.1";
        long longIp = IPToNumber.ipToLong(ipStr);
        System.out.println("192.168.0.1 的整数形式为：" + longIp);
        System.out.println("整数" + "" + "转化成字符串IP地址："
                + IPToNumber.longToIP(1941320278L));
        //ip地址转化成二进制形式输出
        System.out.println("192.168.0.1 的二进制形式为：" + Long.toBinaryString(longIp));

    }

	/*=========研发登录专用====*/
	public static int bswap(int i)
	{
		int b0 = i & 0xff;
		int b1 = (i >> 8) & 0xff;
		int b2 = (i >> 16) & 0xff;
		int b3 = (i >> 24) & 0xff;
		return 0xff000000 & ((b0) << 24) | 0xff0000 & ((b1) << 16) | 0xff00 & ((b2) << 8) | 0xff & b3;
	}

	public static String getIP(int ip)
	{
		int _ip = bswap(ip);
		try
		{
			String r = "" + ((_ip >> 24) & 0xFF) + "." + ((_ip >> 16) & 0xFF) + "." + ((_ip >> 8) & 0xFF) + "." + (_ip & 0xFF);
			return r;
		} catch (Exception ex)
		{
		}
		return "";
	}

}

