# protoconf-java - A Java SDK for protoconf

protoconf-java is the java sdk for [protoconf](https://github.com/yoozoo/protoconf) .

## Java Versions

Java 8 or above is required.

## Download

### Maven

Download protoconf-java jar from [Release] (https://github.com/yoozoo/protoconf-java/releases)
Install protoconf-java jar to local maven repository

```bash
mvn install:install-file -Dfile=<path-to-jar>
```

Add dependency to Pom.xml

```xml
<dependency>
  <groupId>com.yoozoo.protoconf</groupId>
  <artifactId>protoconf-java</artifactId>
  <version>${protoconf-java-version}</version>
</dependency>
```

### Usage

```java
// set app token
String appToken = "U2FsdGVkX1+EGNROfb41wAhtOumHKQPkli1FEL54C/U=";
EtcdReader etcdReader = new EtcdReader();
etcdReader.setAppToken(appToken);

// connect to etcd
ConfigurationReader configurationReader = new ConfigurationReader(etcdReader);

// fill in etcd values to config instance
configurationReader.config(Configuration.instance());

// watch config changes
Configuration.instance().watch_name(new ChangeListener() {
    @Override
    public void onChange(String newValue) {
        System.out.println("name has been changed to: " + newValue);
    }
});
configurationReader.watchKeys(Configuration.instance());

// get config value
dataSource.setUrl(Configuration.instance().get_mysqlDsn());
dataSource.setUsername(Configuration.instance().get_username());
dataSource.setPassword(Configuration.instance().get_password());
```
