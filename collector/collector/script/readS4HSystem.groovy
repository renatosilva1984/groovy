import com.sap.it.api.ITApiFactory;
import com.sap.it.api.mapping.ValueMappingApi;
import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {
    def messageHeaders = message.getHeaders();
    def route = messageHeaders.get("route");
    def s4hSystem = message.getProperty("S4HSystem");
    
    if (route != null){
        def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
        try{
            def mappedS4HSystem = valueMapApi.getMappedValue('External', 'S4HSystem', route, 'Internal', 'S4HSystem');
            if (mappedS4HSystem != null){
                s4hSystem = mappedS4HSystem;
            }
        }catch(Exception e){
            // Nothing to do 
        }
    }
    message.setProperty("Router", route);
    message.setProperty("S4HSystem", s4hSystem);
    return message;
}