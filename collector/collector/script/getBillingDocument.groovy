import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

def Message processData(Message message) {
    // modelo: billingDocument=123456
    String sQuery = message.getProperty("pBillingDocument");
    String sBillingDocument = sQuery.substring(16)
    //println "Teste:"+sBillingDocument;

    message.setProperty("pOnlyBillingNumber", sBillingDocument)
    return message;
}