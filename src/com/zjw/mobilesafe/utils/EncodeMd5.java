package com.zjw.mobilesafe.utils;

import java.security.MessageDigest;

public class EncodeMd5 {
	public static String md5(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] str = digest.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : str) {
				int item = b & 0xff;//╪сян
				String tmp = Integer.toHexString(item);

				if (tmp.length() == 1) {
					sb.append("0");
				}
				sb.append(tmp);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
