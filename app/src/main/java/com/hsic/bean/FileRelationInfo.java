package com.hsic.bean;

public class FileRelationInfo {
	private String RelationID;
	private String FilePath;
	private String ImageName;
	//车次
	private String TruckNoId;
	private String SaleID;
	public String getRelationID() {
		return RelationID;
	}

	public void setRelationID(String relationID) {
		RelationID = relationID;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getImageName() {
		return ImageName;
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

	public String getTruckNoId() {
		return TruckNoId;
	}

	public void setTruckNoId(String truckNoId) {
		TruckNoId = truckNoId;
	}

	public String getSaleID() {
		return SaleID;
	}

	public void setSaleID(String saleID) {
		SaleID = saleID;
	}
}
