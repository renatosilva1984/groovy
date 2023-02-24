import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.*;
import com.sap.gateway.ip.core.customdev.logging.*;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


def Message processData(message) { 
    
	StringBuffer reqXML = new StringBuffer(); 
	def payload = message.getBody(java.lang.String) as String; 
	def flag  = false;
	def start = false;
	
    if (payload != null){
        def tokens = payload.split("(?=<)|(?<=>)"); 
    	for(int i=0;i<tokens.length;i++) { 
    	    
    		if( tokens[i] == "<errordetails>" ){
    		    
    		    reqXML.append(tokens[i]);
    		    flag  = false;
    		    start = true;
    		}
    		
    		else if( tokens[i] == "<errordetail>" ){
    		    
    		    reqXML.append(tokens[i]);
    		    flag = false;
    		    
    		}
    		
    		else if( tokens[i] == "<message>" ){
    		    
    		    flag = true;
    		    
    		}
    		
    		else if( tokens[i] == "</errordetails>" || tokens[i] == "</errordetail>" || tokens[i] == "</message>" ){
    		    
    		    if( start == true ){
    		    
        		    reqXML.append(tokens[i]);
        		    flag = false;
    		    }
    		    
    		}
    		    
    		if( start == true && flag == true){
    		    
    		    reqXML.append(tokens[i]);
    		    
    		}
    		
    	}
    	
    	message.setBody(reqXML.toString()); 
    }else{
        def ex = message.getProperty("CamelExceptionCaught");
        if (ex.getClass().getCanonicalName().equals("org.apache.camel.component.ahc.AhcOperationFailedException")) {
            message.setBody(ex.getResponseBody());
        }
    }
	
	return message; 
	
}