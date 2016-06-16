package org.area515.resinprinter.services;

public class Customizer {
	private String name;
	private String printerName;
	private String printableName;
	private boolean supportsAffineTransformSettings;        //True means supported/False means not supported
	private AffineTransformSettings affineTransformSettings;//null means it's not used event if this customizer does support the AffineTransform	
	
	public static class AffineTransformSettings {
		private Integer xTranslate;//negative printerwidth for xflip
		private Integer yTranslate;//negative printerlength for yflip
		private Double xScale;//-1 for xflip
		private Double yScale;//-1 for yflip
		private String affineTransformScriptCalculator;//Ignore this for now
		
		public Integer getxTranslate() {
			return xTranslate;
		}
		public void setxTranslate(Integer xTranslate) {
			this.xTranslate = xTranslate;
		}
		
		public Integer getyTranslate() {
			return yTranslate;
		}
		public void setyTranslate(Integer yTranslate) {
			this.yTranslate = yTranslate;
		}
		
		public Double getxScale() {
			return xScale;
		}
		public void setxScale(Double xScale) {
			this.xScale = xScale;
		}
		
		public Double getyScale() {
			return yScale;
		}
		public void setyScale(Double yScale) {
			this.yScale = yScale;
		}
		
		public String getAffineTransformScriptCalculator() {
			return affineTransformScriptCalculator;
		}
		public void setAffineTransformScriptCalculator(String affineTransformScriptCalculator) {
			this.affineTransformScriptCalculator = affineTransformScriptCalculator;
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPrinterName() {
		return printerName;
	}
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}
	
	public String getPrintableName() {
		return printableName;
	}
	public void setPrintableName(String printableName) {
		this.printableName = printableName;
	}

	public boolean isSupportsAffineTransformSettings() {
		return supportsAffineTransformSettings;
	}
	public void setSupportsAffineTransformSettings(boolean supportsAffineTransformSettings) {
		this.supportsAffineTransformSettings = supportsAffineTransformSettings;
	}

	public AffineTransformSettings getAffineTransformSettings() {
		return affineTransformSettings;
	}
	public void setAffineTransformSettings(AffineTransformSettings affineTransformSettings) {
		this.affineTransformSettings = affineTransformSettings;
	}
}
