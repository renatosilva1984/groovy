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
    def filterVbelv = "";
    def filterSDDocument = "";
    def fieldName1 = "vbelv"
    def fieldName2 = "ReferenceSDDocument"
    

    //Salva o número da remessa
    root.'**'.each
        { r ->
            if (r.name() == 'vbelv') {
                filterVbelv = filterVbelv + "(" + fieldName1 + " eq '"+r.text()+"') or ";
                filterSDDocument = filterSDDocument + "(" + fieldName2 + " eq '"+r.text()+"') or ";
                //println filterRemessa;
            }
        };
    //println filterRemessa.length();
    //Salva a variável no property
    message.setProperty( "filterVbelv" , filterVbelv.substring(0,filterVbelv.length()-3));
    message.setProperty( "filterSDDocument" , filterSDDocument.substring(0,filterSDDocument.length()-3));
    
    return message;
}