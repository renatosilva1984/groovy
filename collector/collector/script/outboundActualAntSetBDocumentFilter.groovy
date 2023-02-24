import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory

def Message processData(Message message) {
    //Leitura XML
    def body = message.getBody(java.lang.String) as String;
    def root = new XmlSlurper().parseText(body);

    //Criação Variável
    def filterBDocument = "" ;
    def fieldName = "BillingDocument"

    //Salva o número da remessa
    root.'**'.each
        { r ->
            if (r.name() == 'BillingDocument') {
                filterBDocument = filterBDocument + "(" + fieldName + " eq '" + r.text() + "') or ";
                //println filterBDocument;
            }
        };
    //println filterRemessa.length();
    //Salva a variável no property
    message.setProperty( "filterBDocument" , filterBDocument.substring(0,filterBDocument.length()-3));
    return message;
}
