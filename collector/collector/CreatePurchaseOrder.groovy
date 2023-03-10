import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.*;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import java.text.SimpleDateFormat;  
import java.text.DateFormat;
import groovy.json.JsonOutput;
import groovy.xml.MarkupBuilder;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.mapping.ValueMappingApi;
import com.sap.gateway.ip.core.customdev.util.Message;
import javax.xml.namespace.QName;
import com.sap.gateway.ip.core.customdev.util.SoapHeader;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import java.util.HashMap;
import java.security.*;
import groovy.json.*;
import groovy.util.CharsetToolkit;
import groovy.xml.XmlUtil;


class PurchaseOrderResponse{
	String lineIndex
	String messageId
	String messageType
	String messageNumber
	String messageString
}

//****************************************************************************
// Find Error Messages
//****************************************************************************
def Message findErrorMessage(Message message){
	def messageBody = message.getBody(java.lang.String) as String;
	def messageFound = "";
	
	if(messageBody){
		def xmlParser = new XmlParser();
		def xmlResponse = xmlParser.parseText(messageBody);
		
		def messageBase = xmlResponse?.RETURN?.item?.find { item -> item.TYPE.text() == "E" || item.TYPE.text() == "A" }
		if (messageBase){
			messageFound = "S";
		}else{
			messageFound = "N";
		}
	}else{
		messageFound = "N";
	}
	
	message.setProperty("MessageFound", messageFound);
	return message;
}

//****************************************************************************
// Initialize Response
//****************************************************************************
def Message initializeResponseList(Message message){
	List<PurchaseOrderResponse> purchaseOrderResponseList = new ArrayList<PurchaseOrderResponse>();
	message.setProperty("PurchaseOrderResponse", purchaseOrderResponseList);
	return message;
}

//****************************************************************************
// Enrich JSON Output
//****************************************************************************
def Message enrichJSONOutput(Message message){
	def messageBody = message.getBody(java.lang.String) as String;
	def headers = message.getHeaders()
	def CamelFileName = headers.get("CamelFileName")
	def purchaseOrderResponseList = message.getProperty("PurchaseOrderResponse")
	
	// File Name
	message.setProperty("FileNameReturn", CamelFileName.replace(".csv", ""))
	
	// JSON Handling
	if(messageBody){
		def jsonParser = new JsonSlurper()
		def jsonObject = jsonParser.parseText(messageBody)

		jsonObject.groups.each{ e ->		
			def lineRow
			if (e.row.class == java.util.ArrayList){
				lineRow = e.row[0];
			}else{
				lineRow = e.row;
			}		

			PurchaseOrderResponse purchaseOrderResponse
			Iterator<PurchaseOrderResponse> iterator = purchaseOrderResponseList.iterator();
			while (iterator.hasNext()) {
				PurchaseOrderResponse purchaseOrderResponseCheck = iterator.next();
				if (purchaseOrderResponseCheck.lineIndex.equals(lineRow.INDEX.toString())) {
					purchaseOrderResponse = purchaseOrderResponseCheck;
					break;
				}
			}			
			
			if(purchaseOrderResponse && purchaseOrderResponse != null){
				lineRow.TYPE = purchaseOrderResponse.messageType
				lineRow.ID = purchaseOrderResponse.messageId
				lineRow.NUM = purchaseOrderResponse.messageNumber
				lineRow.MESSAGE = purchaseOrderResponse.messageString
			}
		}
		
		jsonTOTAL = JsonOutput.toJson(jsonObject)
		message.setBody(JsonOutput.prettyPrint(jsonTOTAL))
	}
  
   return message;
}

