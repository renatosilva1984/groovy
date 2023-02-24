import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.mapping.ValueMappingApi;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.gateway.ip.core.customdev.util.SoapHeader;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import groovy.xml.XmlUtil;

def Message processData(Message message) {
    String body = message.getBody(java.lang.String);
    def bodyXML = new XmlParser().parseText(body);
    bodyXML = XmlUtil.serialize(bodyXML);
    bodyXML = bodyXML.replaceAll("ns1","otm");
    message.setBody(bodyXML)
    return message;
}
