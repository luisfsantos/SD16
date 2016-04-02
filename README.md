# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 25 - Campus Taguspark

Luis Santos 77900 007lads@gmail.com

Pedro Fernandes 77961 (...)

Constantin Zavgorodnii 78030 (...)


Repositório:
[tecnico-distsys/T_25-project](https://github.com/tecnico-distsys/T_25-project)

-------------------------------------------------------------------------------

## Instruções de instalação


### Ambiente

[0] Iniciar sistema operativo

Linux

[1] Iniciar servidores de apoio

JUDDI:
```
...
```


[2] Criar pasta temporária

```
mkdir T_25-project
cd T_25-project

```


[3] Obter código fonte do projeto (versão entregue)

```
git clone https://github.com/tecnico-distsys/T_25-project
```
*(colocar aqui comandos git para obter a versão entregue a partir da tag e depois apagar esta linha)*


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd ...
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd transporter-ws
mvn clean install
mvn exec:java
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
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd broker-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
