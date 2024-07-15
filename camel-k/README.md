### Subindo o Camel K no cluster Kubernetes (Kind)
| É necessario as credencias para o docker.hub serem validas para o kamel realizar o login.

```shell
kubectl -n default create secret docker-registry external-registry-secret --docker-username fernandoerosa --docker-password "senha"
```

```shell
kamel install --olm=false -n default --registry docker.io --organization fernandoerosa --registry-secret external-registry-secret --wait
```

### Rodando integrações

| A integração pode ser feita com arquivo groovy (CLI Kamel) ou componentes do Kubernetes.

#### Groovy

Criar arquivos hello.groovy:
```groovy
from('timer:tick?period=3000')
  .setBody().constant('Hello world from Camel K')
  .to('log:info')
```

Rodar o comando:
```shell
kamel run hello.groovy
```
#### Componentes do Kubernetes

Criar my-integration.yaml:
```yaml
apiVersion: camel.apache.org/v1
kind: Integration
metadata:
  creationTimestamp: null
  name: my-integration
  namespace: default
spec:
  sources:
  - content: "
    import org.apache.camel.builder.RouteBuilder;
    public class Sample extends RouteBuilder {
      @Override
      public void configure()
      throws Exception {
        from(\"timer:tick\")
        .log(\"Hello Integration!\");
       }
      }"
    name: Sample.java
status: {}
```

Rodar comando kubectl para subir o componente:
```shell
kubectl apply -f my-integration.yaml
```