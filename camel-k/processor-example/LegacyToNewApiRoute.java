import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import java.util.Map;
import java.util.HashMap;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;

public class LegacyToNewApiRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("rest:post:/order/create")
            .choice()
                .when(simple("${header.legacy} == 'true'"))
                    .to("direct:legacy-orders")
                .otherwise()
                    .to("direct:orders");
        
        from("direct:legacy-orders")
            .unmarshal().json(JsonLibrary.Jackson)
            .process(new OrderProcessor())
            .marshal().json(JsonLibrary.Jackson)
            .to("direct:orders");
        
        from("direct:orders")
            .setHeader("CamelHttpMethod", constant("POST"))
            .to("http://10.244.0.12:8081?bridgeEndpoint=true");
    }

    class OrderProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            Map<String, Object> legacyOrder = exchange.getIn().getBody(Map.class);
    
            Map<String, Object> newOrder = new HashMap<>();
            newOrder.put("cartId", legacyOrder.get("numeroCarrinho"));
            newOrder.put("userId", legacyOrder.get("numeroUsuario"));
    
            exchange.getIn().setBody(newOrder);
        }
    }
}
