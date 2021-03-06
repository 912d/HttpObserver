package HttpObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author re
 */
final class WebsiteParser implements Runnable {
    private static Set<String> results;
    private static String sourceOfWebsite;
    private static String hashOfWebsite;
    private static int observerIDTracker = 0; /*global counter*/
    private int observerID; /*to tracking observers*/
    private String websiteURL;
    private String searchWord;
    private URL mainURL;
    
    public WebsiteParser(String websiteURL, String searchWord) {
        results = new TreeSet<>();
        
        if (isValidURL(websiteURL)) {
            this.websiteURL = websiteURL;
            this.searchWord = searchWord;
        }
        else {
            System.out.println("URL is incorrect. Exiting: -1");
            System.exit(-1);
        }
      
        this.observerID = ++observerIDTracker;
        
        System.out.print("OBSERVER " + this.observerID);
        System.out.printf(", looking in %s for \"%s\" \n",
                websiteURL, searchWord);
    }
    
    public boolean isValidURL(String url) {  
        URL u;

        try {  
            u = new URL(url);  
        } catch (MalformedURLException e) {  
            return false;  
        }
        
        try {  
            u.toURI();  
        } catch (URISyntaxException e) {  
            return false;  
        }  
        mainURL = u;
        return true;
    } 
    
    @Override
    public void run() {
      //while(!isDone) { 
          //isDone = true; // we can finish, our job is done
        if (websiteURL.length() <= 3 ||
            searchWord.length() <= 3 ) {
            System.err.println(".length of websiteURL OR searchWord is <= 3.");
            return;
        }
        
        WebsiteGrabber websiteGrabber = new WebsiteGrabber();
        WebsiteObserver websiteObserver1 = new WebsiteObserver(websiteGrabber);
        
        URLConnection yc;
        try {
            String inputLine;
            yc = mainURL.openConnection();
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
            yc.getInputStream()))) {
                int counter = 0;
                
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.indexOf(searchWord) != -1) {
                        results.add(inputLine);
                        System.out.println(inputLine);
                        ++counter;
                    }
                }
                if (counter > 0) {
                    Date date = Calendar.getInstance().getTime();
                    System.out.printf("OBSERVER %d: %d matches on %s website, found at %s\n",
                        this.observerID,counter, this.websiteURL, date.toString());
                    results.stream().forEach(System.out::println);
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException: "+ ex.getMessage());
        }
        //}
    }
}