//****************************************************************************
// Enrich Response with the Return
//****************************************************************************
def Message enrichResponse(Message message){
	def xmlInput = message.getBody(java.lang.String) as String;
	def jsonInput = message.getProperty("jsonIn") as String;
	def lineIndex = message.getProperty("LineIndex") as String;
	def messageId = "";
	def messageType = "";
	def messageNumber = "";
	def messageString = "";
	
	// XML Handling 
    if(xmlInput){
		def xmlParser = new XmlParser();
		def xmlResponse = xmlParser.parseText(xmlInput);	
		
		def returnLines = xmlResponse?.RETURN?.item?.findAll { item -> item.TYPE.text().length() > 0 }
		if(returnLines){
			returnLines.each {
				if (messageId.length() == 0){
					messageId = it.ID.text();
					messageType = it.TYPE.text();
					messageNumber = it.NUMBER.text();
					messageString = it.MESSAGE.text().replaceAll("|","").replaceAll(";","");
				}else{
					messageId += "|" + it.ID.text();
					messageType += "|" + it.TYPE.text();
					messageNumber += "|" + it.NUMBER.text();
					messageString += "|" + it.MESSAGE.text().replace("|","").replace(";","");					
				}
			}
		}	
	}	
	
	def purchaseOrderResponseList = message.getProperty("PurchaseOrderResponse");
	
	def purchaseOrderResponse = new PurchaseOrderResponse();
	purchaseOrderResponse.lineIndex = lineIndex
	purchaseOrderResponse.messageId = messageId
	purchaseOrderResponse.messageType = messageType
	purchaseOrderResponse.messageNumber = messageNumber
	purchaseOrderResponse.messageString = messageString	
	purchaseOrderResponseList.add(purchaseOrderResponse);		
	
	message.setProperty("PurchaseOrderResponse", purchaseOrderResponseList)	
	return message;
}


//****************************************************************************
// Store the Log if necessary
//****************************************************************************
def Message storeLogging(Message message){
	map = message.getProperties();
	def enableLogging = map.get("EnableLogging");
	
	if (enableLogging.toUpperCase().equals("TRUE")) {	
		def body = message.getBody(java.lang.String) as String;
		def messageLog = messageLogFactory.getMessageLog(message);
		messageLog.addAttachmentAsString("Before Send ", body, "application/xml");
	}	
 
	return message;    
}

//****************************************************************************
// Thread Sleep
//****************************************************************************
def Message sleepThread(Message message) {
   sleep(3000);
   return message;
}

//****************************************************************************
// Get a iFlow Property
//****************************************************************************
def String getProperty(String propertyName, MappingContext context) {

    def property = context.getProperty(propertyName);
	if (property){	
	  return property.toString();
	}
	return "";

}

//****************************************************************************
// Set Return from Body
//****************************************************************************
def Message setReturnFromBody(Message message){
    message.setProperty("ReturnMessage", message.getBody() as String);
    return message;
}

//****************************************************************************
// Check the Body Emptiness
//****************************************************************************
def Message isErrorBodyEmpty(Message message){
    def body = message.getBody(String)
    message.setProperty('EmptyPayload', body == null || body.isEmpty())
    return message
}


//****************************************************************************
// Context Change 
//****************************************************************************
def void addContextChange(String[] input, Output output, MappingContext context) { 
    for(item in input[0].split('\\*')){
     output.addValue(item);
    }
}


//****************************************************************************
// Raise a Custom Exception
//****************************************************************************
def Message raiseCustomException(Message message){
	def body = message.getBody(java.lang.String)as String;
	throw new Exception(body);
	return message;
}

//****************************************************************************
// Remove Non Numeric
//****************************************************************************
def String removeNonNumeric(String sNumericIn){
    return sNumericIn.replaceAll("[^\\d]", "");
}

//****************************************************************************
// Find the Error from the Exception
//****************************************************************************
def Message findErrorException(Message message){
	// get a map of iflow properties
	def map = message.getProperties()
	def logException = map.get("ExceptionLogging")
	def errordetails = ""
	
	// get an exception java class instance
	def ex = map.get("CamelExceptionCaught")
	if (ex!=null) 
	{
	// save the error response as a message attachment 
	def messageLog = messageLogFactory.getMessageLog(message);
	
	errordetails = ex.toString()
	message.setProperty("ReturnMessage", errordetails);
	}

	return message;
}

