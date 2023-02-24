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
    def remessa = "" ;
    def foNumber = "";

    //Salva o número da remessa
    root.'**'.each
        { r ->
            if (r.name() == 'vbeln') {
                remessa = r.text()
            }
            if (r.name() == 'fo') {
                foNumber = r.text()
            }
            
        };

    //Salva a variável no property
    message.setProperty( "remessa" , remessa);
    message.setProperty( "foNumber" , foNumber);
    return message;
}
