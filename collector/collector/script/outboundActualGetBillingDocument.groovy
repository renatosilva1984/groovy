import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import groovy.xml.XmlUtil;

def Message processData(Message message) {

    def body = message.getBody(java.lang.String) as String;
    def root = new XmlParser().parseText(body);
    def sReferenceSDDocumentItem = '';
    def sRemessa = "" ;

    root.'**'.findAll {it.name() == 'A_BillingDocumentItemType'}.each
            {
                sReferenceSDDocumentItem = it.ReferenceSDDocumentItem.text().toString();
                sRemessa = it.ReferenceSDDocument.text().toString()

                if (sReferenceSDDocumentItem.indexOf('9000') != -1){
                    it.replaceNode {}
                }
            }

    message.setProperty( "remessa" , sRemessa);
    message.setBody(XmlUtil.serialize(root));
    return message;
}