//****************************************************************************
// Convert the Error Message
//****************************************************************************
def Message convertErrorData(Message message){
    
    def returnMessage = message.getProperty("ReturnMessage");
    returnMessage = returnMessage.replaceAll("<", "");
    returnMessage = returnMessage.replaceAll(">", "");
    
    message.setProperty("ReturnMessage", returnMessage);
    return message;
}

//****************************************************************************
// Save the Error Data
//****************************************************************************
def Message saveErrorData(Message message){
    //Body
    def messageLog = messageLogFactory.getMessageLog(message);
    
    def logProperty = message.getProperty("LogProperty") as String;
    def logHeader = message.getProperty("LogHeader") as String;
    def logBody = message.getProperty("LogBody") as String;
    
    String content = "";
    
    if(messageLog != null) {
        messageLog.setStringProperty("Logging#1", "Payload");
        
        if (logProperty != null && logProperty.equalsIgnoreCase("true")) {
            def propertyMap = message.getProperties();
            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                buffer.append("<").append(entry.getKey()).append(">");
                buffer.append(entry.getValue());
                buffer.append("</").append(entry.getKey()).append(">\n");
            }
            content = content + "\n" + buffer.toString();
        }
        
        if (logHeader != null && logHeader.equalsIgnoreCase("true")) {
            def header = message.getHeaders() as String;
            content = content + "\n" + header;
        }
        
        if (logBody == null || logBody.equalsIgnoreCase("true")) {
            def body = message.getBody(java.lang.String) as String;
            content = content + "\n" + body;
        }
        
        if (content.length() > 0) {
            messageLog.addAttachmentAsString("Payload Erro: ", content, "text/plain");    
        }
    }

    return message;
}


//****************************************************************************
// Format the Error Payload
//****************************************************************************
def Message formatErrorPayload(Message message){
	StringBuffer reqXML = new StringBuffer(); 
	def payload = message.getBody(java.lang.String) as String; 
	def tokens = payload.split("(?=<)|(?<=>)"); 
	def flag  = false;
	def start = false;
	def ReturnMessage = "" as String;
	def CustomError = message.getProperty("CustomError");

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
	
    ReturnMessage = reqXML.toString();
    
    if( ReturnMessage != "" ){
	    message.setProperty("ReturnMessage", ReturnMessage);
	}
	else if( ReturnMessage == ""){
	    if ( CustomError == "" || CustomError == null){
          def map = message.getProperties()
          def logException = map.get("ExceptionLogging")
          def errordetails = ""
          def ex = map.get("CamelExceptionCaught")
          if (ex!=null) 
          {
            def messageLog = messageLogFactory.getMessageLog(message);
            errordetails = ex.toString()
            message.setProperty("ReturnMessage", errordetails);         
		  }
		  else{
		    message.setProperty("ReturnMessage", "");           
		  }
		}else{
			message.setProperty("ReturnMessage", CustomError);         
		}
	}
	
	return message; 
	
}


//****************************************************************************
// Log the Message Payload
//****************************************************************************
def Message logMessagePayload(Message message) {
    //Body
    def messageLog = messageLogFactory.getMessageLog(message);
    
    def logProperty = message.getProperty("LogProperty") as String;
    def logHeader = message.getProperty("LogHeader") as String;
    def logBody = message.getProperty("LogBody") as String;
    String content = "";
    
    if(messageLog != null) {
        messageLog.setStringProperty("Logging#1", "Payload");
        
        if (logProperty != null && logProperty.equalsIgnoreCase("true")) {
            def propertyMap = message.getProperties();
            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                buffer.append("<").append(entry.getKey()).append(">");
                buffer.append(entry.getValue());
                buffer.append("</").append(entry.getKey()).append(">\n");
            }
            content = content + "\n" + buffer.toString();
        }
        
        if (logHeader != null && logHeader.equalsIgnoreCase("true")) {
            def header = message.getHeaders() as String;
            content = content + "\n" + header;
        }
        
        if (logBody == null || logBody.equalsIgnoreCase("true")) {
            def body = message.getBody(java.lang.String) as String;
            content = content + "\n" + body;
        }
        
        if (content.length() > 0) {
            messageLog.addAttachmentAsString("Payload Entrada", content, "text/plain");    
        }
    }

    return message;  
}

