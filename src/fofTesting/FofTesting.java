package fofTesting;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.webdriven.commands.Click;



public class FofTesting  {

    private static WebDriver driver;
    String homepageUrl, moderatorPage;
    String browser;
    
	public static void main(String[] args) {
    	
    }
	
	@BeforeTest
	public void setUp(){
		browser = "firefox";
		setBrowser(browser);
	
		// Creates implicit 10 second wait time for any element to be returned; during that time the driver will continue to pole while suppressing warnings 
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
    @Test
    public void loadPage(){
    	String url = "http://localhost:9000/";
        driver.get(url);
        assert(driver.getCurrentUrl().equals(url));
    }
    
    @Test(dependsOnMethods = {"loadPage"})
    public void dismissBrowserWarning(){
        if(!browser.equals("chrome")){
        	WebElement okButton = driver.findElement(By.id("button-1005"));
        	okButton.click();
        	System.out.println("Click ok button");
        	// Dissmiss non-chrome alert window
            // just hit enter
        	//driver.findElement(By.xpath("/html/body")).sendKeys(Keys.ENTER);
        	//System.out.println("sending enter key signal to dismiss dialogue box");
        	waitUntilElementDisappears(By.id("messagebox-1001"));
        	System.out.println("message box has disappeared");
        }
    }
    
    
    
    @Test(dependsOnMethods = {"dismissBrowserWarning"})
    public void startSinglePlayerGame(){
	    // Click on the letter 'A'       
	    driver.findElement(By.id("FOF-letter-a")).click();
	
	    // Select single player game
	    driver.findElement(By.id("FOF-single-player-game")).click();
	    
	    // Begin single player game
	    driver.findElement(By.id("FOF-single-player-begin")).click();
	    
	    // Hit start button
	    driver.findElement(By.id("FOF-moderator-start-button")).click();
    }
    
    
    @Test(dependsOnMethods = {"startSinglePlayerGame"})
    public void switchToPlayerPage(){
	    //handle windows change
	    System.out.println("Page url before switch is: " + driver.getCurrentUrl());
	    moderatorPage = driver.getWindowHandle();
	    Set<String> pageList = driver.getWindowHandles();
	     
	    pageList.remove(moderatorPage);
	    //assert pageList.size() == 1;
	    driver.switchTo().window((String) pageList.toArray()[0]);
	    System.out.println("Page url after switch is: " + driver.getCurrentUrl());
    }
    
    @Test(dependsOnMethods = {"switchToPlayerPage"})
    public void playSinglePlayer(){
    	
	    int numberOfRounds = 5;
	    
	    System.out.println("Page url is: " + driver.getCurrentUrl());
	    
	    for(int i=1; i<numberOfRounds; i++){
	    	
	        // Plant fields
	        for(int f=0; f < 4; f++){
	        	// wait until exclamation point appears before proceeding
	        	waitUntilElementAppears(By.id("FOF-progress-exclamation-point"));
	        	WebElement plantingIcon = driver.findElement(By.id("FOF-planting-icon-" + f));
	        	// sleep to allow time for listeners to get attached to the element (extjs)
	        	if(f == 0){
		        	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	plantingIcon.click();
	        	System.out.println("Clicked planting icon");
	        	if(f == 0) {
	        		driver.findElement(By.id("FOF-plant-corn-" + f)).click();
	        		System.out.println("Clicked corn");
	        	} else if (f == 1 || f == 2){
	        		driver.findElement(By.id("FOF-plant-grass-" + f)).click();
	        		System.out.println("Clicked grass");
	        	} else {
	        		driver.findElement(By.id("FOF-plant-alfalfa-" + f)).click();
	        		System.out.println("Clicked alfalfa");
	        	}
	        }
	        
	        // Click 'Done planting'
	        //driver.findElement(By.id("FOF-progress-button-0")).click();
	        driver.findElement(By.id("FOF-progress-exclamation-point")).click();
	        System.out.println("Clicked done planting");
	        
	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	        // Click 'Harvest' NOTE: only works without mgmt - otherwise, need to change #
	        //driver.findElement(By.id("FOF-progress-button-1")).click();
	        driver.findElement(By.id("FOF-progress-exclamation-point")).click();
	        System.out.println("Clicked harvest");
	        // dismiss round wrap-up
	        try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    	WebElement continueButton = driver.findElement(By.id("close_button"));
	        
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	        continueButton.click();
	        System.out.println("Clicked continue to next season");
	        
	    }
    }
    
    @Test(dependsOnMethods = {"playSinglePlayer"})
    public void closeAndCleanUpGames(){
    	//close the window and switch back to the moderator tab
	    driver.close();
	    driver.switchTo().window(moderatorPage);
	    
	    // Click end game
	    driver.findElement(By.id("FOF-moderator-end-game-button")).click();
	    
	    //Close the browser
	    driver.quit();
    }
    
    
    
    
    
    // Helper Methods
    //--------------------------------------------------------------------
    
    public static void setBrowser(String browser){
    	if(browser.equals("firefox")){
    		driver = new FirefoxDriver();
    	} else if(browser.equals("chrome")) {
    		System.setProperty("webdriver.chrome.driver", "/usr/local/chromedriver");
    		driver = new ChromeDriver();  		
    	} else {
    		driver = null;
    		System.out.println("browser not specified");
    	}
    }
    
    public static boolean waitUntilElementDisappears(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    
    public static void waitUntilElementAppears(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
}