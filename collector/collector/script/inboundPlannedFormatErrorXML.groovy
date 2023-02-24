import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.logging.*;

def Message processData(message) {

    StringBuffer reqXML = new StringBuffer();
    def payload = message.getBody(java.lang.String) as String;
    def tokens = payload.split("(?=<)|(?<=>)");
    def flag  = false;
    def start = false;

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
    return message;
}
