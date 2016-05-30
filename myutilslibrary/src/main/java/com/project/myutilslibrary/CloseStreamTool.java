package com.project.myutilslibrary;

import java.io.Closeable;
import java.io.IOException;

public class CloseStreamTool {

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}