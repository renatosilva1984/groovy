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
    def valor = "";
    def check = "";
    def division ="";
    def docType = "" ;
    def shipP = "" ;
    def shipTo = "" ;
    def prioridade = "" ;
    def deliveryNum = "";
    def quantidade = "";
    def customer = "";
    def planTo = "";
    def dexco = "";
    def size = 0;
    def count = 0;
    def getValue ="";
    def produto ="";
    def verificador ="";
    def shipToTransf ="";
    def shipFromTransf ="";

    if (root.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.size() == 0){
        message.setProperty( "dexco" , dexco);
        message.setProperty( "customer" , customer);
        message.setProperty( "planTo" , planTo);
        message.setProperty( "quantidade" , quantidade);
        message.setProperty( "deliveryNum" , deliveryNum);
        message.setProperty( "prioridade" , prioridade);
        message.setProperty( "shipTo" , shipTo);
        message.setProperty( "shipToTransf" , shipToTransf);
        message.setProperty( "shipFromTransf" , shipFromTransf);
        message.setProperty( "tipo_remessa" , valor);
        message.setProperty( "ship_from" , check);
        message.setProperty( "produto" , produto);
    }

    else{
        //Lê as linhas do XML
        size = root.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.A_OutbDeliveryItemType.size();
        root.'**'.each
                { r ->
                    //Verifica o tipo da remessa e converte o valor para o formato do OTM
                    if (r.name() == 'DeliveryDocumentType') {
                        docType = r.text()
                        switch(docType) {

                            case "ZDAG":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRMA":
                                valor = "DEXCO_TRANSFERENCIA"
                                break

                            case "ZVEF":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZVFD":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZVFR":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "TFDT":
                                valor = "DEXCO_TRANSFERENCIA"
                                break

                            case "ZAPS":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZASR":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZAVI":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZDA1":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZEXP":
                                valor = "DEXCO_EXPORTACAO"
                                break

                            case "ZMAE":
                                valor = "DEXCO_EXPORTACAO"
                                break

                            case "ZMAG":
                                valor = "DEXCO_EXPORTACAO"
                                break

                            case "ZRXP":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZDXP":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZDAD":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZDAP":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZFUN":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZNOR":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZREB":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZROB":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRTR":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZREY":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZRAD":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRPD":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRCM":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZDCM":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZXCM":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRVF":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZRVD":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZRVR":
                                valor = "DEXCO_DEVOLUCAO"
                                break

                            case "ZVDT":
                                valor = "DEXCO_TRANSFERENCIA"
                                break

                            case "ZAPS":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZBON":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZRAG":
                                valor = "DEXCO_EXPORTACAO"
                                break

                            case "ZREF":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            case "ZRGE":
                                valor = "DEXCO_ORDEM_VENDA"
                                break

                            default:
                                valor =""
                                break
                        }
                    }

                    //Verifica se o ShippingPoint é do tipo que deve subir para OTM
                    if (r.name() == 'ShippingPoint') {
                        shipP = r.text()
                        switch(shipP) {
                            case "D004":
                                check = "X"
                                verificador = "X"
                                break

                            case "D005":
                                check = "X"
                                verificador = "X"
                                break

                            case "D015":
                                check = "X"
                                verificador = "X"
                                break

                            case "D034":
                                check = "X"
                                break

                            case "D040":
                                check = "X"
                                verificador = "X"
                                break

                            case "D041":
                                check = "X"
                                break

                            case "D043":
                                check = "X"
                                break

                            case "D050":
                                check = "X"
                                break

                            case "D060":
                                check = "X"
                                verificador = "X"
                                break

                            case "D063":
                                check = "X"
                                verificador = "X"
                                break

                            case "D064":
                                check = "X"
                                break

                            case "D065":
                                check = "X"
                                break

                            case "D068":
                                check = "X"
                                break

                            case "D069":
                                check = "X"
                                break

                            case "D085":
                                check = "X"
                                break

                            case "HY01":
                                check = "X"
                                break

                            default:
                                check =""
                                break
                        }
                    }

                    //if( ZMAE/ZMAG/ZEXP e ZRAG )

                    if (docType == 'ZRAG' && verificador == "X"){
                        valor = "DEXCO_ORDEM_VENDA"
                    }
                    if ( r.name() == 'ShippingCondition' && r.text() == 'Y5'){
                        if (docType == 'ZVEF' || docType == 'TFDT' || docType == 'ZAPS' || docType == 'ZASR' || docType == 'ZAVI' || docType == 'ZFUN' || docType == 'ZNOR' || docType == 'ZREY' || docType == 'ZRCM' || docType == 'ZRVF'){
                            valor = "DEXCO_CABOTAGEM"
                        }
                    }
                    //Verifica o tipo do documento de remessa e altera o destino para cliente em caso de Ordem de venda ou exportação


                    if (r.name() == 'ReceivingPlant' ) {
                        shipToTransf = r.text()
                    }
                    if (r.name() == 'ShippingPoint' ) {
                        shipFromTransf = r.text()
                    }

                    //Verifica a prioridade e converte o valor para o formato do OTM
                    if (r.name() == 'DeliveryPriority') {
                        prioridade = r.text()
                        if(prioridade == '00' || prioridade == '') {
                            prioridade = '1';
                        }
                    }

                    //Salva o valor para utilização no mapeamento
                    if (r.name() == 'DeliveryDocument') {
                        deliveryNum = r.text()
                    }

                    //Salva o valor para utilização no mapeamento
                    if (r.name() == 'ActualDeliveryQuantity') {
                        quantidade = r.text()
                    }

                    if (r.name() == 'Division') {
                        division = r.text()
                    }


                };
        while ( count < size){
            getValue = root.A_OutbDeliveryHeaderType.to_DeliveryDocumentItem.A_OutbDeliveryItemType[count].Material.text();
            if (count == 0){
                produto = "((Product eq '" + getValue +"')"
            }
            else{
                produto = produto + " or " + "(Product eq '" + getValue +"')"
            }
            count = count + 1;
        }
        produto = produto +")"

        //Salva as variáveis dentro das properties
        message.setProperty( "division" , division);
        message.setProperty( "customer" , customer);
        message.setProperty( "dexco" , dexco);
        message.setProperty( "quantidade" , quantidade);
        message.setProperty( "deliveryNum" , deliveryNum);
        message.setProperty( "prioridade" , prioridade);
        message.setProperty( "tipo_remessa" , valor);
        message.setProperty( "check" , check);
        message.setProperty( "shipToTransf" , shipToTransf);
        message.setProperty( "shipFromTransf" , shipFromTransf);
        message.setProperty( "produto" , produto);
    }
    return message;
}
