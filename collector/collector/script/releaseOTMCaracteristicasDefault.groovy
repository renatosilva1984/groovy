import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import groovy.xml.*;

def Message processData(Message message) {

    def body = message.getBody(String);
    def root = new XmlParser().parseText(body);
    def quantidade = 0;

    root.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.A_OutbDeliveryItemType.each
            { node ->

                if (node.ItemWeightUnit.text() == 'G'){
                    quantidade = node.ItemGrossWeight.text()
                    quantidade = quantidade.toFloat()/1000;
                    node.ItemGrossWeight[0].value = quantidade
                }

                def QntPacote = new NodeBuilder().QntPacote()
                def newCodMaterial = new NodeBuilder().newCodMaterial()
                def Processado = new NodeBuilder().Processado()

                node.append(QntPacote)
                node.append(newCodMaterial)
                node.append(Processado)

                quantidade =  node.ActualDeliveryQuantity[0].text()
                int value1  = quantidade.toFloat();

                node.QntPacote[0].value = 1
                node.newCodMaterial[0].value = node.Material.text();
                node.Processado[0].value = "N"
            }

    def xml = XmlUtil.serialize(root);
    message.setBody(xml);

    return message;
}
