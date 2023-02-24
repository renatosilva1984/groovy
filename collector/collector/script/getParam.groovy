import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
def Message processData(Message message) {
    String[] param;
    String[] query;
    
    def map = message.getHeaders();
    def header = map.get("CamelHttpQuery");
    
    query = header.split('&')

	for(String i in query) { 
		param = i.split('=')
    	def name = param[0]
    	def value = param[1] 
		message.setProperty(name, value);
    } 

    return message;
}