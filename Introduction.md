# Introduction #

## Download ##
  * Download the archive file and extract.

  * http://code.google.com/p/tamacat/downloads/list

## Configuring httpd ##

Please make the following configuration files for the place where in CLASSPATH.
> (It makes it referring to `tamacat-httpd/conf`.)

### Configuration Files: ###
  * httpd.xml
  * server.properties
  * components.xml
  * url-config.xml
  * velocity.properties (use by default it)
  * log4j.properties (use by default it)
  * mime-types.properties (option)


#### httpd.xml (required) ####
```
<?xml version="1.0" encoding="UTF-8"?>
<beans>
  <bean id="gzip" class="org.tamacat.httpd.filter.GzipResponseInterceptor" singleton="false">
    <property name="contentType">
      <value>html,xml,css,javascript</value>
    </property>
  </bean>
  <bean id="server" class="org.tamacat.httpd.core.HttpEngine" singleton="false">
    <property name="propertiesName">
      <value>server.properties</value>
    </property>
    <property name="httpResponseInterceptor">
      <ref bean="gzip" />
    </property>
  </bean>  
</beans>
```

#### server.properties (required) ####
```
ServerName=tamacat-httpd
Port=80

https=false
https.keyStoreFile=test.keystore
https.keyPassword=nopassword
https.keyStoreType=JKS
https.protocol=TLS
https.clientAuth=false

MaxServerThreads=5
ServerSocketTimeout=15000
ConnectionTimeout=15000
ServerSocketBufferSize=8192
WorkerThreadName=httpd

BackEndSocketTimeout=15000
BackEndConnectionTimeout=15000
BackEndSocketBufferSize=8192

url-config.file=url-config.xml
components.file=components.xml

#JMX.server-url=service:jmx:rmi:///jndi/rmi://localhost:1099/httpd
#JMX.objectname=org.tamacat.httpd:type=HttpEngine
#JMX.rmi.port=1099
```
##### Details explanation of the setting: #####
| **key** | **value** | **description** |
|:--------|:----------|:----------------|
| Port    | 80        |  Httpd Listen Port |
| https   | false/true | true: use https protocol |
| https.keyStoreFile | test.keystore | The key.store file is specified. It is possible to make it by using keytool. keystore file create by keytools |
| https.keyPassword | nopassword | keystore file's pasword |
| https.protocol | SSL,SSLv2,SSLv3,TLS,TLSv1,TLSv1\_1 | SSL/TLS version |
| https.clientAuth | false/true | true: use client authentication |
| MaxServerThreads | 5         | Maximum worker threads of server. |
| ServerSocketTimeout | 15000     | socket timeout (ms) |
| ConnectionTimeout| 15000     | connection timeout (ms) |
| ServerSocketBufferSize | 8192      | socket buffer size (bytes) |
| WorkerThreadName | httpd     | name of child threads |
| BackEndSocketTimeout| 15000     | socket timeout for reverse proxy (ms) |
| BackEndConnectionTimeout| 15000     | connection timeout for reverse proxy (ms) |
| BackEndSocketBufferSize| 8192      | socket buffer size for reverse proxy (bytes) |
| url-config.file | url-config.xml | URL configuration file in CLASSPATH. |
| components.file | components.xml | Components configuration file in CLASSPATH. |
| JMX.server-url| service:jmx:rmi:///jndi/rmi://localhost:1099/httpd | JMX remote access URL (option)|
| JMX.objectname | org.tamacat.httpd:type=HttpEngine | ObjectName of httpd (option)|
| JMX.rmi.port | 1099      | JMX remote access port of RMI.(option)|

#### components.xml (required) ####
```
<?xml version="1.0" encoding="UTF-8"?> 
<beans>
  <bean id="DefaultHandler" class="org.tamacat.httpd.core.LocalFileHttpHandler" singleton="false" />
</beans>
```
##### Handler class #####
| **class** | **description** |
|:----------|:----------------|
| org.tamacat.httpd.core.LocalFileHttpHandler | returns local contents. |
| org.tamacat.httpd.core.VelocityHttpHandler | returns local contents using velocity templates. |
| org.tamacat.httpd.core.ReverseProxyHttpHandler | returns remote server contents. |

#### url-config.xml (required) ####
```
<?xml version="1.0" encoding="UTF-8"?> 
<service-config>
  <service>
    <url path="/" type="normal" handler="DefaultHandler" />
  </service>
</service-config>
```

##### url parameters #####
| **name** | **value** | **description** |
|:---------|:----------|:----------------|
| type     | normal, reverse, lb | "normal" is standard web server. "reverse" is Reverse proxy. "lb" is Load balancing with reverse proxy. |
| handler  | The name of handler in components.xml | The Bean name in components.xml. |
| lb-method| The name of load balancing method for type="lb" | lb-method="RoundRobin" or "LeastConnection" default is "RoundRobin" (since v0.9) |

##### ex. Virtual host (using HTTP Host request header) #####
```
<service host="http://www.example.com">
```

#### mime-types.properties (option) ####
```
...
gif=image/gif
...
html=text/html
...
jpg=image/jpeg
...
```