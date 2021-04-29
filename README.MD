# MC-SERVER

![version: 1.0-SNAPSHOT](https://img.shields.io/badge/version-1.0-05b.svg?style=flat)
![codename: ZERO](https://img.shields.io/badge/codename-ZERO-509.svg?style=flat)
![protocol: 1.12.2](https://img.shields.io/badge/protocol-1.12.2-075.svg?style=flat)

---

* Java 11

---

## Запуск

### Gradle

```shell
gradle :server:run --args="--config=config.yml" --project-prop jvmArgs="-Dlogback.configurationFile=logback.xml"
```

### Jar

```shell
java -Dlogback.configurationFile=logback.xml -jar server.jar --config=config.yml
```