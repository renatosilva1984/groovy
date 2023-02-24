import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
def Message processData(Message message) {
    String[] param;
    String[] query;
    
    def map = message.getHeaders();
    def header = map.get("CamelHttpQuery");
    
    if (!header){
        header = "dummy=1"
    }
    
      message.setProperty("CustomQuery", header)

    return message;
}