import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory

def Message processData(Message message) {

    //Faz a leitura do xml para validação das informações
    def body = message.getBody(java.lang.String) as String;
    def root = new XmlSlurper().parseText(body);

    //Cria variáveis
    def valor = 0;
    def variavel = "" ;

    //Armazena valor da FU
    def tor_id = root.ZCDS_DLV_MM_POType.tor_id[0].text()

    //Lê o XML e soma todos os valores de custo da remessa
    root.'**'.each
            { r ->
                if (r.name() == 'value') {
                    variavel = Double.parseDouble(r.text())
                    valor = valor + variavel
                }
            };

    //Salva valores das variáveis dentro do property
    message.setProperty( "tor_id" , tor_id);
    message.setProperty( "valor" , valor);
    return message;
}