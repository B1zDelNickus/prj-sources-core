# Источник "Общие библиотеки для использования в источниках" (core)

## Основные ресурсы

1. UI клиента
    1. [Стейдж](http://rest-dev.sources.ssc.int/api/v1/core/ui)
    2. [Прод](https://sources-core.spectrum.codes/api/v1/core/ui)
2. UI ETL
    1. [Стейдж](http://rest-dev.sources-etl.ssc.int/api/v1/core/etl/ui)
    2. [Прод](https://sources-core-etl.spectrum.codes/api/v1/core/etl/ui)
    
    
## Информация, связанная с шаблоном

Шаблон предназначен для создания библиотек для источников.

Идеология шаблона - 

1. максимальная централизация всех сведений и алогоритмов, связанных
с конкретным источником 
2. минимизацией зоны покрытия, необходимой при интеграци с источником
3. обеспечение автономных служб для развертывания

## Переменные окружения для настроек соединения bus сервиса (смотри в `codes.spectrum.bus.builder.*`)
- `rabbitHost` - пример `rbt-test-01.spectrum.codes`
- `rabbitPort` - пример `5672`
- `rabbitUser` - пример `test`
- `rabbitPassword` - пример `test`

## Бутстрап

После клонирования:

1. Выполнить в консоли `./upbuilder` (обеспечит применение последней версии билдера) - эту операцию можно проводить в принципе регулярно
2. Открыть файл `source.json` и настроить в нем поля (делать осмысленно, потом переделывать будет сложно) 
   1. `code`, желательно чтобы совпадало с именем репозитория - будет использоваться как код источника в URL и именах пакетов JAVA
   2. `name`, читаемое имя источника 
3. Выполнить `gradle setup-source`
4. После этого будет настроена структура проекта в части папок кода и будет сформирован README.md


> Внимание! Все подробности по текущей версии шаблона доступны [здесь](https://gitlab.com/spectrum-internal/buildSrc/blob/master/TEMPLATE-SOURCE.md)