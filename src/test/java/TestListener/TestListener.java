package TestListener;

import Report.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestListener implements ITestListener {
    private  final static Logger logger = LogManager.getLogger(TestListener.class);
    private final ExtentReports extent = ExtentManager.getInstance();
    private ExtentTest test;
    private static Map<String, ExtentTest> classTestMap = new HashMap<>();
    private static ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();
    int i=0;
    public TestListener() {

    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Capture screenshot only when the test fails
        String failedMethodName = result.getName();
//        File screenshot = ScreenshotOps.takeScreenshot();
//        String fileName = DataManipulator.generateDate(failedMethodName) + "_ScreenShot";
        String className = result.getTestClass().getName();
//        String screenShotPath = TestConfig.saveScreenShot(fileName, screenshot,className);
        logger.error("Test failed: {}", failedMethodName,result.getThrowable());
//        logger.info("Screen shot taken and saved at: {}",screenShotPath);
        test.log(Status.FAIL, MarkupHelper.createLabel("Test Failed: "+ result.getMethod().getMethodName(), ExtentColor.RED));

//        String imageByte = null;
//        File file = new File(screenShotPath);
//        try (FileInputStream imageStream = new FileInputStream(file)) {
//            byte[] imageBytes = new byte[(int) file.length()];
//            imageStream.read(imageBytes);
//            imageByte = Base64.getEncoder().encodeToString(imageBytes);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }

//        if (imageByte != null) {
//            test.addScreenCaptureFromBase64String(imageByte,"Failure Sanpshot");
//        } else {
//            System.out.println("Failed to convert screenshot to Base64.");
//        }

    }


    @Override
    public void onTestStart(ITestResult result) {
        // This can be used to log test start if needed
        logger.info("Test started: {}", result.getName());
//        logger.info("User logged in: {}","{"+TestConfig.username+", "+TestConfig.userid+"}");

        test = extent.createTest(result.getMethod().getMethodName());
//        test.info("{" + TestConfig.username + ", " + TestConfig.userid + "}");
        while(i<1) {
            extent.setSystemInfo("Application", "ContactList");
            extent.setSystemInfo("Environment","Test" );
//            extent.setSystemInfo("Browser Details", TestConfig.getBrowserDetails());
            i++;
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // This can be used to log test success if needed
        logger.info("Test passed: {}", result.getName());
//        if(TestConfig.canId!=null){
//            logger.info("Candidate created with ID: {}",TestConfig.canId);
//        }
//        if(TestConfig.employeeId!=null){
//            logger.info("Employee created with ID: {}",TestConfig.employeeId);
//        }
        test.log(Status.PASS, MarkupHelper.createLabel("Test Passed: "+ result.getMethod().getMethodName(), ExtentColor.GREEN));

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // This can be used to log skipped tests if needed
        logger.warn("Test skipped: {}", result.getName());
        test.log(Status.SKIP, "Test skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // This can be used to log partial failures if needed
    }

    @Override
    public void onStart(ITestContext context) {
        // This can be used to initialize things before all tests start
        logger.info("Test suite started: {}", context.getName());
        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
        File reportFile = new File(reportPath);

        if (reportFile.exists()) {
            logger.info("Deleting old Extent Report...");
            reportFile.delete();
        }

    }

    @Override
    public void onFinish(ITestContext context) {
        // This can be used for cleanup after all tests finish

        logger.info("Test suite finished: {}", context.getName());
        if (extent != null) {
            extent.flush();
            logger.info("Extent report flushed.");
        } else {
            logger.error("ExtentReports instance is NULL! Report may not be generated.");
        }

        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
        File reportFile = new File(reportPath);

        if (reportFile.exists()) {
            logger.info("Extent Report generated successfully: {}", reportPath);
        } else {
            logger.error("Extent Report NOT FOUND!");
        }
        try {
            Desktop.getDesktop().browse(new File(reportPath).toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    }
