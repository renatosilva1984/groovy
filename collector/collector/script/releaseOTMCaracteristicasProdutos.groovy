import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.mapping.*;
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import javax.xml.namespace.QName;
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.ITApiFactory
import groovy.xml.*;

def Message processData(Message message) {

    def body = message.getBody(String);
    def root = new XmlParser().parseText(body);

    def mapProperties = message.getProperties();
    payload = mapProperties.get("payload")  as String;
    def root2 = new XmlParser().parseText(payload);

    def sizeP = root.A_ProductCharcType.size();
    def sizePr = root2.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.A_OutbDeliveryItemType.size();

    def produtoR ="";
    def result = 0;
    def quantidade = 0;
    def quantidadeP = 0;
    def produtoI = 0;
    def produtoIControle = '';
    def check = "";
    def produtoIsize = 0;

    root2.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.A_OutbDeliveryItemType.each
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

                produtoR = node.Material.text()

                root.A_ProductCharcType.to_Valuation.A_ProductCharcValueType.each
                        { p ->
                            //     Caracteristica Qtde Itens por Pacote
                            check = p.Product.text();
                            if (check.isNumber() == true) {
                                check = check.toInteger()
                            }

                            if (p.CharcInternalID.text() == '2367' && check.toString() == produtoR ) {
                                if (produtoR == check.toString()) {
                                    produtoI = check
                                    produtoIControle = p.Product.text()
                                }
//                                println "produtoR - " + produtoR;
//                                println "check - " + check;
//                                println "check = produtoR : " + check==produtoR
                                if (produtoR == produtoI.toString()) {
                                    root.A_ProductCharcType.to_Valuation.A_ProductCharcValueType.each
                                            { node2 ->
                                                if (node2.Product.text() == produtoIControle && node2.CharcInternalID.text() == '2367') {
                                                    def checkCharc = node2.CharcToDecimalValue.text();
                                                    if (checkCharc != '0E-14') {
                                                        quantidadeP = node2.CharcToDecimalValue.text();
                                                    }
                                                }
                                            }
                                    quantidade = node.ActualDeliveryQuantity[0].text()
                                    int value1 = quantidadeP.toFloat();
                                    int value2 = quantidade.toFloat();
                                    result = value2 / value1
                                    iResto = value2 % value1;
                                    
//                                    println "produtoI - " + produtoI.toString();
//                                    println "Resto - " + value2.toString() + "/" +value1.toString() + " = " +  iResto.toString();
//                                    println p;

                                    if (iResto == 0) {
                                        node.newCodMaterial[0].value = produtoR;
                                        node.QntPacote[0].value = value1
                                        node.ActualDeliveryQuantity[0].value = result
                                        node.Processado[0].value = "S";
                                    } else {
                                        node.newCodMaterial[0].value = produtoR + 'U';
                                        node.QntPacote[0].value = 1;
                                        node.ActualDeliveryQuantity[0].value = value2;
                                        node.Processado[0].value = "N";
                                    }

                                    if (node.QntPacote[0].text() == "") {
                                        node.QntPacote[0].value = 1
                                    }
                                    if (node.newCodMaterial[0].text() == "") {
                                        node.newCodMaterial[0].value = "";
                                    }
                                    if (node.Processado[0].text() == "") {
                                        node.Processado[0].value = "N"
                                    }
                                }

                            }
                        }
            }
    def xml = XmlUtil.serialize(root2);
    message.setBody(xml);

    return message;
}