package org.area515.resinprinter.job;

import java.awt.Graphics2D;

import javax.script.ScriptException;

import org.area515.resinprinter.display.InappropriateDeviceException;
import org.area515.resinprinter.gcode.eGENERICGCodeControl;
import org.area515.resinprinter.job.AbstractPrintFileProcessor.DataAid;
import org.area515.resinprinter.printer.BuildDirection;
import org.area515.resinprinter.printer.MachineConfig;
import org.area515.resinprinter.printer.MachineConfig.MonitorDriverConfig;
import org.area515.resinprinter.printer.Printer;
import org.area515.resinprinter.printer.PrinterConfiguration;
import org.area515.resinprinter.printer.SlicingProfile;
import org.area515.resinprinter.printer.SlicingProfile.InkConfig;
import org.area515.resinprinter.serial.SerialCommunicationsPort;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public class AbstractPrintFileProcessorTest {
	public static PrintJob createTestPrintJob(PrintFileProcessor processor) throws InappropriateDeviceException, Exception {
		PrintJob printJob = Mockito.mock(PrintJob.class);
		Printer printer = Mockito.mock(Printer.class);
		PrinterConfiguration printerConfiguration = Mockito.mock(PrinterConfiguration.class);
		SlicingProfile slicingProfile = Mockito.mock(SlicingProfile.class);
		InkConfig inkConfiguration = Mockito.mock(InkConfig.class);
		eGENERICGCodeControl gCode = Mockito.mock(eGENERICGCodeControl.class);
		SerialCommunicationsPort serialPort = Mockito.mock(SerialCommunicationsPort.class);
		MonitorDriverConfig monitorConfig = Mockito.mock(MonitorDriverConfig.class);
		MachineConfig machine = Mockito.mock(MachineConfig.class);
		
		Mockito.when(printJob.getPrinter()).thenReturn(printer);
		Mockito.when(printer.getPrinterFirmwareSerialPort()).thenReturn(serialPort);
		Mockito.when(printJob.getPrintFileProcessor()).thenReturn(processor);
		Mockito.when(printer.getConfiguration()).thenReturn(printerConfiguration);
		Mockito.when(printer.waitForPauseIfRequired()).thenReturn(true);
		Mockito.when(printer.isPrintActive()).thenReturn(true);
		Mockito.when(printerConfiguration.getSlicingProfile()).thenReturn(slicingProfile);
		Mockito.when(slicingProfile.getSelectedInkConfig()).thenReturn(inkConfiguration);
		Mockito.when(slicingProfile.getDirection()).thenReturn(BuildDirection.Bottom_Up);
		Mockito.when(printer.getGCodeControl()).thenReturn(gCode);
		Mockito.when(slicingProfile.getgCodeLift()).thenReturn("Lift z");
		Mockito.doCallRealMethod().when(gCode).executeGCodeWithTemplating(Mockito.any(PrintJob.class), Mockito.anyString());
		Mockito.when(printer.getConfiguration().getMachineConfig()).thenReturn(machine);
		Mockito.when(printer.getConfiguration().getMachineConfig().getMonitorDriverConfig()).thenReturn(monitorConfig);
		return printJob;
	}

	@Test
	public void EnsureMethodsThrowExceptionIfNotInitialized() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		PrintJob printJob = createTestPrintJob(processor);
		DataAid aid = null;
		try {
			processor.applyBulbMask(aid, null, 0, 0);
			Assert.fail("Failed to throw IllegalStateException.");
		} catch (IllegalStateException e) {
		}
		try {
			processor.performFooter(aid);
			Assert.fail("Failed to throw IllegalStateException.");
		} catch (IllegalStateException e) {
		}
		try {
			processor.performHeader(aid);
			Assert.fail("Failed to throw IllegalStateException.");
		} catch (IllegalStateException e) {
		}
		try {
			processor.performPostSlice(aid);
			Assert.fail("Failed to throw IllegalStateException.");
		} catch (IllegalStateException e) {
		}
		try {
			processor.performPreSlice(aid, null);
			Assert.fail("Failed to throw IllegalStateException.");
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void unsupportedBuildAreaDoesntBreakProjectorGradient() throws InappropriateDeviceException, ScriptException, Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		Graphics2D graphics = Mockito.mock(Graphics2D.class);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getProjectorGradientCalculator()).thenReturn("var mm = $buildAreaMM * 2;java.awt.Color.ORANGE");
		Mockito.when(printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class))).thenReturn(null);
		DataAid aid = processor.initializeDataAid(printJob);
		processor.applyBulbMask(aid, graphics, 0, 0);
	}

	@Test
	public void unsupportedBuildAreaDoesntBreakLiftDistanceCalculator() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getzLiftDistanceCalculator()).thenReturn("var mm = $buildAreaMM * 2;mm");
		Mockito.when(printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class))).thenReturn(null);
		DataAid aid = processor.initializeDataAid(printJob);
		processor.performPostSlice(aid);
	}

	@Test
	public void getExceptionWhenWeReturnGarbageForLiftDistanceCalculator() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getzLiftDistanceCalculator()).thenReturn("var mm = $buildAreaMM * 2;java.awt.Color.ORANGE");
		Mockito.when(printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class))).thenReturn(null);
		DataAid aid = processor.initializeDataAid(printJob);
		try {
			processor.performPostSlice(aid);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("The result of your lift distance script needs to evaluate to an instance of java.lang.Number", e.getMessage());
		}
	}

	@Test
	public void noNullPointerWhenWeReturnNull() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getzLiftDistanceCalculator()).thenReturn(";");
		Mockito.when(printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class))).thenReturn(null);
		DataAid aid = processor.initializeDataAid(printJob);
		try {
			processor.performPostSlice(aid);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("The result of your lift distance script needs to evaluate to an instance of java.lang.Number", e.getMessage());
		}
	}

	@Test
	public void usingUnsupportedBuildAreaWithLiftDistance() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		Graphics2D graphics = Mockito.mock(Graphics2D.class);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getZLiftDistanceGCode()).thenReturn("G99 ${1 + UnknownVariable * 2} ;dependent on buildArea");
		Double whenBuilAreaMMCalled = printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class));
		Mockito.when(whenBuilAreaMMCalled).thenReturn(null);
		DataAid aid = processor.initializeDataAid(printJob);
		try {
			processor.performPostSlice(aid);
			Assert.fail("Must throw InappropriateDeviceException");
		} catch (InappropriateDeviceException e) {
			Mockito.verify(printJob.getPrintFileProcessor(), Mockito.times(1)).getBuildAreaMM(Mockito.any(PrintJob.class));
		}
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getZLiftDistanceGCode()).thenReturn("G99 ${1 + buildAreaMM * 2} ;dependent on buildArea");
		try {
			processor.performPostSlice(aid);
			Mockito.verify(printJob.getPrintFileProcessor(), Mockito.times(5)).getBuildAreaMM(Mockito.any(PrintJob.class));
		} catch (InappropriateDeviceException e) {
			Assert.fail("Should not throw InappropriateDeviceException");
		}
	}

	@Test
	public void syntaxErrorInTemplate() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		Graphics2D graphics = Mockito.mock(Graphics2D.class);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getZLiftDistanceGCode()).thenReturn("G99 ${ ;dependent on buildArea");
		Double whenBuilAreaMMCalled = printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class));
		DataAid aid = processor.initializeDataAid(printJob);
		try {
			processor.performPostSlice(aid);
			Assert.fail("Must throw InappropriateDeviceException");
		} catch (InappropriateDeviceException e) {
			Mockito.verify(printJob.getPrintFileProcessor(), Mockito.times(2)).getBuildAreaMM(Mockito.any(PrintJob.class));
		}
	}

	@Test
	public void properGCodeCreated() throws Exception {
		AbstractPrintFileProcessor processor = Mockito.mock(AbstractPrintFileProcessor.class, Mockito.CALLS_REAL_METHODS);
		Graphics2D graphics = Mockito.mock(Graphics2D.class);
		PrintJob printJob = createTestPrintJob(processor);
		Mockito.when(printJob.getPrinter().getConfiguration().getSlicingProfile().getZLiftDistanceGCode()).thenReturn("${1 + buildAreaMM * 2}");
		Double whenBuilAreaMMCalled = printJob.getPrintFileProcessor().getBuildAreaMM(Mockito.any(PrintJob.class));
		Mockito.when(whenBuilAreaMMCalled).thenReturn(new Double("5.0"));
		DataAid aid = processor.initializeDataAid(printJob);
		Mockito.when(printJob.getPrinter().getGCodeControl().sendGcode(Mockito.anyString())).then(new Answer<String>() {
			private int count = 0;

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				switch (count) {
					case 0:
						Assert.assertEquals("11", invocation.getArguments()[0]);
						break;
					case 1:
						Assert.assertEquals("Lift z", invocation.getArguments()[0]);
						break;
				}
				count++;
				return (String)invocation.getArguments()[0];
			}
		});
		processor.performPostSlice(aid);
	}
}
