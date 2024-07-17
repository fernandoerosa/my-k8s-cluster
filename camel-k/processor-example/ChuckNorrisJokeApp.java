import org.apache.camel.builder.RouteBuilder;

public class ChuckNorrisJokeApp extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        rest("/random")
            .consumes("application/json")
            .produces("application/json")
            .get()
            .to("direct:start");

        from("direct:start")
            .to("https://api.chucknorris.io/jokes?bridgeEndpoint=true&throwExceptionOnFailure=false")
            .process(exchange -> {
                String joke = exchange.getIn().getBody(String.class);
                System.out.println(joke);
            });

    }
}
