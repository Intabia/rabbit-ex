## Rabbit retry library - библиотека, позволяющая выполнить переданную функцию над сообщением rabbit, а в случае ошибки повторно отправить сообщение в очередь.

* При этом можно настроить задержку повторной отправки сообщения в очередь и
  механизм парковки, позволяющий отправить сообщение в отдельную очередь по
  истечении определенного количества попыток.


* Для использования библиотеки необходимо добавить ее в зависимость и в
  используемом классе заинжектить RabbitMQRetryExecutor.


* Если необходимо менять настройки задержки и парковки на ходу (без перезапуска
  приложения), то необходимо добавлять сервис через ObjectFactory (
  ObjectFactory<RabbitMQRetryExecutor>). И указать в application.yml следующие
  настройки актуатора:

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    env:
      post:
        enabled: true
```

* У RabbitMQRetryExecutor есть метод executeWithRetries принимающий сообщение,
  функцию для обработки сообщения и конфиги для механизмов задержки и парковки (
  EXCHANGE и ROUTING_KEY). Необходимо сконфигурировать отдельный exchange для
  механизма задержки и отдельную очередь и exchange для механизма парковки.


* Так же в executeWithRetries можно передать свои реализации интерфейсов
  DelayPolitic и ParkingPolitic, но в при этом варианте не будет работать
  подмена на ходу. Для использования стандартных политик, нужно указать в
  application.yml или application.properties параметры для политик.

### Для политики задержки параметр politic.delay-name принимает значения:

1) increase - политика увеличения задержки отправки сообщений в очередь.
   Изначальное время задержки 5 секунд, с каждой отправкой время увеличивается,
   максимальное время 15 секунд
2) const - политика постоянной задержки отправки сообщений в очередь. Время
   задержки 5 секунд
3) custom-increase - аналогично increase, но можно задать параметры через
   application (изначальное время задержки указывается через параметр
   politic.custom.delay, максимальное время через politic.custom.max-delay)
4) custom-const- аналогично const, с указанием параметров через application (
   изначальное время задержки указывается через параметр politic.custom.delay)

* Задержка указывается в миллисекундах

### Для политик парковки параметр politic.parking-name принимает значения:

1) short - политика механизма парковки с коротким циклом ретраев
2) long - политика механизма парковки с длинным циклом ретраев
3) custom - политика, принимающая максимальное количество ретраев через
   application (politic.custom.retries)
4) without - условно без парковки (максимальное количество ретраев
   Long.MAX_VALUE)

* Необходимо использовать версию RabbitMQ поддерживающую механизм задержки.

### Build и Push в Nexus

#### Build проекта

```shell
mvn clean install
```

#### Push в nexus

```shell
mvn deploy
```

#### NOTE

Настройки для пуша в nexus можно найти в confluence.

#### Build Docker image:

```shell
docker build --tag=rabbitmq-stage:1.0.0 .
```

#### Запуск RabbitMQ в Docker:

```shell
docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq-stage:1.0.0
```

#### Команда для изменения параметров при работающем приложении:

```shell
curl -H "Content-Type: application/json" -X POST -d '{"name":NAME, "value":VALUE}' http://localhost:8080/actuator/env/
```

#### Вместо NAME и VALUE подставляются необходимые параметры. Например, чтобы задать время задержки 20 секунд и переключиться на кастомную политику задержки выполняются следующие команды:

```shell
curl -H "Content-Type: application/json" -X POST -d '{"name":"politic.custom.delay", "value":"20000"}' http://localhost:8080/actuator/env/
```

```shell
curl -H "Content-Type: application/json" -X POST -d '{"name":"politic.delay-name", "value":"custom-const"}' http://localhost:8080/actuator/env/
```

### Настройка десериализатора ответов RabbitMQ

Для игнорирования ошибок во время десериализации ответов из кролика добавьте в
конфигурацию приложения следующее свойство:

```yaml
deserialization-config:
  ignore-deserialization-exceptions: true
```

Вышеупомянутая конфигурация ответчает за создание
**DeserializationProblemHandler**'а, ответственного за обработку ошибок во время
десериализации ответа из **RabbitMQ**.

Данный handler имеет логику игнорирования ошибок во время десериализации
входного сообщения.

По умолчанию свойство **
deserialization-config.ignore-deserialization-exceptions**
имеет значение '**false**'.
