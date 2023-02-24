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
    //Faz a leitura do xml para validação das informações
    def body = message.getBody(java.lang.String) as String;
    def root = new XmlParser().parseText(body);

    def releaseGid = "";
    def releaseLineGid = "";
    def itemCountShipUnit = "";
    def canceledShipments = "";
    def shipUnit = "";
    def iPosicao = 0;

    root.Header.HeaderToItem.Item.each
            { p->
                def CanceledShipments = new NodeBuilder().CanceledShipments()
                p.append(CanceledShipments)

                releaseGid = p.ReleaseGid.text()
                releaseLineGid = p.ShipUnit.text()
                itemCountShipUnit = p.ItemCount.text()
                iPosicao = releaseLineGid.indexOf("_",15);
                if (iPosicao == -1){
                    canceledShipments = "";
                    shipUnit = releaseLineGid;
                }
                else
                {   canceledShipments = releaseLineGid.substring(iPosicao+1);
                    shipUnit = releaseLineGid.substring(0,iPosicao);
                }

                p.ItemToItemContent.ItemContent[0].ReleaseGid[0].value = releaseGid
                p.ItemToItemContent.ItemContent[0].ReleaseLineGid[0].value = releaseLineGid.substring(0,15);
                p.ItemToItemContent.ItemContent[0].ItemCountShipUnit[0].value = itemCountShipUnit
                p.CanceledShipments[0].value = canceledShipments;
                p.ShipUnit[0].value = shipUnit;

                //       println "releaseLineGid: "+releaseLineGid;
                //       println "iPosicao: " + iPosicao;
            }

    def xml = XmlUtil.serialize(root);
    message.setBody(xml);
    return message;
}