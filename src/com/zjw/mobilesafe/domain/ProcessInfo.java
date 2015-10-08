package com.zjw.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	private String name;
	private String packName;
	private Drawable icon;
	private long memSize;
	private boolean isUserProcress;
	private boolean checked;
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isUserProcress() {
		return isUserProcress;
	}
	public void setUserProcress(boolean isUserProcress) {
		this.isUserProcress = isUserProcress;
	}
	@Override
	public String toString() {
		return "ProcessInfo [name=" + name + ", packName=" + packName + ", memSize=" + memSize + ", isUserProcress="
				+ isUserProcress + "]";
	}
	

}
