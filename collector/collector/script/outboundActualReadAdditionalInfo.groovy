import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import groovy.xml.XmlUtil;

def Message processData(Message message) {

    def body = message.getBody(java.lang.String) as String;
    //def root = new XmlSlurper().parseText(body);
    def root = new XmlParser().parseText(body);
    def vbeln = "" ;
    def docnum = "" ;
    def nfenum = "" ;
    def shipment = "" ;
    def acckey = "" ;
    def num_funit = "" ;


    root.'**'.each
            { r ->
                if (r.name() == 'HDR_TXT_ZOTM') {
                    vbeln = r.text()
                }
                if (r.name() == 'docnum') {
                    docnum = r.text()
                }
                if (r.name() == 'nfenum') {
                    nfenum = r.text()
                }
                if (r.name() == 'shipment') {
                    shipment = r.text().substring(0, 10);
                    r.value = shipment; 
                }
                if (r.name() == 'acckey') {
                    acckey = r.text()
                }
                if (r.name() == 'fu') {
                    num_funit = r.text()
                }

            };
    println (shipment);
    message.setProperty( "vbeln" , vbeln);
    message.setProperty( "docnum" , docnum);
    message.setProperty( "nfenum" , nfenum);
    message.setProperty( "shipment" , shipment);
    message.setProperty( "acckey" , acckey);
    message.setProperty( "num_funit" , num_funit);

    def xml = XmlUtil.serialize(root);
    message.setBody(xml);

    return message;
}