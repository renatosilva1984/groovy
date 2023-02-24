import com.sap.it.api.mapping.*;

def String getProperty(String propertyName, MappingContext context){
	def property = context.getProperty(propertyName);
	return property.toString(); 
}