package com.zjw.mobilesafe.test;

import java.util.List;

import com.zjw.mobilesafe.domain.ProcessInfo;
import com.zjw.mobilesafe.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class RunningProcessInfosTest extends AndroidTestCase {

	public void testGetRunningProcessList(){
		List<ProcessInfo> processList = TaskInfoProvider.getProcessList(getContext());
		for (ProcessInfo processInfo : processList) {
			System.out.println(processInfo.toString());
		}
	}
	
}