//****************************************************************************
// Handle HTTP Error
//****************************************************************************
def Message handleHTTPError(Message message){
    def map = message.getProperties();
    def ex = map.get("CamelExceptionCaught");
	def logBody = map.get("LogBody") as String;
	def generalException;
	
    if (ex!=null) {                               
        if (ex.getClass().getCanonicalName().equals("com.sap.gateway.core.ip.component.odata.exception.OsciException")) {                                               
            def messageLog = messageLogFactory.getMessageLog(message);
			if (logBody == null || logBody.equalsIgnoreCase("true")) {
				messageLog.addAttachmentAsString("http.requestUri", ex.getRequestUri(), "text/plain");
				messageLog.addAttachmentAsString("http.response", message.getBody(), "text/plain");
				messageLog.addAttachmentAsString("http.statusCode", message.getHeaders().get("CamelHttpResponseCode").toString(), "text/plain");          
			}
			def messageBody = message.getBody(java.lang.String) as String;
			if (messageBody != null){
			    try{
    				def xmlParser = new XmlParser();
    				def xmlResponse = xmlParser.parseText(messageBody);   			
    				message.setProperty("ReturnMessage", xmlResponse.message.text());			
    				message.setProperty("http.response", messageBody);                       				
    				message.setProperty("http.requestUri",ex.getRequestUri());                       
    				message.setProperty("http.statusCode", message.getHeaders().get("CamelHttpResponseCode").toString());				
			    }catch(Exception e){
    				message.setProperty("ReturnMessage", messageBody);			
    				message.setProperty("http.response", messageBody);                       				
    				message.setProperty("http.requestUri",ex.getRequestUri());                       
    				message.setProperty("http.statusCode", message.getHeaders().get("CamelHttpResponseCode").toString());							        
			    }
			}else{
				generalException = map.get("CamelExceptionCaught");
				message.setProperty("ReturnMessage", "Erro T??cnico: " + generalException.toString());			
			}
        }else{
            if (message.getProperty("ReturnMessage").length() == 0){
			    generalException = map.get("CamelExceptionCaught");
			    message.setProperty("ReturnMessage", "Erro T??cnico: " + generalException.toString());			
            }
		}
		
    }
    return message;
}

//****************************************************************************
// Log the Error Payload
//****************************************************************************
def Message logErrorPayload(Message message) {
    //Body
    def messageLog = messageLogFactory.getMessageLog(message);
    
    def logProperty = message.getProperty("LogProperty") as String;
    def logHeader = message.getProperty("LogHeader") as String;
    def logBody = message.getProperty("LogBody") as String;
    
    String content = "";
    
    if(messageLog != null) {
        messageLog.setStringProperty("Logging#1", "Payload");
        
        if (logProperty != null && logProperty.equalsIgnoreCase("true")) {
            def propertyMap = message.getProperties();
            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                buffer.append("<").append(entry.getKey()).append(">");
                buffer.append(entry.getValue());
                buffer.append("</").append(entry.getKey()).append(">\n");
            }
            content = content + "\n" + buffer.toString();
        }
        
        if (logHeader != null && logHeader.equalsIgnoreCase("true")) {
            def header = message.getHeaders() as String;
            content = content + "\n" + header;
        }
        
        if (logBody == null || logBody.equalsIgnoreCase("true")) {
            def body = message.getBody(java.lang.String) as String;
            content = content + "\n" + body;
        }
        
        if (content.length() > 0) {
            messageLog.addAttachmentAsString("Payload Erro:", content, "text/plain");    
        }
    }

    return message;    
}