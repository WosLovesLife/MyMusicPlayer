package com.project.myutilslibrary.mp3info;

/**
 * 字节操作类
 * */
public class ByteUtil {

	/**
	 * 正向索引
	 * */
	public static int indexOf(byte[] tag, byte[] src) {
		return indexOf(tag, src, 1);
	}

	/**
	 * 获取第index个的位置<br />
	 * index从1开始
	 * */
	public static int indexOf(byte[] tag, byte[] src, int index) {
		return indexOf(tag, src, 1, src.length);
	}

	/**
	 * 获取第index个的位置<br />
	 * index从1开始
	 * 
	 * */
	public static int indexOf(byte[] tag, byte[] src, int index, int len) {
		if (len > src.length) {
			try {
				throw new Exception("大于总个数");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int size = 0;
		int tagLen = tag.length;
		byte[] tmp = new byte[tagLen];
		for (int j = 0; j < len - tagLen + 1; j++) {
			for (int i = 0; i < tagLen; i++) {
				tmp[i] = src[j + i];
			}
			// 判断是否相等
			for (int i = 0; i < tagLen; i++) {
				if (tmp[i] != tag[i])
					break;
				if (i == tagLen - 1) {
					size++;
					return j;
				}
			}

		}
		return -1;
	}

	/**
	 * 倒序索引<br />
	 * 
	 * */
	public static int lastIndexOf(byte[] tag, byte[] src) {

		return lastIndexOf(tag, src, 1);
	}

	/**
	 * 倒序获取第index个的位置<br />
	 * index从1开始
	 * */
	public static int lastIndexOf(byte[] tag, byte[] src, int index) {
		return lastIndexOf(tag, src, src.length);
	}

	/**
	 * 倒序获取第index个的位置<br />
	 * index从1开始
	 * */
	public static int lastIndexOf(byte[] tag, byte[] src, int index, int len) {
		if (len > src.length) {
			try {
				throw new Exception("大于总个数");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int size = 0;
		int tagLen = tag.length;
		byte[] tmp = new byte[tagLen];
		for (int j = len - tagLen; j >= 0; j--) {
			for (int i = 0; i < tagLen; i++) {
				tmp[i] = src[j + i];

			}
			for (int i = 0; i < tagLen; i++) {
				if (tmp[i] != tag[i])
					break;
				if (i == tagLen - 1) {
					size++;
					return j;
				}
			}

		}
		return -1;
	}

	/**
	 * 统计个数
	 * */
	public static int size(byte[] tag, byte[] src) {
		int size = 0;
		int tagLen = tag.length;
		int srcLen = src.length;
		byte[] tmp = new byte[tagLen];
		for (int j = 0; j < srcLen - tagLen + 1; j++) {
			for (int i = 0; i < tagLen; i++) {
				tmp[i] = src[j + i];
			}
			for (int i = 0; i < tagLen; i++) {
				if (tmp[i] != tag[i])
					break;
				if (i == tagLen - 1) {
					size++;
				}
			}
			// 速度较慢
			// if (Arrays.equals(tmp, tag)) {
			// size++;
			// }
		}
		return size;
	}

	/**
	 * 截取byte[]
	 * */
	public static byte[] cutBytes(int start, int end, byte[] src) {
		if (end <= start || start < 0 || end > src.length) {
			try {
				throw new Exception("参数错误");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte[] tmp = new byte[end - start];
		for (int i = 0; i < end - start; i++) {
			tmp[i] = src[start + i];
		}
		return tmp;
	}

	public static void main(String[] str) {

	}
}
