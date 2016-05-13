# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 25 - Campus Taguspark

Luis Santos 77900 007lads@gmail.com

Pedro Fernandes 77961 pedro.f.fernandes@tecnico.ulisboa.pt

Constantin Zavgorodnii 78030 constantin.zavgorodnii@tecnico.ulisboa.pt


Repositório:
[tecnico-distsys/T_25-project](https://github.com/tecnico-distsys/T_25-project)

-------------------------------------------------------------------------------

## Instruções de instalação


### Ambiente

[0] Iniciar sistema operativo

Linux

[1] Iniciar servidores de apoio

[JUDDI](http://disciplinas.tecnico.ulisboa.pt/leic-sod/2015-2016/download/juddi-3.3.2_tomcat-7.0.64_9090.zip) (assumindo que existe o JUDDI na maquina):

```
cd $JUDDI_HOME/bin
startup.sh
```


[2] Criar pasta temporária

```
mkdir T_25-project
cd T_25-project
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone -b SD_R2 https://github.com/tecnico-distsys/T_25-project/

cd T_25-project
```

[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

[5] Instalar serviço de handlers

```
cd ws-handlers
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço Certificate Authority (CA)

[1] Construir e executar **servidor**

```
cd ca-ws
mvn package exec:java
```

[2] Construir **cliente** e executar testes

```
cd ca-ws-cli
mvn clean install
```

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd transporter-ws
mvn clean install
mvn exec:java -Dws.i=1
mvn exec:java -Dws.i=2
```

[2] Construir **cliente** e executar testes

```
cd transporter-ws-cli
mvn clean install
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd broker-ws
mvn clean install

mvn exec:java -Dws.num=0
mvn exec:java -Dws.num=1
```


[2] Construir **cliente** e executar testes

```
cd broker-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
