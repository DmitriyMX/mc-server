# MC-SERVER

![version: 1.0-SNAPSHOT](https://img.shields.io/badge/version-1.0-05b.svg?style=flat)
![codename: ZERO](https://img.shields.io/badge/codename-ZERO-509.svg?style=flat)
![protocol: 1.12.2](https://img.shields.io/badge/protocol-1.12.2-075.svg?style=flat)

Написанный с нуля сервер **Minecraft 1.12.2**.

На данный момент может только показывать информацию о себе. Подключение к серверу не возможно.

---

## Требования

* Java 11

## Запуск

Для запуска требуются некоторые файлы настроек. Для их генерации можно воспользоваться командой инициализации окружения:

```shell
java -jar server.jar --init
```

После выполнить запуск самого сервера:

```shell
java -jar server.jar
```

### Параметры командной строки

`--init`  
Инициализация окружения. Генерирует необхидимые для запуска сервера файлы.

`--config=path/to/config.yml`  
Указание альтернативного пути для конфигурационного файла сервера.

`--logconfig=path/to/logback.xml`  
Указание альтернативного пути для конфигурационного файла логгера (logback).

## Настройки

### Стилизованный текст

Файл конфига позволяет использовать специальные коды для добавления цвета и стиля в текст.  

| Код  | Цвет                                        | Код  | Стиль                                                           |
| ---- | ------------------------------------------- | ---- | --------------------------------------------------------------- |
| `&0` | <span style="color:#000">Black</span>       | `&l` | <span style="font-weight:bold">Bold</span>                      |
| `&1` | <span style="color:#00A">Dark Blue</span>   | `&o` | <span style="font-style:italic">Italic</span>                   |
| `&2` | <span style="color:#0A0">Dark Green</span>  | `&n` | <span style="text-decoration:underline">Underline</span>        |
| `&3` | <span style="color:#0AA">Dark Aqua</span>   | `&m` | <span style="text-decoration:line-through">Strikethrough</span> |
| `&4` | <span style="color:#A00">Dark Red</span>    | `&k` | Obfuscated                                                      |
| `&5` | <span style="color:#A0A">Dark Purple</span> |
| `&6` | <span style="color:#FA0">Gold</span>        |
| `&7` | <span style="color:#AAA">Gray</span>        |
| `&8` | <span style="color:#555">Dark Gray</span>   |
| `&9` | <span style="color:#55F">Blue</span>        |
| `&a` | <span style="color:#5F5">Green</span>       |
| `&b` | <span style="color:#5FF">Aqua</span>        |
| `&c` | <span style="color:#F55">Red</span>         |
| `&d` | <span style="color:#F5F">Purple</span>      |
| `&e` | <span style="color:#FF5">Yellow</span>      |
| `&f` | White                                       |


### motd

```yaml
motd: |
  mc-project :: ZERO
  develop by DmitriyMX
```

Настройка надписи, которая будет отображаться в списке серверов у клиента. Максимум может состоять из двух строк.

### disconnect-reason

```yaml
disconnect-reason: Server is not available.
```

Причина отключения от сервера. Количество строк не ограничено.

### players

```yaml
players:
  max-online: 0
  online: 0
```

Фиктивные данные об онлайне сервера.

### icon

```yaml
icon:
  enable: true
  path: favicon.png
```

Использовать значок сервера.  
Настройка `enable` говорит о факте использования значка, а в настройке `path` указывается путь к значку.

Формат значка должен быть **PNG** и быть размерами **64x64 px**. Другие форматы или размеры _не поддерживаются_.